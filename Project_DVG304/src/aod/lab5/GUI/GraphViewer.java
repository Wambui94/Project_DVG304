package aod.lab5.GUI;

import aod.lab5.graph.Graph;
import aod.lab5.graph.Vertex;
import aod.lab5.graph.Edge;
import algorithms.Dijkstra;
import algorithms.Quadtree;

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

/**
 * Main window for visualizing the graph on top of a map of Sweden.
 *
 * This class creates the graph, generates random vertices on valid land
 * pixels, connects nearby vertices, and displays the result in a GUI.
 * It also contains buttons for showing the shortest path and finding
 * the nearest server hall using Dijkstra's algorithm.
 *
 * @param <T> the type of data stored in each vertex
 */
public class GraphViewer<T> extends JFrame {

    public static final int MAP_WIDTH = 800;
    public static final int MAP_HEIGHT = 600;

    public GraphViewer(Graph<T> graph) {
        setTitle("Mapping of server rooms and electrical centers in sweden");
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
                String start = startInput;
                String goal = goalInput;

                showShortestPath((Graph<String>) graph, start, goal);
                panel.repaint();
            }
        });
        JButton nearestButton =
                new JButton("Find nearest server hall");

        nearestButton.addActionListener(e -> {

            String startInput =
                    JOptionPane.showInputDialog(
                            "Start point example: 70"
                    );

            if (startInput != null) {

                findNearestServerHall(
                        (Graph<String>) graph,
                        startInput
                );

                panel.repaint();
            }
        });

        JPanel buttonPanel = new JPanel();

        buttonPanel.add(shortestButton);
        buttonPanel.add(nearestButton);

        add(buttonPanel, BorderLayout.NORTH);
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
                //System.out.println(mapImage.getWidth());
                //System.out.println(mapImage.getHeight());

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

                graph.addVertex(x, y,""+ i);
            }

            connectNearbyPoints(graph);
            

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
   
  
    private static void connectNearbyPoints(Graph<String> graph) {

        double maxDistance = 30.0;

        Quadtree quadtree =
                new Quadtree(
                        new Quadtree.Rectangle(
                                0,
                                0,
                                MAP_WIDTH,
                                MAP_HEIGHT
                        ),
                        4
                );

        /*
         * INSERT ALL VERTICES
         */
        for (Vertex<String> v : graph.getAllVertices()) {

            quadtree.insert(
                    new Quadtree.Point(
                            v.getX(),
                            v.getY(),
                            v.getInfo()
                    )
            );
        }

        /*
         * SEARCH NEARBY POINTS
         */
        for (Vertex<String> v1 : graph.getAllVertices()) {

            Quadtree.Rectangle searchArea =
                    new Quadtree.Rectangle(
                            v1.getX() - maxDistance,
                            v1.getY() - maxDistance,
                            maxDistance * 2,
                            maxDistance * 2
                    );

            List<Quadtree.Point> nearbyPoints =
                    quadtree.query(searchArea);

            for (Quadtree.Point p : nearbyPoints) {

                /*
                 * AVOID DUPLICATES
                 */
                if (v1.getInfo().compareTo(p.name) >= 0) {
                    continue;
                }

                double dx = v1.getX() - p.x;
                double dy = v1.getY() - p.y;

                double distance =
                        Math.sqrt(dx * dx + dy * dy);

                if (distance < maxDistance) {

                    graph.addEdge(
                            v1.getInfo(),
                            p.name
                    );
                }
            }
        }
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

    /**
    Changes the color of an edge.

    @param graph the graph
    @param from the start vertex
    @param to the destination vertex
    @param color the new color
    */
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
    
   
    private static void findNearestServerHall(
            Graph<String> graph,
            String start
    ) {
        clearEdgeColors(graph);

        Dijkstra<String> dijkstra =
                new Dijkstra<>();

        dijkstra.shortestPaths(graph, start);

        String nearest =
                dijkstra.getNearestPoint(start);

        if (nearest == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "No nearest server hall found"
            );
            return;
        }

        colorEdge(graph, start, nearest, Color.RED);
        colorEdge(graph, nearest, start, Color.RED);

        JOptionPane.showMessageDialog(
                null,
                "Nearest server hall from "
                        + start
                        + " is "
                        + nearest
                        + "\nDistance: "
                        + String.format(
                                "%.1f",
                                dijkstra.getDistanceTo(nearest)
                        )
        );
    }

    /**
    Checks if a pixel represents land.

    @param image the map image
    @param x the x-coordinate
    @param y the y-coordinate
    @return true if land, otherwise false
    */
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
                red > 180 && green > 80 && green < 180 && blue < 100;

        boolean blueSweden =
                blue > 120
                && green > 90
                && green < 180
                && red < 120;

        return blueSweden
                && !whiteBackground
                && !blackText
                && !orangeCityDot;
    }
}