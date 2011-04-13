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

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.RangeCategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

/**
 * Temperature demo range chart.
 */
public class TemperatureChart extends AbstractDemoChart {

  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  public String getName() {
    return "Temperature range chart";
  }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */
  public String getDesc() {
    return "The monthly temperature (vertical range chart)";
  }

  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
    double[] minValues = new double[] { -24, -19, -10, -1, 7, 12, 15, 14, 9, 1, -11, -16 };
    double[] maxValues = new double[] { 7, 12, 24, 28, 33, 35, 37, 36, 28, 19, 11, 4 };

    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
    RangeCategorySeries series = new RangeCategorySeries("Temperature");
    int length = minValues.length;
    for (int k = 0; k < length; k++) {
      series.add(minValues[k], maxValues[k]);
    }
    dataset.addSeries(series.toXYSeries());

    int[] colors = new int[] { Color.CYAN };
    XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
    setChartSettings(renderer, "Monthly temperature range", "Month", "Celsius degrees", 0.5, 12.5,
        -30, 45, Color.GRAY, Color.LTGRAY);
    // renderer.setBackgroundColor(Color.GREEN);
    // renderer.setApplyBackgroundColor(true);
    renderer.setBarSpacing(0.5);
    renderer.setXLabels(1);
    renderer.setYLabels(10);
    renderer.addXTextLabel(1, "Jan");
    renderer.addXTextLabel(3, "Mar");
    renderer.addXTextLabel(5, "May");
    renderer.addXTextLabel(7, "Jul");
    renderer.addXTextLabel(10, "Oct");
    renderer.addXTextLabel(12, "Dec");
    renderer.addYTextLabel(-25, "Very cold");
    renderer.addYTextLabel(-15, "Cold");
    renderer.addYTextLabel(-5, "Quite cold");
    renderer.addYTextLabel(5, "OK");
    renderer.addYTextLabel(15, "Decent");
    renderer.addYTextLabel(25, "Warm");
    renderer.setMargins(new int[] {30, 70, 10, 0});
    renderer.setYLabelsAlign(Align.RIGHT);
    
    renderer.setDisplayChartValues(true);
    renderer.setChartValuesTextSize(12);
    return ChartFactory.getRangeBarChartIntent(context, dataset, renderer, Type.DEFAULT,
        "Temperature range");
  }

}
