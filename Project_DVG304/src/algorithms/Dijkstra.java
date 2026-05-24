package algorithms;

import java.util.*;

import aod.lab5.graph.Edge;
import aod.lab5.graph.Graph;
import aod.lab5.graph.Vertex;

/**
 * Calculating the shortest distance
 * between nodes/vertices
 * 
 * @param <T> the nodes can contain any type
 */
public class Dijkstra<T> {
	
	// Keeps track of the previous node
	// A -> B -> C -> D
	// [B -> A] [ C -> B] ...
    private Map<T, T> previous = new HashMap<>();
    /**
     * Calculates the shortest path to all nodes in a graph
     * from a starting node.
     * @param graph
     * @param start
     * @return a hashmap with a key (the node) 
     * 		and a value (the distance from start)
     */
    public Map<T, Double> shortestPaths(Graph<T> graph, T start) {

        Map<T, Double> distances = new HashMap<>();
        Set<T> visited = new HashSet<>(); // single instances, faster
        previous.clear();	// reset from earlier runs
        
        // Give all nodes "infinite" distance
        for (Vertex<T> vertex : graph.getAllVertices()) {
            distances.put(vertex.getInfo(), Double.MAX_VALUE);
        }

        // set distance from and to start-node to 0
        distances.put(start, 0.0);

        // sort nodes according to shortest distance, when adding items to the queue
        PriorityQueue<T> queue = new PriorityQueue<>(
                (a, b) -> Double.compare(distances.get(a), distances.get(b))
        );

        queue.add(start);

        while (!queue.isEmpty()) {
            T current = queue.poll();
            
            // Skip current iteration if already visited
            if (visited.contains(current)) {
                continue;
            }
            
            visited.add(current);
            
            // visit all neighbors through edges
            for (Edge<T> edge : graph.getEdges(current)) {
                T neighbor = edge.getTo().getInfo();
                // Get distance to neighbor (from start)
                double newDistance =
                        distances.get(current) + edge.getDistance();

                if (newDistance < distances.get(neighbor)) { // true first time
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, current);
                    queue.add(neighbor);	// Check its neighbors later
                }
            }
        }

        return distances;
    }
    /**
     * Uses previous (the HashMap attribute) to backtrack 
     * the path from a specified node
     * @param goal, the specified node
     * @return a list of nodes from start to goal
     */
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