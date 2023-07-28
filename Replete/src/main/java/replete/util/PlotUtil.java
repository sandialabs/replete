package replete.util;

import java.awt.Color;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeFrame;

public class PlotUtil extends EscapeFrame {

    public static void quickPlot(String title, double[] y, String xLabel, String yLabel) {
        PlotUtil p = new PlotUtil(title, null, y, xLabel, yLabel);
        p.setVisible(true);
    }
    public static void quickPlot(String title, double[] x, double[] y, String xLabel, String yLabel) {
        PlotUtil p = new PlotUtil(title, x, y, xLabel, yLabel);
        p.setVisible(true);
    }

    private PlotUtil(String title, double[] x, double[] y, String xLabel, String yLabel) {
        super(title);
        Lay.BLtg(this,
            "C", createGraphPanel(title, x, y, xLabel, yLabel),
            "bg=white,augb=mb(1,black),size=500,center"
        );
    }

    private JPanel createGraphPanel(String title, double[] x, double[] y, String xLabel, String yLabel) {
        XYDataset dataset = createDataset(x, y);
        JFreeChart chart = createChart(title, dataset, xLabel, yLabel);
        ChartPanel chartPanel = new ChartPanel(chart);
        return chartPanel;
    }

    private XYDataset createDataset(double[] x, double[] y) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("Data");
        if(x == null) {
            for(int t = 0; t < y.length; t++) {
                series.add(t, y[t]);
            }
        } else {
            for(int t = 0; t < x.length; t++) {
                series.add(x[t], y[t]);
            }
        }
        dataset.addSeries(series);
        return dataset;
    }

    private JFreeChart createChart(String title, XYDataset dataset, String xLabel, String yLabel) {

        // create the chart...
        final JFreeChart chart = ChartFactory.createXYLineChart(
            title,      // chart title
            xLabel,                      // x axis label
            yLabel,                      // y axis label
            dataset,                  // data
            PlotOrientation.VERTICAL,
            true,                     // include legend
            true,                     // tooltips
            false                     // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);

//            final StandardLegend legend = (StandardLegend) chart.getLegend();
//            legend.setDisplaySeriesShapes(true);

        // get a reference to the plot for further customisation...
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
    //    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesLinesVisible(0, false);
        plot.setRenderer(renderer);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // OPTIONAL CUSTOMISATION COMPLETED.

        return chart;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        PlotUtil.quickPlot("Title of My Chart!", new double[] {1, 2, 5, 9}, "Time Steps", "Dollars");
    }
}
