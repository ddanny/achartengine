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
import org.achartengine.tools.*;
import org.achartengine.tools.Zoom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
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
  /** The old x2 coordinate. */
  private float oldX2;
  /** The old y2 coordinate. */
  private float oldY2;
  /** The zoom buttons rectangle. */
  private RectF zoomR = new RectF();
  /** The zoom in icon. */
  private Bitmap zoomInImage;
  /** The zoom out icon. */
  private Bitmap zoomOutImage;
  /** The fit zoom icon. */
  private Bitmap fitZoomImage;
  /** The zoom area size. */
  private static final int ZOOM_SIZE = 45;
  /** The zoom buttons background color. */
  private static final int ZOOM_BUTTONS_COLOR = Color.argb(175, 150, 150, 150);
  /** The pan tool. */
  private Pan pan;
  /** The zoom in tool. */
  private Zoom zoomIn;
  /** The zoom out tool. */
  private Zoom zoomOut;
  /** The zoom for the pinch gesture. */
  private Zoom pinchZoom;
  /** The fit zoom tool. */
  private FitZoom fitZoom;
  /** The paint to be used when drawing the chart. */
  private Paint mPaint = new Paint();

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
      zoomInImage = BitmapFactory.decodeStream(getClass().getResourceAsStream("image/zoom_in.png"));
      zoomOutImage = BitmapFactory.decodeStream(getClass()
          .getResourceAsStream("image/zoom_out.png"));
      fitZoomImage = BitmapFactory.decodeStream(getClass().getResourceAsStream("image/zoom-1.png"));
      mRenderer = ((XYChart) mChart).getRenderer();
      if (mRenderer.getMarginsColor() == XYMultipleSeriesRenderer.NO_COLOR) {
        mRenderer.setMarginsColor(mPaint.getColor());
      }
      if (mRenderer.isPanXEnabled() || mRenderer.isPanYEnabled()) {
        pan = new Pan((XYChart) mChart);
      }
      if (mRenderer.isZoomXEnabled() || mRenderer.isZoomYEnabled()) {
        zoomIn = new Zoom((XYChart) mChart, true, mRenderer.getZoomRate());
        zoomOut = new Zoom((XYChart) mChart, false, mRenderer.getZoomRate());
        fitZoom = new FitZoom((XYChart) mChart);
        pinchZoom = new Zoom((XYChart) mChart, true, 1);
      }
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
    mChart.draw(canvas, left, top, width, height, mPaint);
    if (mRenderer != null && (mRenderer.isZoomXEnabled() || mRenderer.isZoomYEnabled())) {
      mPaint.setColor(ZOOM_BUTTONS_COLOR);
      zoomR.set(left + width - ZOOM_SIZE * 3, top + height - ZOOM_SIZE * 0.775f, left + width, top
          + height);
      canvas.drawRoundRect(zoomR, ZOOM_SIZE / 3, ZOOM_SIZE / 3, mPaint);
      float buttonY = top + height - ZOOM_SIZE * 0.625f;
      canvas.drawBitmap(zoomInImage, left + width - ZOOM_SIZE * 2.75f, buttonY, null);
      canvas.drawBitmap(zoomOutImage, left + width - ZOOM_SIZE * 1.75f, buttonY, null);
      canvas.drawBitmap(fitZoomImage, left + width - ZOOM_SIZE * 0.75f, buttonY, null);
    }
  }

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
        repaint();
      }
    } else if (action == MotionEvent.ACTION_DOWN) {
      oldX = event.getX(0);
      oldY = event.getY(0);
      if (mRenderer != null && (mRenderer.isZoomXEnabled() || mRenderer.isZoomYEnabled())
          && zoomR.contains(oldX, oldY)) {
        if (oldX < zoomR.left + zoomR.width() / 3) {
          zoomIn.apply();
        } else if (oldX < zoomR.left + zoomR.width() * 2 / 3) {
          zoomOut.apply();
        } else {
          fitZoom.apply();
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
  
  /**
   * Sets the zoom rate.
   * 
   * @param rate the zoom rate
   */
  public void setZoomRate(float rate) {
    if (zoomIn != null && zoomOut != null) {
      zoomIn.setZoomRate(rate);
      zoomOut.setZoomRate(rate);
    }
  }
  
  /**
   * Do a chart zoom in.
   */
  public void zoomIn() {
    if (zoomIn != null) {
      zoomIn.apply();
      repaint();
    }
  }
  
  /**
   * Do a chart zoom out.
   */
  public void zoomOut() {
    if (zoomOut != null) {
      zoomOut.apply();
      repaint();
    }
  }

  /**
   * Do a chart zoom reset / fit zoom.
   */
  public void zoomReset() {
    if (fitZoom != null) {
      fitZoom.apply();
      repaint();
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (mRenderer != null
        && (mRenderer.isPanXEnabled() || mRenderer.isZoomYEnabled() || mRenderer.isZoomXEnabled() || mRenderer
            .isZoomYEnabled())) {
      handleTouch(event);
      return true;
    }
    return super.onTouchEvent(event);
  }

  /**
   * Schedule a view content repaint.
   */
  public void repaint() {
    mHandler.post(new Runnable() {
      public void run() {
        invalidate();
      }
    });
  }

  /**
   * Schedule a view content repaint, in the specified rectangle area.
   * 
   * @param left the left position of the area to be repainted 
   * @param top the top position of the area to be repainted
   * @param right the right position of the area to be repainted
   * @param bottom the bottom position of the area to be repainted
   */
  public void repaint(final int left, final int top, final int right, final int bottom) {
    mHandler.post(new Runnable() {
      public void run() {
        invalidate(left, top, right, bottom);
      }
    });
  }

}