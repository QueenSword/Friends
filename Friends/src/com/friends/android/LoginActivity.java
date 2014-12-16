package com.friends.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class LoginActivity extends Activity {
    public static final String DEBUG_TAG = "LoginActivity";
    private ImageView mLoginImageView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginImageView = (ImageView) findViewById(R.id.login_imageview);
        mLoginImageView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(LoginActivity.this, YaoYiYaoActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //new MyThread().start();
        
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

}
