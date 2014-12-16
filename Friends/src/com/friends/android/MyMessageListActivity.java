package com.friends.android;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.friends.android.internal.Constans;
import com.friends.android.internal.FClient;
import com.friends.android.internal.FClient.FException;
import com.friends.android.internal.FClient.InvalidAccessToken;
import com.friends.android.internal.FClient.UnknowServerError;
import com.friends.android.object.RewardMessage;

public class MyMessageListActivity extends Activity {
    
    public static final String DEBUG_TAG = "MyMessageListActivity";
    
    private ListView mListView;
    private ListAdapter mListAdapter;
    private List<RewardMessage> mCurrentList = new ArrayList<RewardMessage>();
    private List<String> mAvatarList = new ArrayList<String>();
    private String friendCount;
    private String friend2Count;
    
    public void onSaveInstanceState(Bundle savedInstanceState) {  
        // Save away the original text, so we still have it if the activity  
        // needs to be killed while paused.  
      savedInstanceState.putString(Constans.FRIEND_COUNT, friendCount);
      savedInstanceState.putString(Constans.FRIEND_2_COUNT, friend2Count);
      super.onSaveInstanceState(savedInstanceState);  
      Log.e(DEBUG_TAG, "save");  
    }    
    @Override  
    public void onRestoreInstanceState(Bundle savedInstanceState) {  
      super.onRestoreInstanceState(savedInstanceState);  
      friendCount = savedInstanceState.getString(Constans.FRIEND_COUNT);
      friend2Count = savedInstanceState.getString(Constans.FRIEND_2_COUNT);
      Log.e(DEBUG_TAG, "onRestoreInstanceState+IntTest");  
    }  
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message);
        initActionBar();
        friendCount = getIntent().getStringExtra(Constans.FRIEND_COUNT);
        friend2Count = getIntent().getStringExtra(Constans.FRIEND_2_COUNT);
        if (friend2Count != null && friendCount != null) {
            new AlertDialog.Builder(MyMessageListActivity.this) 
            .setMessage("信息将发给" + friendCount + "个朋友，和" + friend2Count + "个二度朋友")
            .setPositiveButton("确定", null)
            .show();
        }
        
        mListView = (ListView) findViewById(R.id.listview);
        mListAdapter = new ListAdapter();
        mListView.setAdapter(mListAdapter);
        
        new RefreshDataThread().start();
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
        rightImageView.setImageResource(R.drawable.icon_logo);
        rightImageView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyMessageListActivity.this, NewMessageActivity.class);
                startActivity(intent);
            }
        });
        TextView titleTextView = (TextView) view.findViewById(R.id.title_textview);
        titleTextView.setText("我的信息");
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
        getMenuInflater().inflate(R.menu.my_message, menu);
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
            View view = getLayoutInflater().inflate(R.layout.my_message_list_item_layout, null);
            final RewardMessage rewardMessage = mCurrentList.get(position);
            ImageView avatarImageView = (ImageView) view.findViewById(R.id.avatar_imageview);
            if (!mAvatarList.get(position).equals("") && !mAvatarList.get(position).endsWith("/")) {
                MainApplication.imageLoader.displayImage(mAvatarList.get(position), avatarImageView);
            } else {
                new RefreshAvatarThread(position, rewardMessage.publisher.contactId, avatarImageView).start();
            }
            TextView contentTextView = (TextView) view.findViewById(R.id.content_textview);
            TextView dateTextView = (TextView) view.findViewById(R.id.date_textview);
            contentTextView.setText(rewardMessage.content);
            dateTextView.setText(rewardMessage.createdAtStr.substring(0, 10));
            final int pos = position;
            view.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(MyMessageListActivity.this, MyMessageDetailActivity.class);
                    intent.putExtra(Constans.REWARD_MESSAGE_ID, rewardMessage.rmId);
                    intent.putExtra(Constans.URL, mAvatarList.get(pos));
                    startActivity(intent);
                }
            });
            
            view.setOnLongClickListener(new OnLongClickListener() {
                
                @Override
                public boolean onLongClick(View v) {
                    // TODO Auto-generated method stub
                    new AlertDialog.Builder(MyMessageListActivity.this) 
                    .setTitle("警告")
                    .setMessage("确定删除吗？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            new DeleteMessageThread(rewardMessage.rmId).start();
                        }
                    })
                    .setNegativeButton("否", null)
                    .show();
                    
                    return true;
                }
            });
            return view;
        }
        
    }
    
    public class DeleteMessageThread extends Thread {
        
        private String rmId;
        
        public DeleteMessageThread(String rmId) {
            this.rmId = rmId;
        }
        
        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                String response = client.messageCloseRM(rmId);
                Log.e(DEBUG_TAG, response);
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
                        Toast.makeText(MyMessageListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(MyMessageListActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MyMessageListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
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
                JSONObject jsonObject = new JSONObject(client.messageGetMyRMs(1, 20));
                JSONArray jsonArray = jsonObject.getJSONArray("reward_messages");
                mCurrentList.clear();
                mAvatarList.clear();
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
                        Toast.makeText(MyMessageListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(MyMessageListActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MyMessageListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (FException e) {
                // TODO: handle exception
            } catch (JSONException e) {
                // TODO: handle exception
            }
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
                            MainApplication.imageLoader.displayImage(mAvatarList.get(position), avatarImageView);
                        }
                    }
                });
            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                final InvalidAccessToken temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MyMessageListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(MyMessageListActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MyMessageListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (FException e) {
                // TODO: handle exception
            }
        }
    }

}
