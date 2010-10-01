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
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

/**
 * Project status demo chart.
 */
public class ProjectStatusChart extends AbstractDemoChart {
  /**
   * Returns the chart name.
   * @return the chart name
   */
  public String getName() {
    return "Project tickets status";
  }
  
  /**
   * Returns the chart description.
   * @return the chart description
   */
  public String getDesc() {
    return "The opened tickets and the fixed tickets (time chart)";
  }
  
  /**
   * Executes the chart demo.
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
    String[] titles = new String[] { "New tickets", "Fixed tickets" };
    List<Date[]> dates = new ArrayList<Date[]>();
    List<double[]> values = new ArrayList<double[]>();
    int length = titles.length;
    for (int i = 0; i < length; i++) {
      dates.add(new Date[12]);
      dates.get(i)[0] = new Date(108, 9, 1);
      dates.get(i)[1] = new Date(108, 9, 8);
      dates.get(i)[2] = new Date(108, 9, 15);
      dates.get(i)[3] = new Date(108, 9, 22);
      dates.get(i)[4] = new Date(108, 9, 29);
      dates.get(i)[5] = new Date(108, 10, 5);
      dates.get(i)[6] = new Date(108, 10, 12);
      dates.get(i)[7] = new Date(108, 10, 19);
      dates.get(i)[8] = new Date(108, 10, 26);
      dates.get(i)[9] = new Date(108, 11, 3);
      dates.get(i)[10] = new Date(108, 11, 10);
      dates.get(i)[11] = new Date(108, 11, 17);
    }
    values.add(new double[] { 142, 123, 142, 152, 149, 122, 110, 120, 125, 155, 146, 150 });
    values.add(new double[] { 102, 90, 112, 105, 125, 112, 125, 112, 105, 115, 116, 135 });
    length = values.get(0).length;
    int[] colors = new int[] { Color.BLUE, Color.GREEN };
    PointStyle[] styles = new PointStyle[] { PointStyle.POINT, PointStyle.POINT };
    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
    setChartSettings(renderer, "Project work status", "Date", "Tickets", dates.get(0)[0]
        .getTime(), dates.get(0)[11].getTime(), 50, 190, Color.GRAY, Color.LTGRAY);
    renderer.setXLabels(5);
    renderer.setYLabels(10);
    renderer.setDisplayChartValues(true);
    return ChartFactory.getTimeChartIntent(context, buildDateDataset(titles, dates, values),
        renderer, "MM/dd/yyyy");
  }

}
