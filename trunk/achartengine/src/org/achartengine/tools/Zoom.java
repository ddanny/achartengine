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

import org.achartengine.chart.XYChart;

/**
 * The zoom tool.
 */
public class Zoom extends AbstractTool {
  /** A flag to be used to know if this is a zoom in or out. */
  private boolean mZoomIn;
  /** The zoom rate. */
  private float mZoomRate;

  /**
   * Builds the zoom tool.
   * 
   * @param chart the chart
   * @param in zoom in or out
   * @param rate the zoom rate
   */
  public Zoom(XYChart chart, boolean in, float rate) {
    super(chart);
    mZoomIn = in;
    setZoomRate(rate);
  }

  /**
   * Sets the zoom rate.
   * 
   * @param rate
   */
  public void setZoomRate(float rate) {
    mZoomRate = rate;
  }

  /**
   * Apply the zoom.
   */
  public void apply() {
    int scales = mRenderer.getScalesCount();
    for (int i = 0; i < scales; i++) {
      double[] range = getRange(i);
      checkRange(range, i);
      double[] limits = mRenderer.getZoomLimits();
      boolean limited = limits != null && limits.length == 4;

      double centerX = (range[0] + range[1]) / 2;
      double centerY = (range[2] + range[3]) / 2;
      double newWidth = range[1] - range[0];
      double newHeight = range[3] - range[2];
      if (mZoomIn) {
        if (mRenderer.isZoomXEnabled()) {
          newWidth /= mZoomRate;
        }
        if (mRenderer.isZoomYEnabled()) {
          newHeight /= mZoomRate;
        }
      } else {
        if (mRenderer.isZoomXEnabled()) {
          newWidth *= mZoomRate;
        }
        if (mRenderer.isZoomYEnabled()) {
          newHeight *= mZoomRate;
        }
      }

      if (mRenderer.isZoomXEnabled()) {
        double newXMin = centerX - newWidth / 2;
        double newXMax = centerX + newWidth / 2;
        if (!limited || limits[0] <= newXMin && limits[1] >= newXMax) {
          setXRange(newXMin, newXMax, i);
        }
      }
      if (mRenderer.isZoomYEnabled()) {
        double newYMin = centerY - newHeight / 2;
        double newYMax = centerY + newHeight / 2;
        if (!limited || limits[2] <= newYMin && limits[3] >= newYMax) {
          setYRange(newYMin, newYMax, i);
        }
      }
    }
  }

}
