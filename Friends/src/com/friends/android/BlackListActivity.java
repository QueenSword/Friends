package com.friends.android;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.friends.android.internal.Constans;
import com.friends.android.internal.FClient;
import com.friends.android.internal.FClient.FException;
import com.friends.android.internal.FClient.InvalidAccessToken;
import com.friends.android.internal.FClient.UnknowServerError;
import com.friends.android.object.Contact;

public class BlackListActivity extends Activity {

public static final String DEBUG_TAG = "BlackListActivity";
    
    private ListView mListView;
    private ListAdapter mListAdapter;
    private List<Contact> mCurrentList = new ArrayList<Contact>();
    private List<String> mAvatarUrl = new ArrayList<String>();
    private List<Boolean> mHasAvatar = new ArrayList<Boolean>();
    private List<Boolean> mHasSelected = new ArrayList<Boolean>();
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);
        initActionBar();
        
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                List<String> contactIds = new ArrayList<String>();
                for (int i = 0; i < mCurrentList.size(); i ++) {
                    if (mHasSelected.get(i)) {
                        contactIds.add("" + mCurrentList.get(i).contactId);
                    }
                }
                new RemoveBlockThread(contactIds).start();
            }
        });
        
        mListView = (ListView) findViewById(R.id.listview);
        mListAdapter = new ListAdapter();
        mListView.setAdapter(mListAdapter);
        new RefreshDataThread().start();
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
                mAvatarUrl.set(position, imageUrl);
                mHasAvatar.set(position, true);
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (!imageUrl.endsWith("/")) {
                            MainApplication.imageLoader.displayImage(mAvatarUrl.get(position), avatarImageView);
                        }
                    }
                });
            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                final InvalidAccessToken temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(BlackListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(BlackListActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(BlackListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
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
                JSONObject jsonObject = new JSONObject(client.userGetBlackList(1));
                JSONArray jsonArray = jsonObject.getJSONArray("contacts");
                mCurrentList.clear();
                mAvatarUrl.clear();
                mHasAvatar.clear();
                for (int i = 0; i < jsonArray.length(); i ++) {
                    Contact contact = new Contact();
                    contact.fromJSON(jsonArray.getJSONObject(i));
                    mCurrentList.add(contact);
                    mAvatarUrl.add("");
                    mHasAvatar.add(false);
                    mHasSelected.add(false);
                }
                
                runOnUiThread(new Runnable() {
                    public void run() {
                        mListAdapter.notifyDataSetChanged();
                    }
                });
            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                Toast.makeText(BlackListActivity.this, e.message, Toast.LENGTH_SHORT).show();
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(BlackListActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                Toast.makeText(BlackListActivity.this, e.message, Toast.LENGTH_SHORT).show();
            } catch (FException e) {
                // TODO: handle exception
            } catch (JSONException e) {
                // TODO: handle exception
            }
        }
    }
    
    public void initActionBar() {
        ActionBar actionBar = getActionBar();
        View view = getLayoutInflater().inflate(R.layout.actionbar_grey_layout, null);
        ImageView rightImageView = (ImageView) view.findViewById(R.id.right_imageview);
        rightImageView.setVisibility(View.INVISIBLE);
        TextView titleTextView = (TextView) view.findViewById(R.id.title_textview);
        titleTextView.setText("好友请求验证");
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
        getMenuInflater().inflate(R.menu.confirm_friend_list, menu);
        return true;
    }
    
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        List<String> contactIds = new ArrayList<String>();
        for (int i = 0; i < mCurrentList.size(); i ++) {
            if (mHasSelected.get(i)) {
                contactIds.add("" + mCurrentList.get(i).contactId);
            }
        }
        new RemoveBlockThread(contactIds).start();
        super.onBackPressed();
    }
    
    public class RemoveBlockThread extends Thread {
        private List<String> contactIds;
        
        public RemoveBlockThread(List<String> contactIds) {
            this.contactIds = contactIds;
        }
        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                client.userUnBlockContacts(contactIds);
                runOnUiThread(new Runnable() {
                    public void run() {
                        new RefreshDataThread().start();
                    }
                });
            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                final InvalidAccessToken temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(BlackListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(BlackListActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(BlackListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (FException e) {
                // TODO: handle exception
            }
        }
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
            Contact contact = mCurrentList.get(position);
            View view = getLayoutInflater().inflate(R.layout.confirm_friend_list_item_layout, null);
            ImageView avatarImageView = (ImageView) view.findViewById(R.id.avatar_imageview);
            TextView nameTextView = (TextView) view.findViewById(R.id.name_textview);
            TextView contentTextView = (TextView) view.findViewById(R.id.content_textview);
            ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
            
            if (mHasAvatar.get(position) == false) {
                new RefreshAvatarThread(position, contact.contactId, avatarImageView).start();
            } else {
                if (!mAvatarUrl.get(position).endsWith("/")) {
                    MainApplication.imageLoader.displayImage(mAvatarUrl.get(position), avatarImageView);
                }
            }
            nameTextView.setText(contact.displayName);
            contentTextView.setText(contact.currentTitle);
            if (mHasSelected.get(position)) {
                imageView.setImageResource(R.drawable.icon_check_circle_blue);
            } else {
                imageView.setImageResource(R.drawable.icon_check_circle_gray);
            }
            final Integer pos = position;
            imageView.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    mHasSelected.set(pos, !mHasSelected.get(pos));
                    notifyDataSetChanged();
                }
            });
            
            return view;
        }
        
        
    }

}
