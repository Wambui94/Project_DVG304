package algorithms;

import java.util.*;

import aod.lab5.graph.Edge;
import aod.lab5.graph.Graph;
import aod.lab5.graph.Vertex;

public class dijkstra<T> {

    private Map<T, T> previous = new HashMap<>();

    public Map<T, Double> shortestPaths(Graph<T> graph, T start) {

        Map<T, Double> distances = new HashMap<>();
        Set<T> visited = new HashSet<>();
        previous.clear();

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

                double newDistance =
                        distances.get(current) + edge.getDistance();

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        return distances;
    }

    public List<T> getShortestPath(T goal) {

        List<T> path = new ArrayList<>();

        T current = goal;

        while (current != null) {
            path.add(current);
            current = previous.get(current);
        }

        Collections.reverse(path);

        return path;
    }
}