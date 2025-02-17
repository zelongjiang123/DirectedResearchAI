import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.util.List;

public class LineChart extends JFrame {

    public LineChart(String title, List<Double> numbers, int[] yAxisRange) {
        super(title);
        
        // Create dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < numbers.size(); i++) {
            dataset.addValue(numbers.get(i), "Values", Double.valueOf(i)); // (y, series, x)
        }

        // Create chart
        JFreeChart chart = ChartFactory.createLineChart(
                "Number Line Plot",    // Chart title
                "Index",               // X-Axis Label
                "Value",               // Y-Axis Label
                dataset,               // Dataset
                PlotOrientation.VERTICAL,
                true, true, false      // Legend, tooltips, URLs
        );

        // Get the plot and set Y-axis range manually
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setRange(yAxisRange[0], yAxisRange[1]);  // Set Y-axis range from 0 to 50

        // Show chart in a panel
        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);
    }

    public static void main(String[] args) {
        List<Double> numbers = List.of(10.0, 15.3, 8.2, 23.4, 18.5, 5.5, 30.5); // Sample data

        SwingUtilities.invokeLater(() -> {
            LineChart example = new LineChart("Line Chart Example", numbers, new int[] {5, 30});
            example.setSize(600, 400);
            example.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
    }
}
