package com.friends.android;

import java.util.Timer;
import java.util.TimerTask;

import com.friends.android.internal.Constans;
import com.friends.android.internal.FClient;
import com.friends.android.internal.FClient.FException;
import com.friends.android.internal.FClient.InvalidAccessToken;
import com.friends.android.internal.FClient.UnknowServerError;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SubmitModifyPwdActivity extends Activity {

    public static final String DEBUG_TAG = "SubmitModifyPwdActivity";
    
    private ImageView mOkImageView;
    private TextView mCountTextView;
    private Integer mCount = 60;
    private EditText mCodeEditText;
    private EditText mPwdEditText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_modify_pwd);
        initActionBar();
        
        mCodeEditText = (EditText) findViewById(R.id.code_edittext);
        mPwdEditText = (EditText) findViewById(R.id.pwd_edittext);
        mCountTextView = (TextView) findViewById(R.id.count_textview);
        mOkImageView = (ImageView) findViewById(R.id.ok_imageview);
        mOkImageView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new UpdatePasswordThread().start();
                Intent intent = new Intent(SubmitModifyPwdActivity.this, SubmitModifyPwdActivity.class);
                startActivity(intent);
            }
        });
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                mCount --;
                if (mCount >= 0) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            
                            mCountTextView.setText("" + mCount);
                        }
                    });
                }
                
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 0, 1000);
    }
    
    public class UpdatePasswordThread extends Thread {
        
        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                client.userChangPwd(mPwdEditText.getText().toString(), mCodeEditText.getText().toString());
            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                final InvalidAccessToken temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SubmitModifyPwdActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(SubmitModifyPwdActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SubmitModifyPwdActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (FException e) {
                // TODO: handle exception
            }
        }
    }
    
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        
        finish();
        super.onBackPressed();
    }
    
    public void initActionBar() {
        ActionBar mActionBar = getActionBar();
        View view = getLayoutInflater().inflate(R.layout.actionbar_grey_layout, null);
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
        titleTextView.setText("密码修改");
        ActionBar.LayoutParams lp = new LayoutParams(android.app.ActionBar.LayoutParams.MATCH_PARENT,
                android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        mActionBar.setCustomView(view, lp);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setDisplayShowCustomEnabled(true);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.submit_modify_pwd, menu);
        return true;
    }

}
