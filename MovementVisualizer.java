import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Arrays;

public class MovementVisualizer extends JPanel {
    private final int gridSize = 3;  // 3x3 grid
    private final int cellSize = 80; // Cell size

    // First movement path (red)
    private List<int[]> path1 = Arrays.asList(
        new int[]{0, 0}, new int[]{0, 1}, new int[]{1, 1}, new int[]{1, 2}
    );

    // Second movement path (blue)
    private List<int[]> path2 = Arrays.asList(
        new int[]{2, 2}, new int[]{2, 1}, new int[]{1, 1}, new int[]{1, 0}
    );

    public MovementVisualizer(){
        
    }

    public MovementVisualizer(List<int[]> path1, List<int[]> path2){
        this.path1 = path1;
        this.path2 = path2;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Draw 3x3 grid
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                int x = j * cellSize;
                int y = i * cellSize;
                g2.drawRect(x, y, cellSize, cellSize);
            }
        }

        // Draw arrows for path1 (Red)
        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.RED);
        drawPath(g2, path1);

        // Draw arrows for path2 (Blue)
        g2.setColor(Color.BLUE);
        drawPath(g2, path2);
    }

    // Draws arrows for a given path
    private void drawPath(Graphics2D g, List<int[]> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            int[] start = path.get(i);
            int[] end = path.get(i + 1);

            int startX = start[1] * cellSize + cellSize / 2;
            int startY = start[0] * cellSize + cellSize / 2;
            int endX = end[1] * cellSize + cellSize / 2;
            int endY = end[0] * cellSize + cellSize / 2;

            drawArrow(g, startX, startY, endX, endY);
        }
    }

    // Draws an arrow from (x1, y1) to (x2, y2)
    private void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2) {
        g.drawLine(x1, y1, x2, y2);

        int arrowSize = 8;
        double angle = Math.atan2(y2 - y1, x2 - x1);

        int xArrow1 = (int) (x2 - arrowSize * Math.cos(angle - Math.PI / 6));
        int yArrow1 = (int) (y2 - arrowSize * Math.sin(angle - Math.PI / 6));

        int xArrow2 = (int) (x2 - arrowSize * Math.cos(angle + Math.PI / 6));
        int yArrow2 = (int) (y2 - arrowSize * Math.sin(angle + Math.PI / 6));

        g.drawLine(x2, y2, xArrow1, yArrow1);
        g.drawLine(x2, y2, xArrow2, yArrow2);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Matrix Movement Visualization (Two Paths)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.add(new MovementVisualizer());
        frame.setVisible(true);
    }
}
