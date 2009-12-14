package org.achartengine.demo;

import org.achartengine.demo.data.DonutData;
import org.achartengine.demo.data.TemperatureData;

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
	

    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
        setContentView(R.layout.main);
        getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.titlebar_icon);


        
        findViewById(R.id.button_multiseries_data_provider).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//		    	Uri u = DataContentProvider.constructUri(DataContentProvider.CHART_DATA_MULTISERIES_PATH, 12345);
			    
			    Uri u = DataContentProvider.BASE_URI.buildUpon()
			        .appendPath(DataContentProvider.CHART_DATA_MULTISERIES_PATH)
			        .appendPath(DataContentProvider.CHART_DATA_UNLABELED_PATH).build();			    
			    
				Intent i = new Intent(Intent.ACTION_VIEW, u);
				i.putExtra(Intent.EXTRA_TITLE, TemperatureData.DEMO_CHART_TITLE);
		    	startActivity(i);
			}
        });
        
        
        findViewById(R.id.button_labeled_multiseries_data_provider).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                
                Uri u = DataContentProvider.BASE_URI.buildUpon()
                    .appendPath(DataContentProvider.CHART_DATA_MULTISERIES_PATH)
                    .appendPath(DataContentProvider.CHART_DATA_LABELED_PATH).build(); 
                
                Intent i = new Intent(Intent.ACTION_VIEW, u);
                i.putExtra(Intent.EXTRA_TITLE, DonutData.DEMO_CHART_TITLE);
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