package aod.lab5.graph;

import java.awt.Color;

/**
 * Represents a vertex (node) in a graph.
 * 
 * Each vertex contains:
 * <ul>
 *   <li>Information of type T used as an identifier</li>
 *   <li>An x-coordinate</li>
 *   <li>A y-coordinate</li>
 *   <li>A color used for visualisation</li>
 * </ul>
 * 
 * Vertices can be used to represent positions in a 2D graph structure.
 * 
 * @param <T> the type of information stored in the vertex
 */
public class Vertex<T> {

    /** The information and identifier stored in the vertex. */
    private T info;

    /** The x-coordinate of the vertex. */
    private double x;

    /** The y-coordinate of the vertex. */
    private double y;

    /** The color of the vertex, used for visualization. */
    private Color color;

    /**
     * Creates a new vertex with a position and identifier.
     * 
     * The default color is set to black.
     * 
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param info the information and identifier of the vertex
     */
    public Vertex(double x, double y, T info) {
        this.x = x;
        this.y = y;
        this.info = info;
        this.color = Color.BLACK;
    }

    /**
     * Returns the information stored in the vertex.
     * 
     * @return the vertex information
     */
    public T getInfo() {
        return info;
    }

    /**
     * Sets the information stored in the vertex.
     * 
     * @param info the new vertex information
     */
    public void setInfo(T info) {
        this.info = info;
    }

    /**
     * Returns the x-coordinate of the vertex.
     * 
     * @return the x-coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the x-coordinate of the vertex.
     * 
     * @param x the new x-coordinate
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Returns the y-coordinate of the vertex.
     * 
     * @return the y-coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the y-coordinate of the vertex.
     * 
     * @param y the new y-coordinate
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Returns the color of the vertex.
     * 
     * @return the vertex color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color of the vertex.
     * 
     * @param color the new color
     */
    public void setColor(Color color) {
        this.color = color;
    }
}