package com.friends.android;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AccountHistory extends Activity {

    public static final String DEBUG_TAG = "AccountHistory";
    
    private ListView mListView;
    private ListAdapter mListAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_history);
        initActionBar();
        
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
        ActionBar mActionBar = getActionBar();
        View view = getLayoutInflater().inflate(R.layout.actionbar_blue_layout, null);
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
        titleTextView.setText("财富历史记录");
        ActionBar.LayoutParams lp = new LayoutParams(android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        mActionBar.setCustomView(view, lp);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.account_history, menu);
        return true;
    }
    
    public class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 12;
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
            View view = getLayoutInflater().inflate(R.layout.account_history_list_item_layout, null);
            return view;
            
        }
        
        
    }

}
