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

    private class GraphPanel<T> extends JPanel {

        private final Graph<T> graph;
        private double zoom = 1.0;
        private final double FILTER_ZOOM_LEVEL = 2.5;
        private double offsetX = 0;
        private double offsetY = 0;
        private Point lastMousePosition;
        private BufferedImage swedenMap;

        public GraphPanel(Graph<T> graph) {
            this.graph = graph;
            setBackground(Color.WHITE);

            try {
                swedenMap = ImageIO.read(
                        getClass().getResource("/map-of-sweden-with-cities.png")
                );
            } catch (IOException | IllegalArgumentException e) {
                System.out.println("Could not load map image");
            }

            addMouseWheelListener(e -> {
                double oldZoom = zoom;

                if (e.getPreciseWheelRotation() < 0) {
                    zoom *= 1.1;
                } else {
                    zoom /= 1.1;
                }

                zoom = Math.max(0.2, Math.min(10.0, zoom));

                double scale = zoom / oldZoom;

                double mouseX = e.getX();
                double mouseY = e.getY();

                offsetX = mouseX - scale * (mouseX - offsetX);
                offsetY = mouseY - scale * (mouseY - offsetY);

                repaint();
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    lastMousePosition = e.getPoint();
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    int dx = e.getX() - lastMousePosition.x;
                    int dy = e.getY() - lastMousePosition.y;

                    offsetX += dx;
                    offsetY += dy;

                    lastMousePosition = e.getPoint();

                    repaint();
                }
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

            g2.translate(offsetX, offsetY);
            g2.scale(zoom, zoom);

            drawMapBackground(g2);

            if (zoom >= FILTER_ZOOM_LEVEL) {
                drawFilteredEdges(g2);
                drawFilteredVertices(g2);
            } else {
                drawFilteredEdges(g2);
                drawVertices(g2);
            }
        }

        private void drawMapBackground(Graphics2D g2) {
            if (swedenMap != null) {
                g2.drawImage(
                        swedenMap,
                        0,
                        0,
                        MAP_WIDTH,
                        MAP_HEIGHT,
                        null
                );
            }
        }

        private void drawFilteredEdges(Graphics2D g2) {
            for (Vertex<T> v : graph.getAllVertices()) {
                List<Edge<T>> edgeList = graph.getEdges(v.getInfo());

                for (Edge<T> edge : edgeList) {
                    Vertex<T> from = edge.getFrom();
                    Vertex<T> to = edge.getTo();

                    if (from.getInfo().toString()
                            .compareTo(to.getInfo().toString()) < 0) {

                        if (isVisible(from) && isVisible(to)) {
                            drawEdge(g2, edge);
                        }
                    }
                }
            }
        }

        private void drawEdge(Graphics2D g2, Edge<T> edge) {

            boolean isShortestPath =
                    edge.getColor() != null
                            && edge.getColor().equals(Color.RED);

            if (zoom < 6.0 && !isShortestPath) {
                return;
            }

            Vertex<T> from = edge.getFrom();
            Vertex<T> to = edge.getTo();

            int x1 = (int) from.getX();
            int y1 = (int) from.getY();
            int x2 = (int) to.getX();
            int y2 = (int) to.getY();

            if (isShortestPath) {
                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(1.0f));
            } else {
                g2.setColor(edge.getColor());
                g2.setStroke(new BasicStroke(0.5f));
            }

            g2.drawLine(x1, y1, x2, y2);

            if (zoom >= 8.0) {
                String distStr = String.format("%.1f", edge.getDistance());

                int textX = (int)(x1 + (x2 - x1) * 0.6);
                int textY = (int)(y1 + (y2 - y1) * 0.6);

                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.PLAIN, 1));
                g2.drawString(distStr, textX, textY);
            }
        }

        private void drawVertices(Graphics2D g2) {
            for (Vertex<T> v : graph.getAllVertices()) {
                drawVertex(g2, v);
            }
        }

        private void drawFilteredVertices(Graphics2D g2) {
            for (Vertex<T> v : graph.getAllVertices()) {
                if (isVisible(v)) {
                    drawVertex(g2, v);
                }
            }
        }

        private void drawVertex(Graphics2D g2, Vertex<T> v) {
            int x = (int) v.getX();
            int y = (int) v.getY();

            g2.setColor(new Color(0, 120, 255, 180));

            int size = 4;

            g2.fillOval(
                    x - size / 2,
                    y - size / 2,
                    size,
                    size
            );

            if (zoom >= 6.0) {
                String label = v.getInfo().toString().replace("SE", "");

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 2));

                FontMetrics fm = g2.getFontMetrics();

                int textWidth = fm.stringWidth(label);
                int textHeight = fm.getAscent();

                g2.drawString(
                        label,
                        x - textWidth / 2,
                        y + textHeight / 3
                );
            }
        }

        private boolean isVisible(Vertex<T> v) {
            double visibleLeft = -offsetX / zoom;
            double visibleTop = -offsetY / zoom;
            double visibleRight = visibleLeft + getWidth() / zoom;
            double visibleBottom = visibleTop + getHeight() / zoom;

            double x = v.getX();
            double y = v.getY();

            return x >= visibleLeft
                    && x <= visibleRight
                    && y >= visibleTop
                    && y <= visibleBottom;
        }
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