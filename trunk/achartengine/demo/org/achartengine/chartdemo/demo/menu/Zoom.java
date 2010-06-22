package org.achartengine.chartdemo.demo.menu;

import org.achartengine.chart.XYChart;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.graphics.Point;

public class Zoom {

  public void apply(XYChart chart, XYMultipleSeriesRenderer renderer, Point point, Point point2) {
    if (chart == null || renderer == null) {
      return;
    }
    double minX = renderer.getXAxisMin();
    double maxX = renderer.getXAxisMax();
    double minY = renderer.getYAxisMin();
    double maxY = renderer.getYAxisMax();
    double centerX = (minX + maxX) / 2;
    double centerY = (minY + maxY) / 2;
    double newWidth = maxX - minX;
    double newHeight = maxY - minY;
//    if (zoomIn) {
//      newWidth /= zoomRate;
//      newHeight /= zoomRate;
//    } else {
//      newWidth *= zoomRate;
//      newHeight *= zoomRate;
//    }

    renderer.setXAxisMin(centerX - newWidth / 2);
    renderer.setXAxisMax(centerX + newWidth / 2);
    renderer.setYAxisMin(centerY - newHeight / 2);
    renderer.setYAxisMax(centerY + newHeight / 2);
  }
}
