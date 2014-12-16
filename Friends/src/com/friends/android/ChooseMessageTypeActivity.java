package com.friends.android;

import java.util.ArrayList;
import java.util.List;

import com.friends.android.internal.Constans;

import android.os.Bundle;
import android.R.bool;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ChooseMessageTypeActivity extends Activity {

    public static final String DEBUG_TAG = "ChooseMessageTypeActivity";
    
    private ListView mListView;
    private ListAdapter mListAdapter;
    private List<Boolean> mCurrentList = new ArrayList<Boolean>();
    private String[] mFields;
    private String content;
    private String srcActivity;
    
    public void onSaveInstanceState(Bundle savedInstanceState) {  
        // Save away the original text, so we still have it if the activity  
        // needs to be killed while paused.  
      savedInstanceState.putString(Constans.CONTENT, content);  
      savedInstanceState.putString(Constans.SRC_ACTIVITY, srcActivity);
      super.onSaveInstanceState(savedInstanceState);  
      Log.e(DEBUG_TAG, "save");  
    }    
    @Override  
    public void onRestoreInstanceState(Bundle savedInstanceState) {  
      super.onRestoreInstanceState(savedInstanceState);  
      content = savedInstanceState.getString(Constans.CONTENT);
      srcActivity = savedInstanceState.getString(Constans.SRC_ACTIVITY);
      Log.e(DEBUG_TAG, "onRestoreInstanceState+IntTest");  
    }  
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_message_type);
        initActionBar();
        content = getIntent().getStringExtra(Constans.CONTENT);
        srcActivity = getIntent().getStringExtra(Constans.SRC_ACTIVITY);
        mFields = getResources().getStringArray(R.array.field_array);
        
        for (int i = 0; i < mFields.length + 1; i ++) {
            mCurrentList.add(false);
        }
        
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
        rightImageView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Boolean hasChoose = false;
                for (int i = 0; i < mCurrentList.size(); i ++) {
                    if (mCurrentList.get(i) == true) {
                        hasChoose = true;
                    }
                }
                if (!hasChoose) {
                    return ;
                }
                Intent intent = new Intent(ChooseMessageTypeActivity.this, MessageFabuActivity.class);
                String fields = "";
                
                if (mCurrentList.get(0) == true) {
                    fields = "0";
                } else {
                    for (int i = 1; i < mCurrentList.size(); i ++) {
                        if (mCurrentList.get(i) == true) {
                            fields += i;
                            fields += ',';
                        }
                    }
                    fields = fields.substring(0, fields.length() - 1);
                }
                intent.putExtra(Constans.SRC_ACTIVITY, srcActivity);
                intent.putExtra(Constans.CONTENT, content);
                intent.putExtra(Constans.FIELDS, fields);
                startActivity(intent);
            }
        });
        TextView titleTextView = (TextView) view.findViewById(R.id.title_textview);
        titleTextView.setText("信息相关类别");
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
        getMenuInflater().inflate(R.menu.choose_message_type, menu);
        return true;
    }
    
    public class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mFields.length + 1;
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
            View view = getLayoutInflater().inflate(R.layout.choose_message_type_list_item, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
            TextView textView = (TextView) view.findViewById(R.id.textview);
            final int temPos = position;
            if (position == 0) {
                textView.setText("全部行业");
            } else {
                textView.setText(mFields[position - 1]);
            }
            if (mCurrentList.get(position) == false) {
                imageView.setImageResource(R.drawable.circle_uncheck);
            } else {
                imageView.setImageResource(R.drawable.circle_check);
            }
            imageView.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    if (temPos == 0) {
                        if (mCurrentList.get(0) == true) {
                            mCurrentList.set(0, false);
                        } else {
                            for (int i = 1; i < mCurrentList.size(); i ++) {
                                mCurrentList.set(i, false);
                            }
                            mCurrentList.set(0, true);
                        }
                    } else {
                        mCurrentList.set(0, false);
                        mCurrentList.set(temPos, !mCurrentList.get(temPos));
                    }
                    notifyDataSetChanged();
                }
            });
            return view;
        }
        
    }

}
