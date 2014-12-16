package com.friends.android;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;

public class SplashActivity extends Activity {

    public static final String DEBUG_TAG = "SplashActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LogingWebActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }

        }, 500);

    }
}
