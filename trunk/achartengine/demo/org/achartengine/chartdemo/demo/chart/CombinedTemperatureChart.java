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
import org.achartengine.chart.BarChart;
import org.achartengine.chart.BubbleChart;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
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
    return "The average temperature in 4 Greek islands, water temperature and sun shine hours (combined XY chart)";
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
    int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.CYAN, Color.rgb(200, 150, 0) };
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

    XYValueSeries sunSeries = new XYValueSeries("Sunshine hours");
    sunSeries.add(1, 5, 4.3);
    sunSeries.add(2, 5, 4.9);
    sunSeries.add(3, 5, 5.9);
    sunSeries.add(4, 5, 8.8);
    sunSeries.add(5, 5, 10.8);
    sunSeries.add(6, 5, 11.9);
    sunSeries.add(7, 5, 13.6);
    sunSeries.add(8, 5, 12.8);
    sunSeries.add(9, 5, 11.4);
    sunSeries.add(10, 5, 9.5);
    sunSeries.add(11, 5, 7.5);
    sunSeries.add(12, 5, 5.5);
    XYSeriesRenderer lightRenderer = new XYSeriesRenderer();
    lightRenderer.setColor(Color.YELLOW);

    XYSeries waterSeries = new XYSeries("Water Temperature");
    waterSeries.add(1, 16);
    waterSeries.add(2, 15);
    waterSeries.add(3, 16);
    waterSeries.add(4, 17);
    waterSeries.add(5, 20);
    waterSeries.add(6, 23);
    waterSeries.add(7, 25);
    waterSeries.add(8, 25.5);
    waterSeries.add(9, 26.5);
    waterSeries.add(10, 24);
    waterSeries.add(11, 22);
    waterSeries.add(12, 18);
    renderer.setBarSpacing(0.5);
    XYSeriesRenderer waterRenderer = new XYSeriesRenderer();
    waterRenderer.setColor(Color.argb(200, 0, 200, 200));

    XYMultipleSeriesDataset dataset = buildDataset(titles, x, values);
    dataset.addSeries(0, sunSeries);
    dataset.addSeries(0, waterSeries);
    renderer.addSeriesRenderer(0, lightRenderer);
    renderer.addSeriesRenderer(0, waterRenderer);

    String[] types = new String[] { BarChart.TYPE, BubbleChart.TYPE, LineChart.TYPE,
        LineChart.TYPE, LineChart.TYPE, LineChart.TYPE };
    Intent intent = ChartFactory.getCombinedXYChartIntent(context, dataset, renderer, types,
        "Weather parameters");
    return intent;
  }

}
