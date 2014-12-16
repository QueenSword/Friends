package com.friends.android;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity {

    public static final String DEBUG_TAG = "SettingActivity";
    
    private LinearLayout mPersonalInfoLayout;
    private LinearLayout mSettingPwdLayout;
    private LinearLayout mSettingEyeLayout;
    private LinearLayout mSettingAbLayout;
    private LinearLayout mSettingBlockLayout;
    private LinearLayout mFeedBackLayout;
    private LinearLayout mSettingSoundLayout;
    private TextView mLogouTextView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initActionBar();
        
        mPersonalInfoLayout = (LinearLayout) findViewById(R.id.personal_info_layou);
        mPersonalInfoLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ProfileInfoActivity.class);
                startActivity(intent);
            }
        });
        mSettingPwdLayout = (LinearLayout) findViewById(R.id.setting_pwd_layout);
        mSettingPwdLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ModifyPasswordActivity.class);
                startActivity(intent);
            }
        });
        mSettingEyeLayout = (LinearLayout) findViewById(R.id.setting_eye_layout);
        mSettingEyeLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ConfirmFriendListActivity.class);
                startActivity(intent);
            }
        });
        mSettingAbLayout = (LinearLayout) findViewById(R.id.setting_ab_layout);
        mSettingAbLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(SettingActivity.this, LocalContactActivity.class);
                startActivity(intent);
            }
        });
        mSettingBlockLayout = (LinearLayout) findViewById(R.id.setting_block_layout);
        mSettingBlockLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(SettingActivity.this, BlackListActivity.class);
                startActivity(intent);
            }
        });
        mFeedBackLayout = (LinearLayout) findViewById(R.id.feedback_layout);
        mFeedBackLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, FeedBackActivity.class);
                startActivity(intent);
            }
        });
        mSettingSoundLayout = (LinearLayout) findViewById(R.id.setting_sound_layout);
        mSettingSoundLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SettingSoundActivity.class);
                startActivity(intent);
            }
        });
        
        mLogouTextView = (TextView) findViewById(R.id.logout_textview);
        mLogouTextView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                new LogoutThread().start();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                finish();
                Intent intent = new Intent(SettingActivity.this, LogingWebActivity.class);
                startActivity(intent);
            }
        });
        
        ImageView avatarImageView = (ImageView) findViewById(R.id.avatar_imageview);
        new RefreshAvatarThread(avatarImageView).start();
        
    }
    
    public class LogoutThread extends Thread {
        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                client.userLogout();
            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                final InvalidAccessToken temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SettingActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(SettingActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SettingActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (FException e) {
                // TODO: handle exception
            }
        }
    }
    
    
    public class RefreshAvatarThread extends Thread {
        public ImageView avatarImageView;

        public RefreshAvatarThread(ImageView avatarImageView) {
            this.avatarImageView = avatarImageView;
        }

        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                String response = client.getImageUrl("" + MainApplication.userId);
                final String imageUrl = Constans.API_HOST + response.substring(1, response.length() - 1);
                MainApplication.prefs.edit().putString(Constans.AVATAR_PATH, imageUrl).commit();
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (!imageUrl.endsWith("/")) {
                            MainApplication.imageLoader.displayImage(imageUrl, avatarImageView);
                        }
                    }
                });
            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                final InvalidAccessToken temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SettingActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(SettingActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SettingActivity.this, temp.message, Toast.LENGTH_SHORT).show();
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
        ActionBar actionBar = getActionBar();
        View view = getLayoutInflater().inflate(R.layout.actionbar_grey_layout, null);
        ImageView rightImageView = (ImageView) view.findViewById(R.id.right_imageview);
        rightImageView.setVisibility(View.INVISIBLE);
        TextView titleTextView = (TextView) view.findViewById(R.id.title_textview);
        titleTextView.setText("设置");
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
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }

}
