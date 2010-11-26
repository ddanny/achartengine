package org.achartengine.tools;

import org.achartengine.chart.XYChart;
import org.achartengine.model.XYSeries;

public class FitZoom extends AbstractTool {

  /**
   * Builds an instance of the fit zoom tool.
   * 
   * @param chart the XY chart
   */
  public FitZoom(XYChart chart) {
    super(chart);
  }

  /**
   * Apply the tool.
   */
  public void apply() {
    if (mChart.getDataset() == null) {
      return;
    }
    XYSeries[] series = mChart.getDataset().getSeries();
    double[] range = null;
    int length = series.length;
    if (length > 0) {
      range = new double[] { series[0].getMinX(), series[0].getMaxX(), 
          Math.min(mChart.getDefaultMinimum(), series[0].getMinY()), series[0].getMaxY() };
      for (int i = 1; i < length; i++) {
        range[0] = Math.min(range[0], series[i].getMinX());
        range[1] = Math.max(range[1], series[i].getMaxX());
        range[2] = Math.min(range[2], series[i].getMinY());
        range[3] = Math.max(range[3], series[i].getMaxY());
      }
      double marginX = Math.abs(range[1] - range[0]) / 40;
      double marginY = Math.abs(range[3] - range[2]) / 40;
      mRenderer.setXAxisMin(range[0] - marginX);
      mRenderer.setXAxisMax(range[1] + marginX);
      mRenderer.setYAxisMin(range[2] - marginY);
      mRenderer.setYAxisMax(range[3] + marginY);
    }
  }
}
