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
import org.achartengine.consumer.DatumExtractor;
import org.achartengine.intent.ContentSchema;
import org.achartengine.intent.ContentSchema.PlotData;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
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


  protected static final String TAG = "AChartEngine"; 
  
  /** The encapsulated graphical view. */
  protected GraphicalView mView;
  
  /** The chart to be drawn. */
  protected AbstractChart mChart;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    
    
    
    Uri intent_data = getIntent().getData();
    
    // Zip the data.
    if (intent_data != null) {
      // We have been passed a cursor to the data via a content provider.

      
      
      mChart = generateChartFromContentProvider(intent_data);
      mView = new GraphicalView(this, mChart);

      
      
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
  
  
  

  // ---------------------------------------------
  
  
  Comparator<Entry<Integer, ?>> integer_keyed_entry_comparator = new Comparator<Entry<Integer, ?>>() {

    @Override
    public int compare(Entry<Integer, ?> object1, Entry<Integer, ?> object2) {
      return object1.getKey().compareTo(object2.getKey());
    }
  };

  // ---------------------------------------------
  
  <T> List<T> sortAndSimplify(Map<Integer, T> input_map) {
    // Sort the axes by index
    ArrayList<Entry<Integer,T>> sorted_axes_series_map = new ArrayList<Entry<Integer, T>>(input_map.entrySet());
    Collections.sort(sorted_axes_series_map, integer_keyed_entry_comparator);
    
    // Simplify the sorted axes as a list
    List<T> simplified_sorted_axes_series_maps = new ArrayList<T>();
    for (Entry<Integer, T> entry : sorted_axes_series_map)
      simplified_sorted_axes_series_maps.add( entry.getValue() );
    
    return simplified_sorted_axes_series_maps;
  }    

  // ---------------------------------------------
  
  <T> List<T> pickAxisSeries(Map<Integer, Map<Integer, List<T>>> axes_series_map, Cursor cursor, int axis_column, int series_column) {
    // Pick the correct axis
    int axis_index = cursor.getInt(axis_column);
    Map<Integer, List<T>> series_map;            
    if (axes_series_map.containsKey(axis_index)) {
      series_map = axes_series_map.get(axis_index);
    } else {
      series_map = new HashMap<Integer, List<T>>();
      axes_series_map.put(axis_index, series_map);
    }

    // Pick the correct series for this axis
    int series_index = cursor.getInt(series_column);
    List<T> series_axis_data;
    if (series_map.containsKey(series_index)) {
      series_axis_data = series_map.get(series_index);
    } else {
      series_axis_data = new ArrayList<T>();
      series_map.put(series_index, series_axis_data);
    }
    
    return series_axis_data;
  }

  // ---------------------------------------------
  protected String[] getSortedSeriesTitles(Uri intent_data) {
    
    Uri meta_uri = intent_data.buildUpon().appendEncodedPath( ContentSchema.DATASET_ASPECT_META ).build();
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
    List<String> sorted_series_labels = sortAndSimplify(series_label_map);
    
    String[] titles = sorted_series_labels.toArray(new String[] {});
    return titles;
  }

  // ---------------------------------------------
  public static class LabeledDatum {
    public String label;
    public Number datum;
  }
  
  // ---------------------------------------------
  // Retrieve Series data
  
  // Outermost list: Axes
  // Second-outermost list: All Series
  // Third-outermost list: Data for a single series
  protected <T> List<List<List<T>>> getGenericSortedSeriesData(Uri intent_data, DatumExtractor<T> extractor) {

    Uri data_uri = intent_data.buildUpon().appendEncodedPath( ContentSchema.DATASET_ASPECT_DATA ).build();
    Log.d(TAG, "Querying content provider for: " + data_uri);

    Map<Integer, Map<Integer, List<T>>> axes_series_map = new HashMap<Integer, Map<Integer, List<T>>>();

      Cursor cursor = managedQuery(data_uri,
          new String[] {
            BaseColumns._ID,
            PlotData.COLUMN_AXIS_INDEX,
            PlotData.COLUMN_SERIES_INDEX,
            PlotData.COLUMN_DATUM_VALUE,
            PlotData.COLUMN_DATUM_LABEL},
          null, null, null);

      int id_column = cursor.getColumnIndex(BaseColumns._ID);
      int axis_column = cursor.getColumnIndex(PlotData.COLUMN_AXIS_INDEX);
      int series_column = cursor.getColumnIndex(PlotData.COLUMN_SERIES_INDEX);
      int data_column = cursor.getColumnIndex(PlotData.COLUMN_DATUM_VALUE);
      int label_column = cursor.getColumnIndex(PlotData.COLUMN_DATUM_LABEL);
      
      
      int i=0;
      if (cursor.moveToFirst()) {
          do {

            List<T> series_axis_data = pickAxisSeries(axes_series_map, cursor, axis_column, series_column);
            
              T datum = extractor.getDatum(cursor, data_column, label_column);
              series_axis_data.add(datum);

              i++;
          } while (cursor.moveToNext());
      }


    // Sort each axis map by key; that is, sort by the series index - then add it to the simplified axis list
      List<List<List<T>>> simplified_sorted_axes_series = new ArrayList<List<List<T>>>();
      for (Map<Integer, List<T>> series_map : sortAndSimplify(axes_series_map))
        simplified_sorted_axes_series.add( sortAndSimplify(series_map) );

    return simplified_sorted_axes_series;
  }

  // ---------------------------------------------
  //  Retrieve Axes data
  protected List<String> getAxisTitles(Uri intent_data) {

    Uri axes_uri = intent_data.buildUpon().appendEncodedPath( ContentSchema.DATASET_ASPECT_AXES ).build();
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
//            int axis_index = meta_cursor.getInt(axis_column);
              String axis_label = meta_cursor.getString(label_column);
              
              
              axis_labels.add(axis_label);

              
              i++;
          } while (meta_cursor.moveToNext());
      }
    }
    
    return axis_labels;
  }
  
  
  
  
  protected List<List<Number>> stripSeriesDatumLabels(List<List<LabeledDatum>> sorted_labeled_series_list) {
    
    // Discard the datum labels
    List<List<Number>> sorted_series_list = new ArrayList<List<Number>>();
    for (List<LabeledDatum> labeled_series : sorted_labeled_series_list) {
      List<Number> series = new ArrayList<Number>();
      sorted_series_list.add( series );
      for (LabeledDatum labeled_datum : labeled_series)
        series.add(labeled_datum.datum);
    }
    return sorted_series_list;
  }
  
  // TODO: This should be an "abstract" method
  protected AbstractChart generateChartFromContentProvider(Uri intent_data) {
    return null;
    };
}