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
package org.achartengine.renderer;

import java.io.Serializable;

import android.graphics.Color;

/**
 * A simple series renderer.
 */
public class SimpleSeriesRenderer implements Serializable {
  /** The series color. */
  private int mColor = Color.BLUE;
  /** If the values should be displayed above the chart points. */
  private boolean mDisplayChartValues;
  /** The chart values text size. */
  private float mChartValuesTextSize = 10;
  /** The stroke style. */
  private BasicStroke mStroke;

  /**
   * Returns the series color.
   * 
   * @return the series color
   */
  public int getColor() {
    return mColor;
  }

  /**
   * Sets the series color.
   * 
   * @param color the series color
   */
  public void setColor(int color) {
    mColor = color;
  }

  /**
   * Returns if the chart point values should be displayed as text.
   * 
   * @return if the chart point values should be displayed as text
   */
  public boolean isDisplayChartValues() {
    return mDisplayChartValues;
  }

  /**
   * Sets if the chart point values should be displayed as text.
   * 
   * @param display if the chart point values should be displayed as text
   */
  public void setDisplayChartValues(boolean display) {
    mDisplayChartValues = display;
  }

  /**
   * Returns the chart values text size.
   * 
   * @return the chart values text size
   */
  public float getChartValuesTextSize() {
    return mChartValuesTextSize;
  }

  /**
   * Sets the chart values text size.
   * 
   * @param textSize the chart values text size
   */
  public void setChartValuesTextSize(float textSize) {
    mChartValuesTextSize = textSize;
  }

  /**
   * Returns the stroke style.
   * 
   * @return the stroke style
   */
  public BasicStroke getStroke() {
    return mStroke;
  }

  /**
   * Sets the stroke style.
   * 
   * @param stroke the stroke style
   */
  public void setStroke(BasicStroke stroke) {
    mStroke = stroke;
  }

}
