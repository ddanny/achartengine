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
package org.achartengine.chart;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;


/**
 * The bar chart rendering class.
 */
public class BarChart extends XYChart {
  /** The legend shape width. */
  private static final int SHAPE_WIDTH = 12;
  /** The chart type. */
  private Type mType = Type.DEFAULT;
  
  /** 
   * The bar chart type enum.
   */
  public enum Type {
    DEFAULT, STACKED;
  }
  
  /**
   * Builds a new bar chart instance.
   * @param dataset the multiple series dataset
   * @param renderer the multiple series renderer
   * @param type the bar chart type
   */
  public BarChart(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer, Type type) {
    super(dataset, renderer);
    mType = type;
  }

  /**
   * The graphical representation of a series.
   * @param canvas the canvas to paint to
   * @param paint the paint to be used for drawing
   * @param points the array of points to be used for drawing the series
   * @param seriesRenderer the series renderer
   * @param yAxisValue the minimum value of the y axis
   * @param seriesIndex the index of the series currently being drawn
   */
  public void drawSeries(Canvas canvas, Paint paint, float[] points,
      SimpleSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex) {
    int seriesNr = mDataset.getSeriesCount();
    int length = points.length;
    paint.setColor(seriesRenderer.getColor());
    paint.setStyle(Style.FILL);
    float halfDiffX = getHalfDiffX(points, length, seriesNr);
    for (int i = 0; i < length; i += 2) {
      float x = points[i];
      float y = points[i + 1];
      if (mType == Type.STACKED) {
        canvas.drawRect(x - halfDiffX, y, x + halfDiffX, yAxisValue, paint);
      } else {
        float startX = x - seriesNr * halfDiffX + seriesIndex * 2 * halfDiffX;
        canvas.drawRect(startX, y, startX + 2 * halfDiffX, yAxisValue, paint);
      }
    }
  }

  /**
   * The graphical representation of the series values as text.
   * 
   * @param canvas the canvas to paint to
   * @param series the series to be painted
   * @param paint the paint to be used for drawing
   * @param points the array of points to be used for drawing the series
   * @param seriesIndex the index of the series currently being drawn
   */
  protected void drawChartValuesText(Canvas canvas, XYSeries series, Paint paint, float[] points, int seriesIndex) {
    int seriesNr = mDataset.getSeriesCount();
    float halfDiffX = getHalfDiffX(points, points.length, seriesNr);
    for (int k = 0; k < points.length; k += 2) {
      float x = points[k];
      if (mType == Type.DEFAULT) {
        x += seriesIndex * 2 * halfDiffX - (seriesNr - 1.5f) * halfDiffX;
      }
      drawText(canvas, getLabel(series.getY(k / 2)), x, points[k + 1] - 3.5f, paint, 0);
    }
  }

  /**
   * Returns the legend shape width.
   * @return the legend shape width 
   */
  public int getLegendShapeWidth() {
    return SHAPE_WIDTH;
  }

  /**
   * The graphical representation of the legend shape.
   * @param canvas the canvas to paint to
   * @param renderer the series renderer
   * @param x the x value of the point the shape should be drawn at
   * @param y the y value of the point the shape should be drawn at
   * @param paint the paint to be used for drawing
   */
  public void drawLegendShape(Canvas canvas, SimpleSeriesRenderer renderer, float x, float y,
      Paint paint) {
    float halfShapeWidth = SHAPE_WIDTH / 2;
    canvas.drawRect(x, y - halfShapeWidth, x + SHAPE_WIDTH, y + halfShapeWidth, paint);
  }
  
  
  private float getHalfDiffX(float[] points, int length, int seriesNr) {
    float halfDiffX = (points[length - 2] - points[0]) / length;
    if (halfDiffX == 0) {
      halfDiffX = 10;
    }
    
    if (mType != Type.STACKED) {
      halfDiffX /= seriesNr;
    }
    return halfDiffX;
  }

}
