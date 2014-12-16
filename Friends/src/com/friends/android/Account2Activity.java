package com.friends.android;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Account2Activity extends Activity {
    
    public static final String DEBUG_TAG = "Account2Activity";
    
    private LinearLayout mAddLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account2);
        initActionBar();
        
        mAddLayout = (LinearLayout) findViewById(R.id.add_linearlayout);
        mAddLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Account2Activity.this, AddAlipayAccountActivity.class);
                startActivity(intent);
            }
        });
    }
    
    
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        
        finish();
        super.onBackPressed();
    }
    
    public void initActionBar() {
        ActionBar mActionBar = getActionBar();
        View view = getLayoutInflater().inflate(R.layout.actionbar_orange_layout, null);
        ImageView leftImageView = (ImageView) view.findViewById(R.id.left_imageview);
        leftImageView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        ImageView rightImageView = (ImageView) view.findViewById(R.id.right_imageview);
        rightImageView.setVisibility(View.INVISIBLE);
        TextView titleTextView = (TextView) view.findViewById(R.id.title_textview);
        titleTextView.setText("提现");
        ActionBar.LayoutParams lp = new LayoutParams(android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        mActionBar.setCustomView(view, lp);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.account2, menu);
        return true;
    }

}
