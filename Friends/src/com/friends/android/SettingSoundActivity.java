package com.friends.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

public class SettingSoundActivity extends Activity {

    public static final String DEBUG_TAG = "";
    
    private ListView mListView;
    private ListAdapter mListAdapter;
    private String[] mSounds;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_sound);
        initActionBar();
        mSounds = getResources().getStringArray(R.array.sound_array);
        
        mListView = (ListView) findViewById(R.id.listview);
        mListAdapter = new ListAdapter();
        mListView.setAdapter(mListAdapter);
        
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
        titleTextView.setText("声音设置");
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
        getMenuInflater().inflate(R.menu.setting_sound, menu);
        return true;
    }
    
    public class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mSounds.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.setting_sound_list_item_layout, null);
            TextView textView = (TextView) view.findViewById(R.id.textview);
            textView.setText(mSounds[position]);
            Switch soundSwitch = (Switch) view.findViewById(R.id.sound_switch);
            soundSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    MediaPlayer mp = new MediaPlayer();
                    mp.reset();                    
                    try
                    {
                        // 短信
                        mp.setDataSource(SettingSoundActivity.this,RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION ));
                                
                        // 铃声
                        //mp.setDataSource(this,RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
                                
                        // 自定义音频文件
                        //mp.setDataSource("/mnt/sdcard/AAAAA_CHENJIAN/test/music/1.mp3");
                                
                        mp.prepare();
                        mp.start();
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            return view;
        }
        
    }

}
