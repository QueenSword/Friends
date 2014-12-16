package com.friends.android;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.friends.android.internal.Constans;
import com.friends.android.object.Contact;

import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SelectTypeActivity extends Activity {
    public static final String DEBUG_TAG = "SelectTypeActivity";

    private ListView mListView;
    private ListAdapter mListAdapter;
    private List<Boolean> mCurrentList = new ArrayList<Boolean>();
    private String[] mFields;
    private String fields = null;
    
    public void onSaveInstanceState(Bundle savedInstanceState) {  
        // Save away the original text, so we still have it if the activity  
        // needs to be killed while paused.  
      savedInstanceState.putString(Constans.FIELDS, fields);  
      super.onSaveInstanceState(savedInstanceState);  
      Log.e(DEBUG_TAG, "save");  
    }    
    @Override  
    public void onRestoreInstanceState(Bundle savedInstanceState) {  
      super.onRestoreInstanceState(savedInstanceState);  
      fields = savedInstanceState.getString(Constans.FIELDS);  
      Log.e(DEBUG_TAG, "onRestoreInstanceState+IntTest");  
    }  

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_type);
        initActionBar();
        mFields = getResources().getStringArray(R.array.field_array);
        fields = getIntent().getStringExtra(Constans.CONTENT);
        Log.e(DEBUG_TAG, "fields: " + fields);
        if (fields != "") {
            String[] temp = fields.split(",");
            for (int i = 0; i < mFields.length; i ++) {
                mCurrentList.add(false);
            }
            for (int i = 0; i < temp.length; i ++) {
                mCurrentList.set(Integer.parseInt(temp[i]) - 1, true);
            }
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
                String fields = "";
                
                    for (int i = 0; i < mCurrentList.size(); i ++) {
                        if (mCurrentList.get(i) == true) {
                            fields += i + 1;
                            fields += ',';
                        }
                    }
                    fields = fields.substring(0, fields.length() - 1);
                
                    Intent data = new Intent();
                    data.putExtra(Constans.CONTENT, fields);
                    setResult(0, data);
                    finish();
            }
        });
        TextView titleTextView = (TextView) view.findViewById(R.id.title_textview);
        titleTextView.setText("擅长领域");
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
    
    public class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mFields.length;
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

            textView.setText(mFields[position]);
            if (mCurrentList.get(position) == false) {
                imageView.setImageResource(R.drawable.circle_uncheck);
            } else {
                imageView.setImageResource(R.drawable.circle_check);
            }
            imageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.e(DEBUG_TAG, "click " + temPos);
                        mCurrentList.set(temPos, !mCurrentList.get(temPos));
                        Boolean hasChoose = false;
                        for (int i = 0; i < mCurrentList.size(); i ++) {
                            if (mCurrentList.get(i)) {
                                hasChoose = true;
                                break;
                            }
                        }
                        if (!hasChoose) {
                            mCurrentList.set(temPos, !mCurrentList.get(temPos));
                        }
                        notifyDataSetChanged();
                }
            });
            return view;
        }

    }

}
