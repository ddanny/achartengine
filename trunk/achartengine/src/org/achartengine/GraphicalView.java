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
  /** The zoom buttons rectangle. */
  private RectF zoomR = new RectF();
  /** The zoom in icon. */
  private Bitmap zoomInImage;
  /** The zoom out icon. */
  private Bitmap zoomOutImage;
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
      mRenderer = ((XYChart) mChart).getRenderer();
      if (mRenderer.getMarginsColor() == XYMultipleSeriesRenderer.NO_COLOR) {
        mRenderer.setMarginsColor(mPaint.getColor());
      }
      if (mRenderer.isPanXEnabled() || mRenderer.isPanYEnabled()) {
        pan = new Pan((XYChart) mChart, mRenderer);
      }
      if (mRenderer.isZoomXEnabled() || mRenderer.isZoomYEnabled()) {
        zoomIn = new Zoom((XYChart) mChart, mRenderer, true, mRenderer.getZoomRate());
        zoomOut = new Zoom((XYChart) mChart, mRenderer, false, mRenderer.getZoomRate());
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
      zoomR.set(left + width - ZOOM_SIZE * 2, top + height - ZOOM_SIZE * 0.775f, left + width, top
          + height);
      canvas.drawRoundRect(zoomR, ZOOM_SIZE / 3, ZOOM_SIZE / 3, mPaint);
      canvas.drawBitmap(zoomInImage, left + width - ZOOM_SIZE * 1.75f, top + height - ZOOM_SIZE
          * 0.625f, null);
      canvas.drawBitmap(zoomOutImage, left + width - ZOOM_SIZE * 0.75f, top + height - ZOOM_SIZE
          * 0.625f, null);
    }
  }

  public void handleTouch(MotionEvent event) {
    int action = event.getAction();
    if (mRenderer != null && action == MotionEvent.ACTION_MOVE) {
      if (oldX >= 0 || oldY >= 0) {
        float newX = event.getX();
        float newY = event.getY();
        if (mRenderer.isPanXEnabled() || mRenderer.isPanYEnabled()) {
          pan.apply(oldX, oldY, newX, newY);
        }
        oldX = newX;
        oldY = newY;
        repaint();
      }
    } else if (action == MotionEvent.ACTION_DOWN) {
      oldX = event.getX();
      oldY = event.getY();
      if (mRenderer != null && (mRenderer.isZoomXEnabled() || mRenderer.isZoomYEnabled())
          && zoomR.contains(oldX, oldY)) {
        if (oldX < zoomR.centerX()) {
          zoomIn.apply();
        } else {
          zoomOut.apply();
        }
      }
    } else if (action == MotionEvent.ACTION_UP) {
      oldX = 0;
      oldY = 0;
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