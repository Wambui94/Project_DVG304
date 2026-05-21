package aod.lab5.GUI;

import aod.lab5.graph.Graph;
import aod.lab5.graph.Vertex;
import aod.lab5.graph.Edge;

import javax.swing.*;

import algorithms.Dijkstra;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Top layer in GUI
 * 
 * @param <T>
 */
public class GraphViewer<T> extends JFrame {

    public GraphViewer(Graph<T> graph) {
        setTitle("Graph Viewer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        GraphPanel<T> panel = new GraphPanel<>(graph);
        add(panel);
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

            Dijkstra<String> dijkstra = new Dijkstra<>();

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