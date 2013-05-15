/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
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
import org.achartengine.GraphicalView;
import org.achartengine.chartdemo.demo.R;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class PieChartBuilder extends Activity {
  /** Colors to be used for the pie slices. */
  private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN };
  /** The main series that will include all the data. */
  private CategorySeries mSeries = new CategorySeries("");
  /** The main renderer for the main dataset. */
  private DefaultRenderer mRenderer = new DefaultRenderer();
  /** Button for adding entered data to the current series. */
  private Button mAdd;
  /** Edit text field for entering the slice value. */
  private EditText mValue;
  /** The chart view that displays the data. */
  private GraphicalView mChartView;

  @Override
  protected void onRestoreInstanceState(Bundle savedState) {
    super.onRestoreInstanceState(savedState);
    mSeries = (CategorySeries) savedState.getSerializable("current_series");
    mRenderer = (DefaultRenderer) savedState.getSerializable("current_renderer");
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putSerializable("current_series", mSeries);
    outState.putSerializable("current_renderer", mRenderer);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.xy_chart);
    mValue = (EditText) findViewById(R.id.xValue);
    mRenderer.setZoomButtonsVisible(true);
    mRenderer.setStartAngle(180);
    mRenderer.setDisplayValues(true);

    mAdd = (Button) findViewById(R.id.add);
    mAdd.setEnabled(true);
    mValue.setEnabled(true);

    mAdd.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        double value = 0;
        try {
          value = Double.parseDouble(mValue.getText().toString());
        } catch (NumberFormatException e) {
          mValue.requestFocus();
          return;
        }
        mValue.setText("");
        mValue.requestFocus();
        mSeries.add("Series " + (mSeries.getItemCount() + 1), value);
        SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
        renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
        mRenderer.addSeriesRenderer(renderer);
        mChartView.repaint();
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (mChartView == null) {
      LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
      mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
      mRenderer.setClickEnabled(true);
      mChartView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
          if (seriesSelection == null) {
            Toast.makeText(PieChartBuilder.this, "No chart element selected", Toast.LENGTH_SHORT)
                .show();
          } else {
            for (int i = 0; i < mSeries.getItemCount(); i++) {
              mRenderer.getSeriesRendererAt(i).setHighlighted(i == seriesSelection.getPointIndex());
            }
            mChartView.repaint();
            Toast.makeText(
                PieChartBuilder.this,
                "Chart data point index " + seriesSelection.getPointIndex() + " selected"
                    + " point value=" + seriesSelection.getValue(), Toast.LENGTH_SHORT).show();
          }
        }
      });
      layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
          LayoutParams.FILL_PARENT));
    } else {
      mChartView.repaint();
    }
  }
}
