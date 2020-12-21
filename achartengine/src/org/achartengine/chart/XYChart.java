/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
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

import android.graphics.*;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;

import org.achartengine.model.Point;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.util.MathHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

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
  private Point mCenter;
  /** The visible chart area, in screen coordinates. */
  private Rect mScreenR;
  /** The calculated range. */
  private final Map<Integer, double[]> mCalcRange = new HashMap<Integer, double[]>();
  /** The paint to be used when drawing the grid lines. */
  protected transient Paint mGridPaint;

  /**
   * The clickable areas for all points. The array index is the series index,
   * and the RectF list index is the point index in that series.
   */
  private Map<Integer, List<ClickableArea>> clickableAreas = new HashMap<Integer, List<ClickableArea>>();

  {
    mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mGridPaint.setStyle(Style.STROKE);
  }

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
  public void draw(Canvas canvas, int x, int y, int width, int height, Paint paint) {
    paint.setAntiAlias(mRenderer.isAntialiasing());
    int legendSize = getLegendSize(mRenderer, height / 5, mRenderer.getAxisTitleTextSize());
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
    if (mScreenR == null) {
      mScreenR = new Rect();
    }
    mScreenR.set(left, top, right, bottom);
    drawBackground(mRenderer, canvas, x, y, width, height, paint, false, DefaultRenderer.NO_COLOR);

    if (paint.getTypeface() == null
            || (mRenderer.getTextTypeface() != null && paint.getTypeface().equals(
            mRenderer.getTextTypeface()))
            || !paint.getTypeface().toString().equals(mRenderer.getTextTypefaceName())
            || paint.getTypeface().getStyle() != mRenderer.getTextTypefaceStyle()) {
      if (mRenderer.getTextTypeface() != null) {
        paint.setTypeface(mRenderer.getTextTypeface());
      } else {
        paint.setTypeface(Typeface.create(mRenderer.getTextTypefaceName(),
                mRenderer.getTextTypefaceStyle()));
      }
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
    mCenter = new Point((x + width) / 2, (y + height) / 2);
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
      if (mCalcRange.get(i) == null) {
        mCalcRange.put(i, new double[4]);
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
        mCalcRange.get(scale)[0] = minX[scale];
      }
      if (!isMaxXSet[scale]) {
        double maximumX = series.getMaxX();
        maxX[scale] = Math.max(maxX[scale], maximumX);
        mCalcRange.get(scale)[1] = maxX[scale];
      }
      if (!isMinYSet[scale]) {
        double minimumY = series.getMinY();
        minY[scale] = Math.min(minY[scale], (float) minimumY);
        mCalcRange.get(scale)[2] = minY[scale];
      }
      if (!isMaxYSet[scale]) {
        double maximumY = series.getMaxY();
        maxY[scale] = Math.max(maxY[scale], (float) maximumY);
        mCalcRange.get(scale)[3] = maxY[scale];
      }
    }
    for (int i = 0; i < maxScaleNumber; i++) {
      if (maxX[i] - minX[i] != 0) {
        xPixelsPerUnit[i] = (right - left) / (maxX[i] - minX[i]);
      }
      if (maxY[i] - minY[i] != 0) {
        yPixelsPerUnit[i] = (float) ((bottom - top) / (maxY[i] - minY[i]));
      }
      // the X axis on multiple scales was wrong without this fix
      if (i > 0) {
        xPixelsPerUnit[i] = xPixelsPerUnit[0];
        minX[i] = minX[0];
        maxX[i] = maxX[0];
      }
    }

    boolean showGridX = mRenderer.isShowGridX();
    boolean showGridY = mRenderer.isShowGridY();
    boolean showCustomTextXLabels = mRenderer.isShowCustomTextXLabels();
    boolean showCustomTextYLabels = mRenderer.isShowCustomTextYLabels();

    if (showGridX || showGridY) {
      // Draw the grid lines under everything else.
      List<Double> xLabels = getValidLabels(getXLabels(minX[0], maxX[0], mRenderer.getXLabels()));
      Map<Integer, List<Double>> allYLabels = getYLabels(minY, maxY, maxScaleNumber);

      boolean showXLabels = mRenderer.isShowXLabels();
      boolean showYLabels = mRenderer.isShowYLabels();
      // Only draw the grid.
      mRenderer.setShowLabels(false);
      mRenderer.setShowCustomTextLabels(false);

      drawXLabels(xLabels, mRenderer.getXTextLabelLocations(), canvas, paint, left, top,
              bottom, xPixelsPerUnit[0], minX[0], maxX[0]);
      drawYLabels(allYLabels, canvas, paint, maxScaleNumber, left, right, bottom, yPixelsPerUnit, minY);

      mRenderer.setShowLabels(showXLabels, showYLabels);
      mRenderer.setShowCustomTextLabels(showCustomTextXLabels, showCustomTextYLabels);
    }

    // use a linked list for these reasons:
    // 1) Avoid a large contiguous memory allocation
    // 2) We don't need random seeking, only sequential reading/writing, so
    // linked list makes sense
    clickableAreas = new HashMap<Integer, List<ClickableArea>>();
    for (int i = 0; i < sLength; i++) {
      XYSeries series = mDataset.getSeriesAt(i);
      int scale = series.getScaleNumber();
      if (series.getItemCount() == 0) {
        continue;
      }
      XYSeriesRenderer seriesRenderer = (XYSeriesRenderer) mRenderer.getSeriesRendererAt(i);

      // int originalValuesLength = series.getItemCount();
      // int valuesLength = originalValuesLength;
      // int length = valuesLength * 2;

      List<Float> points = new ArrayList<Float>();
      List<Double> values = new ArrayList<Double>();
      float yAxisValue = Math.min(bottom, (float) (bottom + yPixelsPerUnit[scale] * minY[scale]));
      LinkedList<ClickableArea> clickableArea = new LinkedList<ClickableArea>();

      clickableAreas.put(i, clickableArea);

      synchronized (series) {
        SortedMap<Double, Double> range = series.getRange(minX[scale], maxX[scale],
                seriesRenderer.isDisplayBoundingPoints());
        int startIndex = -1;

        for (Entry<Double, Double> value : range.entrySet()) {
          double xValue = value.getKey();
          double yValue = value.getValue();
          if (startIndex < 0 && (!isNullValue(yValue) || isRenderNullValues())) {
            startIndex = series.getIndexForKey(xValue);
          }

          // points.add((float) (left + xPixelsPerUnit[scale]
          // * (value.getKey().floatValue() - minX[scale])));
          // points.add((float) (bottom - yPixelsPerUnit[scale]
          // * (value.getValue().floatValue() - minY[scale])));
          values.add(value.getKey());
          values.add(value.getValue());

          if (!isNullValue(yValue)) {
            points.add((float) (left + xPixelsPerUnit[scale] * (xValue - minX[scale])));
            points.add((float) (bottom - yPixelsPerUnit[scale] * (yValue - minY[scale])));
          } else if (isRenderNullValues()) {
            points.add((float) (left + xPixelsPerUnit[scale] * (xValue - minX[scale])));
            points.add((float) (bottom - yPixelsPerUnit[scale] * (-minY[scale])));
          } else {
            if (points.size() > 0) {
              drawSeries(series, canvas, paint, points, seriesRenderer, yAxisValue, i, or,
                      startIndex);
              ClickableArea[] clickableAreasForSubSeries = clickableAreasForPoints(points, values,
                      yAxisValue, i, startIndex);
              clickableArea.addAll(Arrays.asList(clickableAreasForSubSeries));
              points.clear();
              values.clear();
              startIndex = -1;
            }
            clickableArea.add(null);
          }
        }

        int count = series.getAnnotationCount();
        if (count > 0) {
          paint.setColor(seriesRenderer.getAnnotationsColor());
          paint.setTextSize(seriesRenderer.getAnnotationsTextSize());
          paint.setTextAlign(seriesRenderer.getAnnotationsTextAlign());
          Rect bound = new Rect();
          for (int j = 0; j < count; j++) {
            float xS = (float) (left + xPixelsPerUnit[scale]
                    * (series.getAnnotationX(j) - minX[scale]));
            float yS = (float) (bottom - yPixelsPerUnit[scale]
                    * (series.getAnnotationY(j) - minY[scale]));
            paint.getTextBounds(series.getAnnotationAt(j), 0, series.getAnnotationAt(j).length(),
                    bound);
            if (xS < (xS + bound.width()) && yS < canvas.getHeight()) {
              drawString(canvas, series.getAnnotationAt(j), xS, yS, paint);
            }
          }
        }

        if (points.size() > 0) {
          drawSeries(series, canvas, paint, points, seriesRenderer, yAxisValue, i, or, startIndex);
          ClickableArea[] clickableAreasForSubSeries = clickableAreasForPoints(points, values,
                  yAxisValue, i, startIndex);
          clickableArea.addAll(Arrays.asList(clickableAreasForSubSeries));
        }
      }
    }
    // draw stuff over the margins so that data doesn't render on these areas
    drawBackground(mRenderer, canvas, x, bottom, width, height - bottom, paint, true,
            mRenderer.getMarginsColor());
    drawBackground(mRenderer, canvas, x, y, width, margins[0], paint, true,
            mRenderer.getMarginsColor());
    if (or == Orientation.HORIZONTAL) {
      drawBackground(mRenderer, canvas, x, y, left - x, height - y, paint, true,
              mRenderer.getMarginsColor());
      drawBackground(mRenderer, canvas, right, y, margins[3], height - y, paint, true,
              mRenderer.getMarginsColor());
    } else if (or == Orientation.VERTICAL) {
      drawBackground(mRenderer, canvas, right, y, width - right, height - y, paint, true,
              mRenderer.getMarginsColor());
      drawBackground(mRenderer, canvas, x, y, left - x, height - y, paint, true,
              mRenderer.getMarginsColor());
    }

    boolean hasValues = false;
    for (int i = 0; i < sLength; i++) {
      XYSeries series = mDataset.getSeriesAt(i);
      if (series.getItemCount() == 0) {
        continue;
      }
      hasValues = true;
    }

    boolean showXLabels = mRenderer.isShowXLabels() && hasValues;
    boolean showYLabels = mRenderer.isShowYLabels() && hasValues;

    boolean showLabels = mRenderer.isShowLabels() && hasValues;
    boolean showTickMarks = mRenderer.isShowTickMarks();
    // boolean showCustomTextGridX = mRenderer.isShowCustomTextGridX();
    boolean showCustomTextGridY = mRenderer.isShowCustomTextGridY();

    if (showXLabels) {
      List<Double> xLabels = getValidLabels(getXLabels(minX[0], maxX[0], mRenderer.getXLabels()));

      paint.setColor(mRenderer.getXLabelsColor());
      paint.setTextSize(mRenderer.getLabelsTextSize());
      paint.setTextAlign(mRenderer.getXLabelsAlign());

      // Draw just the labels and not the grid lines.
      mRenderer.setShowGrid(false);
      drawXLabels(xLabels, mRenderer.getXTextLabelLocations(), canvas, paint, left, top,
              bottom, xPixelsPerUnit[0], minX[0], maxX[0]);
      mRenderer.setShowGridX(showGridX);

      paint.setColor(mRenderer.getLabelsColor());
      float size = mRenderer.getAxisTitleTextSize();
      paint.setTextSize(size);
      paint.setTextAlign(Align.CENTER);
      if (or == Orientation.HORIZONTAL) {
        drawText(canvas,
                mRenderer.getXTitle(),
                x + width / 2,
                bottom + mRenderer.getLabelsTextSize() * 4 / 3 + mRenderer.getXLabelsPadding() + size,
                paint, 0);

      } else if (or == Orientation.VERTICAL) {
        drawText(canvas, mRenderer.getXTitle(), x + width / 2,
                y + height - size + mRenderer.getXLabelsPadding(), paint, -90);
      }
    }

    if (showCustomTextXLabels) {
        drawXTextLabels(mRenderer.getXTextLabelLocations(), canvas, paint, true, left, top, bottom, xPixelsPerUnit[0], minX[0], maxX[0]);
    }

    if (showYLabels) {
      Map<Integer, List<Double>> allYLabels = getYLabels(minY, maxY, maxScaleNumber);

      paint.setColor(mRenderer.getXLabelsColor());
      paint.setTextSize(mRenderer.getLabelsTextSize());
      paint.setTextAlign(mRenderer.getXLabelsAlign());

      // Draw just the labels and not the grid lines.
      mRenderer.setShowGrid(false);
      drawYLabels(allYLabels, canvas, paint, maxScaleNumber, left, right, bottom, yPixelsPerUnit, minY);
      mRenderer.setShowGridY(showGridY);

      paint.setColor(mRenderer.getLabelsColor());
      float size = mRenderer.getAxisTitleTextSize();
      paint.setTextSize(size);
      paint.setTextAlign(Align.CENTER);
      if (or == Orientation.HORIZONTAL) {
        for (int i = 0; i < maxScaleNumber; i++) {
          Align axisAlign = mRenderer.getYAxisAlign(i);
          if (axisAlign == Align.LEFT) {
            drawText(canvas, mRenderer.getYTitle(i), x + size, y + height / 2, paint, -90);
          } else {
            drawText(canvas, mRenderer.getYTitle(i), x + width, y + height / 2, paint, -90);
          }
        }
      } else if (or == Orientation.VERTICAL) {
        drawText(canvas, mRenderer.getYTitle(), right + 20, y + height / 2, paint, 0);
      }
    }

    if (showCustomTextYLabels) {
      paint.setColor(mRenderer.getLabelsColor());
      paint.setTextSize(mRenderer.getLabelsTextSize());
      paint.setTextAlign(mRenderer.getXLabelsAlign());

      for (int i = 0; i < maxScaleNumber; i++) {
        Align axisAlign = mRenderer.getYAxisAlign(i);
        Double[] yTextLabelLocations = mRenderer.getYTextLabelLocations(i);
        for (Double location : yTextLabelLocations) {
          if (minY[i] <= location && location <= maxY[i]) {
            float yLabel = (float) (bottom - yPixelsPerUnit[i]
                    * (location.doubleValue() - minY[i]));
            String label = mRenderer.getYTextLabel(location, i);
            paint.setColor(mRenderer.getYLabelsColor(i));
            paint.setTextAlign(mRenderer.getYLabelsAlign(i));

            if (or == Orientation.HORIZONTAL) {
              if (axisAlign == Align.LEFT) {
                if (showTickMarks) {
                  canvas.drawLine(left + getLabelLinePos(axisAlign), yLabel, left, yLabel, paint);
                }
                drawText(canvas, label, left - mRenderer.getYLabelsPadding(),
                        yLabel - mRenderer.getYLabelsVerticalPadding(), paint,
                        mRenderer.getYLabelsAngle());
              } else {
                if (showTickMarks) {
                  canvas.drawLine(right, yLabel, right + getLabelLinePos(axisAlign), yLabel,
                          paint);
                }
                drawText(canvas, label, right - mRenderer.getYLabelsPadding(),
                        yLabel - mRenderer.getYLabelsVerticalPadding(), paint,
                        mRenderer.getYLabelsAngle());
              }
              if (showCustomTextGridY) {
                setStroke(mRenderer.getGridLineYStroke(), mGridPaint);
                mGridPaint.setStrokeWidth(mRenderer.getGridLineWidth());
                mGridPaint.setColor(mRenderer.getGridColor(i));

                Path gridLinePath = new Path();
                gridLinePath.moveTo(left, yLabel);
                gridLinePath.lineTo(right, yLabel);
                canvas.drawPath(gridLinePath, mGridPaint);

              }
            } else {
              if (showTickMarks) {
                canvas.drawLine(right - getLabelLinePos(axisAlign), yLabel, right, yLabel, paint);
              }
              drawText(canvas, label, right + 10, yLabel - mRenderer.getYLabelsVerticalPadding(),
                      paint, mRenderer.getYLabelsAngle());
              if (showCustomTextGridY) {
                setStroke(mRenderer.getGridLineYStroke(), mGridPaint);
                mGridPaint.setStrokeWidth(mRenderer.getGridLineWidth());
                mGridPaint.setColor(mRenderer.getGridColor(i));

                Path gridLinePath = new Path();
                gridLinePath.moveTo(right, yLabel);
                gridLinePath.lineTo(left, yLabel);
                canvas.drawPath(gridLinePath, mGridPaint);
              }
            }
          }
        }
      }
    }

      if (showLabels) {
          paint.setColor(mRenderer.getLabelsColor());
          float axisTitleTextSize = mRenderer.getAxisTitleTextSize();
          paint.setTextSize(axisTitleTextSize);
          paint.setTextAlign(Align.CENTER);
          paint.setTextSize(mRenderer.getChartTitleTextSize());

          if (or == Orientation.HORIZONTAL) {
              drawText(canvas, mRenderer.getChartTitle(), x + width / 2,
                      y + mRenderer.getChartTitleTextSize(), paint, 0);

          } else if (or == Orientation.VERTICAL) {
              drawText(canvas, mRenderer.getChartTitle(), x + axisTitleTextSize, top + height / 2, paint, 0);
          }
      }


    if (or == Orientation.HORIZONTAL) {
      drawLegend(canvas, mRenderer, titles, left, right, y + (int) mRenderer.getXLabelsPadding(),
              width, height, legendSize, paint, false);
    } else if (or == Orientation.VERTICAL) {
      transform(canvas, angle, true);
      drawLegend(canvas, mRenderer, titles, left, right, y + (int) mRenderer.getXLabelsPadding(),
              width, height, legendSize, paint, false);
      transform(canvas, angle, false);
    }
    if (mRenderer.isShowAxes()) {
      paint.setColor(mRenderer.getXAxisColor());
      canvas.drawLine(left, bottom, right, bottom, paint);
      paint.setColor(mRenderer.getYAxisColor());
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

  protected List<Double> getXLabels(double min, double max, int count) {
    return MathHelper.getLabels(min, max, count);
  }

  protected Map<Integer, List<Double>> getYLabels(double[] minY, double[] maxY, int maxScaleNumber) {
    Map<Integer, List<Double>> allYLabels = new HashMap<Integer, List<Double>>();
    for (int i = 0; i < maxScaleNumber; i++) {
      allYLabels.put(i,
              getValidLabels(MathHelper.getLabels(minY[i], maxY[i], mRenderer.getYLabels())));
    }
    return allYLabels;
  }

  protected Rect getScreenR() {
    return mScreenR;
  }

  protected void setScreenR(Rect screenR) {
    mScreenR = screenR;
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

  /**
   * Draws the series.
   *
   * @param series the series
   * @param canvas the canvas
   * @param paint the paint object
   * @param pointsList the points to be rendered
   * @param seriesRenderer the series renderer
   * @param yAxisValue the y axis value in pixels
   * @param seriesIndex the series index
   * @param or the orientation
   * @param startIndex the start index of the rendering points
   */
  protected void drawSeries(XYSeries series, Canvas canvas, Paint paint, List<Float> pointsList,
                            XYSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex, Orientation or,
                            int startIndex) {

    Paint paintCopy = new Paint(paint);

    BasicStroke stroke = seriesRenderer.getStroke();

    if (stroke != null) {
      setStroke(stroke, paintCopy);
    }
    // float[] points = MathHelper.getFloats(pointsList);
    drawSeries(canvas, paintCopy, pointsList, seriesRenderer, yAxisValue, seriesIndex, startIndex);
    drawPoints(canvas, paintCopy, pointsList, seriesRenderer, yAxisValue, seriesIndex, startIndex);

    paintCopy.setTextSize(seriesRenderer.getChartValuesTextSize());
    if (or == Orientation.HORIZONTAL) {
      paintCopy.setTextAlign(Align.CENTER);
    } else {
      paintCopy.setTextAlign(Align.LEFT);
    }
    if (seriesRenderer.isDisplayChartValues()) {
      paintCopy.setTextAlign(seriesRenderer.getChartValuesTextAlign());
      drawChartValuesText(canvas, series, seriesRenderer, paintCopy, pointsList, seriesIndex, startIndex);
    }
  }

  /**
   * Draws the series points.
   *
   * @param canvas the canvas
   * @param paint the paint object
   * @param pointsList the points to be rendered
   * @param seriesRenderer the series renderer
   * @param yAxisValue the y axis value in pixels
   * @param seriesIndex the series index
   * @param startIndex the start index of the rendering points
   */
  protected void drawPoints(Canvas canvas, Paint paint, List<Float> pointsList,
                            XYSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex, int startIndex) {
    if (isRenderPoints(seriesRenderer)) {
      ScatterChart pointsChart = getPointsChart();
      if (pointsChart != null) {
        pointsChart.drawSeries(canvas, paint, pointsList, seriesRenderer, yAxisValue, seriesIndex,
                startIndex);
      }
    }
  }

  private void setStroke(BasicStroke stroke, Paint paint) {
    if (stroke == null || paint == null) {
      return;
    }
    paint.setStrokeCap(stroke.getCap());
    paint.setStrokeJoin(stroke.getJoin());
    paint.setStrokeMiter(stroke.getMiter());

    if (stroke.getIntervals() != null) {
      DashPathEffect effect = new DashPathEffect(stroke.getIntervals(), stroke.getPhase());
      paint.setPathEffect(effect);
    }
    paint.setStyle(Style.FILL_AND_STROKE);
  }

  /**
   * The graphical representation of the series values as text.
   *
   * @param canvas the canvas to paint to
   * @param series the series to be painted
   * @param renderer the series renderer
   * @param paint the paint to be used for drawing
   * @param points the array of points to be used for drawing the series
   * @param seriesIndex the index of the series currently being drawn
   * @param startIndex the start index of the rendering points
   */
  protected void drawChartValuesText(Canvas canvas, XYSeries series, XYSeriesRenderer renderer,
                                     Paint paint, List<Float> points, int seriesIndex, int startIndex) {
    if (points.size() > 2) { // there are more than one point
      // record the first point's position
      float previousPointX = points.get(0);
      float previousPointY = points.get(1);
      for (int k = 0; k < points.size(); k += 2) {
        if (k == 2) { // decide whether to display first two points' values or
          // not
          if (Math.abs(points.get(2) - points.get(0)) > renderer.getDisplayChartValuesDistance()
                  || Math.abs(points.get(3) - points.get(1)) > renderer.getDisplayChartValuesDistance()) {
            // first point
            drawText(canvas, getLabel(renderer.getChartValuesFormat(), series.getY(startIndex)),
                    points.get(0), points.get(1) - renderer.getChartValuesSpacing(), paint, 0);
            // second point
            drawText(canvas,
                    getLabel(renderer.getChartValuesFormat(), series.getY(startIndex + 1)),
                    points.get(2), points.get(3) - renderer.getChartValuesSpacing(), paint, 0);

            previousPointX = points.get(2);
            previousPointY = points.get(3);
          }
        } else if (k > 2) {
          // compare current point's position with the previous point's, if they
          // are not too close, display
          if (Math.abs(points.get(k) - previousPointX) > renderer.getDisplayChartValuesDistance()
                  || Math.abs(points.get(k + 1) - previousPointY) > renderer
                  .getDisplayChartValuesDistance()) {
            drawText(canvas,
                    getLabel(renderer.getChartValuesFormat(), series.getY(startIndex + k / 2)),
                    points.get(k), points.get(k + 1) - renderer.getChartValuesSpacing(), paint, 0);
            previousPointX = points.get(k);
            previousPointY = points.get(k + 1);
          }
        }
      }
    } else { // if only one point, display it
      for (int k = 0; k < points.size(); k += 2) {
        drawText(canvas,
                getLabel(renderer.getChartValuesFormat(), series.getY(startIndex + k / 2)),
                points.get(k), points.get(k + 1) - renderer.getChartValuesSpacing(), paint, 0);
      }
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
    drawString(canvas, text, x, y, paint);
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
      canvas.rotate(-angle, mCenter.getX(), mCenter.getY());
    } else {
      canvas.rotate(angle, mCenter.getX(), mCenter.getY());
      canvas.translate(-mTranslate, mTranslate);
      canvas.scale(mScale, 1 / mScale);
    }
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
   * @param maxX the maximum value on the X axis in the chart
   */
  protected void drawXLabels(List<Double> xLabels, Double[] xTextLabelLocations, Canvas canvas,
                             Paint paint, int left, int top, int bottom, double xPixelsPerUnit, double minX, double maxX) {

    boolean showXLabels = mRenderer.isShowXLabels();
    boolean showCustomXLabels = mRenderer.isShowCustomTextXLabels();
    boolean showGridX = mRenderer.isShowGridX();
    boolean showTickMarks = mRenderer.isShowTickMarks();

    for (int i = 0; i < xLabels.size(); i++) {
      double label = xLabels.get(i);
      float xLabel = (float) (left + xPixelsPerUnit * (label - minX));

      if (showXLabels) {
        paint.setColor(mRenderer.getXLabelsColor());

        if (showTickMarks) {
          canvas.drawLine(xLabel, bottom, xLabel, bottom + mRenderer.getLabelsTextSize() / 3, paint);
        }
        drawText(canvas, getLabel(mRenderer.getXLabelFormat(), label), xLabel,
                bottom + mRenderer.getLabelsTextSize() * 4 / 3 + mRenderer.getXLabelsPadding(), paint,
                mRenderer.getXLabelsAngle());
      }
      if (showGridX) {
        setStroke(mRenderer.getGridLineXStroke(), mGridPaint);
        mGridPaint.setStrokeWidth(mRenderer.getGridLineWidth());
        mGridPaint.setColor(mRenderer.getGridColor(0));

        Path gridLinePath = new Path();
        gridLinePath.moveTo(xLabel, bottom);
        gridLinePath.lineTo(xLabel, top);
        canvas.drawPath(gridLinePath, mGridPaint);
      }
    }
    drawXTextLabels(xTextLabelLocations, canvas, paint, showCustomXLabels, left, top, bottom,
            xPixelsPerUnit, minX, maxX);
  }

  /**
   * The graphical representation of the labels on the Y axis.
   *
   * @param allYLabels the Y labels values
   * @param canvas the canvas to paint to
   * @param paint the paint to be used for drawing
   * @param maxScaleNumber the maximum scale number
   * @param left the left value of the labels area
   * @param right the right value of the labels area
   * @param bottom the bottom value of the labels area
   * @param yPixelsPerUnit the amount of pixels per one unit in the chart labels
   * @param minY the minimum value on the Y axis in the chart
   */
  protected void drawYLabels(Map<Integer, List<Double>> allYLabels, Canvas canvas, Paint paint,
                             int maxScaleNumber, int left, int right, int bottom, double[] yPixelsPerUnit, double[] minY) {

    Orientation or = mRenderer.getOrientation();
    boolean showGridY = mRenderer.isShowGridY();
    boolean showYLabels = mRenderer.isShowYLabels();
    boolean showTickMarks = mRenderer.isShowTickMarks();

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
          if (showYLabels && !textLabel) {
            paint.setColor(mRenderer.getYLabelsColor(i));
            if (axisAlign == Align.LEFT) {
              if (showTickMarks) {
                canvas.drawLine(left + getLabelLinePos(axisAlign), yLabel, left, yLabel, paint);
              }
              drawText(canvas, getLabel(mRenderer.getYLabelFormat(i), label),
                      left - mRenderer.getYLabelsPadding(),
                      yLabel - mRenderer.getYLabelsVerticalPadding(), paint,
                      mRenderer.getYLabelsAngle());
            } else {
              if (showTickMarks) {
                canvas.drawLine(right, yLabel, right + getLabelLinePos(axisAlign), yLabel, paint);
              }
              drawText(canvas, getLabel(mRenderer.getYLabelFormat(i), label),
                      right + mRenderer.getYLabelsPadding(),
                      yLabel - mRenderer.getYLabelsVerticalPadding(), paint,
                      mRenderer.getYLabelsAngle());
            }
          }
          if (showGridY) {
            setStroke(mRenderer.getGridLineYStroke(), mGridPaint);
            mGridPaint.setStrokeWidth(mRenderer.getGridLineWidth());
            mGridPaint.setColor(mRenderer.getGridColor(i));

            Path gridLinePath = new Path();
            gridLinePath.moveTo(left, yLabel);
            gridLinePath.lineTo(right, yLabel);
            canvas.drawPath(gridLinePath, mGridPaint);
          }
        } else if (or == Orientation.VERTICAL) {
          if (showYLabels && !textLabel) {
            paint.setColor(mRenderer.getYLabelsColor(i));
            if (showTickMarks) {
              canvas.drawLine(right - getLabelLinePos(axisAlign), yLabel, right, yLabel, paint);
            }
            drawText(canvas, getLabel(mRenderer.getLabelFormat(), label),
                    right + 10 + mRenderer.getYLabelsPadding(),
                    yLabel - mRenderer.getYLabelsVerticalPadding(), paint, mRenderer.getYLabelsAngle());
          }
          if (showGridY) {
            mGridPaint.setColor(mRenderer.getGridColor(i));
            if (showTickMarks) {
              canvas.drawLine(right, yLabel, left, yLabel, mGridPaint);
            }
          }
        }
      }
    }
  }

  /**
   * The graphical representation of the text labels on the X axis.
   *
   * @param xTextLabelLocations the X text label locations
   * @param canvas the canvas to paint to
   * @param paint the paint to be used for drawing
   * @param left the left value of the labels area
   * @param top the top value of the labels area
   * @param bottom the bottom value of the labels area
   * @param xPixelsPerUnit the amount of pixels per one unit in the chart labels
   * @param minX the minimum value on the X axis in the chart
   * @param maxX the maximum value on the X axis in the chart
   */
  protected void drawXTextLabels(Double[] xTextLabelLocations, Canvas canvas, Paint paint,
                                 boolean showCustomXLabels, int left, int top, int bottom, double xPixelsPerUnit, double minX,
                                 double maxX) {
      if (!showCustomXLabels) {
          return;
      }
      boolean showCustomTextGridX = mRenderer.isShowCustomTextGridX();
      boolean showTickMarks = mRenderer.isShowTickMarks();

      paint.setColor(mRenderer.getXLabelsColor());
      paint.setTextSize(mRenderer.getLabelsTextSize());
      paint.setTextAlign(mRenderer.getXLabelsAlign());

    for (Double location : xTextLabelLocations) {
        if (minX <= location && location <= maxX) {
          float xLabel = (float) (left + xPixelsPerUnit * (location.doubleValue() - minX));
          paint.setColor(mRenderer.getXLabelsColor());
          if (showTickMarks) {
            canvas.drawLine(xLabel, bottom, xLabel, bottom + mRenderer.getLabelsTextSize() / 3,
                    paint);
          }
          drawText(canvas, mRenderer.getXTextLabel(location), xLabel,
                  bottom + mRenderer.getLabelsTextSize() * 4 / 3 + mRenderer.getXLabelsPadding(),
                  paint, mRenderer.getXLabelsAngle());

          if (showCustomTextGridX) {
            if (minX == location) {
              setStroke(BasicStroke.SOLID, mGridPaint);
            } else {
              setStroke(mRenderer.getGridLineXStroke(), mGridPaint);
            }
            mGridPaint.setStrokeWidth(mRenderer.getGridLineWidth());
            mGridPaint.setColor(mRenderer.getGridColor(0));

            Path gridLinePath = new Path();
            gridLinePath.moveTo(xLabel, bottom);
            gridLinePath.lineTo(xLabel, top);
            canvas.drawPath(gridLinePath, mGridPaint);
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
    return mCalcRange.get(scale);
  }

  public void setCalcRange(double[] range, int scale) {
    mCalcRange.put(scale, range);
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
    if (!mRenderer.isMinXSet(scale) || !mRenderer.isMaxXSet(scale) || !mRenderer.isMinYSet(scale)
            || !mRenderer.isMaxYSet(scale)) {
      double[] calcRange = getCalcRange(scale);
      if (calcRange != null) {
        realMinX = calcRange[0];
        realMaxX = calcRange[1];
        realMinY = calcRange[2];
        realMaxY = calcRange[3];
      }
    }
    if (mScreenR != null) {
      return new double[] {
              (screenX - mScreenR.left) * (realMaxX - realMinX) / mScreenR.width() + realMinX,
              (mScreenR.top + mScreenR.height() - screenY) * (realMaxY - realMinY) / mScreenR.height()
                      + realMinY };
    } else {
      return new double[] { screenX, screenY };
    }
  }

  public double[] toScreenPoint(double[] realPoint, int scale) {
    double realMinX = mRenderer.getXAxisMin(scale);
    double realMaxX = mRenderer.getXAxisMax(scale);
    double realMinY = mRenderer.getYAxisMin(scale);
    double realMaxY = mRenderer.getYAxisMax(scale);
    if (!mRenderer.isMinXSet(scale) || !mRenderer.isMaxXSet(scale) || !mRenderer.isMinYSet(scale)
            || !mRenderer.isMaxYSet(scale)) {
      double[] calcRange = getCalcRange(scale);
      realMinX = calcRange[0];
      realMaxX = calcRange[1];
      realMinY = calcRange[2];
      realMaxY = calcRange[3];
    }
    if (mScreenR != null) {
      return new double[] {
              (realPoint[0] - realMinX) * mScreenR.width() / (realMaxX - realMinX) + mScreenR.left,
              (realMaxY - realPoint[1]) * mScreenR.height() / (realMaxY - realMinY) + mScreenR.top };
    } else {
      return realPoint;
    }
  }

  public SeriesSelection getSeriesAndPointForScreenCoordinate(final Point screenPoint) {
    if (clickableAreas != null)
      for (int seriesIndex = clickableAreas.size() - 1; seriesIndex >= 0; seriesIndex--) {
        // series 0 is drawn first. Then series 1 is drawn on top, and series 2
        // on top of that.
        // we want to know what the user clicked on, so traverse them in the
        // order they appear on the screen.
        int pointIndex = 0;
        if (clickableAreas.get(seriesIndex) != null) {
          RectF rectangle;
          for (ClickableArea area : clickableAreas.get(seriesIndex)) {
            if (area != null) {
              rectangle = area.getRect();
              if (rectangle != null && rectangle.contains(screenPoint.getX(), screenPoint.getY())) {
                return new SeriesSelection(seriesIndex, pointIndex, area.getX(), area.getY());
              }
            }
            pointIndex++;
          }
        }
      }
    return super.getSeriesAndPointForScreenCoordinate(screenPoint);
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
   * @param startIndex the start index of the rendering points
   */
  public abstract void drawSeries(Canvas canvas, Paint paint, List<Float> points,
                                  XYSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex, int startIndex);

  /**
   * Returns the clickable areas for all passed points
   *
   * @param points the array of points
   * @param values the array of values of each point
   * @param yAxisValue the minimum value of the y axis
   * @param seriesIndex the index of the series to which the points belong
   * @return an array of rectangles with the clickable area
   * @param startIndex the start index of the rendering points
   */
  protected abstract ClickableArea[] clickableAreasForPoints(List<Float> points,
                                                             List<Double> values, float yAxisValue, int seriesIndex, int startIndex);

  /**
   * Returns if the chart should display the null values.
   *
   * @return if null values should be rendered
   */
  protected boolean isRenderNullValues() {
    return false;
  }

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
