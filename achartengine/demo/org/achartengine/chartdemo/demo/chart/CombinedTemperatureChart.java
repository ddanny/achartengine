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
package org.achartengine.chartdemo.demo.chart;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BubbleChart;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYValueSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

/**
 * Average temperature demo chart.
 */
public class CombinedTemperatureChart extends AbstractDemoChart {
  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  public String getName() {
    return "Combined temperature";
  }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */
  public String getDesc() {
    return "The average temperature in 4 Greek islands and other parameters (combined chart)";
  }

  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
    String[] titles = new String[] { "Crete", "Corfu", "Thassos", "Skiathos" };
    List<double[]> x = new ArrayList<double[]>();
    for (int i = 0; i < titles.length; i++) {
      x.add(new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 });
    }
    List<double[]> values = new ArrayList<double[]>();
    values.add(new double[] { 12.3, 12.5, 13.8, 16.8, 20.4, 24.4, 26.4, 26.1, 23.6, 20.3, 17.2,
        13.9 });
    values.add(new double[] { 10, 10, 12, 15, 20, 24, 26, 26, 23, 18, 14, 11 });
    values.add(new double[] { 5, 5.3, 8, 12, 17, 22, 24.2, 24, 19, 15, 9, 6 });
    values.add(new double[] { 9, 10, 11, 15, 19, 23, 26, 25, 22, 18, 13, 10 });
    int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW };
    PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND,
        PointStyle.TRIANGLE, PointStyle.SQUARE };
    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
    int length = renderer.getSeriesRendererCount();
    for (int i = 0; i < length; i++) {
      ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
    }
    setChartSettings(renderer, "Average temperature", "Month", "Temperature", 0.5, 12.5, 0, 32,
        Color.LTGRAY, Color.LTGRAY);
    renderer.setXLabels(12);
    renderer.setYLabels(10);
    renderer.setShowGrid(true);
    renderer.setXLabelsAlign(Align.RIGHT);
    renderer.setYLabelsAlign(Align.RIGHT);
    renderer.setZoomButtonsVisible(true);
    renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
    renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });
    
    XYMultipleSeriesDataset series = new XYMultipleSeriesDataset();
    XYValueSeries lightSeries = new XYValueSeries("Visible light");
    lightSeries.add(0.5, 0, 0);
    lightSeries.add(1, 5, 9);
    lightSeries.add(2, 5, 10);
    lightSeries.add(3, 5, 11.5);
    lightSeries.add(4, 5, 12.5);
    lightSeries.add(5, 5, 14);
    lightSeries.add(6, 5, 15);
    lightSeries.add(7, 5, 14);
    lightSeries.add(8, 5, 12.75);
    lightSeries.add(9, 5, 12);
    lightSeries.add(10, 5, 11);
    lightSeries.add(11, 5, 10);
    lightSeries.add(12, 5, 9);
    lightSeries.add(12.5, 20, 0);
    series.addSeries(lightSeries);
    XYMultipleSeriesRenderer lRenderer = new XYMultipleSeriesRenderer();
    
    
//    lightRenderer.setAxisTitleTextSize(16);
//    lightRenderer.setChartTitleTextSize(20);
//    lightRenderer.setLabelsTextSize(15);
//    lightRenderer.setLegendTextSize(15);
//    renderer.setMargins(new int[] { 20, 30, 15, 0 });
    XYSeriesRenderer lightRenderer = new XYSeriesRenderer();
    lightRenderer.setColor(Color.YELLOW);
    lRenderer.addSeriesRenderer(lightRenderer);
    
    XYMultipleSeriesDataset[] datasets = new XYMultipleSeriesDataset[2];
    XYMultipleSeriesRenderer[] renderers = new XYMultipleSeriesRenderer[] {renderer, lRenderer};
    datasets[0] = buildDataset(titles, x, values);
    datasets[1] = series;
    String[] types = new String[] {LineChart.TYPE, BubbleChart.TYPE};
    Intent intent = ChartFactory.getCombinedXYChartIntent(context, datasets,
        renderers, types, "Average temperature");
    return intent;
  }

}
