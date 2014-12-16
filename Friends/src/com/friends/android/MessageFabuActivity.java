package com.friends.android;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.friends.android.MyMessageDetailActivity.AddRewardThread;
import com.friends.android.internal.Constans;
import com.friends.android.internal.FClient;
import com.friends.android.internal.FClient.FException;
import com.friends.android.internal.FClient.InvalidAccessToken;
import com.friends.android.internal.FClient.UnknowServerError;
import com.friends.android.object.Contact;
import com.friends.android.object.RewardMessage;

public class MessageFabuActivity extends Activity {

    public static final String DEBUG_TAG = "MessageFabuActivity";

    private List<Contact> mCurrentList = new ArrayList<Contact>();
    private List<String> mAvatarList = new ArrayList<String>();
    private ListView mListView;
    private ListAdapter mListAdapter;
    private String content;
    private String fields;
    private String[] mFields;
    private List<Contact> mBlackList = new ArrayList<Contact>();
    private List<Boolean> mInviteList = new ArrayList<Boolean>();
    private String blackContent = null;
    private TextView countTextView;
    private RewardMessage mRewardMessage;
    private String srcActivity;
    
    public void onSaveInstanceState(Bundle savedInstanceState) {  
        // Save away the original text, so we still have it if the activity  
        // needs to be killed while paused.  
      savedInstanceState.putString(Constans.SRC_ACTIVITY, srcActivity);
      savedInstanceState.putString(Constans.CONTENT, content);
      savedInstanceState.putString(Constans.FIELDS, fields);
      super.onSaveInstanceState(savedInstanceState);  
      Log.e(DEBUG_TAG, "save");  
    }    
    @Override  
    public void onRestoreInstanceState(Bundle savedInstanceState) {  
      super.onRestoreInstanceState(savedInstanceState);  
      srcActivity = savedInstanceState.getString(Constans.SRC_ACTIVITY);
      content = savedInstanceState.getString(Constans.CONTENT);
      fields = savedInstanceState.getString(Constans.FIELDS);
      Log.e(DEBUG_TAG, "onRestoreInstanceState+IntTest");  
    }  
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_fabu);
        initActionBar();
        srcActivity = getIntent().getStringExtra(Constans.SRC_ACTIVITY);
        content = getIntent().getStringExtra(Constans.CONTENT);
        fields = getIntent().getStringExtra(Constans.FIELDS);
        mFields = getResources().getStringArray(R.array.field_array);

        mListView = (ListView) findViewById(R.id.listview);
        mListAdapter = new ListAdapter();
        mListView.setAdapter(mListAdapter);

        RefreshHeaderView();
        new RefreshDataThread().start();
    }

    public class RefreshAvatarThread extends Thread {

        public ImageView avatarImageView;

        public RefreshAvatarThread(ImageView avatarImageView) {
            this.avatarImageView = avatarImageView;
        }

        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                String response = client.getImageUrl("" + MainApplication.userId);
                final String imageUrl = Constans.API_HOST + response.substring(1, response.length() - 1);

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
                        Toast.makeText(MessageFabuActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(MessageFabuActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MessageFabuActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (FException e) {
                // TODO: handle exception
            }
        }
    }

    public void RefreshHeaderView() {
        ImageView avatarImageView = (ImageView) findViewById(R.id.avatar_imageview);
        new RefreshAvatarThread(avatarImageView).start();
        TextView dateTextView = (TextView) findViewById(R.id.date_textview);
        countTextView = (TextView) findViewById(R.id.count_textview);
        TextView contentTextView = (TextView) findViewById(R.id.content_textview);
        Date d = new Date();
        System.out.println(d);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateTextView.setText(sdf.format(d));
        countTextView.setText("￥0");
        countTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Dialog dialog = new Dialog(MessageFabuActivity.this);
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
                        countTextView.setText("￥0");
                        dialog.dismiss();
                    }
                });
                
                textView2.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        countTextView.setText("￥5");
                        dialog.dismiss();
                    }
                });
                
                textView3.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        countTextView.setText("￥10");
                        dialog.dismiss();
                    }
                });
                
                textView4.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        countTextView.setText("￥20");
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

        
        contentTextView.setText(content);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        String[] fieldList = fields.split(",");
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.setMargins(5, 5, 5, 5);

        if (fieldList[0].equals("0")) {
            TextView textView = new TextView(MessageFabuActivity.this);
            textView.setText("全部行业");
            textView.setPadding(5, 5, 5, 5);
            textView.setLayoutParams(llp);
            textView.setBackground(getResources().getDrawable(R.drawable.textview_corner));
            linearLayout.addView(textView);
        } else {
            for (int i = 0; i < fieldList.length; i++) {
                TextView textView = new TextView(MessageFabuActivity.this);
                textView.setText(mFields[Integer.parseInt(fieldList[i]) - 1]);
                textView.setPadding(5, 5, 5, 5);
                textView.setLayoutParams(llp);
                textView.setBackground(getResources().getDrawable(R.drawable.textview_corner));
                linearLayout.addView(textView);
            }
        }

        Switch switch1 = (Switch) findViewById(R.id.public_switch);
        switch1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked == false) {
                    Log.e(DEBUG_TAG, "false");
                    Intent intent = new Intent(MessageFabuActivity.this, FriendListActivity.class);
                    intent.putExtra(Constans.CONTENT, blackContent);
                    startActivityForResult(intent, 0);
                } else {
                    mBlackList.clear();
                    blackContent = null;
                    LinearLayout blackLinearLayout = (LinearLayout) findViewById(R.id.black_linearlayout);
                    blackLinearLayout.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (0 == resultCode && requestCode == 0) {
            if (data == null) {
                return;
            }
            blackContent = data.getExtras().getString(Constans.CONTENT);
            try {
                JSONArray jsonArray = new JSONArray(blackContent);
                mBlackList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    Contact contact = new Contact();
                    contact.fromJSON(jsonArray.getJSONObject(i));
                    mBlackList.add(contact);
                }
                if (mBlackList != null && mBlackList.size() > 0) {
                    LinearLayout blackLinearLayout = (LinearLayout) findViewById(R.id.black_linearlayout);
                    TextView blackTextView = (TextView) findViewById(R.id.black_textview);
                    blackLinearLayout.setVisibility(View.VISIBLE);
                    blackTextView.setText("已经选择规避" + mBlackList.get(0).displayName + "等" + mBlackList.size() + "位好友");
                    blackLinearLayout.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MessageFabuActivity.this, FriendListActivity.class);
                            intent.putExtra(Constans.CONTENT, blackContent);
                            startActivityForResult(intent, 0);

                        }
                    });
                } else {
                    LinearLayout blackLinearLayout = (LinearLayout) findViewById(R.id.black_linearlayout);
                    blackLinearLayout.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                // TODO: handle exception
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class AddRMThread extends Thread {

        
        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                RewardMessage rewardMessage = new RewardMessage();
                //rewardMessage.rmId = 
                rewardMessage.content = content;
                rewardMessage.type = RewardMessage.RM_TYPE_NORMAL;
                rewardMessage.reward = Integer.parseInt(countTextView.getText().toString().substring(1, countTextView.getText().toString().length()));
                if (mBlackList == null || mBlackList.size() <= 0) {
                    rewardMessage.isPrivacy = 0;
                } else {
                    rewardMessage.isPrivacy = 1;
                }
                String[] fieldList = fields.split(",");
                rewardMessage.fields = new JSONArray();
                for (int i = 0; i < fieldList.length; i++) {
                    rewardMessage.fields.put(Integer.parseInt(fieldList[i]));
                }
                rewardMessage.inviteIds = new JSONArray();
                for (int i = 0; i < mInviteList.size(); i ++) {
                    if (mInviteList.get(i) == true) {
                        rewardMessage.inviteIds.put(mCurrentList.get(i).contactId);
                    }
                }
                
                rewardMessage.contactIds = new JSONArray();
                for (int i = 0; i < mBlackList.size(); i ++) {
                    rewardMessage.contactIds.put(mBlackList.get(i).contactId);
                }
                
                
                JSONObject jsonObject = new JSONObject(client.messageDoAddRm(rewardMessage));
                if (!jsonObject.isNull("reward_message")) {
                    mRewardMessage = new RewardMessage();
                    mRewardMessage.fromJSON(jsonObject.getJSONObject("reward_message"));
                } else if (!jsonObject.isNull("reward_message_id")) {
                    mRewardMessage = new RewardMessage();
                    mRewardMessage.fromJSON(jsonObject);
                }
                
                runOnUiThread(new Runnable() {
                    public void run() {
                        Intent intent;
                        if (srcActivity.equals(Constans.MAIN_ACTIVITY)) {
                            intent = new Intent(MessageFabuActivity.this, MessageFabuActivity.class);
                        } else {
                            intent = new Intent(MessageFabuActivity.this, MyMessageListActivity.class);
                        }
                        intent.putExtra(Constans.FRIEND_COUNT, "" + mRewardMessage.friendCount);
                        intent.putExtra(Constans.FRIEND_2_COUNT, "" + mRewardMessage.friend2Count);
                        startActivity(intent);
                    }
                });
            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                final InvalidAccessToken temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MessageFabuActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(MessageFabuActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MessageFabuActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (FException e) {
                // TODO: handle exception
            } catch (JSONException e) {
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
                JSONObject jsonObject = new JSONObject(client.messageGetReferal(1, fields));
                JSONArray jsonArray = jsonObject.getJSONArray("contacts");
                Log.e(DEBUG_TAG, jsonObject.toString());
                mCurrentList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    Contact contact = new Contact();
                    contact.fromJSON(jsonArray.getJSONObject(i));
                    mCurrentList.add(contact);
                    mInviteList.add(false);
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
                        Toast.makeText(MessageFabuActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(MessageFabuActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MessageFabuActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (FException e) {
                // TODO: handle exception
            } catch (JSONException e) {
                // TODO: handle exception
                e.printStackTrace();
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
        View view = getLayoutInflater().inflate(R.layout.actionbar_grey_layout, null);
        ImageView rightImageView = (ImageView) view.findViewById(R.id.right_imageview);
        rightImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new AddRMThread().start();
            }
        });
        TextView titleTextView = (TextView) view.findViewById(R.id.title_textview);
        titleTextView.setText("信息发布");
        ImageView leftImageView = (ImageView) view.findViewById(R.id.left_imageview);
        leftImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ActionBar.LayoutParams lp = new LayoutParams(android.app.ActionBar.LayoutParams.MATCH_PARENT,
                android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        actionBar.setCustomView(view, lp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
    }
    
    public class RefreshAvatarThread1 extends Thread {
        public int position;
        public Integer contactId;
        public ImageView avatarImageView;

        public RefreshAvatarThread1(int position, Integer contactID, ImageView avatarImageView) {
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
                        Toast.makeText(MessageFabuActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(MessageFabuActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MessageFabuActivity.this, temp.message, Toast.LENGTH_SHORT).show();
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
            // TODO Auto-generated method stub
            View view = getLayoutInflater().inflate(R.layout.message_fabu_list_item_layout, null);
            Contact contact = mCurrentList.get(position);
            ImageView avatarImageView = (ImageView) view.findViewById(R.id.avatar_imageview);
            if (!mAvatarList.get(position).equals("") && !mAvatarList.get(position).endsWith("/")) {
                MainApplication.imageLoader.displayImage(mAvatarList.get(position), avatarImageView);
            } else {
                new RefreshAvatarThread1(position, contact.contactId, avatarImageView).start();
            }
            TextView nameTextView = (TextView) view.findViewById(R.id.name_textview);
            TextView contentTextView = (TextView) view.findViewById(R.id.content_textview);
            nameTextView.setText(contact.displayName);
            contentTextView.setText(contact.currentTitle);
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linearlayout);
            ImageView checkImageView = (ImageView) view.findViewById(R.id.check_imageview);
            TextView inviteTextView = (TextView) view.findViewById(R.id.invite_textview);
            if (mInviteList.get(position) == true) {
                linearLayout.setBackgroundColor(R.color.invite_color);
                checkImageView.setImageResource(R.drawable.icon_check_b_8);
            } else {
                linearLayout.setBackgroundColor(R.color.light_grey);
                checkImageView.setImageResource(R.drawable.icon_check_g2_8);
            }
            final int pos = position;
            linearLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    mInviteList.set(pos, !mInviteList.get(pos));
                    notifyDataSetChanged();
                }
            });
            return view;
        }
    }
}
