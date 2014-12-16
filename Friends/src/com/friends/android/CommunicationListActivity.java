package com.friends.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.friends.android.ChatListActivity.DownloadVoiceThread;
import com.friends.android.internal.Constans;
import com.friends.android.internal.FClient;
import com.friends.android.internal.FClient.FException;
import com.friends.android.internal.FClient.InvalidAccessToken;
import com.friends.android.internal.FClient.UnknowServerError;
import com.friends.android.object.Chat;
import com.friends.android.object.Communication;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.media.MediaPlayer;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CommunicationListActivity extends Activity {

    public static final String DEBUG_TAG = "CommunicationListActivity";

    private SlidingMenu mSlidingMenu;
    private View mSlidingMenuView;
    private ListView detailListView;
    private DetailListAdapter detailListAdapter;
    private List<Chat> detailList = new ArrayList<Chat>();

    private ListView mListView;
    private ListAdapter mListAdapter;
    private List<Communication> mCurrentList = new ArrayList<Communication>();
    private List<String> mAvatarLeft = new ArrayList<String>();
    private List<String> mAvatarRight = new ArrayList<String>();
    private TextView mAllTextView;
    private TextView mPendingTextView;
    private TextView mAcceptTextView;
    private TextView mDeclineTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication_list);
        initSlidingMenu();
        initActionBar();

        mListView = (ListView) findViewById(R.id.listview);
        mListAdapter = new ListAdapter();
        mListView.setAdapter(mListAdapter);

        mAllTextView = (TextView) findViewById(R.id.all_textview);
        mPendingTextView = (TextView) findViewById(R.id.pending_textview);
        mAcceptTextView = (TextView) findViewById(R.id.accept_textview);
        mDeclineTextView = (TextView) findViewById(R.id.decline_textview);
        mAllTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new RefreshDataThread(Communication.COMMUNICATION_STATUS_ALL).start();
            }
        });
        mPendingTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new RefreshDataThread(Communication.COMMUNICATION_STATUS_PENDING).start();
            }
        });
        mAcceptTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new RefreshDataThread(Communication.COMMUNICATION_STATUS_ACCEPT).start();
            }
        });
        mDeclineTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new RefreshDataThread(Communication.COMMUNICATION_STATUS_DECLINE).start();
            }
        });

        new RefreshDataThread(Communication.COMMUNICATION_STATUS_ALL).start();
    }
    
    

    public class RefreshAvatarThread extends Thread {
        public int position;
        public Integer contactId;
        public ImageView imageView;
        public Integer type; // 0 left, 1 right

        public RefreshAvatarThread(int position, Integer contactId, ImageView imageView, Integer type) {
            this.position = position;
            this.contactId = contactId;
            this.imageView = imageView;
            this.type = type;
        }

        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                String response = client.getImageUrl("" + contactId);
                final String imageUrl = Constans.API_HOST + response.substring(1, response.length() - 1);
                if (position < mAvatarLeft.size()) {
                    if (type == 0) {
                        mAvatarLeft.set(position, imageUrl);
                    } else if (type == 1) {
                        mAvatarRight.set(position, imageUrl);
                    }

                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!imageUrl.endsWith("/")) {
                                MainApplication.imageLoader.displayImage(imageUrl, imageView);
                            }
                        }
                    });
                }

            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                final InvalidAccessToken temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(CommunicationListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(CommunicationListActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(CommunicationListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (FException e) {
                // TODO: handle exception
            }
        }
    }

    public class RefreshSlidingMenuThread extends Thread {
        private String communicationId;

        public RefreshSlidingMenuThread(String communicationId) {
            this.communicationId = communicationId;
        }

        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                String response = client.messageGetCommunicationById(this.communicationId);
                JSONObject jsonObject = new JSONObject(response);
                Log.e(DEBUG_TAG, "sliding menu data: " + jsonObject.toString());
                Communication communication = new Communication();
                communication.fromJSON(jsonObject);
                Chat temp = new Chat();
                temp.content = communication.rm.content;
                detailList.clear();
                detailList.add(temp);
                Log.e(DEBUG_TAG, "content: " + temp.content);
                for (int i = 0; i < communication.chats.size(); i++) {
                    detailList.add(communication.chats.get(communication.chats.size() - i - 1));
                }
                // detailList = communication.chats;
                runOnUiThread(new Runnable() {
                    public void run() {
                        detailListAdapter.notifyDataSetChanged();
                    }
                });
                Log.e(DEBUG_TAG, response);
            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                final InvalidAccessToken temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(CommunicationListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(CommunicationListActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(CommunicationListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
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

        private Integer status;

        public RefreshDataThread(Integer status) {
            this.status = status;
        }

        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                JSONObject jsonObject = new JSONObject(client.messageGetCommunications(1, 20, status));

                Log.e(DEBUG_TAG, "fuck: " + jsonObject.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("communications");
                mCurrentList.clear();
                mAvatarLeft.clear();
                mAvatarRight.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    Communication communication = new Communication();
                    communication.fromJSON(jsonArray.getJSONObject(i));
                    mCurrentList.add(communication);
                    mAvatarLeft.add("");
                    mAvatarRight.add("");
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
                        Toast.makeText(CommunicationListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(CommunicationListActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(CommunicationListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (FException e) {
                // TODO: handle exception
            } catch (JSONException e) {
                // TODO: handle exception
            }

        }
    }

    public void initSlidingMenu() {
        mSlidingMenu = new SlidingMenu(CommunicationListActivity.this);
        mSlidingMenu.setMode(SlidingMenu.RIGHT);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        mSlidingMenu.setBehindWidth(4 * MainApplication.width / 5);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.attachToActivity(CommunicationListActivity.this, SlidingMenu.SLIDING_WINDOW);
        mSlidingMenuView = getLayoutInflater().inflate(R.layout.sliding_menu_communicate_list_layout, null);
        detailListView = (ListView) mSlidingMenuView.findViewById(R.id.detail_listview);
        detailListAdapter = new DetailListAdapter();
        detailListView.setAdapter(detailListAdapter);
        mSlidingMenuView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mSlidingMenu.isMenuShowing()) {
                    mSlidingMenu.showContent();
                }
            }
        });
        mSlidingMenu.setMenu(mSlidingMenuView);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (mSlidingMenu.isMenuShowing()) {
            mSlidingMenu.showContent();
        } else {
            super.onBackPressed();
        }
        
        
    }

    public void initActionBar() {
        ActionBar mActionBar = getActionBar();
        View view = getLayoutInflater().inflate(R.layout.actionbar_blue_layout, null);
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
        titleTextView.setText("搭桥");
        ActionBar.LayoutParams lp = new LayoutParams(android.app.ActionBar.LayoutParams.MATCH_PARENT,
                android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        mActionBar.setCustomView(view, lp);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.communication_list, menu);
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
            View view = getLayoutInflater().inflate(R.layout.communication_list_item_layout, null);
            final Communication communication = mCurrentList.get(position);
            ImageView statusImageView = (ImageView) view.findViewById(R.id.status_imageview);
            ImageView okImageView = (ImageView) view.findViewById(R.id.ok_imageview);
            TextView countTextView = (TextView) view.findViewById(R.id.count_textview);
            if (communication.status == Communication.COMMUNICATION_STATUS_ACCEPT) {
                statusImageView.setImageResource(R.drawable.icon_communication_blue);
                okImageView.setImageResource(R.drawable.icon_ok_green);
            } else if (communication.status == Communication.COMMUNICATION_STATUS_DECLINE) {
                statusImageView.setImageResource(R.drawable.icon_communication_gray);
                okImageView.setImageResource(R.drawable.icon_cancel);
            } else if (communication.status == Communication.COMMUNICATION_STATUS_PENDING) {
                statusImageView.setImageResource(R.drawable.icon_communication_blue);
                okImageView.setImageResource(R.drawable.icon_arrow);
            }
            ImageView imageView1 = (ImageView) view.findViewById(R.id.imageview1);
            ImageView imageView2 = (ImageView) view.findViewById(R.id.imageview2);
            if (mAvatarLeft.get(position).equals("")) {
                new RefreshAvatarThread(position, communication.rm.publisher.contactId, imageView1, 0).start();
            } else if (mAvatarLeft.get(position).endsWith("/")) {
                imageView1.setImageResource(R.drawable.icon_default_profile);
            } else {
                MainApplication.imageLoader.displayImage(mAvatarLeft.get(position), imageView1);
            }

            if (mAvatarRight.get(position).equals("")) {
                new RefreshAvatarThread(position, communication.target.contactId, imageView2, 1).start();

            } else if (mAvatarRight.get(position).endsWith("/")) {
                imageView2.setImageResource(R.drawable.icon_default_profile);
            } else {
                MainApplication.imageLoader.displayImage(mAvatarRight.get(position), imageView2);
            }
            Log.e(DEBUG_TAG, "right: " + mAvatarRight.get(position));
            countTextView.setText("￥" + communication.rm.reward);
            final int pos = position;
            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (!mSlidingMenu.isMenuShowing()) {
                        mSlidingMenu.showMenu();
                        ImageView statusImageView = (ImageView) mSlidingMenuView.findViewById(R.id.status_imageview);
                        ImageView imageViewL = (ImageView) mSlidingMenu.findViewById(R.id.imageview1);
                        ImageView imageViewR = (ImageView) mSlidingMenu.findViewById(R.id.imageview2);
                        TextView textViewL = (TextView) mSlidingMenu.findViewById(R.id.textview1);
                        TextView textViewR = (TextView) mSlidingMenu.findViewById(R.id.textview2);
                        Log.e(DEBUG_TAG, "left:" + mAvatarLeft.get(pos) + "==" + mAvatarRight.get(pos));
                        if (mAvatarLeft.get(pos).equals("") || mAvatarLeft.get(pos).endsWith("/")) {
                            imageViewL.setImageResource(R.drawable.icon_default_profile);
                        } else {
                            MainApplication.imageLoader.displayImage(mAvatarLeft.get(pos), imageViewL);
                        }
                        if (mAvatarRight.get(pos).equals("") || mAvatarRight.get(pos).endsWith("/")) {
                            imageViewR.setImageResource(R.drawable.icon_default_profile);
                        } else {
                            MainApplication.imageLoader.displayImage(mAvatarRight.get(pos), imageViewR);
                        }
                        detailListAdapter.setUrl(mAvatarLeft.get(pos), mAvatarRight.get(pos));

                        textViewL.setText(communication.rm.publisher.displayName);
                        textViewR.setText(communication.target.displayName);

                        if (communication.status == Communication.COMMUNICATION_STATUS_DECLINE) {
                            statusImageView.setImageResource(R.drawable.icon_communication_gray);
                        } else {
                            statusImageView.setImageResource(R.drawable.icon_communication_blue);
                        }
                        ImageView okImageView = (ImageView) mSlidingMenu.findViewById(R.id.ok_imageview);
                        ImageView cancleImageView = (ImageView) mSlidingMenu.findViewById(R.id.cancle_imageview);
                        if (communication.status == Communication.COMMUNICATION_STATUS_PENDING) {
                            okImageView.setVisibility(View.VISIBLE);
                            cancleImageView.setVisibility(View.VISIBLE);

                        } else {
                            okImageView.setVisibility(View.GONE);
                            cancleImageView.setVisibility(View.GONE);

                        }
                        okImageView.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                new PostCommunicationThread(communication.communicationId, 0).start();
                                mSlidingMenu.showContent();

                            }
                        });

                        cancleImageView.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                new PostCommunicationThread(communication.communicationId, 1).start();
                                mSlidingMenu.showContent();
                            }
                        });
                        new RefreshSlidingMenuThread(communication.communicationId).start();
                    }
                }
            });
            return view;
        }

    }

    public class PostCommunicationThread extends Thread {
        private String communicationId;
        private Integer isDecline;

        public PostCommunicationThread(String communicationId, Integer isDecline) {
            this.communicationId = communicationId;
            this.isDecline = isDecline;
        }

        @Override
        public void run() {

            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                String response = client.messagePostCommunication(communicationId, isDecline);
                Log.e(DEBUG_TAG, response);
                runOnUiThread(new Runnable() {
                    public void run() {
                        new RefreshDataThread(Communication.COMMUNICATION_STATUS_ALL).start();
                    }
                });
            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                final InvalidAccessToken temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(CommunicationListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(CommunicationListActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(CommunicationListActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (FException e) {
                // TODO: handle exception
            }
        }
    }

    public class DetailListAdapter extends BaseAdapter {

        private String leftUrl;
        private String rightUrl;

        public void setUrl(String leftUrl, String rightUrl) {
            this.leftUrl = leftUrl;
            this.rightUrl = rightUrl;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            Log.e(DEBUG_TAG, "" + detailList.size());
            return detailList.size();

        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            View view = null;
            if (arg0 == 0) {
                view = getLayoutInflater().inflate(R.layout.chatting_item_msg_text_left, null);
                ImageView imageView = (ImageView) view.findViewById(R.id.iv_userhead);
                if (leftUrl.endsWith("/") || leftUrl.equals("")) {
                    imageView.setImageResource(R.drawable.icon_default_profile);
                } else {
                    MainApplication.imageLoader.displayImage(leftUrl, imageView);
                }
                LinearLayout llSendTime = (LinearLayout) view.findViewById(R.id.ll_sendtime);
                llSendTime.setVisibility(View.GONE);
                TextView tvContent = (TextView) view.findViewById(R.id.tv_chatcontent);
                tvContent.setText(detailList.get(arg0).content);
                tvContent.setVisibility(View.VISIBLE);

                ImageView playerImageView = (ImageView) view.findViewById(R.id.player_imageview);
                ImageView thumbImageView = (ImageView) view.findViewById(R.id.thumb_imageview);
                playerImageView.setVisibility(View.GONE);
                thumbImageView.setVisibility(View.GONE);
                return view;

            }

            final Chat item = detailList.get(arg0);

            if (!String.valueOf(item.publisherId).equals(MainApplication.userId)) {
                view = getLayoutInflater().inflate(R.layout.chatting_item_msg_text_left, null);
                ImageView imageView = (ImageView) view.findViewById(R.id.iv_userhead);
                Log.e(DEBUG_TAG, "right: " + rightUrl);
                if (rightUrl.endsWith("/") || rightUrl.equals("")) {
                    imageView.setImageResource(R.drawable.icon_default_profile);
                    
                } else {
                    MainApplication.imageLoader.displayImage(rightUrl, imageView);
                }
            } else {
                view = getLayoutInflater().inflate(R.layout.chatting_item_msg_text_right, null);
                ImageView imageView = (ImageView) view.findViewById(R.id.iv_userhead);
                Log.e(DEBUG_TAG, "left: " + leftUrl);
                if (leftUrl.endsWith("/") || leftUrl.equals("")) {
                    imageView.setImageResource(R.drawable.icon_default_profile);
                } else {
                    MainApplication.imageLoader.displayImage(leftUrl, imageView);
                }
            }

            TextView tvSendTime = (TextView) view.findViewById(R.id.tv_sendtime);
            TextView tvUserName = (TextView) view.findViewById(R.id.tv_username);
            ImageView playerImageView = (ImageView) view.findViewById(R.id.player_imageview);
            TextView tvContent = (TextView) view.findViewById(R.id.tv_chatcontent);
            ImageView thumbImageView = (ImageView) view.findViewById(R.id.thumb_imageview);
            if (item.type == Chat.CHAT_TYPE_TEXT) {
                playerImageView.setVisibility(View.GONE);
                thumbImageView.setVisibility(View.GONE);
                tvContent.setVisibility(View.VISIBLE);
                tvContent.setText(item.content);
            } else if (item.type == Chat.CHAT_TYPE_AUDIO) {
                playerImageView.setVisibility(View.VISIBLE);
                thumbImageView.setVisibility(View.GONE);
                tvContent.setVisibility(View.GONE);
                final String filePath = "" + item.chatId + ".amr";
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            } else if (item.type == Chat.CHAT_TYPE_FILE) {
                playerImageView.setVisibility(View.GONE);
                tvContent.setVisibility(View.GONE);
                thumbImageView.setVisibility(View.VISIBLE);
                MainApplication.imageLoader.displayImage(item.content, thumbImageView);
                thumbImageView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Log.e(DEBUG_TAG, item.content);
                        Intent intent = new Intent(CommunicationListActivity.this, BrowseImageActivity.class);
                        intent.putExtra(Constans.URL, item.content);
                        startActivity(intent);
                    }
                });
            } else if (item.type == Chat.CHAT_TYPE_CONTACT) {
                // TODO:

            }
            tvSendTime.setText(item.createdAtStr.substring(0, 10));

            return view;
        }

    }

}
