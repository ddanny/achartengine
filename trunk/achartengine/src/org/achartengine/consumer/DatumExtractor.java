package org.achartengine.consumer;

import android.database.Cursor;

public interface DatumExtractor<E> {

  E getDatum(Cursor cursor, int data_column, int label_column);
}
