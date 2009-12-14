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
package org.achartengine.activity;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalActivity;
import org.achartengine.GraphicalView;
import org.achartengine.R;
import org.achartengine.chart.AbstractChart;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.XYChart;
import org.achartengine.consumer.DoubleDatumExtractor;
import org.achartengine.intent.ContentSchema;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity that encapsulates a graphical view of the chart.
 */
public class LineChartActivity extends GraphicalActivity {

  
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
    super.onCreate(savedInstanceState);
    getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.typepointline);
  }

  // ---------------------------------------------
  @Override
  protected AbstractChart generateChartFromContentProvider(Uri intent_data) {

    
    List<? extends List<? extends List<? extends Number>>> sorted_series_list = getGenericSortedSeriesData(intent_data, new DoubleDatumExtractor());
    List<List<Number>> x_axis_series = (List<List<Number>>) sorted_series_list.get( ContentSchema.X_AXIS_INDEX );
    List<List<Number>> y_axis_series = (List<List<Number>>) sorted_series_list.get( ContentSchema.Y_AXIS_INDEX );
    
    assert (x_axis_series.size() == y_axis_series.size()
        || x_axis_series.size() == 1
        || x_axis_series.size() == 0);

      String[] titles = getSortedSeriesTitles(intent_data);

      assert (titles.length == y_axis_series.size());


      assert (titles.length == y_axis_series.get(0).size());
      
      
      // If there is no x-axis data, just number the y-elements.
      List<Number> prototypical_x_values; 
      if (x_axis_series.size() == 0) {
        for (int i=0; i < y_axis_series.size(); i++) {
          prototypical_x_values = new ArrayList<Number>();
          x_axis_series.add( prototypical_x_values );
          for (int j=0; j < y_axis_series.get(i).size(); j++)
            prototypical_x_values.add(j);
        }
      }

      
      // Replicate the X-axis data for each series if necessary
      if (x_axis_series.size() == 1) {
        Log.i(TAG, "Replicating x-axis series...");
        prototypical_x_values = x_axis_series.get(0);
        Log.d(TAG, "Size of prototypical x-set: " + prototypical_x_values.size());
        while (x_axis_series.size() < titles.length)
          x_axis_series.add( prototypical_x_values );
      }
      
      
      int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW };
      PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND,
          PointStyle.TRIANGLE, PointStyle.SQUARE };

      
      
      List<String> axis_labels = getAxisTitles(intent_data);
      
      
      
      
      
      XYMultipleSeriesRenderer renderer = org.achartengine.chartdemo.demo.chart.AbstractChart.buildRenderer(colors, styles);
      int length = renderer.getSeriesRendererCount();
      for (int i = 0; i < length; i++) {
        ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
      }
      
      

      String chart_title = getIntent().getStringExtra(Intent.EXTRA_TITLE);
      String x_label = axis_labels.get( ContentSchema.X_AXIS_INDEX );
      String y_label = axis_labels.get( ContentSchema.Y_AXIS_INDEX );
      Log.d(TAG, "X LABEL: " + x_label);
      Log.d(TAG, "X LABEL: " + y_label);
      Log.d(TAG, "chart_title: " + chart_title);
      
      org.achartengine.chartdemo.demo.chart.AbstractChart.setChartSettings(renderer, chart_title, x_label, y_label, 0.5, 12.5, 0, 32,
          Color.LTGRAY, Color.GRAY);
      renderer.setXLabels(12);
      renderer.setYLabels(10);
      
      
      XYMultipleSeriesDataset dataset = org.achartengine.chartdemo.demo.chart.AbstractChart.buildDataset2(titles, x_axis_series, y_axis_series);

      ChartFactory.checkParameters(dataset, renderer);

      XYChart chart = new LineChart(dataset, renderer);
      return chart;

  }
}