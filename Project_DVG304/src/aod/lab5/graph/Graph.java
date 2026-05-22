package aod.lab5.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a graph structure consisting of vertices and edges.
 * 
 * The graph is implemented using adjacency lists stored in HashMaps.
 * Vertices are identified by a unique key of type T.
 * 
 * The graph handles undirected edges by internally storing them
 * as two directed Edge objects, one in each direction.
 * 
 * @param <T> the type of information stored in each vertex
 */
public class Graph<T> implements GraphInterface<T> {

    /** The number of vertices in the graph. */
    private int nVertices;

    /** The number of undirected edges in the graph. */
    private int nEdges;

    /** Stores all vertices in the graph. */
    private HashMap<T, Vertex<T>> vertices;

    /** Stores adjacency lists for each vertex. */
    private HashMap<T, ArrayList<Edge<T>>> edges;

    /**
     * Creates an empty graph.
     */
    public Graph() {
        this.nVertices = 0;
        this.nEdges = 0;
        this.vertices = new HashMap<>();
        this.edges = new HashMap<>();
    }

  
    @Override
    public List<Vertex<T>> getAllVertices() {
        return new ArrayList<>(vertices.values());
    }

   
    @Override
    public List<Edge<T>> getEdges(T info) {
        if (!edges.containsKey(info)) {
            return new ArrayList<>();
        }

        return new ArrayList<>(edges.get(info));
    }

    @Override
    public void addVertex(double x, double y, T info) {
        if (info == null) {
            return;
        }

        if (vertices.containsKey(info)) {
            return;
        }

        Vertex<T> vertex = new Vertex<>(x, y, info);

        vertices.put(info, vertex);
        edges.put(info, new ArrayList<>());

        nVertices++;
    }

    @Override
    public void addEdge(T infoA, T infoB) {
        if (infoA == null || infoB == null) {
            return;
        }

        if (!vertices.containsKey(infoA) || !vertices.containsKey(infoB)) {
            return;
        }

        if (infoA.equals(infoB)) {
            return;
        }

        if (edgeExists(infoA, infoB)) {
            return;
        }

        Vertex<T> vertexA = vertices.get(infoA);
        Vertex<T> vertexB = vertices.get(infoB);

        Edge<T> edgeAB = new Edge<>(vertexA, vertexB);
        Edge<T> edgeBA = new Edge<>(vertexB, vertexA);

        edges.get(infoA).add(edgeAB);
        edges.get(infoB).add(edgeBA);

        nEdges++;
    }

    /**
     * Checks whether an edge already exists between two vertices.
     * 
     * @param infoA the identifier of the first vertex
     * @param infoB the identifier of the second vertex
     * 
     * @return true if the edge exists, otherwise false
     */
    private boolean edgeExists(T infoA, T infoB) {
        ArrayList<Edge<T>> edgeList = edges.get(infoA);

        if (edgeList == null) {
            return false;
        }

        for (Edge<T> edge : edgeList) {
            if (edge.getTo().getInfo().equals(infoB)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void remove(T info) {
        if (info == null) {
            return;
        }

        if (!vertices.containsKey(info)) {
            return;
        }

        int removedEdges = edges.get(info).size();

        for (ArrayList<Edge<T>> edgeList : edges.values()) {
            edgeList.removeIf(edge -> edge.getTo().getInfo().equals(info));
        }

        edges.remove(info);
        vertices.remove(info);

        nVertices--;
        nEdges -= removedEdges;
    }

    public List<Vertex<T>> getVerticesInArea(
            double minX,
            double maxX,
            double minY,
            double maxY) {

        List<Vertex<T>> result = new ArrayList<>();

        for (Vertex<T> vertex : vertices.values()) {

            if (vertex.getX() >= minX &&
                vertex.getX() <= maxX &&
                vertex.getY() >= minY &&
                vertex.getY() <= maxY) {

                result.add(vertex);
            }
        }

        return result;
    }
  
    @Override
    public int numberOfEdges() {
        return nEdges;
    }

    @Override
    public int numberOfVertices() {
        return nVertices;
    }
}