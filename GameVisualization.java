import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameVisualization extends JPanel implements ActionListener {
    static final int GRID_SIZE = 3;
    static final int CELL_SIZE = 100;
    static final int DELAY = 1000; // Delay in milliseconds

    private int car1Row = 0, car1Col = 0; // Car 1 position
    private int car2Row = 2, car2Col = 2; // Car 2 position

    private int[][] positions;

    private int period = 0;

    // the brightness is actually the inverse of brightness
    // the lower the value, the brighter the color
    private int[][] brightness = {
        {1, 2, 3}, 
        {1, 2, 1}, 
        {3, 1, 2}
    }; // Given brightness values
    private int maxBrightness;
    private double ratio;


    private Timer timer;

    public GameVisualization() {
        timer = new Timer(DELAY, this);
        timer.start();
        maxBrightness = findMaxBrightness();
        ratio = 255.0 / maxBrightness; // Compute ratio for scaling
    }

    public GameVisualization(int[][] positions, int[][] brightness) {
        this.brightness = brightness;
        this.positions = positions;
        maxBrightness = findMaxBrightness();
        ratio = 255.0 / maxBrightness; // Compute ratio for scaling
        timer = new Timer(DELAY, this);
        timer.start();
    }

    private int findMaxBrightness() {
        int max = Integer.MIN_VALUE;
        for (int[] row : brightness) {
            for (int val : row) {
                if (val > max) max = val;
            }
        }
        return max;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        drawCars(g);
    }

    private void drawGrid(Graphics g) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int scaledBrightness = 255 - (int) (brightness[row][col] * ratio);
                g.setColor(new Color(scaledBrightness, scaledBrightness, scaledBrightness));

                g.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    private void drawCars(Graphics g) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                boolean car1Here = (row == car1Row && col == car1Col);
                boolean car2Here = (row == car2Row && col == car2Col);

                if (car1Here && car2Here) {
                    // When both cars are in the same cell
                    g.setColor(Color.RED);
                    g.fillOval(col * CELL_SIZE + 15, row * CELL_SIZE + 30, 30, 30);

                    g.setColor(Color.BLUE);
                    g.fillOval(col * CELL_SIZE + 45, row * CELL_SIZE + 30, 30, 30);
                } else if (car1Here) {
                    // Draw Car 1 (Red)
                    g.setColor(Color.RED);
                    g.fillOval(col * CELL_SIZE + 30, row * CELL_SIZE + 30, 40, 40);
                } else if (car2Here) {
                    // Draw Car 2 (Blue)
                    g.setColor(Color.BLUE);
                    g.fillOval(col * CELL_SIZE + 30, row * CELL_SIZE + 30, 40, 40);
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(period < positions.length){
            this.car1Row = positions[period][0];
            this.car1Col = positions[period][1];
            this.car2Row = positions[period][2];
            this.car2Col = positions[period][3];
            period++;
        }
        repaint(); // Refresh the display
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("3x3 Grid with Moving Cars");
        GameVisualization panel = new GameVisualization();
        frame.add(panel);
        frame.setSize(GRID_SIZE * CELL_SIZE + 15, GRID_SIZE * CELL_SIZE + 40);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
