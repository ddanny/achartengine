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
package org.achartengine.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;
import org.achartengine.util.MathHelper;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Align;

/**
 * The XY chart rendering class.
 */
public abstract class XYChart extends AbstractChart {
  /** The multiple series dataset. */
  protected XYMultipleSeriesDataset mDataset;
  /** The multiple series renderer. */
  protected XYMultipleSeriesRenderer mRenderer;
  /** The current scale value. */
  private float mScale;
  /** The current translate value. */
  private float mTranslate;
  /** The canvas center point. */
  private PointF mCenter;
  /** The visible chart area, in screen coordinates. */
  private Rect screenR;
  /** The calculated range. */
  private Map<Integer, double[]> calcRange = new HashMap<Integer, double[]>();

  protected XYChart() {
  }

  /**
   * Builds a new XY chart instance.
   * 
   * @param dataset the multiple series dataset
   * @param renderer the multiple series renderer
   */
  public XYChart(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer) {
    mDataset = dataset;
    mRenderer = renderer;
  }

  // TODO: javadoc
  protected void setDatasetRenderer(XYMultipleSeriesDataset dataset,
      XYMultipleSeriesRenderer renderer) {
    mDataset = dataset;
    mRenderer = renderer;
  }

  /**
   * The graphical representation of the XY chart.
   * 
   * @param canvas the canvas to paint to
   * @param x the top left x value of the view to draw to
   * @param y the top left y value of the view to draw to
   * @param width the width of the view to draw to
   * @param height the height of the view to draw to
   * @param paint the paint
   */
  @Override
  public void draw(Canvas canvas, int x, int y, int width, int height, Paint paint) {
    paint.setAntiAlias(mRenderer.isAntialiasing());
    int legendSize = mRenderer.getLegendHeight();

    if (mRenderer.isShowLegend() && legendSize == 0) {
      legendSize = height / 5;
    }
    int[] margins = mRenderer.getMargins();
    int left = x + margins[1];
    int top = y + margins[0];
    int right = x + width - margins[3];
    int sLength = mDataset.getSeriesCount();
    String[] titles = new String[sLength];
    for (int i = 0; i < sLength; i++) {
      titles[i] = mDataset.getSeriesAt(i).getTitle();
    }
    if (mRenderer.isFitLegend() && mRenderer.isShowLegend()) {
      legendSize = drawLegend(canvas, mRenderer, titles, left, right, y, width, height, legendSize,
          paint, true);
    }
    int bottom = y + height - margins[2] - legendSize;
    if (screenR == null) {
      screenR = new Rect();
    }
    screenR.set(left, top, right, bottom);
    drawBackground(mRenderer, canvas, x, y, width, height, paint, false, DefaultRenderer.NO_COLOR);

    if (paint.getTypeface() == null
        || !paint.getTypeface().toString().equals(mRenderer.getTextTypefaceName())
        || paint.getTypeface().getStyle() != mRenderer.getTextTypefaceStyle()) {
      paint.setTypeface(Typeface.create(mRenderer.getTextTypefaceName(), mRenderer
          .getTextTypefaceStyle()));
    }
    Orientation or = mRenderer.getOrientation();
    if (or == Orientation.VERTICAL) {
      right -= legendSize;
      bottom += legendSize - 20;
    }
    int angle = or.getAngle();
    boolean rotate = angle == 90;
    mScale = (float) (height) / width;
    mTranslate = Math.abs(width - height) / 2;
    if (mScale < 1) {
      mTranslate *= -1;
    }
    mCenter = new PointF((x + width) / 2, (y + height) / 2);
    if (rotate) {
      transform(canvas, angle, false);
    }

    int maxScaleNumber = -Integer.MAX_VALUE;
    for (int i = 0; i < sLength; i++) {
      maxScaleNumber = Math.max(maxScaleNumber, mDataset.getSeriesAt(i).getScaleNumber());
    }
    maxScaleNumber++;
    if (maxScaleNumber < 0) {
      return;
    }
    double[] minX = new double[maxScaleNumber];
    double[] maxX = new double[maxScaleNumber];
    double[] minY = new double[maxScaleNumber];
    double[] maxY = new double[maxScaleNumber];
    boolean[] isMinXSet = new boolean[maxScaleNumber];
    boolean[] isMaxXSet = new boolean[maxScaleNumber];
    boolean[] isMinYSet = new boolean[maxScaleNumber];
    boolean[] isMaxYSet = new boolean[maxScaleNumber];

    for (int i = 0; i < maxScaleNumber; i++) {
      minX[i] = mRenderer.getXAxisMin(i);
      maxX[i] = mRenderer.getXAxisMax(i);
      minY[i] = mRenderer.getYAxisMin(i);
      maxY[i] = mRenderer.getYAxisMax(i);
      isMinXSet[i] = mRenderer.isMinXSet(i);
      isMaxXSet[i] = mRenderer.isMaxXSet(i);
      isMinYSet[i] = mRenderer.isMinYSet(i);
      isMaxYSet[i] = mRenderer.isMaxYSet(i);
      if (calcRange.get(i) == null) {
        calcRange.put(i, new double[4]);
      }
    }
    double[] xPixelsPerUnit = new double[maxScaleNumber];
    double[] yPixelsPerUnit = new double[maxScaleNumber];
    for (int i = 0; i < sLength; i++) {
      XYSeries series = mDataset.getSeriesAt(i);
      int scale = series.getScaleNumber();
      if (series.getItemCount() == 0) {
        continue;
      }
      if (!isMinXSet[scale]) {
        double minimumX = series.getMinX();
        minX[scale] = Math.min(minX[scale], minimumX);
        calcRange.get(scale)[0] = minX[scale];
      }
      if (!isMaxXSet[scale]) {
        double maximumX = series.getMaxX();
        maxX[scale] = Math.max(maxX[scale], maximumX);
        calcRange.get(scale)[1] = maxX[scale];
      }
      if (!isMinYSet[scale]) {
        double minimumY = series.getMinY();
        minY[scale] = Math.min(minY[scale], (float) minimumY);
        calcRange.get(scale)[2] = minY[scale];
      }
      if (!isMaxYSet[scale]) {
        double maximumY = series.getMaxY();
        maxY[scale] = Math.max(maxY[scale], (float) maximumY);
        calcRange.get(scale)[3] = maxY[scale];
      }
    }
    for (int i = 0; i < maxScaleNumber; i++) {
      if (maxX[i] - minX[i] != 0) {
        xPixelsPerUnit[i] = (right - left) / (maxX[i] - minX[i]);
      }
      if (maxY[i] - minY[i] != 0) {
        yPixelsPerUnit[i] = (float) ((bottom - top) / (maxY[i] - minY[i]));
      }
    }

    boolean hasValues = false;
    for (int i = 0; i < sLength; i++) {
      XYSeries series = mDataset.getSeriesAt(i);
      int scale = series.getScaleNumber();
      if (series.getItemCount() == 0) {
        continue;
      }
      hasValues = true;
      SimpleSeriesRenderer seriesRenderer = mRenderer.getSeriesRendererAt(i);
      int originalValuesLength = series.getItemCount();
      int valuesLength = originalValuesLength;
      int length = valuesLength * 2;
      List<Float> points = new ArrayList<Float>();
      for (int j = 0; j < length; j += 2) {
        int index = j / 2;
        double yValue = series.getY(index);
        if (yValue != MathHelper.NULL_VALUE) {
          points.add((float) (left + xPixelsPerUnit[scale] * (series.getX(index) - minX[scale])));
          points.add((float) (bottom - yPixelsPerUnit[scale] * (yValue - minY[scale])));
        } else {
          if (points.size() > 0) {
            drawSeries(series, canvas, paint, points, seriesRenderer, Math.min(bottom,
                (float) (bottom + yPixelsPerUnit[scale] * minY[scale])), i, or);
            points.clear();
          }
        }
      }
      if (points.size() > 0) {
        drawSeries(series, canvas, paint, points, seriesRenderer, Math.min(bottom,
            (float) (bottom + yPixelsPerUnit[scale] * minY[scale])), i, or);
      }
    }

    // draw stuff over the margins such as data doesn't render on these areas
    drawBackground(mRenderer, canvas, x, bottom, width, height - bottom, paint, true, mRenderer
        .getMarginsColor());
    drawBackground(mRenderer, canvas, x, y, width, margins[0], paint, true, mRenderer
        .getMarginsColor());
    if (or == Orientation.HORIZONTAL) {
      drawBackground(mRenderer, canvas, x, y, left - x, height - y, paint, true, mRenderer
          .getMarginsColor());
      drawBackground(mRenderer, canvas, right, y, margins[3], height - y, paint, true, mRenderer
          .getMarginsColor());
    } else if (or == Orientation.VERTICAL) {
      drawBackground(mRenderer, canvas, right, y, width - right, height - y, paint, true, mRenderer
          .getMarginsColor());
      drawBackground(mRenderer, canvas, x, y, left - x, height - y, paint, true, mRenderer
          .getMarginsColor());
    }

    boolean showLabels = mRenderer.isShowLabels() && hasValues;
    boolean showGrid = mRenderer.isShowGrid();
    boolean showCustomTextGrid = mRenderer.isShowCustomTextGrid();
    if (showLabels || showGrid) {
      List<Double> xLabels = getValidLabels(MathHelper.getLabels(minX[0], maxX[0], mRenderer
          .getXLabels()));
      Map<Integer, List<Double>> allYLabels = new HashMap<Integer, List<Double>>();
      for (int i = 0; i < maxScaleNumber; i++) {
        allYLabels.put(i, getValidLabels(MathHelper.getLabels(minY[i], maxY[i], mRenderer
            .getYLabels())));
      }
      int xLabelsLeft = left;
      if (showLabels) {
        paint.setColor(mRenderer.getLabelsColor());
        paint.setTextSize(mRenderer.getLabelsTextSize());
        paint.setTextAlign(mRenderer.getXLabelsAlign());
        if (mRenderer.getXLabelsAlign() == Align.LEFT) {
          xLabelsLeft += mRenderer.getLabelsTextSize() / 4;
        }
      }
      drawXLabels(xLabels, mRenderer.getXTextLabelLocations(), canvas, paint, xLabelsLeft, top,
          bottom, xPixelsPerUnit[0], minX[0]);

      for (int i = 0; i < maxScaleNumber; i++) {
        paint.setTextAlign(mRenderer.getYLabelsAlign(i));
        List<Double> yLabels = allYLabels.get(i);
        int length = yLabels.size();
        for (int j = 0; j < length; j++) {
          double label = yLabels.get(j);
          Align axisAlign = mRenderer.getYAxisAlign(i);
          boolean textLabel = mRenderer.getYTextLabel(label, i) != null;
          float yLabel = (float) (bottom - yPixelsPerUnit[i] * (label - minY[i]));
          if (or == Orientation.HORIZONTAL) {
            if (showLabels && !textLabel) {
              paint.setColor(mRenderer.getLabelsColor());
              if (axisAlign == Align.LEFT) {
                canvas.drawLine(left + getLabelLinePos(axisAlign), yLabel, left, yLabel, paint);
                drawText(canvas, getLabel(label), left, yLabel - 2, paint, mRenderer
                    .getYLabelsAngle());
              } else {
                canvas.drawLine(right, yLabel, right + getLabelLinePos(axisAlign), yLabel, paint);
                drawText(canvas, getLabel(label), right, yLabel - 2, paint, mRenderer
                    .getYLabelsAngle());
              }
            }
            if (showGrid) {
              paint.setColor(mRenderer.getGridColor());
              canvas.drawLine(left, yLabel, right, yLabel, paint);
            }
          } else if (or == Orientation.VERTICAL) {
            if (showLabels && !textLabel) {
              paint.setColor(mRenderer.getLabelsColor());
              canvas.drawLine(right - getLabelLinePos(axisAlign), yLabel, right, yLabel, paint);
              drawText(canvas, getLabel(label), right + 10, yLabel - 2, paint, mRenderer
                  .getYLabelsAngle());
            }
            if (showGrid) {
              paint.setColor(mRenderer.getGridColor());
              canvas.drawLine(right, yLabel, left, yLabel, paint);
            }
          }
        }
      }

      if (showLabels) {
        paint.setColor(mRenderer.getLabelsColor());
        for (int i = 0; i < maxScaleNumber; i++) {
          Align axisAlign = mRenderer.getYAxisAlign(i);
          Double[] yTextLabelLocations = mRenderer.getYTextLabelLocations(i);
          for (Double location : yTextLabelLocations) {
            if (minY[i] <= location && location <= maxY[i]) {
              float yLabel = (float) (bottom - yPixelsPerUnit[i]
                  * (location.doubleValue() - minY[i]));
              String label = mRenderer.getYTextLabel(location, i);
              paint.setColor(mRenderer.getLabelsColor());
              if (or == Orientation.HORIZONTAL) {
                if (axisAlign == Align.LEFT) {
                  canvas.drawLine(left + getLabelLinePos(axisAlign), yLabel, left, yLabel, paint);
                  drawText(canvas, label, left, yLabel - 2, paint, mRenderer.getYLabelsAngle());
                } else {
                  canvas.drawLine(right, yLabel, right + getLabelLinePos(axisAlign), yLabel, paint);
                  drawText(canvas, label, right, yLabel - 2, paint, mRenderer.getYLabelsAngle());
                }
                if (showCustomTextGrid) {
                  paint.setColor(mRenderer.getGridColor());
                  canvas.drawLine(left, yLabel, right, yLabel, paint);
                }
              } else {
                canvas.drawLine(right - getLabelLinePos(axisAlign), yLabel, right, yLabel, paint);
                drawText(canvas, label, right + 10, yLabel - 2, paint, mRenderer.getYLabelsAngle());
                if (showCustomTextGrid) {
                  paint.setColor(mRenderer.getGridColor());
                  canvas.drawLine(right, yLabel, left, yLabel, paint);
                }
              }
            }
          }
        }
      }

      if (showLabels) {
        paint.setColor(mRenderer.getLabelsColor());
        float size = mRenderer.getAxisTitleTextSize();
        paint.setTextSize(size);
        paint.setTextAlign(Align.CENTER);
        // TODO: make y title work on both alignment types
        if (or == Orientation.HORIZONTAL) {
          drawText(canvas, mRenderer.getXTitle(), x + width / 2, bottom
              + mRenderer.getLabelsTextSize() * 4 / 3 + size, paint, 0);
          drawText(canvas, mRenderer.getYTitle(), x + size, y + height / 2, paint, -90);
          paint.setTextSize(mRenderer.getChartTitleTextSize());
          drawText(canvas, mRenderer.getChartTitle(), x + width / 2, y
              + mRenderer.getChartTitleTextSize(), paint, 0);
        } else if (or == Orientation.VERTICAL) {
          drawText(canvas, mRenderer.getXTitle(), x + width / 2, y + height - size, paint, -90);
          drawText(canvas, mRenderer.getYTitle(), right + 20, y + height / 2, paint, 0);
          paint.setTextSize(mRenderer.getChartTitleTextSize());
          drawText(canvas, mRenderer.getChartTitle(), x + size, top + height / 2, paint, 0);
        }
      }
    }
    if (or == Orientation.HORIZONTAL) {
      drawLegend(canvas, mRenderer, titles, left, right, y, width, height, legendSize, paint, false);
    } else if (or == Orientation.VERTICAL) {
      transform(canvas, angle, true);
      drawLegend(canvas, mRenderer, titles, left, right, y, width, height, legendSize, paint, false);
      transform(canvas, angle, false);
    }
    if (mRenderer.isShowAxes()) {
      paint.setColor(mRenderer.getAxesColor());
      canvas.drawLine(left, bottom, right, bottom, paint);
      boolean rightAxis = false;
      for (int i = 0; i < maxScaleNumber && !rightAxis; i++) {
        rightAxis = mRenderer.getYAxisAlign(i) == Align.RIGHT;
      }
      if (or == Orientation.HORIZONTAL) {
        canvas.drawLine(left, top, left, bottom, paint);
        if (rightAxis) {
          canvas.drawLine(right, top, right, bottom, paint);
        }
      } else if (or == Orientation.VERTICAL) {
        canvas.drawLine(right, top, right, bottom, paint);
      }
    }
    if (rotate) {
      transform(canvas, angle, true);
    }
  }

  private List<Double> getValidLabels(List<Double> labels) {
    List<Double> result = new ArrayList<Double>(labels);
    for (Double label : labels) {
      if (label.isNaN()) {
        result.remove(label);
      }
    }
    return result;
  }

  protected void drawSeries(XYSeries series, Canvas canvas, Paint paint, List<Float> pointsList,
      SimpleSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex, Orientation or) {
    float[] points = MathHelper.getFloats(pointsList);
    drawSeries(canvas, paint, points, seriesRenderer, yAxisValue, seriesIndex);
    if (isRenderPoints(seriesRenderer)) {
      ScatterChart pointsChart = getPointsChart();
      if (pointsChart != null) {
        pointsChart.drawSeries(canvas, paint, points, seriesRenderer, yAxisValue, seriesIndex);
      }
    }
    paint.setTextSize(mRenderer.getChartValuesTextSize());
    if (or == Orientation.HORIZONTAL) {
      paint.setTextAlign(Align.CENTER);
    } else {
      paint.setTextAlign(Align.LEFT);
    }
    if (mRenderer.isDisplayChartValues()) {
      drawChartValuesText(canvas, series, paint, points, seriesIndex);
    }
  }

  /**
   * The graphical representation of the series values as text.
   * 
   * @param canvas the canvas to paint to
   * @param series the series to be painted
   * @param paint the paint to be used for drawing
   * @param points the array of points to be used for drawing the series
   * @param seriesIndex the index of the series currently being drawn
   */
  protected void drawChartValuesText(Canvas canvas, XYSeries series, Paint paint, float[] points,
      int seriesIndex) {
    for (int k = 0; k < points.length; k += 2) {
      drawText(canvas, getLabel(series.getY(k / 2)), points[k], points[k + 1] - 3.5f, paint, 0);
    }
  }

  /**
   * The graphical representation of a text, to handle both HORIZONTAL and
   * VERTICAL orientations and extra rotation angles.
   * 
   * @param canvas the canvas to paint to
   * @param text the text to be rendered
   * @param x the X axis location of the text
   * @param y the Y axis location of the text
   * @param paint the paint to be used for drawing
   * @param extraAngle the text angle
   */
  protected void drawText(Canvas canvas, String text, float x, float y, Paint paint,
      float extraAngle) {
    float angle = -mRenderer.getOrientation().getAngle() + extraAngle;
    if (angle != 0) {
      // canvas.scale(1 / mScale, mScale);
      canvas.rotate(angle, x, y);
    }
    canvas.drawText(text, x, y, paint);
    if (angle != 0) {
      canvas.rotate(-angle, x, y);
      // canvas.scale(mScale, 1 / mScale);
    }
  }

  /**
   * Transform the canvas such as it can handle both HORIZONTAL and VERTICAL
   * orientations.
   * 
   * @param canvas the canvas to paint to
   * @param angle the angle of rotation
   * @param inverse if the inverse transform needs to be applied
   */
  private void transform(Canvas canvas, float angle, boolean inverse) {
    if (inverse) {
      canvas.scale(1 / mScale, mScale);
      canvas.translate(mTranslate, -mTranslate);
      canvas.rotate(-angle, mCenter.x, mCenter.y);
    } else {
      canvas.rotate(angle, mCenter.x, mCenter.y);
      canvas.translate(-mTranslate, mTranslate);
      canvas.scale(mScale, 1 / mScale);
    }
  }

  /**
   * Makes sure the fraction digit is not displayed, if not needed.
   * 
   * @param label the input label value
   * @return the label without the useless fraction digit
   */
  protected String getLabel(double label) {
    String text = "";
    if (label == Math.round(label)) {
      text = Math.round(label) + "";
    } else {
      text = label + "";
    }
    return text;
  }

  /**
   * The graphical representation of the labels on the X axis.
   * 
   * @param xLabels the X labels values
   * @param xTextLabelLocations the X text label locations
   * @param canvas the canvas to paint to
   * @param paint the paint to be used for drawing
   * @param left the left value of the labels area
   * @param top the top value of the labels area
   * @param bottom the bottom value of the labels area
   * @param xPixelsPerUnit the amount of pixels per one unit in the chart labels
   * @param minX the minimum value on the X axis in the chart
   */
  protected void drawXLabels(List<Double> xLabels, Double[] xTextLabelLocations, Canvas canvas,
      Paint paint, int left, int top, int bottom, double xPixelsPerUnit, double minX) {
    int length = xLabels.size();
    boolean showLabels = mRenderer.isShowLabels();
    boolean showGrid = mRenderer.isShowGrid();
    boolean showCustomTextGrid = mRenderer.isShowCustomTextGrid();
    for (int i = 0; i < length; i++) {
      double label = xLabels.get(i);
      float xLabel = (float) (left + xPixelsPerUnit * (label - minX));
      if (showLabels) {
        paint.setColor(mRenderer.getLabelsColor());
        canvas.drawLine(xLabel, bottom, xLabel, bottom + mRenderer.getLabelsTextSize() / 3, paint);
        drawText(canvas, getLabel(label), xLabel, bottom + mRenderer.getLabelsTextSize() * 4 / 3,
            paint, mRenderer.getXLabelsAngle());
      }
      if (showGrid) {
        paint.setColor(mRenderer.getGridColor());
        canvas.drawLine(xLabel, bottom, xLabel, top, paint);
      }
    }
    if (showLabels) {
      paint.setColor(mRenderer.getLabelsColor());
      for (Double location : xTextLabelLocations) {
        float xLabel = (float) (left + xPixelsPerUnit * (location.doubleValue() - minX));
        paint.setColor(mRenderer.getLabelsColor());
        canvas.drawLine(xLabel, bottom, xLabel, bottom + 4, paint);
        drawText(canvas, mRenderer.getXTextLabel(location), xLabel, bottom
            + mRenderer.getLabelsTextSize(), paint, mRenderer.getXLabelsAngle());
        if (showCustomTextGrid) {
          paint.setColor(mRenderer.getGridColor());
          canvas.drawLine(xLabel, bottom, xLabel, top, paint);
        }
      }
    }
  }

  // TODO: docs
  public XYMultipleSeriesRenderer getRenderer() {
    return mRenderer;
  }

  public XYMultipleSeriesDataset getDataset() {
    return mDataset;
  }

  public double[] getCalcRange(int scale) {
    return calcRange.get(scale);
  }

  public double[] toRealPoint(float screenX, float screenY) {
    return toRealPoint(screenX, screenY, 0);
  }

  public double[] toScreenPoint(double[] realPoint) {
    return toScreenPoint(realPoint, 0);
  }
  
  private int getLabelLinePos(Align align) {
    int pos = 4;
    if (align == Align.LEFT) {
      pos = -pos;
    }
    return pos;
  }

  /**
   * Transforms a screen point to a real coordinates point.
   * 
   * @param screenX the screen x axis value
   * @param screenY the screen y axis value
   * @return the real coordinates point
   */
  public double[] toRealPoint(float screenX, float screenY, int scale) {
    double realMinX = mRenderer.getXAxisMin(scale);
    double realMaxX = mRenderer.getXAxisMax(scale);
    double realMinY = mRenderer.getYAxisMin(scale);
    double realMaxY = mRenderer.getYAxisMax(scale);
    return new double[] {
        (screenX - screenR.left) * (realMaxX - realMinX) / screenR.width() + realMinX,
        (screenR.top + screenR.height() - screenY) * (realMaxY - realMinY) / screenR.height()
            + realMinY };
  }

  public double[] toScreenPoint(double[] realPoint, int scale) {
    double realMinX = mRenderer.getXAxisMin(scale);
    double realMaxX = mRenderer.getXAxisMax(scale);
    double realMinY = mRenderer.getYAxisMin(scale);
    double realMaxY = mRenderer.getYAxisMax(scale);
    return new double[] {
        (realPoint[0] - realMinX) * screenR.width() / (realMaxX - realMinX) + screenR.left,
        (realMaxY - realPoint[1]) * screenR.height() / (realMaxY - realMinY) + screenR.top };
  }

  /**
   * The graphical representation of a series.
   * 
   * @param canvas the canvas to paint to
   * @param paint the paint to be used for drawing
   * @param points the array of points to be used for drawing the series
   * @param seriesRenderer the series renderer
   * @param yAxisValue the minimum value of the y axis
   * @param seriesIndex the index of the series currently being drawn
   */
  public abstract void drawSeries(Canvas canvas, Paint paint, float[] points,
      SimpleSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex);

  /**
   * Returns if the chart should display the points as a certain shape.
   * 
   * @param renderer the series renderer
   */
  public boolean isRenderPoints(SimpleSeriesRenderer renderer) {
    return false;
  }

  /**
   * Returns the default axis minimum.
   * 
   * @return the default axis minimum
   */
  public double getDefaultMinimum() {
    return MathHelper.NULL_VALUE;
  }

  /**
   * Returns the scatter chart to be used for drawing the data points.
   * 
   * @return the data points scatter chart
   */
  public ScatterChart getPointsChart() {
    return null;
  }

  /**
   * Returns the chart type identifier.
   * 
   * @return the chart type
   */
  public abstract String getChartType();
}
