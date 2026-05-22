package tests;
import java.util.Map;
import java.util.Random;

import algorithms.Dijkstra;
import aod.lab5.graph.Graph;

public class DijkstraTimeComplexity_test {

    public static void main(String[] args) {

        int[] sizes = {1000, 2000, 5000, 10000, 20000};

        double edgeProbability = 0.001;

        for (int numberOfVertices : sizes) {

            Graph<String> graph =
                    createGraph(
                            numberOfVertices,
                            edgeProbability
                    );

            Dijkstra<String> dijkstra =
                    new Dijkstra<>();

            long startTime =
                    System.nanoTime();

            Map<String, Double> distances =
                    dijkstra.shortestPaths(
                            graph,
                            "V0"
                    );

            long endTime =
                    System.nanoTime();

            double duration =
                    (endTime - startTime)
                    / 1_000_000.0;

            System.out.println(
                    "Vertices: "
                    + numberOfVertices
            );

            System.out.println(
                    "Dijkstra time: "
                    + duration
                    + " ms"
            );

            System.out.println("-------------------");
        }
    }

    private static Graph<String> createGraph(
            int numberOfVertices,
            double edgeProbability
    ) {

        Graph<String> graph = new Graph<>();

        Random rand = new Random(42);

        /*
         * CREATE VERTICES
         */
        for (int i = 0; i < numberOfVertices; i++) {

            graph.addVertex(
                    rand.nextInt(1000),
                    rand.nextInt(1000),
                    "V" + i
            );
        }

        /*
         * CREATE EDGES
         */
        for (int i = 0; i < numberOfVertices; i++) {

            for (int j = i + 1;
                 j < numberOfVertices;
                 j++) {

                if (rand.nextDouble() < edgeProbability) {

                    graph.addEdge(
                            "V" + i,
                            "V" + j
                    );
                }
            }
        }

        return graph;
    }
}