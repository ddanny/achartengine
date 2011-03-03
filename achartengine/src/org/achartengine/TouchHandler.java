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
import org.achartengine.tools.Pan;
import org.achartengine.tools.Zoom;

import android.graphics.RectF;
import android.view.MotionEvent;

/**
 * The main handler of the touch events.
 */
public class TouchHandler implements ITouchHandler {
  /** The chart renderer. */
  private XYMultipleSeriesRenderer mRenderer;
  /** The old x coordinate. */
  private float oldX;
  /** The old y coordinate. */
  private float oldY;
  /** The old x2 coordinate. */
  private float oldX2;
  /** The old y2 coordinate. */
  private float oldY2;
  /** The zoom buttons rectangle. */
  private RectF zoomR = new RectF();
  /** The pan tool. */
  private Pan pan;
  /** The zoom for the pinch gesture. */
  private Zoom pinchZoom;
  /** The graphical view. */
  private GraphicalView graphicalView;

  /**
   * Creates a new graphical view.
   * 
   * @param context the context
   * @param chart the chart to be drawn
   */
  public TouchHandler(GraphicalView view, AbstractChart chart) {
    graphicalView = view;
    zoomR = graphicalView.getZoomRectangle();
    mRenderer = ((XYChart) chart).getRenderer();
    if (mRenderer.isPanXEnabled() || mRenderer.isPanYEnabled()) {
      pan = new Pan((XYChart) chart);
    }
    if (mRenderer.isZoomXEnabled() || mRenderer.isZoomYEnabled()) {
      pinchZoom = new Zoom((XYChart) chart, true, 1);
    }
  }

  /**
   * Handles the touch event.
   * 
   * @param event the touch event
   */
  public void handleTouch(MotionEvent event) {
    int action = event.getAction();
    if (mRenderer != null && action == MotionEvent.ACTION_MOVE) {
      if (oldX >= 0 || oldY >= 0) {
        float newX = event.getX(0);
        float newY = event.getY(0);
        if (event.getPointerCount() > 1 && (oldX2 >= 0 || oldY2 >= 0) && (mRenderer.isZoomXEnabled() || mRenderer.isZoomYEnabled())) {
          float newX2 = event.getX(1);
          float newY2 = event.getY(1);
          float newDeltaX = Math.abs(newX - newX2);
          float newDeltaY = Math.abs(newY - newY2);
          float oldDeltaX = Math.abs(oldX - oldX2);
          float oldDeltaY = Math.abs(oldY - oldY2);
          float zoomRate = 1;
          if (Math.abs(newX - oldX) >= Math.abs(newY - oldY)) {
            zoomRate = newDeltaX / oldDeltaX;
          } else {
            zoomRate = newDeltaY / oldDeltaY;
          }
          if (zoomRate > 0.909 && zoomRate < 1.1) {
            pinchZoom.setZoomRate(zoomRate);
            pinchZoom.apply();
          }
          oldX2 = newX2;
          oldY2 = newY2;
        } else if (mRenderer.isPanXEnabled() || mRenderer.isPanYEnabled()) {
          pan.apply(oldX, oldY, newX, newY);
          oldX2 = 0;
          oldY2 = 0;
        }
        oldX = newX;
        oldY = newY;
        graphicalView.repaint();
      }
    } else if (action == MotionEvent.ACTION_DOWN) {
      oldX = event.getX(0);
      oldY = event.getY(0);
      if (mRenderer != null && (mRenderer.isZoomXEnabled() || mRenderer.isZoomYEnabled())
          && zoomR.contains(oldX, oldY)) {
        if (oldX < zoomR.left + zoomR.width() / 3) {
          graphicalView.zoomIn();
        } else if (oldX < zoomR.left + zoomR.width() * 2 / 3) {
          graphicalView.zoomOut();
        } else {
          graphicalView.zoomReset();
        }
      }
    } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
      oldX = 0;
      oldY = 0;
      oldX2 = 0;
      oldY2 = 0;
      if (action == MotionEvent.ACTION_POINTER_UP) {
        oldX = -1;
        oldY = -1;
      }
    }
  }

}