package org.achartengine.consumer;

import android.database.Cursor;

public class DoubleDatumExtractor implements DatumExtractor<Double> {

  public Double getDatum(Cursor cursor, int data_column, int label_column) {
    
    return cursor.getDouble(data_column);
  }
}
