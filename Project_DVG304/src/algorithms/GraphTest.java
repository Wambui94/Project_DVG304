package algorithms;

import java.util.Map;

import aod.lab5.graph.Graph;

public class GraphTest {

    public static void main(String[] args) {

        // Skapa graf
        Graph<String> graph = new Graph<>();

        // Lägg till serverhallar
        graph.addVertex(100, 100, "Gävle");
        graph.addVertex(200, 100, "Stockholm");
        graph.addVertex(300, 200, "Malmö");
        graph.addVertex(150, 250, "Uppsala");

        // Koppla ihop serverhallar
        graph.addEdge("Gävle", "Stockholm");
        graph.addEdge("Gävle", "Uppsala");
        graph.addEdge("Uppsala", "Malmö");
        graph.addEdge("Stockholm", "Malmö");

        // Skapa Dijkstra
        Dijkstra<String> dijkstra = new Dijkstra<>();

        // Beräkna kortaste vägar från Gävle
        Map<String, Double> result =
                dijkstra.shortestPaths(graph, "Gävle");

        // Skriv ut resultat
        for (String city : result.keySet()) {

            System.out.println(
                "Avstånd från Gävle till "
                + city
                + " = "
                + result.get(city)
            );
        }
    }
}
