package aod.lab5.graph;

import java.awt.Color;

/**
 * Represents a directed edge between two vertices in a graph.
 * 
 * Each edge contains:
 * <ul>
 *   <li>A start vertex</li>
 *   <li>An end vertex</li>
 *   <li>The Euclidean distance between the vertices</li>
 *   <li>A color used for visualization</li>
 * </ul>
 * 
 * The distance is automatically calculated using the coordinates
 * of the two vertices.
 * 
 * @param <T> the type of information stored in the vertices
 * 
 */
public class Edge<T> {

    /** The starting vertex of the edge. */
    private Vertex<T> from;

    /** The destination vertex of the edge. */
    private Vertex<T> to;

    /** The Euclidean distance between the two vertices. */
    private double distance;

    /** The color of the edge, used for visualization. */
    private Color color;

    /**
     * Creates a new directed edge between two vertices.
     * 
     * The constructor automatically calculates the distance
     * between the vertices and sets the default color to gray.
     * 
     * @param from the starting vertex
     * @param to the destination vertex
     */
    public Edge(Vertex<T> from, Vertex<T> to) {
        this.from = from;
        this.to = to;
        this.distance = calculateDistance(from, to);
        this.color = Color.GRAY;
    }

    /**
     * Calculates the Euclidean distance between two vertices
     * using Pythagoras theorem.
     * 
     * @param a the first vertex
     * @param b the second vertex
     * 
     * @return the distance between the two vertices
     */
    private double calculateDistance(Vertex<T> a, Vertex<T> b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();

        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Returns the starting vertex of the edge.
     * 
     * @return the starting vertex
     */
    public Vertex<T> getFrom() {
        return from;
    }

    /**
     * Sets the starting vertex of the edge.
     * 
     * @param from the new starting vertex
     */
    public void setFrom(Vertex<T> from) {
        this.from = from;
    }

    /**
     * Returns the destination vertex of the edge.
     * 
     * @return the destination vertex
     */
    public Vertex<T> getTo() {
        return to;
    }

    /**
     * Sets the destination vertex of the edge.
     * 
     * @param to the new destination vertex
     */
    public void setTo(Vertex<T> to) {
        this.to = to;
    }

    /**
     * Returns the distance between the two vertices.
     * 
     * @return the edge distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Returns the color of the edge.
     * 
     * @return the edge color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color of the edge.
     * 
     * @param color the new color
     */
    public void setColor(Color color) {
        this.color = color;
    }
}