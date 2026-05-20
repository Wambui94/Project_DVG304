package aod.lab5.GUI;

import aod.lab5.graph.Graph;
import aod.lab5.graph.Vertex;
import aod.lab5.graph.Edge;

import javax.swing.*;

import algorithms.dijkstra;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GraphViewer<T> extends JFrame {

    public GraphViewer(Graph<T> graph) {
        setTitle("Graph Viewer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        GraphPanel<T> panel = new GraphPanel<>(graph);
        add(panel);
    }

    private class GraphPanel<T> extends JPanel {

        private final Graph<T> graph;
        private final int RADIUS = 8;
        private double zoom = 1.0;

        public GraphPanel(Graph<T> graph) {
            this.graph = graph;
            setBackground(Color.WHITE);

            addMouseWheelListener(e -> {

                if (e.getWheelRotation() < 0) {
                    zoom *= 1.1;
                } else {
                    zoom /= 1.1;
                }

                repaint();
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            // Rita kanter
            for (Vertex<T> v : graph.getAllVertices()) {

                List<Edge<T>> edges = graph.getEdges(v.getInfo());

                for (Edge<T> edge : edges) {

                    Vertex<T> from = edge.getFrom();
                    Vertex<T> to = edge.getTo();

                    if (from.getInfo().toString()
                            .compareTo(to.getInfo().toString()) < 0) {

                        g2.setColor(
                                edge.getColor() != null
                                        ? edge.getColor()
                                        : Color.GRAY
                        );

                        int x1 = (int) (from.getX() * zoom);
                        int y1 = (int) (from.getY() * zoom);
                        int x2 = (int) (to.getX() * zoom);
                        int y2 = (int) (to.getY() * zoom);

                        g2.drawLine(x1, y1, x2, y2);

                        String distStr =
                                String.format("%.1f", edge.getDistance());

                        int midX = (x1 + x2) / 2;
                        int midY = (y1 + y2) / 2;

                        g2.setColor(Color.DARK_GRAY);
                        g2.drawString(distStr, midX + 5, midY - 5);
                    }
                }
            }

            // Rita noder
            for (Vertex<T> v : graph.getAllVertices()) {

                int x = (int) (v.getX() * zoom);
                int y = (int) (v.getY() * zoom);

                g2.setColor(
                        v.getColor() != null
                                ? v.getColor()
                                : Color.BLACK
                );

                g2.fillOval(
                        x - RADIUS,
                        y - RADIUS,
                        RADIUS * 2,
                        RADIUS * 2
                );

                g2.setColor(Color.BLACK);
                g2.drawString(
                        v.getInfo().toString(),
                        x + RADIUS + 2,
                        y - RADIUS - 2
                );
            }
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Graph<String> graph = new Graph<>();
            Random rand = new Random();

            for (int i = 0; i < 5; i++) {

                int x = 100 + rand.nextInt(600) - 50;
                int y = 100 + rand.nextInt(500) - 50;

                graph.addVertex(x, y, "STHLM" + i);
            }

            for (int i = 0; i < graph.getAllVertices().size(); i++) {

                for (int j = i + 1;
                     j < graph.getAllVertices().size();
                     j++) {

                    Vertex<String> v1 =
                            graph.getAllVertices().get(i);

                    Vertex<String> v2 =
                            graph.getAllVertices().get(j);

                    graph.addEdge(v1.getInfo(), v2.getInfo());
                }
            }

            dijkstra<String> dijkstra = new dijkstra<>();

            Map<String, Double> result =
                    dijkstra.shortestPaths(graph, "STHLM0");

            for (String city : result.keySet()) {

                System.out.println(
                        "Avstånd från STHLM0 till "
                                + city
                                + " = "
                                + result.get(city)
                );
            }
            System.out.println(graph.getVerticesInArea(0, 300, 0, 300));

            for (Vertex<String> v : graph.getAllVertices()) {

                for (Edge<String> e : graph.getEdges(v.getInfo())) {
                    e.setColor(Color.LIGHT_GRAY);
                }
            }

            GraphViewer<String> viewer =
                    new GraphViewer<>(graph);

            viewer.setVisible(true);
        });
    }
}