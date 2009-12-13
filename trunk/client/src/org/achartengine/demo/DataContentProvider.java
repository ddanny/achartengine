package org.achartengine.demo;

import org.achartengine.intent.ContentSchema;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;



public class DataContentProvider extends ContentProvider {
	

	static final String TAG = "AChartEngine Demo";
	
	// This must be the same as what as specified as the Content Provider authority
	// in the manifest file.
	public static final String AUTHORITY = "org.achartengine.demo.provider.test";
	
	
	static Uri BASE_URI = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(AUTHORITY).path("data").build();



	// Let the appended ID represent a unique dataset, so that the Chart can come
	// back and query for the auxiliary (meta) data (e.g. axes labels, colors, etc.).
	// Alternatively,
	// maybe the meta data could be passed along with the original Intent instead.
   public static Uri constructUri(long data_id) {
       return ContentUris.withAppendedId(BASE_URI, data_id);
   }

   
   @Override
   public boolean onCreate() {
       return true;
   }

   @Override
   public int delete(Uri uri, String s, String[] as) {
       throw new UnsupportedOperationException("Not supported by this provider");
   }

   @Override
   public String getType(Uri uri) {
       

       // TODO: Re-implement with UriMatcher - Distinguish between "Meta" and "Data" type
       // based on the Uri extension
       
	   return "vnd.android.cursor.dir/vnd.org.achartengine.data.test";
   }

   @Override
   public Uri insert(Uri uri, ContentValues contentvalues) {
       throw new UnsupportedOperationException("Not supported by this provider");
   }

   @Override
   public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

       // TODO: Re-implement with UriMatcher
       if (uri.getLastPathSegment().equals("axes")) {
       
           MatrixCursor c = new MatrixCursor(new String[] {
                   BaseColumns._ID,
                   ContentSchema.PlotData.COLUMN_AXIS_LABEL});

           int row_index = 0;
           for (int i=0; i<Demo.DEMO_AXES_LABELS.length; i++) {

               c.newRow().add( row_index ).add( Demo.DEMO_AXES_LABELS[i] );
               row_index++;
           }

//         Log.i(TAG, "Generated cursor with " + c.getCount() + " rows.");
           return c;
       } else if (uri.getLastPathSegment().equals("meta")) {
       
       // TODO: Define more columns for color, line style, marker shape, etc.
           MatrixCursor c = new MatrixCursor(new String[] {
                   BaseColumns._ID,
                   ContentSchema.PlotData.COLUMN_SERIES_LABEL});

           int row_index = 0;
           for (int i=0; i<Demo.DEMO_TITLES.length; i++) {

               c.newRow().add( row_index ).add( Demo.DEMO_TITLES[i] );
               row_index++;
           }

//         Log.i(TAG, "Generated cursor with " + c.getCount() + " rows.");
           return c;
           
       }
       
		MatrixCursor c = new MatrixCursor(new String[] {
				BaseColumns._ID,
				ContentSchema.PlotData.COLUMN_SERIES_INDEX,
				ContentSchema.PlotData.COLUMN_DATUM_VALUE,
				ContentSchema.PlotData.COLUMN_DATUM_LABEL});

		int row_index = 0;
		for (int i=0; i<Demo.DEMO_SERIES_LIST.length; i++) {
		    for (int j=0; j<Demo.DEMO_SERIES_LIST[i].length; j++) {
		        
		        c.newRow().add( row_index ).add( i ).add( Demo.DEMO_SERIES_LIST[i][j] ).add( null );
			
			    row_index++;
		    }
		}

//		Log.i(TAG, "Generated cursor with " + c.getCount() + " rows.");
		return c;
   }

   @Override
   public int update(Uri uri, ContentValues contentvalues, String s, String[] as) {
       throw new UnsupportedOperationException("Not supported by this provider");
   }
}
