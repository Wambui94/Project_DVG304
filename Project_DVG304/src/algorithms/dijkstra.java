/**
 * 
 */
package algorithms;

/**
 * A class wtih the djikstra algorithm to
 * calculate distances between vertecies
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import aod.lab5.graph.Edge;
import aod.lab5.graph.Graph;
import aod.lab5.graph.Vertex;

public class dijkstra<T> {

    public Map<T, Double> shortestPaths(Graph<T> graph, T start) {

        Map<T, Double> distances = new HashMap<>();
        Set<T> visited = new HashSet<>();

        for (Vertex<T> vertex : graph.getAllVertices()) {
            distances.put(vertex.getInfo(), Double.MAX_VALUE);
        }

        distances.put(start, 0.0);

        PriorityQueue<T> queue = new PriorityQueue<>(
            (a, b) -> Double.compare(distances.get(a), distances.get(b))
        );

        queue.add(start);

        while (!queue.isEmpty()) {
            T current = queue.poll();

            if (visited.contains(current)) {
                continue;
            }

            visited.add(current);

            for (Edge<T> edge : graph.getEdges(current)) {
                T neighbor = edge.getTo().getInfo();

                double newDistance = distances.get(current) + edge.getDistance();

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    queue.add(neighbor);
                }
            }
        }

        return distances;
    }
}
