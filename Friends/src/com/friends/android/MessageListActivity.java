package com.friends.android;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class MessageListActivity extends Activity {
    
    public static final String DEBUG_TAG = "";
    
    private ListView mListView;
    private ListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        mListView = (ListView) findViewById(R.id.listview);
        mListAdapter = new ListAdapter();
        mListView.setAdapter(mListAdapter);
    }
    
    public class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 10;
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
            View view = getLayoutInflater().inflate(R.layout.message_list_item_layout, null);
            return view;
        }
        
    }
}
