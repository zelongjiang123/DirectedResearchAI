import javax.swing.*;
import java.awt.*;

public class MatrixVisualizer extends JPanel {
    private double[][] matrix;
    private String title;

    public MatrixVisualizer(double[][] matrix, String title) {
        this.matrix = matrix;
        this.title = title;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int cellSize = 60; // Size of each cell
        int padding = 0;   // Padding between cells
        int titleHeight = 30; // Space for the title

        // Draw the title
        Font titleFont = new Font("Arial", Font.BOLD, 16);
        g.setFont(titleFont);
        FontMetrics titleMetrics = g.getFontMetrics();
        int titleX = (getWidth() - titleMetrics.stringWidth(title)) / 2;
        g.drawString(title, titleX, 20);

        // Set font for matrix values
        g.setFont(new Font("Arial", Font.PLAIN, 14));

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                int x = j * (cellSize + padding);
                int y = i * (cellSize + padding) + titleHeight; // Adjust for title space

                // Draw cell
                g.drawRect(x, y, cellSize, cellSize);

                // Format the double value to 2 decimal places
                String value = String.format("%.2f", matrix[i][j]);

                // Center text within the cell
                FontMetrics fm = g.getFontMetrics();
                int textX = x + (cellSize - fm.stringWidth(value)) / 2;
                int textY = y + (cellSize + fm.getAscent()) / 2 - 3;
                g.drawString(value, textX, textY);
            }
        }
    }

    public static void main(String[] args) {
        double[][] matrix = {
            {1.23, 2.34, 3.45},
            {4.56, 5.67, 6.78},
            {7.89, 8.90, 9.01}
        };

        JFrame frame = new JFrame("Matrix Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 350); // Increased height for title
        frame.add(new MatrixVisualizer(matrix, "My Matrix"));
        frame.setVisible(true);
    }
}
