/**
 * Copyright (C) 2009, 2010 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.achartengine.chart;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * The line chart rendering class.
 */
public class CombinedXYChart extends XYChart {
  /** The datasets. */
  private XYMultipleSeriesDataset[] mDatasets;
  /** The renderers. */
  private XYMultipleSeriesRenderer[] mRenderers;
  /** The embedded charts. */
  private XYChart[] mCharts;
  /** The supported charts for being combined. */
  private Class[] xyChartTypes = new Class[] { TimeChart.class, LineChart.class, BarChart.class,
      BubbleChart.class, LineChart.class, ScatterChart.class, RangeBarChart.class };

  /**
   * Builds a new combined XY chart instance.
   * 
   * @param datasets the multiple series dataset
   * @param renderers the multiple series renderer
   * @param types the XY chart types
   */
  public CombinedXYChart(XYMultipleSeriesDataset[] datasets, XYMultipleSeriesRenderer[] renderers,
      String[] types) {
    super(datasets[0], renderers[0]);
    mDatasets = datasets;
    mRenderers = renderers;
    int length = types.length;
    mCharts = new XYChart[length];
    for (int i = 0; i < length; i++) {
      try {
        mCharts[i] = getXYChart(types[i]);
      } catch (Exception e) {
        // ignore
      }
      if (mCharts[i] == null) {
        throw new IllegalArgumentException("Unknown chart type " + types[i]);
      } else {
        mCharts[i].setDatasetRenderer(datasets[i], renderers[i]);
      }
    }
  }

  private XYChart getXYChart(String type) throws IllegalAccessException, InstantiationException {
    XYChart chart = null;
    int length = xyChartTypes.length;
    for (int i = 0; i < length && chart == null; i++) {
      XYChart newChart = (XYChart) xyChartTypes[i].newInstance();
      if (type.equals(newChart.getChartType())) {
        chart = newChart;
      }
    }
    return chart;
  }

  /**
   * The graphical representation of the XY chart.
   * 
   * @param canvas the canvas to paint to
   * @param x the top left x value of the view to draw to
   * @param y the top left y value of the view to draw to
   * @param width the width of the view to draw to
   * @param height the height of the view to draw to
   * @param paint the paint
   */
  @Override
  public void draw(Canvas canvas, int x, int y, int width, int height, Paint paint) {
    for (XYChart chart : mCharts) {
      chart.draw(canvas, x, y, width, height, paint);
    }
  }

  /**
   * The graphical representation of a series.
   * 
   * @param canvas the canvas to paint to
   * @param paint the paint to be used for drawing
   * @param points the array of points to be used for drawing the series
   * @param seriesRenderer the series renderer
   * @param yAxisValue the minimum value of the y axis
   * @param seriesIndex the index of the series currently being drawn
   */
  public void drawSeries(Canvas canvas, Paint paint, float[] points,
      SimpleSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex) {
  }

  /**
   * Returns the legend shape width.
   * 
   * @return the legend shape width
   */
  public int getLegendShapeWidth() {
    return 0;
  }

  /**
   * The graphical representation of the legend shape.
   * 
   * @param canvas the canvas to paint to
   * @param renderer the series renderer
   * @param x the x value of the point the shape should be drawn at
   * @param y the y value of the point the shape should be drawn at
   * @param paint the paint to be used for drawing
   */
  public void drawLegendShape(Canvas canvas, SimpleSeriesRenderer renderer, float x, float y,
      Paint paint) {
    for (XYChart chart : mCharts) {
      chart.drawLegendShape(canvas, renderer, x, y, paint);
    }
  }

  /**
   * Returns the chart type identifier.
   * 
   * @return the chart type
   */
  public String getChartType() {
    return "";
  }

}
