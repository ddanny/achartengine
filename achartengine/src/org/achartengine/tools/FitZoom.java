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
import org.achartengine.model.XYSeries;

public class FitZoom extends AbstractTool {

  /**
   * Builds an instance of the fit zoom tool.
   * 
   * @param chart the XY chart
   */
  public FitZoom(XYChart chart) {
    super(chart);
  }

  /**
   * Apply the tool.
   */
  public void apply() {
    if (mChart.getDataset() == null) {
      return;
    }
    if (mRenderer.isInitialRangeSet()) {
      mRenderer.setRange(mRenderer.getInitialRange());
    } else {
      XYSeries[] series = mChart.getDataset().getSeries();
      double[] range = null;
      int length = series.length;
      if (length > 0) {
        range = new double[] { series[0].getMinX(), series[0].getMaxX(), 
            Math.min(mChart.getDefaultMinimum(), series[0].getMinY()), series[0].getMaxY() };
        for (int i = 1; i < length; i++) {
          range[0] = Math.min(range[0], series[i].getMinX());
          range[1] = Math.max(range[1], series[i].getMaxX());
          range[2] = Math.min(range[2], series[i].getMinY());
          range[3] = Math.max(range[3], series[i].getMaxY());
        }
        double marginX = Math.abs(range[1] - range[0]) / 40;
        double marginY = Math.abs(range[3] - range[2]) / 40;
        mRenderer.setRange(new double[] { range[0] - marginX, range[1] + marginX, range[2] - marginY, range[3] + marginY });
      }
    }
  }
}
