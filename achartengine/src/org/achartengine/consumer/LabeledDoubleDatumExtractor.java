package org.achartengine.consumer;

import org.achartengine.GraphicalActivity;
import org.achartengine.GraphicalActivity.LabeledDatum;

import android.database.Cursor;

public class LabeledDoubleDatumExtractor implements DatumExtractor<LabeledDatum> {

  public LabeledDatum getDatum(Cursor cursor, int data_column, int label_column) {
    
    LabeledDatum labeled_datum = new LabeledDatum();
    double datum = cursor.getDouble(data_column);
    labeled_datum.datum = datum;
    labeled_datum.label = cursor.getString(label_column);
    return labeled_datum;
  }
}
