package com.friends.android;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.friends.android.MessageFabuActivity.AddRMThread;
import com.friends.android.internal.Constans;
import com.friends.android.internal.FClient;
import com.friends.android.internal.FClient.FException;
import com.friends.android.internal.FClient.InvalidAccessToken;
import com.friends.android.internal.FClient.UnknowServerError;
import com.friends.android.object.Contact;

public class FriendListActivity extends Activity {
    public static final String DEBUG_TAG = "FriendListActivity";
    
    private List<Contact> mCurrentList = new ArrayList<Contact>();
    private List<JSONObject> mCurrentList3 = new ArrayList<JSONObject>();
    private List<Boolean> mCurrentList2 = new ArrayList<Boolean>();
    private ListView mListView;
    private ListAdapter mListAdapter;
    private TextView mTextView;
    private String blackContent;
    
    public void onSaveInstanceState(Bundle savedInstanceState) {  
        // Save away the original text, so we still have it if the activity  
        // needs to be killed while paused.  
      savedInstanceState.putString(Constans.CONTENT, blackContent);  
      
      super.onSaveInstanceState(savedInstanceState);  
      Log.e(DEBUG_TAG, "save");  
    }    
    @Override  
    public void onRestoreInstanceState(Bundle savedInstanceState) {  
      super.onRestoreInstanceState(savedInstanceState);  
      blackContent = savedInstanceState.getString(Constans.CONTENT);  
      Log.e(DEBUG_TAG, "onRestoreInstanceState+IntTest");  
    }  
    
    public void initActionBar() {
        ActionBar actionBar = getActionBar();
        View view = getLayoutInflater().inflate(R.layout.actionbar_grey_layout, null);
        ImageView rightImageView = (ImageView) view.findViewById(R.id.right_imageview);
        rightImageView.setVisibility(View.INVISIBLE);
        TextView titleTextView = (TextView) view.findViewById(R.id.title_textview);
        titleTextView.setText("选择要规避的好友");
        ImageView leftImageView = (ImageView) view.findViewById(R.id.left_imageview);
        leftImageView.setVisibility(View.INVISIBLE);
        ActionBar.LayoutParams lp = new LayoutParams(android.app.ActionBar.LayoutParams.MATCH_PARENT,
                android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        actionBar.setCustomView(view, lp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        initActionBar();
        blackContent = getIntent().getStringExtra(Constans.CONTENT);
        
        mTextView = (TextView) findViewById(R.id.textview);
        mListView = (ListView) findViewById(R.id.listview);
        mListAdapter = new ListAdapter();
        mListView.setAdapter(mListAdapter);
        
        mTextView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < mCurrentList2.size(); i ++) {
                    if (mCurrentList2.get(i)) {
                        jsonArray.put(mCurrentList3.get(i));
                    }
                }
                Intent data = new Intent();
                data.putExtra(Constans.CONTENT, jsonArray.toString());
                setResult(0, data);
                finish();
                
            }
        });
        new RefreshDataThread().start();
    }
    
    public class RefreshDataThread extends Thread {
        
        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                JSONObject jsonObject = new JSONObject(client.userGetFriends(1));
                JSONArray jsonArray = jsonObject.getJSONArray("contacts");
                mCurrentList.clear();
                mCurrentList2.clear();
                mCurrentList3.clear();
                for (int i = 0; i < jsonArray.length(); i ++) {
                    mCurrentList3.add(jsonArray.getJSONObject(i));
                    Contact contact = new Contact();
                    contact.fromJSON(jsonArray.getJSONObject(i));
                    mCurrentList.add(contact);
                    mCurrentList2.add(false);
                }
                if (blackContent != null) {
                    Log.e(DEBUG_TAG, "black: " + blackContent);
                    JSONArray jsonArray2 = new JSONArray(blackContent);
                    Log.e(DEBUG_TAG, "size: " + jsonArray2.length());
                    for (int i = 0; i < mCurrentList.size(); i ++) {
                        for (int j = 0; j < jsonArray2.length(); j ++) {
                            if (mCurrentList.get(i).displayName.equals(jsonArray2.getJSONObject(j).getString("display_name"))) {
                                mCurrentList2.set(i, true);
                            }
                        }
                    }
                    
                }
                Log.e(DEBUG_TAG, "size: " + mCurrentList.size());
                runOnUiThread(new Runnable() {
                    public void run() {
                        mListAdapter.notifyDataSetChanged();
                    }
                });
            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                final InvalidAccessToken temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(FriendListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(FriendListActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(FriendListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (FException e) {
                // TODO: handle exception
            } catch (JSONException e) {
                // TODO: handle exception
            }
            
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.friend_list, menu);
        return true;
    }
    
    public class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mCurrentList.size();
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
            View view = getLayoutInflater().inflate(R.layout.friends_list_item_layout, null);
            Contact contact = mCurrentList.get(position);
            TextView nameTextView = (TextView) view.findViewById(R.id.name_textview);
            TextView contentTextView = (TextView) view.findViewById(R.id.content_textview);
            ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
            
            nameTextView.setText(contact.displayName);
            contentTextView.setText(contact.currentTitle);
            if (mCurrentList2.get(position)) {
                imageView.setImageResource(R.drawable.circle_check);
            } else {
                imageView.setImageResource(R.drawable.circle_uncheck);
            }
            final int tempPos = position;
            imageView.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    mCurrentList2.set(tempPos, !mCurrentList2.get(tempPos));
                    notifyDataSetChanged();
                }
            });
            
            return view;
        }
        
    }
}
