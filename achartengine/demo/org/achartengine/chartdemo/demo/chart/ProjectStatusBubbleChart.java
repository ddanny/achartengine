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
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYValueSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

/**
 * Project status demo bubble chart.
 */
public class ProjectStatusBubbleChart extends AbstractDemoChart {
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
    return "The opened tickets and the fixed tickets (bubble chart)";
  }
  
  /**
   * Executes the chart demo.
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
    XYMultipleSeriesDataset series = new XYMultipleSeriesDataset();
    XYValueSeries newTicketSeries = new XYValueSeries("New Tickets");
    newTicketSeries.add(1, 2, 14);
    newTicketSeries.add(2, 2, 12);
    newTicketSeries.add(3, 2, 18);
    newTicketSeries.add(4, 2, 5);
    newTicketSeries.add(5, 2, 1);
    series.addSeries(newTicketSeries);
    XYValueSeries fixedTicketSeries = new XYValueSeries("Fixed Tickets");
    fixedTicketSeries.add(1, 1, 7);
    fixedTicketSeries.add(2, 1, 4);
    fixedTicketSeries.add(3, 1, 18);
    fixedTicketSeries.add(4, 1, 3);
    fixedTicketSeries.add(5, 1, 1);
    series.addSeries(fixedTicketSeries);
    
    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
    XYSeriesRenderer newTicketRenderer = new XYSeriesRenderer();
    newTicketRenderer.setColor(Color.BLUE);
    renderer.addSeriesRenderer(newTicketRenderer);
    XYSeriesRenderer fixedTicketRenderer = new XYSeriesRenderer();
    fixedTicketRenderer.setColor(Color.GREEN);
    renderer.addSeriesRenderer(fixedTicketRenderer);
    
    setChartSettings(renderer, "Project work status", "Priority", "", 0.5, 5.5, 0, 5, Color.GRAY, Color.LTGRAY);
    renderer.setXLabels(7);
    renderer.setYLabels(0);
    renderer.setDisplayChartValues(false);
    renderer.setShowGrid(false);
    return ChartFactory.getBubbleChartIntent(context, series, renderer, "Project tickets");
  }

}
