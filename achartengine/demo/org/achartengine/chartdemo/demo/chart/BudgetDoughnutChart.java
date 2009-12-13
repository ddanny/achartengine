/**
 * Copyright (C) 2009 SC 4ViewSoft SRL
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
import org.achartengine.renderer.DefaultRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

/**
 * Budget demo pie chart.
 */
public class BudgetDoughnutChart extends AbstractChart {
  /**
   * Returns the chart name.
   * @return the chart name
   */
  public String getName() {
    return "Budget chart for several years";
  }
  
  /**
   * Returns the chart description.
   * @return the chart description
   */
  public String getDesc() {
    return "The budget per project for several years (doughnut chart)";
  }
  
  /**
   * Executes the chart demo.
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
    List<double[]> values = new ArrayList<double[]>();
    values.add(new double[] {12, 14, 11, 10, 19});
    values.add(new double[] {10, 9, 14, 20, 11});
    List<String[]> titles = new ArrayList<String[]>();
    titles.add(new String[] {"P1", "P2", "P3", "P4", "P5"});
    titles.add(new String[] {"P1", "P2", "P3", "P4", "P5"});
    int[] colors = new int[] {Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN};
    DefaultRenderer renderer = buildCategoryRenderer(colors);
    renderer.setApplyBackgroundColor(true);
    renderer.setBackgroundColor(Color.BLACK);
    return ChartFactory.getDoughnutChartIntent(context, buildMultipleCategoryDataset("Project budget", titles, values), renderer, "Doughnut chart demo");
  }

}
