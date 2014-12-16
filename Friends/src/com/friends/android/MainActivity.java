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
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.friends.android.internal.Constans;
import com.friends.android.internal.FClient;
import com.friends.android.internal.FClient.FException;
import com.friends.android.internal.FClient.InvalidAccessToken;
import com.friends.android.internal.FClient.UnknowServerError;
import com.friends.android.object.Communication;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends Activity {
    public static final String DEBUT_TAG = "MainActivity";
    
    //public static final EventBus mBus = new EventBus();

    private SlidingMenu mLeftSlidingMenu;

    private View mSystemMessageView;
    private View mNewMessageView;
    private View mNewCommunicationView;
    private View mNewRmCommunicationView;
    private TextView countTextView0;
    private TextView countTextView1;
    private TextView countTextView2;
    private TextView countTextView3;
    
    private ListView mListView;
    private List<Boolean> mHasAvatar = new ArrayList<Boolean>();
    private List<String> mAvatarUrl = new ArrayList<String>();

    private List<Boolean> mHas1 = new ArrayList<Boolean>();
    private List<String> mAvatar1 = new ArrayList<String>();
    private List<Boolean> mHas2 = new ArrayList<Boolean>();
    private List<String> mAvatar2 = new ArrayList<String>();
    private List<Boolean> mHas3 = new ArrayList<Boolean>();
    private List<String> mAvatar3 = new ArrayList<String>();

    private ListAdapter mListAdapter;
    private HomeMessageResponse mHomeMessageResponse;
    private MyCommunications mMyCommunications;
    private LinearLayout.LayoutParams lp;
    private ViewGroup.LayoutParams mLayoutParams;
    
    private String friendCount;
    private String friend2Count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //mBus.register(this);
        
        friendCount = getIntent().getStringExtra(Constans.FRIEND_COUNT);
        friend2Count = getIntent().getStringExtra(Constans.FRIEND_2_COUNT);
        if (friend2Count != null && friendCount != null) {
            new AlertDialog.Builder(MainActivity.this) 
            .setMessage("信息将发给" + friendCount + "个朋友，和" + friend2Count + "个二度朋友")
            .setPositiveButton("确定", null)
            .show();
        }
        if (getIntent().getStringExtra(Constans.SHOW_FEED_BACK) != null) {
            new AlertDialog.Builder(MainActivity.this).setMessage("感谢您的反馈，我们会尽快给您回复!").setNegativeButton("确定", null).show();
        }
        initSlidingMenu();
        initActionBar();
        lp = new LinearLayout.LayoutParams(110, 110);
        mLayoutParams = new LayoutParams(100, 100, 1);
        lp.setMargins(5, 5, 5, 5);
        
        mSystemMessageView = (View) findViewById(R.id.system_message_item);
        mSystemMessageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                countTextView0.setVisibility(View.GONE);
                Intent intent = new Intent(MainActivity.this, SysMessageActivity.class);
                startActivity(intent);
            }
        });
        mNewMessageView = (View) findViewById(R.id.new_message_item);
        mNewMessageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                
                countTextView1.setVisibility(View.GONE);
                Intent intent = new Intent(MainActivity.this, RMListActivity.class);
                startActivity(intent);
            }
        });
        mNewCommunicationView = (View) findViewById(R.id.new_communication_item);
        mNewCommunicationView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                countTextView2.setVisibility(View.GONE);
                Intent intent = new Intent(MainActivity.this, CommunicationListActivity.class);
                startActivity(intent);
            }
        });
        mNewRmCommunicationView = (View) findViewById(R.id.new_rm_communication_item);
        mNewRmCommunicationView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                countTextView3.setVisibility(View.GONE);
                Intent intent = new Intent(MainActivity.this, MyMessageListActivity.class);
                startActivity(intent);
            }
        
        });

        RefreshHomeMessageView();
        
        
        mListView = (ListView) findViewById(R.id.listview);
        mListAdapter = new ListAdapter();
        mListView.setAdapter(mListAdapter);
        
        
        new RefreshDataThread().start();
        //new TestInterfaceThread().start();
    }

    public void RefreshHomeMessageView() {
        View colorView = (View) mSystemMessageView.findViewById(R.id.color_view);
        colorView.setBackgroundColor(getResources().getColor(R.color.system_message_count));
        ImageView imageView0 = (ImageView) mSystemMessageView.findViewById(R.id.imageview);
        imageView0.setImageResource(R.drawable.icon_sys_info);
        countTextView0 = (TextView) mSystemMessageView.findViewById(R.id.count_textview);
        if (mHomeMessageResponse == null ||mHomeMessageResponse.systemMessageCount == null || mHomeMessageResponse.systemMessageCount == 0) {
            countTextView0.setVisibility(View.GONE);
        } else {
            countTextView0.setVisibility(View.VISIBLE);
            countTextView0.setText("" + mHomeMessageResponse.systemMessageCount);
        }
        
        countTextView1 = (TextView) mNewMessageView.findViewById(R.id.count_textview);
        
        TextView textView1 = (TextView) mNewMessageView.findViewById(R.id.textview);
        HorizontalScrollView scrollView1 = (HorizontalScrollView) mNewMessageView.findViewById(R.id.scrollview);
        if (mHomeMessageResponse == null || mHomeMessageResponse.rmPublishers.size() == 0 || (mHomeMessageResponse.newMessageCount != null && mHomeMessageResponse.newMessageCount <= 0)) {
            scrollView1.setVisibility(View.GONE);
            countTextView1.setVisibility(View.GONE);
            textView1.setVisibility(View.VISIBLE);

        } else {
            countTextView1.setVisibility(View.VISIBLE);
            countTextView1.setText("" + mHomeMessageResponse.newMessageCount);
            textView1.setVisibility(View.GONE);
            scrollView1.setVisibility(View.VISIBLE);
            LinearLayout linearLayout = (LinearLayout) mNewMessageView.findViewById(R.id.linearlayout);
            for (int i = 0; i < mHomeMessageResponse.rmPublishers.size(); i++) {
                RMPublisher publisher = mHomeMessageResponse.rmPublishers.get(i);
                ImageView roundImageView = new ImageView(MainActivity.this);
                roundImageView.setLayoutParams(mLayoutParams);
                roundImageView.setImageResource(R.drawable.icon_profile);
                if (mHas1.get(i) == false) {
                    new RefreshImageThread(i, publisher.contactId, 1, roundImageView).start();
                } else {
                    if (!mAvatar1.get(i).endsWith("/")) {
                        MainApplication.imageLoader.displayImage(mAvatar1.get(i), roundImageView);
                    }

                }
                linearLayout.addView(roundImageView, lp);
            }
        }

        countTextView2 = (TextView) mNewCommunicationView.findViewById(R.id.count_textview);
        
        TextView textView2 = (TextView) mNewCommunicationView.findViewById(R.id.textview);
        HorizontalScrollView scrollView2 = (HorizontalScrollView) mNewCommunicationView.findViewById(R.id.scrollview);
        if (mHomeMessageResponse == null || mHomeMessageResponse.communicationProfessors.size() == 0) {
            countTextView2.setVisibility(View.GONE);
            scrollView2.setVisibility(View.GONE);
            textView2.setVisibility(View.VISIBLE);

        } else {
            countTextView2.setVisibility(View.VISIBLE);
            countTextView2.setText("" + mHomeMessageResponse.newCommunicationCount);
            textView2.setVisibility(View.GONE);
            scrollView2.setVisibility(View.VISIBLE);
            LinearLayout linearLayout = (LinearLayout) mNewCommunicationView.findViewById(R.id.linearlayout);
            for (int i = 0; i < mHomeMessageResponse.communicationProfessors.size(); i++) {

                RMPublisher publisher = mHomeMessageResponse.communicationProfessors.get(i);

                ImageView roundImageView = new ImageView(MainActivity.this);
                roundImageView.setLayoutParams(mLayoutParams);
                roundImageView.setImageResource(R.drawable.icon_profile);
                if (mHas2.get(i) == false) {
                    new RefreshImageThread(i, publisher.contactId, 2, roundImageView).start();
                } else {
                    if (!mAvatar2.get(i).endsWith("/")) {
                        MainApplication.imageLoader.displayImage(mAvatar2.get(i), roundImageView);
                    }

                }

                linearLayout.addView(roundImageView, lp);
            }
        }

        countTextView3 = (TextView) mNewRmCommunicationView.findViewById(R.id.count_textview);
        
        TextView textView3 = (TextView) mNewRmCommunicationView.findViewById(R.id.textview);
        HorizontalScrollView scrollView3 = (HorizontalScrollView) mNewRmCommunicationView.findViewById(R.id.scrollview);
        if (mHomeMessageResponse == null || mHomeMessageResponse.myRMCommunicationProfessors.size() == 0|| ((mHomeMessageResponse.newMyRMCommunicationCount != null && mHomeMessageResponse.newMyRMCommunicationCount <= 0))) {
            countTextView3.setVisibility(View.GONE);
            scrollView3.setVisibility(View.GONE);
            textView3.setVisibility(View.VISIBLE);

        } else {
            countTextView3.setVisibility(View.VISIBLE);
            countTextView3.setText("" + mHomeMessageResponse.myRMCommunicationProfessors.size());
            textView3.setVisibility(View.GONE);
            scrollView3.setVisibility(View.VISIBLE);
            LinearLayout linearLayout = (LinearLayout) mNewRmCommunicationView.findViewById(R.id.linearlayout);
            for (int i = 0; i < mHomeMessageResponse.myRMCommunicationProfessors.size(); i++) {
                Integer publisher = mHomeMessageResponse.myRMCommunicationProfessors.get(i);
                ImageView roundImageView = new ImageView(MainActivity.this);
                roundImageView.setImageResource(R.drawable.icon_profile);
                roundImageView.setLayoutParams(mLayoutParams);
                if (mHas3.get(i) == false) {
                    new RefreshImageThread(i, publisher, 3, roundImageView).start();
                } else {
                    if (!mAvatar3.get(i).endsWith("/")) {
                        MainApplication.imageLoader.displayImage(mAvatar3.get(i), roundImageView);
                    }
                }
                linearLayout.addView(roundImageView, lp);
            }
        }
    }

    public class RefreshDataThread extends Thread {
        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                String response = client.messageHomeMessage();
                if (response == null || response.equals("")) {
                    Log.e(DEBUT_TAG, "response is null");
                    return ;
                }
                JSONObject jsonObject = new JSONObject(response);
                mHomeMessageResponse = new HomeMessageResponse(jsonObject);

                mHas1.clear();
                mAvatar1.clear();
                for (int i = 0; i < mHomeMessageResponse.rmPublishers.size(); i++) {
                    mHas1.add(false);
                    mAvatar1.add("");
                }

                mHas2.clear();
                mAvatar2.clear();
                for (int i = 0; i < mHomeMessageResponse.communicationProfessors.size(); i++) {
                    mHas2.add(false);
                    mAvatar2.add("");
                }

                mHas3.clear();
                mAvatar3.clear();
                for (int i = 0; i < mHomeMessageResponse.myRMCommunicationProfessors.size(); i++) {
                    mHas3.add(false);
                    mAvatar3.add("");
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        RefreshHomeMessageView();
                    }
                });

                JSONObject object = new JSONObject(client.messageMyCommunications(1, 30));
                mMyCommunications = new MyCommunications(object);

                mHasAvatar.clear();
                mAvatarUrl.clear();
                for (int i = 0; i < mMyCommunications.communications.size(); i++) {
                    
                    mHasAvatar.add(false);
                    mAvatarUrl.add("");
                }                
                
                runOnUiThread(new Runnable() {
                    public void run() {
                        mListAdapter.notifyDataSetChanged();
                    }
                });
            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                Log.e(DEBUT_TAG, "home message: token");
                final InvalidAccessToken temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
                
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(MainActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
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
        rightImageView.setImageResource(R.drawable.icon_logo);
        rightImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this, NewMessageActivity.class);
                intent.putExtra(Constans.SRC_ACTIVITY, Constans.MAIN_ACTIVITY);
                startActivity(intent);
            }
        });
        TextView titleTextView = (TextView) view.findViewById(R.id.title_textview);
        titleTextView.setText("二度空间");
        ImageView leftImageView = (ImageView) view.findViewById(R.id.left_imageview);
        leftImageView.setImageResource(R.drawable.icon_menu);
        leftImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mLeftSlidingMenu.isMenuShowing()) {
                    mLeftSlidingMenu.showContent();
                } else {
                    mLeftSlidingMenu.showMenu();
                }
            }
        });
        ActionBar.LayoutParams lp = new LayoutParams(android.app.ActionBar.LayoutParams.MATCH_PARENT,
                android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        actionBar.setCustomView(view, lp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    public void initSlidingMenu() {
        mLeftSlidingMenu = new SlidingMenu(this);
        mLeftSlidingMenu.setMode(SlidingMenu.LEFT);
        mLeftSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mLeftSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        mLeftSlidingMenu.setFadeDegree(0.35f);
        mLeftSlidingMenu.setBehindWidth(500);
        View view = getLayoutInflater().inflate(R.layout.sliding_menu_left_layout, null);
        LinearLayout rmLayout = (LinearLayout) view.findViewById(R.id.rm_linearlayout);
        LinearLayout friendLayout = (LinearLayout) view.findViewById(R.id.friend_linearlayout);
        LinearLayout accountLayout = (LinearLayout) view.findViewById(R.id.account_linearlayout);
        LinearLayout settingLayout = (LinearLayout) view.findViewById(R.id.setting_linearlayout);
        rmLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mLeftSlidingMenu.showContent();
            }
        });

        friendLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FriendActivity.class);
                startActivity(intent);
                mLeftSlidingMenu.showContent();
            }
        });

        accountLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                startActivity(intent);
                mLeftSlidingMenu.showContent();
            }
        });

        settingLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                mLeftSlidingMenu.showContent();
            }
        });
        mLeftSlidingMenu.setMenu(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            int count = 0;
            if (mMyCommunications != null) {
                count += mMyCommunications.communications.size();
            }

            return count;
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
            View view = getLayoutInflater().inflate(R.layout.main_list_item_layout, null);
            ImageView avatarImageView = (ImageView) view.findViewById(R.id.avatar_imageview);
            final Communication myCommunication = mMyCommunications.communications.get(position);
            TextView unreadTextView = (TextView) view.findViewById(R.id.unreadcount_textview);
            if (myCommunication.unreadChatCount == null || myCommunication.unreadChatCount == 0) {
                unreadTextView.setVisibility(View.GONE);
            } else {
                unreadTextView.setVisibility(View.VISIBLE);
                unreadTextView.setText("" + myCommunication.unreadChatCount);
            }
            final String avatarUrl = mAvatarUrl.get(position);
            
            if (myCommunication.target == null) {
                Log.e(DEBUT_TAG, "target is null");
            } else {
                Log.e(DEBUT_TAG, "target: " + myCommunication.target.displayName);
            }
            Log.e(DEBUT_TAG, "publisher: " + myCommunication.rm.publisher.displayName);
            Log.e(DEBUT_TAG, "userid: " + MainApplication.userId);
            TextView nameTextView = (TextView) view.findViewById(R.id.name_textview);
            
            if (MainApplication.userId.equals("" + myCommunication.target.contactId)) {
                Log.e(DEBUT_TAG, "use publisher");
                nameTextView.setText(myCommunication.rm.publisher.displayName);
            } else {
                Log.e(DEBUT_TAG, "use target");
                nameTextView.setText(myCommunication.target.displayName);
            }
            
            if (mHasAvatar.get(position) == false) { 
                Log.e(DEBUT_TAG, "has avatar is false");
                if (MainApplication.userId.equals("" + myCommunication.target.contactId)) {
                    Log.e(DEBUT_TAG, "use publisher");
                    new RefreshAvatarThread(position, myCommunication.rm.publisher.contactId, avatarImageView).start();
                } else {
                    Log.e(DEBUT_TAG, "use target");
                    new RefreshAvatarThread(position, myCommunication.target.contactId, avatarImageView).start();
                }
                
            } else {
                if (!mAvatarUrl.get(position).endsWith("/")) {
                    MainApplication.imageLoader.displayImage(mAvatarUrl.get(position), avatarImageView);
                }
            }
            
            TextView dateTextView = (TextView) view.findViewById(R.id.date_textview);
            dateTextView.setText(myCommunication.createdAtStr.substring(0, 10));
            TextView contentTextView = (TextView) view.findViewById(R.id.content_textview);
            contentTextView.setText(myCommunication.rm.content);
            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ChatListActivity.class);
                    
                    intent.putExtra(Constans.REWARD_MESSAGE_ID, myCommunication.rm.rmId);
                    intent.putExtra(Constans.COMMUNICATION_ID, "" + myCommunication.communicationId);
                    if (MainApplication.userId.equals("" + myCommunication.target.contactId)) {
                        intent.putExtra(Constans.CONTENT, myCommunication.rm.publisher.displayName);
                    } else {
                        intent.putExtra(Constans.CONTENT, myCommunication.target.displayName);
                    }
                    
                    if (avatarUrl != null) {
                        intent.putExtra(Constans.URL, avatarUrl);
                    }
                    
                    startActivity(intent);
                }
            });
            return view;

        }

    }

    public class RefreshImageThread extends Thread {
        public int position;
        public Integer contactId;
        public int type;
        public ImageView avatarImageView;

        public RefreshImageThread(int position, Integer contactID, int type, ImageView avatarImageView) {
            this.position = position;
            this.contactId = contactID;
            this.type = type;
            this.avatarImageView = avatarImageView;
        }

        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                String response = client.getImageUrl("" + contactId);
                final String imageUrl = Constans.API_HOST + response.substring(1, response.length() - 1);
                //mAvatarUrl.set(position, imageUrl);
                //mHasAvatar.set(position, true);
                if (type == 1) {
                    mAvatar1.set(position, imageUrl);
                    mHas1.set(position, true);
                } else if (type == 2) {
                    mAvatar2.set(position, imageUrl);
                    mHas2.set(position, true);
                } else if (type == 3) {
                    mAvatar3.set(position, imageUrl);
                    mHas3.set(position, true);
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (!imageUrl.endsWith("/")) {
                            MainApplication.imageLoader.displayImage(imageUrl, avatarImageView);
                        }

                    }
                });
            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                Toast.makeText(MainActivity.this, e.message, Toast.LENGTH_SHORT).show();
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(MainActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                Toast.makeText(MainActivity.this, e.message, Toast.LENGTH_SHORT).show();
            } catch (FException e) {
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
                        Toast.makeText(MainActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(MainActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (FException e) {
                // TODO: handle exception
            }
        }
    }



    public class RMPublisher {
        public Integer acquaintanceDegree = null;
        public Integer contactId = null;
        public Boolean isFriend = null;

        public RMPublisher(Integer acquaintanceDegree, Integer contactId, Boolean isFriend) {
            this.acquaintanceDegree = acquaintanceDegree;
            this.contactId = contactId;
            this.isFriend = isFriend;
        }

        public RMPublisher(JSONObject jsonObject) {
            try {
                this.acquaintanceDegree = jsonObject.getInt("acquaintance_degree");
                this.contactId = jsonObject.getInt("contact_id");
                this.isFriend = jsonObject.getBoolean("is_friend");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public class HomeMessageResponse {
        public List<RMPublisher> communicationProfessors = new ArrayList<RMPublisher>();
        public List<Integer> myRMCommunicationProfessors = new ArrayList<Integer>();
        public Integer newCommunicationCount = null;
        public Integer newMessageCount = null;
        public Integer newMyRMCommunicationCount = null;
        public List<RMPublisher> rmPublishers = new ArrayList<MainActivity.RMPublisher>();
        public Integer systemMessageCount = null;

        public HomeMessageResponse(JSONObject jsonObject) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("communication_professors");
                this.communicationProfessors.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    this.communicationProfessors.add(new RMPublisher(jsonArray.getJSONObject(i)));
                }

                jsonArray = jsonObject.getJSONArray("myrm_communication_professors");
                this.myRMCommunicationProfessors.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    this.myRMCommunicationProfessors.add(jsonArray.getInt(i));
                }

                this.newCommunicationCount = jsonObject.getInt("new_communication_count");
                this.newMessageCount = jsonObject.getInt("new_message_count");
                this.newMyRMCommunicationCount = jsonObject.getInt("new_myrm_communication_count");

                jsonArray = jsonObject.getJSONArray("reward_message_publishers");
                this.rmPublishers.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    this.rmPublishers.add(new RMPublisher(jsonArray.getJSONObject(i)));
                }

                this.systemMessageCount = jsonObject.getInt("system_message_count");
            } catch (JSONException e) {
                // TODO: handle exception
            }
        }
    }

    public class MyCommunications {
        public List<Communication> communications = new ArrayList<Communication>();
        public Integer limit = null;
        public Integer offset = null;
        public Integer totalCount = null;

        public MyCommunications(JSONObject jsonObject) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("communications");
                this.communications.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    Communication communication = new Communication();
                    communication.fromJSON(jsonArray.getJSONObject(i));
                    this.communications.add(communication);
                }
                this.limit = jsonObject.getInt("limit");
                this.offset = jsonObject.getInt("offset");
                this.totalCount = jsonObject.getInt("total_count");
            } catch (JSONException e) {
                // TODO: handle exception
            }

        }
    }
}