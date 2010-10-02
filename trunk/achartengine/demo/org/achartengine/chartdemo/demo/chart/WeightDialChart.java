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
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DialRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.DialRenderer.Type;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

/**
 * Budget demo pie chart.
 */
public class WeightDialChart extends AbstractDemoChart {
  /**
   * Returns the chart name.
   * @return the chart name
   */
  public String getName() {
    return "Weight chart";
  }
  
  /**
   * Returns the chart description.
   * @return the chart description
   */
  public String getDesc() {
    return "The weight indicator (dial chart)";
  }
  
  /**
   * Executes the chart demo.
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
    CategorySeries category = new CategorySeries("Weight indic");
    category.add("Current", 75);
    category.add("Minimum", 65);
    category.add("Maximum", 90);
    DialRenderer renderer = new DialRenderer();
    SimpleSeriesRenderer r = new SimpleSeriesRenderer();
    r.setColor(Color.BLUE);
    renderer.addSeriesRenderer(r);
    r = new SimpleSeriesRenderer();
    r.setColor(Color.rgb(0, 150, 0));
    renderer.addSeriesRenderer(r);
    r = new SimpleSeriesRenderer();
    r.setColor(Color.GREEN);
    renderer.addSeriesRenderer(r);
    renderer.setLabelsTextSize(10);
    renderer.setLabelsColor(Color.WHITE);
    renderer.setShowLabels(true);
    renderer.setVisualTypes(new DialRenderer.Type[] {Type.ARROW, Type.NEEDLE, Type.NEEDLE});
    renderer.setMinValue(0);
    renderer.setMaxValue(150);
    return ChartFactory.getDialChartIntent(context, category, renderer, "Weight indicator");
  }

}
