package tests;
import java.util.Random;
import aod.lab5.graph.Graph;

public class GraphStructureTimeComplexity_test {


	

	    public static void main(String[] args) {

	        int[] sizes = {1000, 2000, 5000, 10000, 20000};

	        double edgeProbability = 0.001;

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
	}


