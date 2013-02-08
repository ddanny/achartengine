/**
 * Copyright (C) 2009 - 2012 SC 4ViewSoft SRL
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
package org.achartengine.renderer;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;

import android.graphics.Color;

/**
 * A renderer for the XY type series.
 */
public class XYSeriesRenderer extends SimpleSeriesRenderer {
  /** If the chart points should be filled. */
  private boolean mFillPoints = false;
  /** If the chart should be filled outside its line. */
  private List<FillOutsideLine> mFillBelowLine = new ArrayList<FillOutsideLine>();
  /** The point style. */
  private PointStyle mPointStyle = PointStyle.POINT;
  /** The chart line width. */
  private float mLineWidth = 1;

  public enum FillOutsideLine {
    NONE, BOUNDS_ALL, BOUNDS_BELOW, BOUNDS_ABOVE, BELOW, ABOVE;

    private int mColor = Color.argb(125, 0, 0, 200);

    public int getColor() {
      return mColor;
    }

    public void setColor(int color) {
      mColor = color;
    }
  }

  /**
   * Returns if the chart should be filled below the line.
   * 
   * @return the fill below line status
   * 
   * @deprecated Use {@link #getFillOutsideLine()} instead.
   */
  @Deprecated
  public boolean isFillBelowLine() {
    return mFillBelowLine.size() > 0;
  }

  /**
   * Sets if the line chart should be filled below its line. Filling below the
   * line transforms a line chart into an area chart.
   * 
   * @param fill the fill below line flag value
   * 
   * @deprecated Use {@link #setFillOutsideLine(FillOutsideLine)} instead.
   */
  @Deprecated
  public void setFillBelowLine(boolean fill) {
    mFillBelowLine.clear();
    if (fill) {
      mFillBelowLine.add(FillOutsideLine.BOUNDS_ALL);
    } else {
      mFillBelowLine.add(FillOutsideLine.NONE);
    }
  }

  /**
   * Returns the type of the outside fill of the line.
   * 
   * @return the type of the outside fill of the line.
   */
  public FillOutsideLine[] getFillOutsideLine() {
    return mFillBelowLine.toArray(new FillOutsideLine[0]);
  }

  /**
   * Sets if the line chart should be filled outside its line. Filling outside
   * with FillOutsideLine.INTEGRAL the line transforms a line chart into an area
   * chart.
   * 
   * @param the type of the filling
   */
  public void addFillOutsideLine(FillOutsideLine fill) {
    mFillBelowLine.add(fill);
  }

  /**
   * Returns if the chart points should be filled.
   * 
   * @return the points fill status
   */
  public boolean isFillPoints() {
    return mFillPoints;
  }

  /**
   * Sets if the chart points should be filled.
   * 
   * @param fill the points fill flag value
   */
  public void setFillPoints(boolean fill) {
    mFillPoints = fill;
  }

  /**
   * Sets the fill below the line color.
   * 
   * @param color the fill below line color
   * @deprecated Use FillOutsideLine.setColor instead
   */
  public void setFillBelowLineColor(int color) {
    if (mFillBelowLine.size() > 0) {
      mFillBelowLine.get(0).setColor(color);
    }
  }

  /**
   * Returns the point style.
   * 
   * @return the point style
   */
  public PointStyle getPointStyle() {
    return mPointStyle;
  }

  /**
   * Sets the point style.
   * 
   * @param style the point style
   */
  public void setPointStyle(PointStyle style) {
    mPointStyle = style;
  }

  /**
   * Returns the chart line width.
   * 
   * @return the line width
   */
  public float getLineWidth() {
    return mLineWidth;
  }

  /**
   * Sets the chart line width.
   * 
   * @param lineWidth the line width
   */
  public void setLineWidth(float lineWidth) {
    mLineWidth = lineWidth;
  }

}
