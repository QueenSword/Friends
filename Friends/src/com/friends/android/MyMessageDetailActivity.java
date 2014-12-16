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
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.friends.android.MyMessageListActivity.DeleteMessageThread;
import com.friends.android.MyMessageListActivity.RefreshDataThread;
import com.friends.android.internal.Constans;
import com.friends.android.internal.FClient;
import com.friends.android.internal.FClient.FException;
import com.friends.android.internal.FClient.InvalidAccessToken;
import com.friends.android.internal.FClient.UnknowServerError;
import com.friends.android.object.Communication;
import com.friends.android.object.RewardMessage;

public class MyMessageDetailActivity extends Activity {

    public static final String DEBUG_TAG = "MyMessageDetailActivity";
    private String rmId;
    private String avatarUrl;
    private LinearLayout mEmptyLayout;
    private ListView mListView;
    private ListAdapter mListAdapter;
    private List<String> mAvatarList = new ArrayList<String>();
    private List<Communication> mCurrentList = new ArrayList<Communication>();
    private LinearLayout mAddLinearLayout;
    private String[] mFields;
    private RewardMessage mRewardMessage;
    
    
    public void onSaveInstanceState(Bundle savedInstanceState) {  
        // Save away the original text, so we still have it if the activity  
        // needs to be killed while paused.  
      savedInstanceState.putString(Constans.REWARD_MESSAGE_ID, rmId);
      savedInstanceState.putString(Constans.URL, avatarUrl);
      
      super.onSaveInstanceState(savedInstanceState);  
      Log.e(DEBUG_TAG, "save");  
    }    
    @Override  
    public void onRestoreInstanceState(Bundle savedInstanceState) {  
      super.onRestoreInstanceState(savedInstanceState);  
      rmId = savedInstanceState.getString(Constans.REWARD_MESSAGE_ID);
      avatarUrl = savedInstanceState.getString(Constans.URL);
      Log.e(DEBUG_TAG, "onRestoreInstanceState+IntTest");  
    }  

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_message_detail);
        initActionBar();
        rmId = getIntent().getStringExtra(Constans.REWARD_MESSAGE_ID);
        avatarUrl = getIntent().getStringExtra(Constans.URL);
        mFields = getResources().getStringArray(R.array.field_array);

        ImageView avatarImageView = (ImageView) findViewById(R.id.avatar_imageview);
        MainApplication.imageLoader.displayImage(avatarUrl, avatarImageView);
        
        mEmptyLayout = (LinearLayout) findViewById(R.id.empty_linearlayout);
        mListView = (ListView) findViewById(R.id.listview);
        mListAdapter = new ListAdapter();
        mListView.setAdapter(mListAdapter);

        mEmptyLayout.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
        
        mAddLinearLayout = (LinearLayout) findViewById(R.id.add_linearlayout);
        mAddLinearLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Dialog dialog = new Dialog(MyMessageDetailActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.select_reward_price_layout);
                dialog.show();
                ImageView closeImageView = (ImageView) dialog.findViewById(R.id.close_imageview);
                TextView textView1 = (TextView) dialog.findViewById(R.id.textview1);
                TextView textView2 = (TextView) dialog.findViewById(R.id.textview2);
                TextView textView3 = (TextView) dialog.findViewById(R.id.textview3);
                TextView textView4 = (TextView) dialog.findViewById(R.id.textview4);
                
                textView1.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        new AddRewardThread(0).start();
                        dialog.dismiss();
                    }
                });
                
                textView2.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        new AddRewardThread(5).start();
                        dialog.dismiss();
                    }
                });
                
                textView3.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        new AddRewardThread(10).start();
                        dialog.dismiss();
                    }
                });
                
                textView4.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        new AddRewardThread(20).start();
                        dialog.dismiss();
                    }
                });
                closeImageView.setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });

            }
        });

        new RefreshDataThread().start();
    }
    

    public void RefreshHeaderView(RewardMessage rewardMessage) {
        TextView dateTextView = (TextView) findViewById(R.id.date_textview);
        TextView countTextView = (TextView) findViewById(R.id.count_textview);
        TextView contentTextView = (TextView) findViewById(R.id.content_textview);
        TextView hintTextView = (TextView) findViewById(R.id.textview);
        dateTextView.setText(rewardMessage.createdAtStr.substring(0, 10));
        countTextView.setText("￥" + rewardMessage.reward);
        contentTextView.setText(rewardMessage.content);
        hintTextView.setText("您的信息于" + rewardMessage.createdAtStr.substring(0, 10) + "发布，仍无人回应");
        LinearLayout scrollLinearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        scrollLinearLayout.removeAllViews();
        try {
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.setMargins(5, 5, 5, 5);
            if (rewardMessage.fields.getString(0).equals("0")) {
                TextView textView = new TextView(MyMessageDetailActivity.this);
                textView.setPaddingRelative(5, 5, 5, 5);
                textView.setPadding(5, 5, 5, 5);
                textView.setBackground(getResources().getDrawable(R.drawable.textview_corner));
                textView.setLayoutParams(llp);
                textView.setText("所有行业");
                scrollLinearLayout.addView(textView);
            } else {
                for (int i = 0; i < rewardMessage.fields.length(); i ++) {
                    TextView textView = new TextView(MyMessageDetailActivity.this);
                    textView.setPaddingRelative(5, 5, 5, 5);
                    textView.setPadding(5, 5, 5, 5);
                    
                    textView.setLayoutParams(llp);
                    String field = (String) rewardMessage.fields.get(i);
                    textView.setBackground(getResources().getDrawable(R.drawable.textview_corner));
                    textView.setText(mFields[Integer.parseInt(field) - 1]);
                    scrollLinearLayout.addView(textView);
                }
            }
            
            
        } catch (JSONException e) {
            // TODO: handle exception
        }
        

    }

    public class AddRewardThread extends Thread {
        private Integer reward;

        public AddRewardThread(Integer reward) {
            this.reward = reward;
        }

        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                client.messageAddReward(Integer.parseInt(rmId), reward);
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
                        Toast.makeText(MyMessageDetailActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(MyMessageDetailActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MyMessageDetailActivity.this, temp.message, Toast.LENGTH_SHORT).show();
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
                final RewardMessage rewardMessage = new RewardMessage();
                rewardMessage.fromJSON(new JSONObject(client.messageGetRM(rmId)));
                runOnUiThread(new Runnable() {
                    public void run() {
                        RefreshHeaderView(rewardMessage);
                    }
                });
                
                JSONObject jsonObject = new JSONObject(client.messageGetCommunicationsForRM(1, 20, rmId));
                Log.e(DEBUG_TAG, jsonObject.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("communications");
                mCurrentList.clear();
                for (int i = 0; i < jsonArray.length(); i ++) {
                    Communication communication = new Communication();
                    communication.fromJSON(jsonArray.getJSONObject(i));
                    mCurrentList.add(communication);
                    mAvatarList.add("");
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (mCurrentList.size() > 0) {
                            mEmptyLayout.setVisibility(View.GONE);
                            mListView.setVisibility(View.VISIBLE);
                        } else {
                            mListView.setVisibility(View.GONE);
                            mEmptyLayout.setVisibility(View.VISIBLE);
                        }
                        mListAdapter.notifyDataSetChanged();
                    }
                });
                /*
                jsonObject = new JSONObject(client.messageGetRM(rmId));
                Log.e(DEBUG_TAG, jsonObject.toString());
                if (!jsonObject.isNull("reward_message")) {
                    mRewardMessage = new RewardMessage();
                    mRewardMessage.fromJSON(jsonObject.getJSONObject("reward_message"));
                } else if (!jsonObject.isNull("reward_message_id")) {
                    mRewardMessage = new RewardMessage();
                    mRewardMessage.fromJSON(jsonObject);
                }
                jsonObject = new JSONObject(client.messageGetCommunicationsForRM(1, 20, rmId));
                jsonArray  = jsonObject.getJSONArray("communications");
                mCommunications.clear();
                for (int i = 0; i < jsonArray.length(); i ++) {
                    Communication communication = new Communication();
                    communication.fromJSON(jsonArray.getJSONObject(i));
                    mCommunications.add(communication);
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        mListAdapter.notifyDataSetChanged();
                    }
                });*/

            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                final InvalidAccessToken temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MyMessageDetailActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(MyMessageDetailActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MyMessageDetailActivity.this, temp.message, Toast.LENGTH_SHORT).show();
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
                finish();
            }
        });
        ImageView rightImageView = (ImageView) view.findViewById(R.id.right_imageview);
        rightImageView.setVisibility(View.INVISIBLE);
        TextView titleTextView = (TextView) view.findViewById(R.id.title_textview);
        titleTextView.setText("我的信息");
        ActionBar.LayoutParams lp = new LayoutParams(android.app.ActionBar.LayoutParams.MATCH_PARENT,
                android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        mActionBar.setCustomView(view, lp);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setDisplayShowCustomEnabled(true);
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
                        Toast.makeText(MyMessageDetailActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(MyMessageDetailActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MyMessageDetailActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (FException e) {
                // TODO: handle exception
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_message_detail, menu);
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
            View view = getLayoutInflater().inflate(R.layout.my_message_detail_list_item_layout, null);
            final Communication communication = mCurrentList.get(position);
            ImageView avatarImageView = (ImageView) view.findViewById(R.id.avatar_imageview);
            if (!mAvatarList.get(position).equals("") && !mAvatarList.get(position).endsWith("/")) {
                MainApplication.imageLoader.displayImage(mAvatarList.get(position), avatarImageView);
            } else {
                new RefreshAvatarThread(position, communication.target.contactId, avatarImageView).start();
            }
            TextView nameTextView = (TextView) view.findViewById(R.id.name_textview);
            TextView contentTextView = (TextView) view.findViewById(R.id.content_textview);
            TextView countTextView = (TextView) view.findViewById(R.id.count_textview);
            if (communication.target.displayName == null || communication.target.displayName.equals("")) {
                nameTextView.setText(communication.builder.displayName + "的朋友");
            } else {
                nameTextView.setText(communication.target.displayName);
            }
            
            contentTextView.setText(communication.target.currentTitle);
            countTextView.setText("" + communication.unreadChatCount);
            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyMessageDetailActivity.this, ChatListActivity.class);
                    intent.putExtra(Constans.COMMUNICATION_ID, communication.communicationId);
                    intent.putExtra(Constans.REWARD_MESSAGE_ID, communication.rm.rmId);
                    startActivity(intent);
                }
            });
            
            view.setOnLongClickListener(new OnLongClickListener() {
                
                @Override
                public boolean onLongClick(View v) {
                    // TODO Auto-generated method stub
                    new AlertDialog.Builder(MyMessageDetailActivity.this) 
                    .setTitle("警告")
                    .setMessage("确定删除吗？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            new DeleteMessageThread(communication.communicationId).start();
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
        
        private String communicationId;
        
        public DeleteMessageThread(String communicationId) {
            this.communicationId = communicationId;
        }
        
        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                String response = client.messageCloseCommunication(communicationId);
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
                        Toast.makeText(MyMessageDetailActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(MyMessageDetailActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MyMessageDetailActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (FException e) {
                // TODO: handle exception
            }
        }
    }
}
