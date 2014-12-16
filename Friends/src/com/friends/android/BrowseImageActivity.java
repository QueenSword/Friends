package com.friends.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

import com.friends.android.internal.Constans;

public class BrowseImageActivity extends Activity {
    public static final String DEBUG_TAG = "BrowseImageActivity";
    
    private String url;
    private ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getIntent().getStringExtra(Constans.URL);
        
        setContentView(R.layout.activity_browse_image);
        mImageView = (ImageView) findViewById(R.id.imageview);
        MainApplication.imageLoader.displayImage(url, mImageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.browse_image, menu);
        return true;
    }
    
    public void onSaveInstanceState(Bundle savedInstanceState) {  
        // Save away the original text, so we still have it if the activity  
        // needs to be killed while paused.  
      savedInstanceState.putString(Constans.URL, url);  
      super.onSaveInstanceState(savedInstanceState);  
      Log.e(DEBUG_TAG, "save");  
    }    
    @Override  
    public void onRestoreInstanceState(Bundle savedInstanceState) {  
      super.onRestoreInstanceState(savedInstanceState);  
      url = savedInstanceState.getString(Constans.URL);  
      Log.e(DEBUG_TAG, "onRestoreInstanceState+IntTest");  
    }  

}
