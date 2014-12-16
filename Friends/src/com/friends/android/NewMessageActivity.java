package com.friends.android;


import com.friends.android.internal.Constans;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class NewMessageActivity extends Activity {

    public static final String DEBUG_TAG = "NewMessageActivity";
    private String srcActivity;
    
    private EditText mEditText;
    
    public void onSaveInstanceState(Bundle savedInstanceState) {  
        // Save away the original text, so we still have it if the activity  
        // needs to be killed while paused.  
      savedInstanceState.putString(Constans.SRC_ACTIVITY, srcActivity);  
      super.onSaveInstanceState(savedInstanceState);  
      Log.e(DEBUG_TAG, "save");  
    }    
    @Override  
    public void onRestoreInstanceState(Bundle savedInstanceState) {  
      super.onRestoreInstanceState(savedInstanceState);  
      srcActivity = savedInstanceState.getString(Constans.SRC_ACTIVITY);  
      Log.e(DEBUG_TAG, "onRestoreInstanceState+IntTest");  
    }  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        srcActivity = getIntent().getStringExtra(Constans.SRC_ACTIVITY);
        initActionBar();
        mEditText = (EditText) findViewById(R.id.edittext);
        
    }
    
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        
        finish();
        super.onBackPressed();
    }
    
    public void initActionBar() {
        ActionBar actionBar = getActionBar();
        View view = getLayoutInflater().inflate(R.layout.actionbar_grey_layout, null);
        ImageView rightImageView = (ImageView) view.findViewById(R.id.right_imageview);
        rightImageView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String content = mEditText.getText().toString();
                if (content.length() < 150 && content.length() > 10) {
                    Intent intent = new Intent(NewMessageActivity.this, ChooseMessageTypeActivity.class);
                    intent.putExtra(Constans.SRC_ACTIVITY, srcActivity);
                    intent.putExtra(Constans.CONTENT, content);
                    startActivity(intent);
                }
                
            }
        });
        TextView titleTextView = (TextView) view.findViewById(R.id.title_textview);
        titleTextView.setText("发布新消息");
        ImageView leftImageView = (ImageView) view.findViewById(R.id.left_imageview);
        leftImageView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ActionBar.LayoutParams lp = new LayoutParams(android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        actionBar.setCustomView(view, lp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_message, menu);
        return true;
    }

}
