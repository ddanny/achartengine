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
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

/**
 * Average temperature demo chart.
 */
public class AverageTemperatureChart extends AbstractDemoChart {
  /**
   * Returns the chart name.
   * @return the chart name
   */
  public String getName() {
    return "Average temperature";
  }
  
  /**
   * Returns the chart description.
   * @return the chart description
   */
  public String getDesc() {
    return "The average temperature in 4 Greek islands (line chart)";
  }
  
  /**
   * Executes the chart demo.
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
//    renderer.setMarginsColor(Color.CYAN);
    
    renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
//    renderer.setXLabelsAngle(-90);
//    renderer.setYLabelsAngle(-90);
    Intent intent = ChartFactory.getLineChartIntent(context, buildDataset(titles, x, values),
        renderer, "Average temperature");
    return intent;
  }

}
