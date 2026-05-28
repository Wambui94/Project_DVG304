package algorithms;

import java.util.ArrayList;
import java.util.List;

/**
 * A quadtree used for spatial partitioning of points.
 *
 * The quadtree divides a rectangular area into smaller
 * regions when the number of stored points exceeds
 * the given capacity.
 *
 * It is used to find nearby points efficiently.
 */
public class Quadtree {

    private Rectangle boundary;
    private int capacity;
    private List<Point> points;

    private Quadtree northWest;
    private Quadtree northEast;
    private Quadtree southWest;
    private Quadtree southEast;

    private boolean divided;

    /**
     * Creates a new quadtree.
     *
     * @param boundary the area covered by this quadtree
     * @param capacity maximum number of points before subdivision
     */
    public Quadtree(Rectangle boundary, int capacity) {
        this.boundary = boundary;
        this.capacity = capacity;
        this.points = new ArrayList<>();
        this.divided = false;
    }

    /**
     * Inserts a point into the quadtree.
     *
     * @param point the point to insert
     * @return true if the point was inserted, otherwise false
     */
    public boolean insert(Point point) {
        if (!boundary.contains(point)) {
            return false;
        }

        if (points.size() < capacity && !divided) {
            points.add(point);
            return true;
        }

        if (!divided) {
            subdivide();
        }

        return northWest.insert(point)
                || northEast.insert(point)
                || southWest.insert(point)
                || southEast.insert(point);
    }

    private void subdivide() {
        double x = boundary.x;
        double y = boundary.y;
        double halfWidth = boundary.width / 2;
        double halfHeight = boundary.height / 2;

        northWest = new Quadtree(
                new Rectangle(x, y, halfWidth, halfHeight),
                capacity
        );

        northEast = new Quadtree(
                new Rectangle(x + halfWidth, y, halfWidth, halfHeight),
                capacity
        );

        southWest = new Quadtree(
                new Rectangle(x, y + halfHeight, halfWidth, halfHeight),
                capacity
        );

        southEast = new Quadtree(
                new Rectangle(x + halfWidth, y + halfHeight, halfWidth, halfHeight),
                capacity
        );

        for (Point point : points) {
            northWest.insert(point);
            northEast.insert(point);
            southWest.insert(point);
            southEast.insert(point);
        }

        points.clear();
        divided = true;
    }

    /**
     * Finds all points inside a given rectangular area.
     *
     * @param area the area to search within
     * @return list of points inside the area
     */
    public List<Point> query(Rectangle area) {
        List<Point> result = new ArrayList<>();

        if (!boundary.intersects(area)) {
            return result;
        }

        for (Point point : points) {
            if (area.contains(point)) {
                result.add(point);
            }
        }

        if (divided) {
            result.addAll(northWest.query(area));
            result.addAll(northEast.query(area));
            result.addAll(southWest.query(area));
            result.addAll(southEast.query(area));
        }

        return result;
    }

    /**
     * Represents a point stored in the quadtree.
     */
    public static class Point {
        public double x;
        public double y;
        public String name;

        public Point(double x, double y, String name) {
            this.x = x;
            this.y = y;
            this.name = name;
        }

        @Override
        public String toString() {
            return name + " (" + x + ", " + y + ")";
        }
    }

    /**
     * Creates a new rectangle.
     *
     * @param x x-coordinate of the top-left corner
     * @param y y-coordinate of the top-left corner
     * @param width rectangle width
     * @param height rectangle height
     */
    public static class Rectangle {
        public double x;
        public double y;
        public double width;
        public double height;

        public Rectangle(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        /**
         * Checks if a point is inside the rectangle.
         *
         * @param point the point to check
         * @return true if the point is inside
         */
        public boolean contains(Point point) {
            return point.x >= x
                    && point.x < x + width
                    && point.y >= y
                    && point.y < y + height;
        }

        /**
         * Checks if this rectangle overlaps another rectangle.
         *
         * @param other the other rectangle
         * @return true if the rectangles overlap
         */
        public boolean intersects(Rectangle other) {
            return !(other.x > x + width
                    || other.x + other.width < x
                    || other.y > y + height
                    || other.y + other.height < y);
        }
    }
    
    /**
     * Returns all quadtree region boundaries.
     *
     * @return list of all rectangle boundaries
     */
    public List<Rectangle> getAllBoundaries() {
        List<Rectangle> rectangles = new ArrayList<>();
        collectBoundaries(rectangles);
        return rectangles;
    }

    /**
     * Collects all region boundaries recursively.
     *
     * @param rectangles list where boundaries are stored
     */
    private void collectBoundaries(List<Rectangle> rectangles) {
        rectangles.add(boundary);

        if (divided) {
            northWest.collectBoundaries(rectangles);
            northEast.collectBoundaries(rectangles);
            southWest.collectBoundaries(rectangles);
            southEast.collectBoundaries(rectangles);
        }
    }
}