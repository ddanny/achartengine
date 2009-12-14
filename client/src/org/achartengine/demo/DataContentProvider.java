package org.achartengine.demo;

import org.achartengine.demo.data.DonutData;
import org.achartengine.demo.data.TemperatureData;
import org.achartengine.intent.ContentSchema;
import org.achartengine.intent.ContentSchema.PlotData;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;



public class DataContentProvider extends ContentProvider {
	

	static final String TAG = "AChartEngine Demo";
	
	// This must be the same as what as specified as the Content Provider authority
	// in the manifest file.
	public static final String AUTHORITY = "org.achartengine.demo.provider.test";
	
	
	public static Uri BASE_URI = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(AUTHORITY).build();



	// Let the appended ID represent a unique dataset, so that the Chart can come
	// back and query for the auxiliary (meta) data (e.g. axes labels, colors, etc.).
	// Alternatively,
	// maybe the meta data could be passed along with the original Intent instead.
   public static Uri constructUri(String dataset_class, long data_id) {
       return ContentUris.withAppendedId(
            Uri.withAppendedPath(BASE_URI, dataset_class),
            data_id);
   }

   

   public static final String CHART_DATA_SERIES_PATH = "singleseries";
   private static final int CHART_DATA_SERIES = 1;
   
   public static final String CHART_DATA_MULTISERIES_PATH = "multiseries";
   private static final int CHART_DATA_MULTISERIES = 2;

   public static final String CHART_DATA_UNLABELED_PATH = "unlabeled";
   private static final int CHART_DATA_LABELED_SERIES = 3;
   
   public static final String CHART_DATA_LABELED_PATH = "labeled";
   private static final int CHART_DATA_LABELED_MULTISERIES = 4;
   
   private static final UriMatcher sUriMatcher;
   static {
       sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

       sUriMatcher.addURI(AUTHORITY, CHART_DATA_SERIES_PATH + "/" + CHART_DATA_UNLABELED_PATH + "/*", CHART_DATA_SERIES);
       sUriMatcher.addURI(AUTHORITY, CHART_DATA_MULTISERIES_PATH + "/" + CHART_DATA_UNLABELED_PATH + "/*", CHART_DATA_MULTISERIES);
       
       sUriMatcher.addURI(AUTHORITY, CHART_DATA_SERIES_PATH + "/" + CHART_DATA_LABELED_PATH + "/*", CHART_DATA_LABELED_SERIES);
       
       String labeled_multiseries_path = CHART_DATA_MULTISERIES_PATH + "/" + CHART_DATA_LABELED_PATH + "/*";
       Log.d(TAG, "UriMatcher labeled_multiseries_path: " + labeled_multiseries_path);
       sUriMatcher.addURI(AUTHORITY, labeled_multiseries_path, CHART_DATA_LABELED_MULTISERIES);
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
       
	   return PlotData.CONTENT_TYPE_PLOT_DATA;
   }

   @Override
   public Uri insert(Uri uri, ContentValues contentvalues) {
       throw new UnsupportedOperationException("Not supported by this provider");
   }

   @Override
   public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

       // TODO: Re-implement UriMatcher
       int match = sUriMatcher.match(uri);
       Log.d(TAG, "UriMatcher match: " + match);
       
       switch (match)
       {
       case CHART_DATA_MULTISERIES:
       {
           if (uri.getLastPathSegment().equals( ContentSchema.DATASET_ASPECT_AXES )) {
               
               MatrixCursor c = new MatrixCursor(new String[] {
                       BaseColumns._ID,
                       ContentSchema.PlotData.COLUMN_AXIS_LABEL});

               int row_index = 0;
               for (int i=0; i<TemperatureData.DEMO_AXES_LABELS.length; i++) {

                   c.newRow().add( row_index ).add( TemperatureData.DEMO_AXES_LABELS[i] );
                   row_index++;
               }

               return c;
           } else if (uri.getLastPathSegment().equals( ContentSchema.DATASET_ASPECT_META )) {
           
               // TODO: Define more columns for color, line style, marker shape, etc.
               MatrixCursor c = new MatrixCursor(new String[] {
                       BaseColumns._ID,
                       ContentSchema.PlotData.COLUMN_SERIES_LABEL});

               int row_index = 0;
               for (int i=0; i<TemperatureData.DEMO_TITLES.length; i++) {

                   c.newRow().add( row_index ).add( TemperatureData.DEMO_TITLES[i] );
                   row_index++;
               }

               return c;
               
           } else {
               // Fetch the actual data

           
            MatrixCursor c = new MatrixCursor(new String[] {
                    BaseColumns._ID,
                    ContentSchema.PlotData.COLUMN_AXIS_INDEX,
                    ContentSchema.PlotData.COLUMN_SERIES_INDEX,
                    ContentSchema.PlotData.COLUMN_DATUM_VALUE,
                    ContentSchema.PlotData.COLUMN_DATUM_LABEL
                    });

            int row_index = 0;
            // Add x-axis data
            for (int i=0; i<TemperatureData.DEMO_X_AXIS_DATA.length; i++) {

                
//                c.newRow().add( X_AXIS_INDEX ).add( i ).add( TemperatureData.DEMO_X_AXIS_DATA[i] ).add( null );
                c.newRow()
                    .add( row_index )
                    .add( ContentSchema.X_AXIS_INDEX )
                    .add( 0 )   // Only create data for the first series.
                    .add( TemperatureData.DEMO_X_AXIS_DATA[i] )
                    .add( null );
            
                row_index++;
            }
            
            // Add y-axis data
            for (int i=0; i<TemperatureData.DEMO_SERIES_LIST.length; i++) {
                for (int j=0; j<TemperatureData.DEMO_SERIES_LIST[i].length; j++) {
                    
//                    c.newRow().add( Y_AXIS_INDEX ).add( i ).add( TemperatureData.DEMO_SERIES_LIST[i][j] ).add( null );
                    c.newRow()
                        .add( row_index )
                        .add( ContentSchema.Y_AXIS_INDEX )
                        .add( i )
                        .add( TemperatureData.DEMO_SERIES_LIST[i][j] )
                        .add( null );
                
                    row_index++;
                }
            }

            return c;
           }
       }
       case CHART_DATA_LABELED_MULTISERIES:
       {
           if (uri.getLastPathSegment().equals( ContentSchema.DATASET_ASPECT_AXES )) {
               
               MatrixCursor c = new MatrixCursor(new String[] {
                       BaseColumns._ID,
                       ContentSchema.PlotData.COLUMN_AXIS_LABEL});

               int row_index = 0;
               for (int i=0; i<DonutData.DEMO_AXES_LABELS.length; i++) {

                   c.newRow().add( row_index ).add( DonutData.DEMO_AXES_LABELS[i] );
                   row_index++;
               }

               return c;
           } else if (uri.getLastPathSegment().equals( ContentSchema.DATASET_ASPECT_META )) {
           
           // TODO: Define more columns for color, line style, marker shape, etc.
               MatrixCursor c = new MatrixCursor(new String[] {
                       BaseColumns._ID,
                       ContentSchema.PlotData.COLUMN_SERIES_LABEL});

               int row_index = 0;
               for (int i=0; i<DonutData.DEMO_SERIES_LABELS.length; i++) {

                   c.newRow().add( row_index ).add( DonutData.DEMO_SERIES_LABELS[i] );
                   row_index++;
               }

               return c;
               
           } else {
           
                MatrixCursor c = new MatrixCursor(new String[] {
                        BaseColumns._ID,
                        ContentSchema.PlotData.COLUMN_AXIS_INDEX,
                        ContentSchema.PlotData.COLUMN_SERIES_INDEX,
                        ContentSchema.PlotData.COLUMN_DATUM_VALUE,
                        ContentSchema.PlotData.COLUMN_DATUM_LABEL});
    
                int row_index = 0;
                for (int i=0; i<DonutData.DEMO_SERIES_LIST.length; i++) {
                    for (int j=0; j<DonutData.DEMO_SERIES_LIST[i].length; j++) {
                        
                        c.newRow()
                            .add( row_index )
                            .add( ContentSchema.Y_AXIS_INDEX )  // XXX Since we're only populating one axis, it probably doesn't matter whether it's the X or Y axis.
                            .add( row_index )
                            .add( i )
                            .add( DonutData.DEMO_SERIES_LIST[i][j] )
                            .add( DonutData.DEMO_SERIES_LABELS_LIST[i][j] );
                    
                        row_index++;
                    }
                }
    
                return c;
           }
       }
       }
       Log.w(TAG, "Failed all matching tests!");
    return null;

   }

   @Override
   public int update(Uri uri, ContentValues contentvalues, String s, String[] as) {
       throw new UnsupportedOperationException("Not supported by this provider");
   }
}
