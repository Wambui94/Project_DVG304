package algorithms;

import java.util.ArrayList;
import java.util.List;

public class Quadtree {

    private Rectangle boundary;
    private int capacity;
    private List<Point> points;

    private Quadtree northWest;
    private Quadtree northEast;
    private Quadtree southWest;
    private Quadtree southEast;

    private boolean divided;

    public Quadtree(Rectangle boundary, int capacity) {
        this.boundary = boundary;
        this.capacity = capacity;
        this.points = new ArrayList<>();
        this.divided = false;
    }

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

        public boolean contains(Point point) {
            return point.x >= x
                    && point.x < x + width
                    && point.y >= y
                    && point.y < y + height;
        }

        public boolean intersects(Rectangle other) {
            return !(other.x > x + width
                    || other.x + other.width < x
                    || other.y > y + height
                    || other.y + other.height < y);
        }
    }
    public List<Rectangle> getAllBoundaries() {
        List<Rectangle> rectangles = new ArrayList<>();
        collectBoundaries(rectangles);
        return rectangles;
    }

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