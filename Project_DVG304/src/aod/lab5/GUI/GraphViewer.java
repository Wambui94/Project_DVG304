package aod.lab5.GUI;

import aod.lab5.graph.Graph;
import aod.lab5.graph.Vertex;
import aod.lab5.graph.Edge;
import algorithms.Dijkstra;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.Map;

public class GraphViewer<T> extends JFrame {

    private static final int MAP_WIDTH = 800;
    private static final int MAP_HEIGHT = 600;

    public GraphViewer(Graph<T> graph) {
        setTitle("Sweden Graph Viewer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(MAP_WIDTH, MAP_HEIGHT);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        GraphPanel<T> panel = new GraphPanel<>(graph);

        JButton shortestButton = new JButton("Show shortest path");

        shortestButton.addActionListener(e -> {
            String startInput = JOptionPane.showInputDialog("Start point example: 0");
            String goalInput = JOptionPane.showInputDialog("End point example: 100");

            if (startInput != null && goalInput != null) {
                String start = "SE" + startInput;
                String goal = "SE" + goalInput;

                showShortestPath((Graph<String>) graph, start, goal);
                panel.repaint();
            }
        });

        add(shortestButton, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }



    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Graph<String> graph = new Graph<>();
            Random rand = new Random();

            BufferedImage mapImage = null;

            try {
                URL mapUrl =
                        GraphViewer.class.getResource(
                                "/map-of-sweden-with-cities.png"
                        );

                mapImage = ImageIO.read(mapUrl);

            } catch (IOException | IllegalArgumentException e) {
                System.out.println("Could not load map for point generation");
            }

            for (int i = 0; i < 250; i++) {

                int x;
                int y;

                while (true) {
                    x = rand.nextInt(MAP_WIDTH);
                    y = rand.nextInt(MAP_HEIGHT);

                    if (mapImage != null && isLandPixel(mapImage, x, y)) {
                        break;
                    }
                }

                graph.addVertex(x, y, "SE" + i);
            }

            List<Vertex<String>> vertices = graph.getAllVertices();

            for (int i = 0; i < vertices.size(); i++) {
                Vertex<String> v1 = vertices.get(i);

                for (int j = i + 1; j < vertices.size(); j++) {
                    Vertex<String> v2 = vertices.get(j);

                    double dx = v1.getX() - v2.getX();
                    double dy = v1.getY() - v2.getY();

                    double distance = Math.sqrt(dx * dx + dy * dy);

                    if (distance < 30) {
                        graph.addEdge(
                                v1.getInfo(),
                                v2.getInfo()
                        );
                    }
                }
            }

            for (Vertex<String> v : graph.getAllVertices()) {
                v.setColor(new Color(0, 120, 255, 180));

                for (Edge<String> e : graph.getEdges(v.getInfo())) {
                    e.setColor(new Color(180, 180, 180, 120));
                }
            }

            GraphViewer<String> viewer = new GraphViewer<>(graph);
            viewer.setVisible(true);
        });
    }

    private static void showShortestPath(
            Graph<String> graph,
            String start,
            String goal
    ) {
        clearEdgeColors(graph);

        Dijkstra<String> dijkstra = new Dijkstra<>();

        Map<String, Double> distances =
                dijkstra.shortestPaths(graph, start);

        List<String> path =
                dijkstra.getShortestPath(goal);

        if (path.size() <= 1 ||
                distances.get(goal) == Double.MAX_VALUE) {

            JOptionPane.showMessageDialog(
                    null,
                    "No path found"
            );
            return;
        }

        for (int i = 0; i < path.size() - 1; i++) {
            String from = path.get(i);
            String to = path.get(i + 1);

            colorEdge(graph, from, to, Color.RED);
            colorEdge(graph, to, from, Color.RED);
        }

        JOptionPane.showMessageDialog(
                null,
                "Shortest distance: "
                        + String.format("%.1f", distances.get(goal))
                        + "\nPath: "
                        + path
        );
    }

    private static void colorEdge(
            Graph<String> graph,
            String from,
            String to,
            Color color
    ) {
        for (Edge<String> edge : graph.getEdges(from)) {
            if (edge.getTo().getInfo().equals(to)) {
                edge.setColor(color);
            }
        }
    }

    private static void clearEdgeColors(Graph<String> graph) {
        for (Vertex<String> v : graph.getAllVertices()) {
            for (Edge<String> e : graph.getEdges(v.getInfo())) {
                e.setColor(new Color(180, 180, 180, 120));
            }
        }
    }

    private static boolean isLandPixel(
            BufferedImage image,
            int x,
            int y
    ) {
        int imgX = x * image.getWidth() / MAP_WIDTH;
        int imgY = y * image.getHeight() / MAP_HEIGHT;

        int rgb = image.getRGB(imgX, imgY);

        int alpha = (rgb >> 24) & 0xff;
        int red = (rgb >> 16) & 0xff;
        int green = (rgb >> 8) & 0xff;
        int blue = rgb & 0xff;

        if (alpha < 50) {
            return false;
        }

        boolean whiteBackground =
                red > 230 && green > 230 && blue > 230;

        boolean blackText =
                red < 80 && green < 80 && blue < 80;

        boolean orangeCityDot =
                red > 180 && green > 80 && green < 180 && blue < 80;

        return !whiteBackground && !blackText && !orangeCityDot;
    }
}