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
import org.achartengine.renderer.XYMultipleSeriesRenderer;

/**
 * Abstract class for being extended by graphical view tools.
 */
public abstract class AbstractTool {
  protected XYChart mChart;

  protected XYMultipleSeriesRenderer mRenderer;

  // TODO: javadoc
  public AbstractTool(XYChart chart, XYMultipleSeriesRenderer renderer) {
    mChart = chart;
    mRenderer = renderer;
  }

  public double[] getRange() {
    double minX = mRenderer.getXAxisMin();
    double maxX = mRenderer.getXAxisMax();
    double minY = mRenderer.getYAxisMin();
    double maxY = mRenderer.getYAxisMax();
    return new double[] { minX, maxX, minY, maxY };
  }

  public void checkRange(double[] range) {
    double[] calcRange = mChart.getCalcRange();
    if (!mRenderer.isMinXSet()) {
      range[0] = calcRange[0];
      mRenderer.setXAxisMin(range[0]);
    }
    if (!mRenderer.isMaxXSet()) {
      range[1] = calcRange[1];
      mRenderer.setXAxisMax(range[1]);
    }
    if (!mRenderer.isMinYSet()) {
      range[2] = calcRange[2];
      mRenderer.setYAxisMin(range[2]);
    }
    if (!mRenderer.isMaxYSet()) {
      range[3] = calcRange[3];
      mRenderer.setYAxisMax(range[3]);
    }
  }
}
