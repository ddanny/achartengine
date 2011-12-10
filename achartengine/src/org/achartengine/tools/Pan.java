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
package org.achartengine.tools;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.chart.AbstractChart;
import org.achartengine.chart.RoundChart;
import org.achartengine.chart.XYChart;

/**
 * The pan tool.
 */
public class Pan extends AbstractTool {
  /** The pan listeners. */
  private List<PanListener> mPanListeners = new ArrayList<PanListener>();

  /**
   * Builds and instance of the pan tool.
   * 
   * @param chart the XY chart
   */
  public Pan(AbstractChart chart) {
    super(chart);
  }

  /**
   * Apply the tool.
   * 
   * @param oldX the previous location on X axis
   * @param oldY the previous location on Y axis
   * @param newX the current location on X axis
   * @param newY the current location on the Y axis
   */
  public void apply(float oldX, float oldY, float newX, float newY) {
    if (mChart instanceof XYChart) {
      int scales = mRenderer.getScalesCount();
      double[] limits = mRenderer.getPanLimits();
      boolean limited = limits != null && limits.length == 4;
      XYChart chart = (XYChart) mChart;
      for (int i = 0; i < scales; i++) {
        double[] range = getRange(i);
        double[] calcRange = chart.getCalcRange(i);
        if (range[0] == range[1] && calcRange[0] == calcRange[1] || range[2] == range[3]
            && calcRange[2] == calcRange[3]) {
          return;
        }
        checkRange(range, i);

        double[] realPoint = chart.toRealPoint(oldX, oldY, i);
        double[] realPoint2 = chart.toRealPoint(newX, newY, i);
        double deltaX = realPoint[0] - realPoint2[0];
        double deltaY = realPoint[1] - realPoint2[1];
        if (mRenderer.isPanXEnabled()) {
          if (limited) {
            boolean notLimitedLeft = limits[0] <= range[0] + deltaX;
            boolean notLimitedRight = limits[1] >= range[1] + deltaX;
            if (notLimitedLeft && notLimitedRight) {
              setXRange(range[0] + deltaX, range[1] + deltaX, i);
            }
          } else {
            setXRange(range[0] + deltaX, range[1] + deltaX, i);
          }
        }
        if (mRenderer.isPanYEnabled()) {
          if (limited) {
            boolean notLimitedBottom = limits[2] <= range[2] + deltaY;
            boolean notLimitedUp = limits[3] < range[3] + deltaY;
            if (notLimitedBottom && !notLimitedUp) {
              setYRange(range[2] + deltaY, range[3] + deltaY, i);
            }
          } else {
            setYRange(range[2] + deltaY, range[3] + deltaY, i);
          }
        }
      }
    } else {
      RoundChart chart = (RoundChart) mChart;
      chart.setCenterX(chart.getCenterX() + (int) (newX - oldX));
      chart.setCenterY(chart.getCenterY() + (int) (newY - oldY));
    }
    notifyPanListeners();
  }

  /**
   * Notify the pan listeners about a pan.
   */
  private synchronized void notifyPanListeners() {
    for (PanListener listener : mPanListeners) {
      listener.panApplied();
    }
  }

  /**
   * Adds a new pan listener.
   * 
   * @param listener pan listener
   */
  public synchronized void addPanListener(PanListener listener) {
    mPanListeners.add(listener);
  }

  /**
   * Removes a pan listener.
   * 
   * @param listener pan listener
   */
  public synchronized void removePanListener(PanListener listener) {
    mPanListeners.add(listener);
  }

}
