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

import java.util.HashMap;
import java.util.Map;

import org.achartengine.util.MathHelper;

/**
 * Multiple XY series renderer.
 */
public class XYMultipleSeriesRenderer extends DefaultRenderer {
  /** The chart title. */
  private String mChartTitle = "";
  /** The chart title text size. */
  private float mChartTitleTextSize = 15;
  /** The X axis title. */
  private String mXTitle = "";
  /** The Y axis title. */
  private String mYTitle = "";
  /** The axis title text size. */
  private float mAxisTitleTextSize = 12;
  /** The start value in the X axis range. */
  private double mMinX = MathHelper.NULL_VALUE;
  /** The end value in the X axis range. */
  private double mMaxX = -MathHelper.NULL_VALUE;
  /** The start value in the Y axis range. */
  private double mMinY = MathHelper.NULL_VALUE;
  /** The end value in the Y axis range. */
  private double mMaxY = -MathHelper.NULL_VALUE;

  /** The approximative number of labels on the x axis. */
  private int mXLabels = 5;
  /** The approximative number of labels on the y axis. */
  private int mYLabels = 5;
  /** The current orientation of the chart. */
  private Orientation mOrientation = Orientation.HORIZONTAL;
  /** The X axis text labels. */
  private Map<Double, String> mXTextLabels = new HashMap<Double, String>();
  /** If the values should be displayed above the chart points. */
  private boolean mDisplayChartValues;
  /** The chart values text size. */
  private float mChartValuesTextSize = 9;
  /** A flag for enabling or not the pan. */
  private boolean panEnabled = true;
  /** A flag for enabling or not the zoom. */
  private boolean zoomEnabled = true;
  /** The zoom rate. */
  private float zoomRate = 1.5f;

  /**
   * An enum for the XY chart orientation of the X axis.
   */
  public enum Orientation {
    HORIZONTAL(0), VERTICAL(90);
    /** The rotate angle. */
    private int mAngle = 0;

    private Orientation(int angle) {
      mAngle = angle;
    }

    /**
     * Return the orientation rotate angle.
     * 
     * @return the orientaion rotate angle
     */
    public int getAngle() {
      return mAngle;
    }
  }

  /**
   * Returns the current orientation of the chart X axis.
   * 
   * @return the chart orientation
   */
  public Orientation getOrientation() {
    return mOrientation;
  }

  /**
   * Sets the current orientation of the chart X axis.
   * 
   * @param orientation the chart orientation
   */
  public void setOrientation(Orientation orientation) {
    mOrientation = orientation;
  }

  /**
   * Returns the chart title.
   * 
   * @return the chart title
   */
  public String getChartTitle() {
    return mChartTitle;
  }

  /**
   * Sets the chart title.
   * 
   * @param title the chart title
   */
  public void setChartTitle(String title) {
    mChartTitle = title;
  }

  /**
   * Returns the chart title text size.
   * 
   * @return the chart title text size
   */
  public float getChartTitleTextSize() {
    return mChartTitleTextSize;
  }

  /**
   * Sets the chart title text size.
   * 
   * @param textSize the chart title text size
   */
  public void setChartTitleTextSize(float textSize) {
    mChartTitleTextSize = textSize;
  }

  /**
   * Returns the title for the X axis.
   * 
   * @return the X axis title
   */
  public String getXTitle() {
    return mXTitle;
  }

  /**
   * Sets the title for the X axis.
   * 
   * @param title the X axis title
   */
  public void setXTitle(String title) {
    mXTitle = title;
  }

  /**
   * Returns the title for the Y axis.
   * 
   * @return the Y axis title
   */
  public String getYTitle() {
    return mYTitle;
  }

  /**
   * Sets the title for the Y axis.
   * 
   * @param title the Y axis title
   */
  public void setYTitle(String title) {
    mYTitle = title;
  }

  /**
   * Returns the axis title text size.
   * 
   * @return the axis title text size
   */
  public float getAxisTitleTextSize() {
    return mAxisTitleTextSize;
  }

  /**
   * Sets the axis title text size.
   * 
   * @param textSize the chart axis text size
   */
  public void setAxisTitleTextSize(float textSize) {
    mAxisTitleTextSize = textSize;
  }

  /**
   * Returns the start value of the X axis range.
   * 
   * @return the X axis range start value
   */
  public double getXAxisMin() {
    return mMinX;
  }

  /**
   * Sets the start value of the X axis range.
   * 
   * @param min the X axis range start value
   */
  public void setXAxisMin(double min) {
    mMinX = min;
  }

  /**
   * Returns if the minimum X value was set.
   * 
   * @return the minX was set or not
   */
  public boolean isMinXSet() {
    return mMinX != MathHelper.NULL_VALUE;
  }

  /**
   * Returns the end value of the X axis range.
   * 
   * @return the X axis range end value
   */
  public double getXAxisMax() {
    return mMaxX;
  }

  /**
   * Sets the end value of the X axis range.
   * 
   * @param max the X axis range end value
   */
  public void setXAxisMax(double max) {
    mMaxX = max;
  }

  /**
   * Returns if the maximum X value was set.
   * 
   * @return the maxX was set or not
   */
  public boolean isMaxXSet() {
    return mMaxX != -MathHelper.NULL_VALUE;
  }

  /**
   * Returns the start value of the Y axis range.
   * 
   * @return the Y axis range end value
   */
  public double getYAxisMin() {
    return mMinY;
  }

  /**
   * Sets the start value of the Y axis range.
   * 
   * @param min the Y axis range start value
   */
  public void setYAxisMin(double min) {
    mMinY = min;
  }

  /**
   * Returns if the minimum Y value was set.
   * 
   * @return the minY was set or not
   */
  public boolean isMinYSet() {
    return mMinY != MathHelper.NULL_VALUE;
  }

  /**
   * Returns the end value of the Y axis range.
   * 
   * @return the Y axis range end value
   */
  public double getYAxisMax() {
    return mMaxY;
  }

  /**
   * Sets the end value of the Y axis range.
   * 
   * @param max the Y axis range end value
   */
  public void setYAxisMax(double max) {
    mMaxY = max;
  }

  /**
   * Returns if the maximum Y value was set.
   * 
   * @return the maxY was set or not
   */
  public boolean isMaxYSet() {
    return mMaxY != -MathHelper.NULL_VALUE;
  }

  /**
   * Returns the approximate number of labels for the X axis.
   * 
   * @return the approximate number of labels for the X axis
   */
  public int getXLabels() {
    return mXLabels;
  }

  /**
   * Sets the approximate number of labels for the X axis.
   * 
   * @param xLabels the approximate number of labels for the X axis
   */
  public void setXLabels(int xLabels) {
    mXLabels = xLabels;
  }

  /**
   * Adds a new text label for the specified X axis value.
   * 
   * @param x the X axis value
   * @param text the text label
   */
  public void addTextLabel(double x, String text) {
    mXTextLabels.put(x, text);
  }

  /**
   * Returns the X axis text label at the specified X axis value.
   * 
   * @param x the X axis value
   * @return the X axis text label
   */
  public String getXTextLabel(Double x) {
    return mXTextLabels.get(x);
  }

  /**
   * Returns the X text label locations.
   * 
   * @return the X text label locations
   */
  public Double[] getXTextLabelLocations() {
    return mXTextLabels.keySet().toArray(new Double[0]);
  }

  /**
   * Returns the approximate number of labels for the Y axis.
   * 
   * @return the approximate number of labels for the Y axis
   */
  public int getYLabels() {
    return mYLabels;
  }

  /**
   * Sets the approximate number of labels for the Y axis.
   * 
   * @param yLabels the approximate number of labels for the Y axis
   */
  public void setYLabels(int yLabels) {
    mYLabels = yLabels;
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
   * Returns the enabled state of the pan.
   * 
   * @return if pan is enabled
   */
  public boolean isPanEnabled() {
    return panEnabled;
  }

  /**
   * Sets the enabled state of the pan.
   * 
   * @param enabled pan enabled
   */
  public void setPanEnabled(boolean enabled) {
    panEnabled = enabled;
  }

  /**
   * Returns the enabled state of the zoom.
   * 
   * @return if zoom is enabled
   */
  public boolean isZoomEnabled() {
    return zoomEnabled;
  }

  /**
   * Sets the enabled state of the zoom.
   * 
   * @param enabled zoom enabled
   */
  public void setZoomEnabled(boolean enabled) {
    zoomEnabled = enabled;
  }

  /**
   * Returns the zoom rate.
   * 
   * @return the zoom rate
   */
  public float getZoomRate() {
    return zoomRate;
  }

  /**
   * Sets the zoom rate.
   * 
   * @param rate the zoom rate
   */
  public void setZoomRate(float rate) {
    zoomRate = rate;
  }

}
