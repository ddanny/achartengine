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
import org.achartengine.util.MathHelper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

/**
 * Trigonometric functions demo chart.
 */
public class TrigonometricFunctionsChart extends AbstractDemoChart {
  /**
   * Returns the chart name.
   * @return the chart name
   */
  public String getName() {
    return "Trigonometric functions";
  }
  
  /**
   * Returns the chart description.
   * @return the chart description
   */
  public String getDesc() {
    return "The graphical representations of the sin and cos functions (line chart)";
  }
  
  /**
   * Executes the chart demo.
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
    String[] titles = new String[] { "sin", "cos" };
    List<double[]> x = new ArrayList<double[]>();
    List<double[]> values = new ArrayList<double[]>();
    int step = 4;
    int count = 360 / step + 1;
    x.add(new double[count]);
    x.add(new double[count]);
    double[] sinValues = new double[count];
    double[] cosValues = new double[count];
    values.add(sinValues);
    values.add(cosValues);
    for (int i = 0; i < count; i++) {
      int angle = i * step;
      x.get(0)[i] = angle;
      x.get(1)[i] = angle;
      double rAngle = Math.toRadians(angle);
      sinValues[i] = Math.sin(rAngle);
      cosValues[i] = Math.cos(rAngle);
    }
    int [] colors = new int[] { Color.BLUE, Color.CYAN };
    PointStyle[] styles = new PointStyle[] { PointStyle.POINT, PointStyle.POINT };
    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
    setChartSettings(renderer, "Trigonometric functions", "X (in degrees)", "Y", 0, 360, -1, 1,
        Color.GRAY, Color.LTGRAY);
    renderer.setXLabels(20);
    renderer.setYLabels(10);
    return ChartFactory.getLineChartIntent(context, buildDataset(titles, x, values), renderer);
  }

}
