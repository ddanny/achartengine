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
package org.achartengine.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.util.MathHelper;


/**
 * An XY series encapsulates values for XY charts like line, time, area,
 * scatter... charts.
 */
public class XYSeries implements Serializable {
  /** The series title. */
  private String mTitle;
  /** A list to contain the values for the X axis. */
  private List<Number> mX = new ArrayList<Number>();
  /** A list to contain the values for the Y axis. */
  private List<Number> mY = new ArrayList<Number>();
  /** The minimum value for the X axis. */
  private Number mMinX = MathHelper.NULL_VALUE;
  /** The maximum value for the X axis. */
  private Number mMaxX = -MathHelper.NULL_VALUE;
  /** The minimum value for the Y axis. */
  private Number mMinY = MathHelper.NULL_VALUE;
  /** The maximum value for the Y axis. */
  private Number mMaxY = -MathHelper.NULL_VALUE;

  /**
   * Builds a new XY series.
   * 
   * @param title the series title.
   */
  public XYSeries(String title) {
    mTitle = title;
    initRange();
  }
  
  /**
   * Initializes the range for both axes.
   */
  private void initRange() {
    mMinX = MathHelper.NULL_VALUE;
    mMaxX = -MathHelper.NULL_VALUE;
    mMinY = MathHelper.NULL_VALUE;
    mMaxY = -MathHelper.NULL_VALUE;
    int length = getItemCount();
    for (int k = 0; k < length; k++) {
      Number x = getX(k);
      Number y = getY(k);
      updateRange(x, y);
    }
  }
  
  /**
   * Updates the range on both axes.
   * @param x the new x value
   * @param y the new y value
   */
  private void updateRange(Number x, Number y) {
      mMinX = Math.min(mMinX.doubleValue(), x.doubleValue());
      mMaxX = Math.max(mMaxX.doubleValue(), x.doubleValue());
      mMinY = Math.min(mMinY.doubleValue(), y.doubleValue());
      mMaxY = Math.max(mMaxY.doubleValue(), y.doubleValue());
  }

  /**
   * Returns the series title.
   * 
   * @return the series title
   */
  public String getTitle() {
    return mTitle;
  }

  /**
   * Sets the series title.
   * 
   * @param title the series title
   */
  public void setTitle(String title) {
    mTitle = title;
  }

  /**
   * Adds a new value to the series.
   * 
   * @param x the value for the X axis
   * @param y the value for the Y axis
   */
  public void add(Number x, Number y) {
    mX.add(x);
    mY.add(y);
    updateRange(x, y);
  }
  

  /**
   * Removes an existing value from the series.
   * @param index the index in the series of the value to remove
   */
  public void remove(int index) {
    Number removedX = mX.remove(index);
    Number removedY = mY.remove(index);
    if (removedX == mMinX || removedX == mMaxX || removedY == mMinY || removedY == mMaxY) {
      initRange();
    }
  }
  
  /**
   * Removes all the existing values from the series.
   */
  public void clear() {
    mX.clear();
    mY.clear();
    initRange();
  }
  
  /**
   * Returns the X axis value at the specified index.
   * 
   * @param index the index
   * @return the X value
   */
  public Number getX(int index) {
    return mX.get(index);
  }

  /**
   * Returns the Y axis value at the specified index.
   * 
   * @param index the index
   * @return the Y value
   */
  public Number getY(int index) {
    return mY.get(index);
  }

  /**
   * Returns the series item count.
   * @return the series item count
   */
  public int getItemCount() {
    return mX.size();
  }

  /**
   * Returns the minimum value on the X axis.
   * @return the X axis minimum value
   */
  public Number getMinX() {
    return mMinX;
  }

  /**
   * Returns the minimum value on the Y axis.
   * @return the Y axis minimum value
   */
  public Number getMinY() {
    return mMinY;
  }

  /**
   * Returns the maximum value on the X axis.
   * @return the X axis maximum value
   */
  public Number getMaxX() {
    return mMaxX;
  }

  /**
   * Returns the maximum value on the Y axis.
   * @return the Y axis maximum value
   */
  public Number getMaxY() {
    return mMaxY;
  }
}
