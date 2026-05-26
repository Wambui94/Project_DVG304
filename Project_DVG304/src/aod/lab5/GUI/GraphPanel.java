package aod.lab5.GUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import algorithms.Quadtree;
import aod.lab5.graph.Edge;
import aod.lab5.graph.Graph;
import aod.lab5.graph.Vertex;

public class GraphPanel<T> extends JPanel {

	
    private final Graph<T> graph;
    private double zoom = 1.0;
    private final double FILTER_ZOOM_LEVEL = 2.5;
    private double offsetX = 0;
    private double offsetY = 0;
    private Point lastMousePosition;
    private BufferedImage swedenMap;

    public GraphPanel(Graph<T> graph) {
        this.graph = graph;
        setBackground(Color.WHITE);

        try {
            swedenMap = ImageIO.read(
                    getClass().getResource("/map-of-sweden-with-cities.png")
            );
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Could not load map image");
        }

        addMouseWheelListener(e -> {
            double oldZoom = zoom;

            if (e.getPreciseWheelRotation() < 0) {
                zoom *= 1.1;
            } else {
                zoom /= 1.1;
            }

            zoom = Math.max(0.2, Math.min(30.0, zoom));

            double scale = zoom / oldZoom;

            double mouseX = e.getX();
            double mouseY = e.getY();

            offsetX = mouseX - scale * (mouseX - offsetX);
            offsetY = mouseY - scale * (mouseY - offsetY);

            repaint();
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMousePosition = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastMousePosition.x;
                int dy = e.getY() - lastMousePosition.y;

                offsetX += dx;
                offsetY += dy;

                lastMousePosition = e.getPoint();

                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.translate(offsetX, offsetY);
        g2.scale(zoom, zoom);

        drawMapBackground(g2);

        if (zoom >= FILTER_ZOOM_LEVEL) {
            drawFilteredEdges(g2);
            drawFilteredVertices(g2);
        } else {
            drawFilteredEdges(g2);
            drawVertices(g2);
        }
    }

    private void drawMapBackground(Graphics2D g2) {
        if (swedenMap != null) {
            g2.drawImage(
                    swedenMap,
                    0,
                    0,
                    GraphViewer.MAP_WIDTH,
                    GraphViewer.MAP_HEIGHT,
                    null
            );
        }
    }

    private void drawFilteredEdges(Graphics2D g2) {
        for (Vertex<T> v : graph.getAllVertices()) {
            List<Edge<T>> edgeList = graph.getEdges(v.getInfo());

            for (Edge<T> edge : edgeList) {
                Vertex<T> from = edge.getFrom();
                Vertex<T> to = edge.getTo();

                if (from.getInfo().toString()
                        .compareTo(to.getInfo().toString()) < 0) {

                    if (isVisible(from) && isVisible(to)) {
                        drawEdge(g2, edge);
                    }
                }
            }
        }
    }

    private void drawEdge(Graphics2D g2, Edge<T> edge) {

        boolean isShortestPath =
                edge.getColor() != null
                        && edge.getColor().equals(Color.RED);

        if (zoom < 6.0 && !isShortestPath) {
            return;
        }

        Vertex<T> from = edge.getFrom();
        Vertex<T> to = edge.getTo();

        int x1 = (int) from.getX();
        int y1 = (int) from.getY();
        int x2 = (int) to.getX();
        int y2 = (int) to.getY();

        if (isShortestPath) {
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(1.0f));
        } else {
            g2.setColor(edge.getColor());
            g2.setStroke(new BasicStroke(0.5f));
        }

        g2.drawLine(x1, y1, x2, y2);

        if (zoom >= 8.0) {
            String distStr = String.format("%.1f", edge.getDistance());

            int textX = (int)(x1 + (x2 - x1) * 0.6);
            int textY = (int)(y1 + (y2 - y1) * 0.6);

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.PLAIN, 2));
            g2.drawString(distStr, textX, textY);
        }
    }

    private void drawVertices(Graphics2D g2) {
        for (Vertex<T> v : graph.getAllVertices()) {
            drawVertex(g2, v);
        }
    }

    private void drawFilteredVertices(Graphics2D g2) {
        for (Vertex<T> v : graph.getAllVertices()) {
            if (isVisible(v)) {
                drawVertex(g2, v);
            }
        }
    }

    private void drawVertex(Graphics2D g2, Vertex<T> v) {
        int x = (int) v.getX();
        int y = (int) v.getY();

        g2.setColor(new Color(0, 120, 255, 180));

        int size = 4;

        g2.fillOval(
                x - size / 2,
                y - size / 2,
                size,
                size
        );

        if (zoom >= 6.0) {
            String label = v.getInfo().toString();

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 2));

            FontMetrics fm = g2.getFontMetrics();

            int textWidth = fm.stringWidth(label);
            int textHeight = fm.getAscent();

            g2.drawString(
                    label,
                    x - textWidth / 2,
                    y + textHeight / 3
            );
        }
    }
    

    private boolean isVisible(Vertex<T> v) {
        double visibleLeft = -offsetX / zoom;
        double visibleTop = -offsetY / zoom;
        double visibleRight = visibleLeft + getWidth() / zoom;
        double visibleBottom = visibleTop + getHeight() / zoom;

        double x = v.getX();
        double y = v.getY();

        return x >= visibleLeft
                && x <= visibleRight
                && y >= visibleTop
                && y <= visibleBottom;
    }
    private void drawQuadtree(Graphics2D g2, Quadtree quadtree) {
        g2.setColor(new Color(255, 0, 0, 80));
        g2.setStroke(new BasicStroke(0.3f));

        for (Quadtree.Rectangle r : quadtree.getAllBoundaries()) {
            g2.drawRect(
                    (int) r.x,
                    (int) r.y,
                    (int) r.width,
                    (int) r.height
            );
        }
    }
}