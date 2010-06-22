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
package org.achartengine;

import org.achartengine.chart.AbstractChart;
import org.achartengine.chart.XYChart;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

/**
 * The view that encapsulates the graphical chart.
 */
public class GraphicalView extends View {
  /** The chart to be drawn. */
  private AbstractChart mChart;
  /** The chart renderer. */
  private XYMultipleSeriesRenderer mRenderer;
  /** The view bounds. */
  private Rect mRect = new Rect();
  /** The user interface thread handler. */
  private Handler mHandler;
  /** The old x coordinate. */
  private float oldX;
  /** The old y coordinate. */
  private float oldY;
  
  /**
   * Creates a new graphical view.
   * 
   * @param context the context
   * @param chart the chart to be drawn
   */
  public GraphicalView(Context context, AbstractChart chart) {
    super(context);
    mChart = chart;
    mHandler = new Handler();
    if (mChart instanceof XYChart) {
      mRenderer = ((XYChart) mChart).getRenderer();
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.getClipBounds(mRect);
    int top = mRect.top;
    int left = mRect.left;
    int width = mRect.width();
    int height = mRect.height();
    mChart.draw(canvas, left, top, width, height);
  }

  public void handleTouch(MotionEvent event) {
    int action = event.getAction();
    if (mRenderer != null && action == MotionEvent.ACTION_MOVE) {
      if (oldX >= 0 || oldY >= 0) {
        float newX = event.getX();
        float newY = event.getY();
        
        double minX = mRenderer.getXAxisMin();
        double maxX = mRenderer.getXAxisMax();
        double minY = mRenderer.getYAxisMin();
        double maxY = mRenderer.getYAxisMax();
        XYChart chart = (XYChart) mChart;
        double[] calcRange = chart.getCalcRange();
        if (minX == minY && calcRange[0] == calcRange[1] || maxX == maxY && calcRange[2] == calcRange[3]) {
          return;
        }
        if (!mRenderer.isMinXSet()) {
          minX = calcRange[0];
          mRenderer.setXAxisMin(minX);
        }
        if (!mRenderer.isMaxXSet()) {
          maxX = calcRange[1];
          mRenderer.setXAxisMax(maxX);
        }
        if (!mRenderer.isMinYSet()) {
          minY = calcRange[2];
          mRenderer.setYAxisMin(minY);
        }
        if (!mRenderer.isMaxYSet()) {
          maxY = calcRange[3];
          mRenderer.setYAxisMax(maxY);
        }
        
        PointF realPoint = chart.toRealPoint(oldX, oldY);
        PointF realPoint2 = chart.toRealPoint(newX, newY);
        double deltaX = realPoint.x - realPoint2.x;
        double deltaY = realPoint.y - realPoint2.y;
        mRenderer.setXAxisMin(minX + deltaX);
        mRenderer.setXAxisMax(maxX + deltaX);
        mRenderer.setYAxisMin(minY + deltaY);
        mRenderer.setYAxisMax(maxY + deltaY);
        oldX = newX;
        oldY = newY;
        repaint();
      }
    } else if (action == MotionEvent.ACTION_DOWN) {
      oldX = event.getX();
      oldY = event.getY();
    } else if (action == MotionEvent.ACTION_UP) {
      oldX = 0;
      oldY = 0;
    }
  }
  
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    handleTouch(event);
    return true;
  }
  
  /**
   * Schedule a user interface repaint.
   */
  public void repaint() {
    mHandler.post(new Runnable() {
      public void run() {
        invalidate();
      }
    });
  }
}