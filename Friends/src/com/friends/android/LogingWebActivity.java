package com.friends.android;

import com.friends.android.internal.Constans;

import android.os.Bundle;
import android.R.bool;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class LogingWebActivity extends Activity {

    public static final String DEBUG_TAG = "LogingWebActivity";
    
    private WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_loging_web);
        String userId = MainApplication.prefs.getString(Constans.USER_ID, null);
        String userToken = MainApplication.prefs.getString(Constans.USER_TOKEN, null);
        if (userId != null && userToken != null) {
            Intent intent = new Intent(LogingWebActivity.this, MainActivity.class);
            startActivity(intent);
        }
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.loadUrl("http://114.255.159.96");
        //13810753100
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e(DEBUG_TAG, url);
                String[] array = url.split("/");
                for (int i = 0; i < array.length; i ++) {
                    Log.e(DEBUG_TAG, array[i]);
                }
                if (array.length < 4) {
                    Toast.makeText(LogingWebActivity.this, "登录失败...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                String userId = array[array.length - 1];
                String userToken = array[array.length - 2];
                Log.e(DEBUG_TAG, "token: " + userId + " " + userToken);
                MainApplication.userId = userId;
                MainApplication.userToken = userToken;
                MainApplication.prefs.edit().putString(Constans.USER_ID, userId).commit();
                MainApplication.prefs.edit().putString(Constans.USER_TOKEN, userToken).commit();
                MainApplication.imageHeaders = new String[] {"X-app-token", "", "X-user-token", userToken, "Authorization", "Basic " + userToken};
                finish();
                Intent intent = new Intent(LogingWebActivity.this, MainActivity.class);
                startActivity(intent);
                
                return true;
            }
        });
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.loging_web, menu);
        return true;
    }

}
