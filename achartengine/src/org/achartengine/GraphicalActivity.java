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
package org.achartengine;

import org.achartengine.chart.AbstractChart;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.XYChart;
import org.achartengine.intent.ContentSchema.PlotData;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * An activity that encapsulates a graphical view of the chart.
 */
public class GraphicalActivity extends Activity {


  static final String TAG = "AChartEngine"; 
  
  /** The encapsulated graphical view. */
  private GraphicalView mView;
  
  /** The chart to be drawn. */
  private AbstractChart mChart;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    
    
    
    Uri intent_data = getIntent().getData();
    
    // Zip the data.
    if (intent_data != null) {
      generateLineChartFromContentProvider(intent_data);
        // We have been passed a cursor to the data via a content provider.

      
      
      String title = getIntent().getStringExtra(Intent.EXTRA_TITLE);
      
      if (title == null) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
      } else if (title.length() > 0) {
        setTitle(title);
      }
      setContentView(mView);

      
      
    } else {

    
      Bundle extras = getIntent().getExtras();
      mChart = (AbstractChart) extras.getSerializable(ChartFactory.CHART);
      mView = new GraphicalView(this, mChart);
      String title = getIntent().getStringExtra(Intent.EXTRA_TITLE);
      if (title == null) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
      } else if (title.length() > 0) {
        setTitle(title);
      }
      setContentView(mView);
    }
  }
  
  
  
  
  
  
  Comparator<Entry<Integer, ?>> series_comparator = new Comparator<Entry<Integer, ?>>() {

    @Override
    public int compare(Entry<Integer, ?> object1, Entry<Integer, ?> object2) {
      return object1.getKey().compareTo(object2.getKey());
    }
  };
  
  
  
  
  
  
  void generateLineChartFromContentProvider(Uri intent_data) {

    
      Log.d(TAG, "Querying content provider for: " + intent_data);


      Map<Integer, List<Double>> series_map = new HashMap<Integer, List<Double>>();

      {
        
        
        Cursor cursor = managedQuery(intent_data,
            new String[] {BaseColumns._ID, PlotData.COLUMN_SERIES_INDEX, PlotData.COLUMN_DATUM_VALUE},
            null, null, null);

        int id_column = cursor.getColumnIndex(BaseColumns._ID);
        int axis_column = cursor.getColumnIndex(PlotData.COLUMN_SERIES_INDEX);
        int data_column = cursor.getColumnIndex(PlotData.COLUMN_DATUM_VALUE);
        int label_column = cursor.getColumnIndex(PlotData.COLUMN_DATUM_LABEL);
        
        
        int i=0;
        if (cursor.moveToFirst()) {
            do {
              int series_index = cursor.getInt(axis_column);
              
              List<Double> series_data;
              if (series_map.containsKey(series_index)) {
                series_data = series_map.get(series_index);
              } else {
                series_data = new ArrayList<Double>();
                series_map.put(series_index, series_data);
              }
              
                double datum = cursor.getDouble(data_column);
                series_data.add(datum);
                
//                slice.label = cursor.getString(label_column);
//                slice.color = color_values[i % color_values.length];
//                list.add(slice);
                
                i++;
            } while (cursor.moveToNext());
        }
      }

      // Sort the map by key; that is, sort by the series index
      ArrayList<Entry<Integer,List<Double>>> series_entry_list = new ArrayList<Entry<Integer,List<Double>>>( series_map.entrySet() );
      Collections.sort(series_entry_list, series_comparator);
      
      List<List<Double>> sorted_series_list = new ArrayList<List<Double>>();
      for (Entry<Integer, List<Double>> entry : series_entry_list) {
        sorted_series_list.add(entry.getValue());
      }

      
      
      
      
      
      
      
      
      
      // Retrieve Series data
      // ---------------------------------------------
      
      Uri meta_uri = intent_data.buildUpon().appendEncodedPath("meta").build();
      Log.d(TAG, "Querying content provider for: " + meta_uri);

      Map<Integer, String> series_label_map = new HashMap<Integer, String>();
      {
        
        Cursor meta_cursor = managedQuery(meta_uri,
            new String[] {BaseColumns._ID, PlotData.COLUMN_SERIES_LABEL},
            null, null, null);
        
        int series_column = meta_cursor.getColumnIndex(BaseColumns._ID);
        int label_column = meta_cursor.getColumnIndex(PlotData.COLUMN_SERIES_LABEL);
        
        int i=0;
        if (meta_cursor.moveToFirst()) {
          // TODO: This could also be used to set color, line style, marker shape, etc.
            do {
              int series_index = meta_cursor.getInt(series_column);
              String series_label = meta_cursor.getString(label_column);
              

              series_label_map.put(series_index, series_label);

                
                i++;
            } while (meta_cursor.moveToNext());
        }
      }
      
      
      
      // Sort the map by key; that is, sort by the series index
      ArrayList<Entry<Integer, String>> series_label_list = new ArrayList<Entry<Integer, String>>( series_label_map.entrySet() );
      Collections.sort(series_entry_list, series_comparator);
      
      List<String> sorted_series_labels = new ArrayList<String>();
      for (Entry<Integer, String> entry : series_label_list) {
        sorted_series_labels.add(entry.getValue());
      }
      String[] titles = sorted_series_labels.toArray(new String[] {});
      

      List<double[]> x = new ArrayList<double[]>();
      for (int i = 0; i < titles.length; i++) {
        x.add(new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 });
      }
      
      int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW };
      PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND,
          PointStyle.TRIANGLE, PointStyle.SQUARE };
      

      
      
      
      
      
      
      
      // Retrieve Axes data
      // ---------------------------------------------
      
      Uri axes_uri = intent_data.buildUpon().appendEncodedPath("axes").build();
      Log.d(TAG, "Querying content provider for: " + axes_uri);

      List<String> axis_labels = new ArrayList<String>();
      {
        
        Cursor meta_cursor = managedQuery(axes_uri,
            new String[] {BaseColumns._ID, PlotData.COLUMN_AXIS_LABEL},
            null, null, null);
        
        int axis_column = meta_cursor.getColumnIndex(BaseColumns._ID);
        int label_column = meta_cursor.getColumnIndex(PlotData.COLUMN_AXIS_LABEL);
        
        int i=0;
        if (meta_cursor.moveToFirst()) {
          // TODO: This could also be used to set color, line style, marker shape, etc.
            do {
//              int axis_index = meta_cursor.getInt(axis_column);
              String axis_label = meta_cursor.getString(label_column);
              

              axis_labels.add(axis_label);

                
                i++;
            } while (meta_cursor.moveToNext());
        }
      }
      
      
      
      
      
      
      
      XYMultipleSeriesRenderer renderer = org.achartengine.chartdemo.demo.chart.AbstractChart.buildRenderer(colors, styles);
      int length = renderer.getSeriesRendererCount();
      for (int i = 0; i < length; i++) {
        ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
      }
      
      

      String chart_title = getIntent().getStringExtra(Intent.EXTRA_TITLE);
      String x_label = axis_labels.get(0);
      String y_label = axis_labels.get(1);
      Log.d(TAG, "X LABEL: " + x_label);
      Log.d(TAG, "X LABEL: " + y_label);
      Log.d(TAG, "chart_title: " + chart_title);
      
      org.achartengine.chartdemo.demo.chart.AbstractChart.setChartSettings(renderer, chart_title, x_label, y_label, 0.5, 12.5, 0, 32,
          Color.LTGRAY, Color.GRAY);
      renderer.setXLabels(12);
      renderer.setYLabels(10);
      
      
      
      
//      XYMultipleSeriesDataset dataset = org.achartengine.chartdemo.demo.chart.AbstractChart.buildDataset(titles, x, values);
      XYMultipleSeriesDataset dataset = org.achartengine.chartdemo.demo.chart.AbstractChart.buildDataset3(titles, x, sorted_series_list);

      ChartFactory.checkParameters(dataset, renderer);


      
      
      XYChart chart = new LineChart(dataset, renderer);
      
      
      mChart = chart;
      mView = new GraphicalView(this, mChart);
      
      
      
      
      
      
      
      
      
      
      
      
      
  }
}