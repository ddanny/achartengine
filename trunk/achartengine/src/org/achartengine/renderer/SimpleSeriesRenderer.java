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

}
