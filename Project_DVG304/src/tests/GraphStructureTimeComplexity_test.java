package tests;

import java.util.List;
import java.util.Random;

import algorithms.Quadtree;
import aod.lab5.graph.Graph;
import aod.lab5.graph.Vertex;

public class GraphStructureTimeComplexity_test {

    public static void main(String[] args) {

        int[] sizes = {1000, 2000, 5000, 10000, 20000, 40000};

        for (int numberOfVertices : sizes) {

            Random rand = new Random(42);

            long startTime = System.nanoTime();

            Graph<String> graph = new Graph<>();

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
             * CONNECT USING QUADTREE
             */
            connectNearbyPoints(graph);

            long endTime = System.nanoTime();

            double duration =
                    (endTime - startTime) / 1_000_000.0;

            System.out.println(
                    "Vertices: "
                            + numberOfVertices
            );

            System.out.println(
                    "Graph structure time: "
                            + duration
                            + " ms"
            );

            System.out.println("-------------------");
        }
    }

    private static void connectNearbyPoints(
            Graph<String> graph
    ) {

        double maxDistance = 30.0;

        Quadtree quadtree =
                new Quadtree(
                        new Quadtree.Rectangle(
                                0,
                                0,
                                1000,
                                1000
                        ),
                        4
                );

        /*
         * INSERT ALL POINTS
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
                 * AVOID DUPLICATE EDGES
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
}