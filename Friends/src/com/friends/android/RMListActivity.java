package com.friends.android;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONObject;

import com.friends.android.internal.Constans;
import com.friends.android.internal.FClient;
import com.friends.android.internal.FClient.FException;
import com.friends.android.internal.FClient.InvalidAccessToken;
import com.friends.android.internal.FClient.UnknowServerError;
import com.friends.android.object.RewardMessage;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RMListActivity extends Activity {

    public static final String DEBUG_TAG = "RMListActivity";
    
    private ListView mListView;
    private ListAdapter mListAdapter;
    private List<RewardMessage> mCurrentList = new ArrayList<RewardMessage>();
    private List<String> mAvatarList = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rmlist);
        initActionBar();
        
        
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
                mAvatarList.set(position, imageUrl);
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (!imageUrl.endsWith("/")) {
                            MainApplication.imageLoader.displayImage(mAvatarList.get(position), avatarImageView);
                        }
                    }
                });
            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                final InvalidAccessToken temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(RMListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(RMListActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(RMListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
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
                /*qsword*/
                JSONObject jsonObject = new JSONObject(client.messageGetRMs(1, 20));
                JSONArray jsonArray = jsonObject.getJSONArray("reward_messages");
                mCurrentList.clear();
                for (int i = 0; i < jsonArray.length(); i ++) {
                    RewardMessage rewardMessage = new RewardMessage();
                    rewardMessage.fromJSON(jsonArray.getJSONObject(i));
                    mCurrentList.add(rewardMessage);
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
                        Toast.makeText(RMListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(RMListActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(RMListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
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
        ActionBar actionBar = getActionBar();
        View view = getLayoutInflater().inflate(R.layout.actionbar_blue_layout, null);
        ImageView rightImageView = (ImageView) view.findViewById(R.id.right_imageview);
        rightImageView.setVisibility(View.INVISIBLE);
        TextView titleTextView = (TextView) view.findViewById(R.id.title_textview);
        titleTextView.setText("信息");
        ImageView leftImageView = (ImageView) view.findViewById(R.id.left_imageview);
        leftImageView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ActionBar.LayoutParams lp = new LayoutParams(android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        actionBar.setCustomView(view, lp);
        //actionBar.setDisplayOptions(null);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rmlist, menu);
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
            View view = getLayoutInflater().inflate(R.layout.rm_list_item_layout, null);
            final RewardMessage rewardMessage = mCurrentList.get(position);
            ImageView avatarImageView = (ImageView) view.findViewById(R.id.avatar_imageview);
            if (mAvatarList.get(position).equals("")) {
                new RefreshAvatarThread(position, rewardMessage.publisher.contactId, avatarImageView).start();
            } else if (mAvatarList.get(position).endsWith("/")) {
                
            } else {
                MainApplication.imageLoader.displayImage(mAvatarList.get(position), avatarImageView);
            }
            TextView nameTextView = (TextView) view.findViewById(R.id.name_textview);
            TextView countTextView = (TextView) view.findViewById(R.id.count_textview);
            TextView dateTextView = (TextView) view.findViewById(R.id.date_textview);
            TextView contentTextView = (TextView) view.findViewById(R.id.content_textview);
            nameTextView.setText(rewardMessage.publisher.displayName);
            countTextView.setText("￥" + rewardMessage.reward);
            dateTextView.setText(rewardMessage.createdAtStr.substring(0, 10));
            
            contentTextView.setText(rewardMessage.content);
            view.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RMListActivity.this, DaqiaoChatActivity.class);
                    intent.putExtra(Constans.REWARD_MESSAGE_ID, rewardMessage.rmId);
                    intent.putExtra(Constans.CONTENT, rewardMessage.publisher.displayName);
                    startActivity(intent);
                }
            });
            return view;
        }
        
    }

}
