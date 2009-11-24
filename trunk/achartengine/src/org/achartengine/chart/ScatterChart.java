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
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;

/**
 * The scatter chart rendering class.
 */
public class ScatterChart extends XYChart {
  /** The point shape size. */
  private static final float SIZE = 3;
  /** The legend shape width. */
  private static final int SHAPE_WIDTH = 10;

  /**
   * Builds a new scatter chart instance.
   * 
   * @param dataset the multiple series dataset
   * @param renderer the multiple series renderer
   */
  public ScatterChart(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer) {
    super(dataset, renderer);
  }

  /**
   * The graphical representation of a series.
   * 
   * @param canvas the canvas to paint to
   * @param paint the paint to be used for drawing
   * @param points the array of points to be used for drawing the series
   * @param seriesRenderer the series renderer
   * @param yAxisValue the minimum value of the y axis
   * @param seriesIndex the index of the series currently being drawn
   */
  public void drawSeries(Canvas canvas, Paint paint, float[] points,
      SimpleSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex) {
    XYSeriesRenderer renderer = (XYSeriesRenderer) seriesRenderer;
    paint.setColor(renderer.getColor());
    if (renderer.isFillPoints()) {
      paint.setStyle(Style.FILL);
    } else {
      paint.setStyle(Style.STROKE);
    }
    int length = points.length;
    switch (renderer.getPointStyle()) {
    case X:
      for (int i = 0; i < length; i += 2) {
        drawX(canvas, paint, points[i], points[i + 1]);
      }
      break;
    case CIRCLE:
      for (int i = 0; i < length; i += 2) {
        drawCircle(canvas, paint, points[i], points[i + 1]);
      }
      break;
    case TRIANGLE:
      float[] path = new float[6];
      for (int i = 0; i < length; i += 2) {
        drawTriangle(canvas, paint, path, points[i], points[i + 1]);
      }
      break;
    case SQUARE:
      for (int i = 0; i < length; i += 2) {
        drawSquare(canvas, paint, points[i], points[i + 1]);
      }
      break;
    case DIAMOND:
      path = new float[8];
      for (int i = 0; i < length; i += 2) {
        drawDiamond(canvas, paint, path, points[i], points[i + 1]);
      }
      break;
    case POINT:
      canvas.drawPoints(points, paint);
      break;
    }
  }

  /**
   * Returns the legend shape width.
   * 
   * @return the legend shape width
   */
  public int getLegendShapeWidth() {
    return SHAPE_WIDTH;
  }

  /**
   * The graphical representation of the legend shape.
   * 
   * @param canvas the canvas to paint to
   * @param renderer the series renderer
   * @param x the x value of the point the shape should be drawn at
   * @param y the y value of the point the shape should be drawn at
   * @param paint the paint to be used for drawing
   */
  public void drawLegendShape(Canvas canvas, SimpleSeriesRenderer renderer, float x, float y,
      Paint paint) {
    if (((XYSeriesRenderer) renderer).isFillPoints()) {
      paint.setStyle(Style.FILL);
    } else {
      paint.setStyle(Style.STROKE);
    }
    switch (((XYSeriesRenderer) renderer).getPointStyle()) {
    case X:
      drawX(canvas, paint, x + SHAPE_WIDTH, y);
      break;
    case CIRCLE:
      drawCircle(canvas, paint, x + SHAPE_WIDTH, y);
      break;
    case TRIANGLE:
      drawTriangle(canvas, paint, new float[6], x + SHAPE_WIDTH, y);
      break;
    case SQUARE:
      drawSquare(canvas, paint, x + SHAPE_WIDTH, y);
      break;
    case DIAMOND:
      drawDiamond(canvas, paint, new float[8], x + SHAPE_WIDTH, y);
      break;
    case POINT:
      canvas.drawPoint(x + SHAPE_WIDTH, y, paint);
      break;
    }
  }

  /**
   * The graphical representation of an X point shape.
   * 
   * @param canvas the canvas to paint to
   * @param paint the paint to be used for drawing
   * @param x the x value of the point the shape should be drawn at
   * @param y the y value of the point the shape should be drawn at
   */
  private void drawX(Canvas canvas, Paint paint, float x, float y) {
    canvas.drawLine(x - SIZE, y - SIZE, x + SIZE, y + SIZE, paint);
    canvas.drawLine(x + SIZE, y - SIZE, x - SIZE, y + SIZE, paint);
  }

  /**
   * The graphical representation of a circle point shape.
   * 
   * @param canvas the canvas to paint to
   * @param paint the paint to be used for drawing
   * @param x the x value of the point the shape should be drawn at
   * @param y the y value of the point the shape should be drawn at
   */
  private void drawCircle(Canvas canvas, Paint paint, float x, float y) {
    canvas.drawCircle(x, y, SIZE, paint);
  }

  /**
   * The graphical representation of a triangle point shape.
   * 
   * @param canvas the canvas to paint to
   * @param paint the paint to be used for drawing
   * @param path the triangle path
   * @param x the x value of the point the shape should be drawn at
   * @param y the y value of the point the shape should be drawn at
   */
  private void drawTriangle(Canvas canvas, Paint paint, float[] path, float x, float y) {
    path[0] = x;
    path[1] = y - SIZE - SIZE / 2;
    path[2] = x - SIZE;
    path[3] = y + SIZE;
    path[4] = x + SIZE;
    path[5] = path[3];
    drawPath(canvas, path, paint, true);
  }

  /**
   * The graphical representation of a square point shape.
   * 
   * @param canvas the canvas to paint to
   * @param paint the paint to be used for drawing
   * @param x the x value of the point the shape should be drawn at
   * @param y the y value of the point the shape should be drawn at
   */
  private void drawSquare(Canvas canvas, Paint paint, float x, float y) {
    canvas.drawRect(x - SIZE, y - SIZE, x + SIZE, y + SIZE, paint);
  }

  /**
   * The graphical representation of a diamond point shape.
   * 
   * @param canvas the canvas to paint to
   * @param paint the paint to be used for drawing
   * @param path the diamond path
   * @param x the x value of the point the shape should be drawn at
   * @param y the y value of the point the shape should be drawn at
   */
  private void drawDiamond(Canvas canvas, Paint paint, float[] path, float x, float y) {
    path[0] = x;
    path[1] = y - SIZE;
    path[2] = x - SIZE;
    path[3] = y;
    path[4] = x;
    path[5] = y + SIZE;
    path[6] = x + SIZE;
    path[7] = y;
    drawPath(canvas, path, paint, true);
  }

}