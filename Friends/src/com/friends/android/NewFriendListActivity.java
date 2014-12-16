package com.friends.android;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.friends.android.FriendActivity.RefreshAvatarThread;
import com.friends.android.FriendActivity.RefreshFriendThread;
import com.friends.android.internal.Constans;
import com.friends.android.internal.FClient;
import com.friends.android.internal.FClient.FException;
import com.friends.android.internal.FClient.InvalidAccessToken;
import com.friends.android.internal.FClient.UnknowServerError;
import com.friends.android.object.Contact;

import android.os.Bundle;
import android.os.Debug;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
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

public class NewFriendListActivity extends Activity {
    public static final String DEBUG_TAG = "NewFriendListActivity";
    
    private ListView mListView;
    private ListAdapter mListAdapter;
    private List<Contact> mCurrentList = new ArrayList<Contact>();
    private List<String> mAvatarList = new ArrayList<String>();
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend_list);
        initActionBar();
        
        mListView = (ListView) findViewById(R.id.listview);
        mListAdapter = new ListAdapter();
        mListView.setAdapter(mListAdapter);
        
        new RefreshDataThread().start();
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
            View view = getLayoutInflater().inflate(R.layout.friend_list_item_layout, null);
            final Contact item = mCurrentList.get(position);
            TextView nameTextView = (TextView) view.findViewById(R.id.name_textview);
            TextView contentTextView = (TextView) view.findViewById(R.id.content_textview);
            nameTextView.setText(item.displayName);
            contentTextView.setText(item.currentTitle);
            ImageView avatarImageView = (ImageView) view.findViewById(R.id.avatar_imageview);
            if (!mAvatarList.get(position).equals("") && !mAvatarList.get(position).endsWith("/")) {
                MainApplication.imageLoader.displayImage(mAvatarList.get(position), avatarImageView);
            } else {
                new RefreshAvatarThread(position, item.contactId, avatarImageView).start();
            }
            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    
                }
            });
            return view;
        }
        
    }
    
    public class RefreshAvatarThread extends Thread {
        public int position;
        public Integer contactId;
        public ImageView avatarImageView;

        public RefreshAvatarThread(int position, Integer contactID, ImageView avatarImageView) {
            this.position = position;
            this.contactId = contactID;
            this.avatarImageView = avatarImageView;
        }

        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                String response = client.getImageUrl("" + contactId);
                final String imageUrl = Constans.API_HOST + response.substring(1, response.length() - 1);
                mAvatarList.set(position, imageUrl);
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
                        Toast.makeText(NewFriendListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(NewFriendListActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(NewFriendListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (FException e) {
                // TODO: handle exception
            }
        }
    }
    
public class RefreshDataThread extends Thread {
        
        @Override
        public void run() {
            
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                JSONObject jsonObject = new JSONObject(client.userGetNewFriends(1));
                Log.e(DEBUG_TAG, jsonObject.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("contacts");
                mCurrentList.clear();
                mAvatarList.clear();
                for (int i = 0; i < jsonArray.length(); i ++) {
                    Contact contact = new Contact();
                    contact.fromJSON(jsonArray.getJSONObject(i));
                    mCurrentList.add(contact);
                    mAvatarList.add("");
                }
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
                        Toast.makeText(NewFriendListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(NewFriendListActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(NewFriendListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
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
        rightImageView.setImageResource(R.drawable.icon_delete_gray);
        rightImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                
            }
        });
        TextView titleTextView = (TextView) view.findViewById(R.id.title_textview);
        titleTextView.setText("新朋友");
        ActionBar.LayoutParams lp = new LayoutParams(android.app.ActionBar.LayoutParams.MATCH_PARENT,
                android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        mActionBar.setCustomView(view, lp);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_friend_list, menu);
        return true;
    }

}
