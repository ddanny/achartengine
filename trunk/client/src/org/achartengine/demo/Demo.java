package org.achartengine.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

public class Demo extends Activity {

    static final String TAG = "AChartEngine"; 
    
    static final String GOOGLE_CODE_URL = "http://achartengine.googlecode.com/";
    
    
	public static final String MARKET_AUTHOR_SEARCH_PREFIX = "pub:";
	public static final String MARKET_AUTHOR_NAME = "Karl Ostmo";
	public static final String MARKET_AUTHOR_SEARCH_STRING = MARKET_AUTHOR_SEARCH_PREFIX + "\"" + MARKET_AUTHOR_NAME + "\"";
	

	final int RETURN_CODE_CALENDAR_SELECTION = 1;
	


    public static String[] DEMO_AXES_LABELS = new String[] { "Month", "Temperature (F)" };
    public static String DEMO_CHART_TITLE = "Average temp";
   
	
	
//    public static String[] DEMO_TITLES = new String[] { "Crete", "Corfu", "Thassos", "Skiathos" };
	public static String[] DEMO_TITLES = new String[] { "Cretin", "Corfu", "Thassos", "Skiathos" };
    
    
    public static double[] DEMO_SERIES_1 = { 12.3, 12.5, 13.8, 16.8, 20.4, 24.4, 26.4, 26.1, 23.6, 20.3, 17.2,
        13.9 };
    public static double[] DEMO_SERIES_2 = { 10, 10, 12, 15, 20, 24, 26, 26, 23, 18, 14, 11 };
//    public static double[] DEMO_SERIES_3 = { 5, 5.3, 8, 12, 17, 22, 24.2, 24, 19, 15, 9, 6 };
    public static double[] DEMO_SERIES_3 = { 5, 5.3, 8, 12, 17, 22, 24.2, 3, 19, 15, 9, 6 };
    public static double[] DEMO_SERIES_4 = { 9, 10, 11, 15, 19, 23, 26, 25, 22, 18, 13, 10 };   
    
    public static double[][] DEMO_SERIES_LIST = {DEMO_SERIES_1, DEMO_SERIES_2, DEMO_SERIES_3, DEMO_SERIES_4};
    
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
        setContentView(R.layout.main);
        getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.titlebar_icon);


        
        findViewById(R.id.button_chart_data_provider).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
		    	Uri u = DataContentProvider.constructUri(12345);
				Intent i = new Intent(Intent.ACTION_VIEW, u);
				i.putExtra(Intent.EXTRA_TITLE, DEMO_CHART_TITLE);
		    	startActivity(i);
			}
        });
    }
    
    
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_main, menu);
        return true;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_about:
        {
			Uri flickr_destination = Uri.parse( GOOGLE_CODE_URL );
        	// Launches the standard browser.
        	startActivity(new Intent(Intent.ACTION_VIEW, flickr_destination));

            return true;
        }
        }

        return super.onOptionsItemSelected(item);
    }
}