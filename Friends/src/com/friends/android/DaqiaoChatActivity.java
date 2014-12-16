package com.friends.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.friends.android.object.Chat;
import com.friends.android.object.Communication;
import com.friends.android.object.RewardMessage;

public class DaqiaoChatActivity extends Activity {

    public static final String DEBUG_TAG = "DaqiaoChatActivity";

    private ListView mListView;
    private ListAdapter mListAdapter;
    private ImageView mTypeImageView;
    private Button mPressButton;
    private EditText mContentEditText;
    private Button mSendButton;
    private ImageView mPicImageView;

    private Dialog mRecordDialog;
    private ImageView mDialogImageView;
    private AudioRecorder mAudioRecorder;
    private MediaPlayer mMediaPlayer;
    private Thread mRecordThread;
    private String mRecordTime;


    private static int MAX_TIME = 30; // 最长录制时间，单位秒，0为无时间限制
    private static int MIX_TIME = 1; // 最短录制时间，单位秒，0为无时间限制，建议设为1
    private static int RECORD_NO = 0; // 不在录音
    private static int RECORD_ING = 1; // 正在录音
    private static int RECODE_ED = 2; // 完成录音
    private static int RECODE_STATE = 0; // 录音的状态
    private static float recodeTime = 0.0f; // 录音的时间
    private static double voiceValue = 0.0; // 麦克风获取的音量值
    private static boolean playState = false; // 播放状态

    private String mAvatarUrlLeft;
    private String mAvatarUrlRight;
    private List<Chat> mCurrentList = new ArrayList<Chat>();
    private String rewardMessageId;
    private String content;
    private RewardMessage mRewardMessage;
    private Communication mCommunication;
    private String[] mFields;
    
    public void onSaveInstanceState(Bundle savedInstanceState) {  
        // Save away the original text, so we still have it if the activity  
        // needs to be killed while paused.  
      savedInstanceState.putString(Constans.REWARD_MESSAGE_ID, rewardMessageId);
      savedInstanceState.putString(Constans.CONTENT, content);
      super.onSaveInstanceState(savedInstanceState);  
      Log.e(DEBUG_TAG, "save");  
    }    
    @Override  
    public void onRestoreInstanceState(Bundle savedInstanceState) {  
      super.onRestoreInstanceState(savedInstanceState);  
      rewardMessageId = savedInstanceState.getString(Constans.REWARD_MESSAGE_ID);  
      content = savedInstanceState.getString(Constans.CONTENT);
      Log.e(DEBUG_TAG, "onRestoreInstanceState+IntTest");  
    }  

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daqiao_chat);
        rewardMessageId = getIntent().getStringExtra(Constans.REWARD_MESSAGE_ID);
        content = getIntent().getStringExtra(Constans.CONTENT);
        initActionBar();
        mFields = getResources().getStringArray(R.array.field_array);

        mListView = (ListView) findViewById(R.id.listview);
        mListAdapter = new ListAdapter();
        mListView.setAdapter(mListAdapter);

        mTypeImageView = (ImageView) findViewById(R.id.type_imageview);
        mPressButton = (Button) findViewById(R.id.press_button);
        mContentEditText = (EditText) findViewById(R.id.content_edittext);
        mSendButton = (Button) findViewById(R.id.send_button);
        mPicImageView = (ImageView) findViewById(R.id.pic_imageview);

        mContentEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mContentEditText.getText().toString().equals("")) {
                    mSendButton.setVisibility(View.GONE);
                    mPicImageView.setVisibility(View.VISIBLE);
                } else {
                    mPicImageView.setVisibility(View.GONE);
                    mSendButton.setVisibility(View.VISIBLE);
                }
            }
        });
        mTypeImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mContentEditText.getVisibility() == View.VISIBLE) {
                    mContentEditText.setVisibility(View.GONE);
                    mPressButton.setVisibility(View.VISIBLE);
                    mTypeImageView.setImageResource(R.drawable.icon_keyboard);
                } else {
                    mPressButton.setVisibility(View.GONE);
                    mContentEditText.setVisibility(View.VISIBLE);
                    mTypeImageView.setImageResource(R.drawable.icon_record);
                }
            }
        });
        // 录音
        mPressButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (RECODE_STATE != RECORD_ING) {
                        // scanOldFile();
                        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        mRecordTime = sDateFormat.format(new java.util.Date());
                        String filePath = MainApplication.voicesPath.getAbsolutePath() + "/" + mRecordTime + ".amr";
                        mAudioRecorder = new AudioRecorder(filePath);
                        RECODE_STATE = RECORD_ING;
                        showVoiceDialog();
                        try {
                            mAudioRecorder.start();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        mythread();
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    if (RECODE_STATE == RECORD_ING) {
                        RECODE_STATE = RECODE_ED;
                        if (mRecordDialog.isShowing()) {
                            mRecordDialog.dismiss();
                        }
                        try {
                            mAudioRecorder.stop();
                            voiceValue = 0.0;
                            if (recodeTime < MIX_TIME) {
                                showWarnToast();
                                // record.setText("按住开始录音");

                                RECODE_STATE = RECORD_NO;
                            } else {
                                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                String sendTime = sDateFormat.format(new java.util.Date());
                                String filePath = MainApplication.voicesPath.getAbsolutePath() + "/" + mRecordTime
                                        + ".amr";
                                new UploadFileThread(new File(filePath), 0).start();
                                /*
                                 * ListItem item = new ListItem("QSword",
                                 * sendTime, MessageType.VOICE, null,
                                 * mRecordTime, null); mCurrentList.add(item);
                                 */mListAdapter.notifyDataSetChanged();
                                // record.setText("录音完成!按住重新录音");
                                // luyin_txt.setText("录音时间："+((int)recodeTime));
                                // luyin_path.setText("文件路径："+getAmrPath());
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    break;
                }
                return false;
            }
        });

        mSendButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mContentEditText.getVisibility() == View.VISIBLE) {
                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String sendTime = sDateFormat.format(new java.util.Date());
                    Chat chat = new Chat();
                    try {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date d2 = df.parse("2001-01-01 00:00:00");
                        chat.chatId = System.currentTimeMillis() - d2.getTime();
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    
                        chat.content = mContentEditText.getText().toString();
                    
                    chat.type = Chat.CHAT_TYPE_TEXT;
                    chat.communicationId = Long.parseLong(mCommunication.communicationId);
                    chat.publisherId = Long.parseLong(MainApplication.userId);
                    chat.otherRMId = Long.parseLong(rewardMessageId);
                    
                    new AddChatThread(chat).start();
                    mContentEditText.setText("");

                } else {

                }
            }
        });

        mPicImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(DaqiaoChatActivity.this);
                builder.setTitle("选择照片");

                builder.setPositiveButton("相机", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface

                    dialog, int which) {
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        startActivityForResult(intent, 0);

                    }
                });
                builder.setNegativeButton("相册", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface

                    dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 1);

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

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
                JSONObject jsonObject = new JSONObject(client.messageGetRM(rewardMessageId));
                if (!jsonObject.isNull("reward_message")) {
                    mRewardMessage = new RewardMessage();
                   mRewardMessage.fromJSON((JSONObject) jsonObject.get("reward_message"));
                } else if (!jsonObject.isNull("reward_message_id")){
                    mRewardMessage = new RewardMessage();
                    mRewardMessage.fromJSON(jsonObject);
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        RefreshHeaderView();
                    }
                });
                
                jsonObject = new JSONObject(client.messageGetCommunication(rewardMessageId));
                if (!jsonObject.isNull("communication")) {
                    mCommunication = new Communication();
                    mCommunication.fromJSON((JSONObject) jsonObject.get("communication"));
                 } else if (!jsonObject.isNull("communication_id")){
                     mCommunication = new Communication();
                     mCommunication.fromJSON(jsonObject);
                 }
                
                runOnUiThread(new Runnable() {
                    public void run() {
                        RefreshMiddleView();
                    }
                });
                 
                jsonObject = new JSONObject(client.messageChat(Integer.parseInt(mCommunication.communicationId), 1, 20));
                ResponseClass2 responseClass2 = new ResponseClass2();
                responseClass2.fromJson(jsonObject);
                mCurrentList = responseClass2.chats;
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
                        Toast.makeText(DaqiaoChatActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(DaqiaoChatActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(DaqiaoChatActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (FException e) {
                // TODO: handle exception
            } catch (JSONException e) {
                // TODO: handle exception
            }
        }
    }
    
    public class ResponseClass2 {
        public Integer totalCount = null;
        public Integer offset = null;
        public List<Chat> chats = new ArrayList<Chat>();

        public void fromJson(JSONObject s) {
            try {
                this.totalCount = s.getInt("total_count");
                this.offset = s.getInt("offset");
                JSONArray jsonArray = s.getJSONArray("chats");
                for (int i = 0; i < jsonArray.length(); i++) {
                    Chat chat = new Chat();
                    chat.fromJSON(jsonArray.getJSONObject(i));
                    this.chats.add(chat);
                }
            } catch (JSONException e) {
                // TODO: handle exception
            }
        }
    }
    
    public void RefreshMiddleView() {
        ImageView imageView = (ImageView) findViewById(R.id.status_imageview);
        TextView textView = (TextView) findViewById(R.id.status_textview);
        if (mCommunication.status == Communication.COMMUNICATION_STATUS_ACCEPT) {
            imageView.setImageResource(R.drawable.icon_communication);
            textView.setText("已成功搭桥");
        } else {
            imageView.setImageResource(R.drawable.icon_communication_gray);
            textView.setText("等待搭桥");
        }
    }
    
    public void RefreshHeaderView() {
        ImageView avatarImageView = (ImageView) findViewById(R.id.avatar_imageview);
        new RefreshAvatarThread(mRewardMessage.publisher.contactId, avatarImageView).start();
        TextView dateTextView = (TextView) findViewById(R.id.date_textview);
        TextView countTextView = (TextView) findViewById(R.id.count_textview);
        TextView contentTextView = (TextView) findViewById(R.id.content_textview);
        dateTextView.setText(mRewardMessage.createdAtStr.substring(0, 10));
        countTextView.setText("￥" + mRewardMessage.reward);
        contentTextView.setText(mRewardMessage.content);
        
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        linearLayout.removeAllViews();
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.setMargins(5, 5, 5, 5);
        try {
            if (mRewardMessage.fields.getString(0).equals("0")) {
                TextView textView = new TextView(DaqiaoChatActivity.this);
                textView.setText("全部行业");
                textView.setPadding(5, 5, 5, 5);
                textView.setLayoutParams(llp);
                textView.setBackground(getResources().getDrawable(R.drawable.textview_corner));
                linearLayout.addView(textView);
            } else {
                for (int i = 0; i < mRewardMessage.fields.length(); i++) {
                    TextView textView = new TextView(DaqiaoChatActivity.this);
                    textView.setText(mFields[Integer.parseInt(mRewardMessage.fields.getString(i)) - 1]);
                    textView.setPadding(5, 5, 5, 5);
                    textView.setLayoutParams(llp);
                    textView.setBackground(getResources().getDrawable(R.drawable.textview_corner));
                    linearLayout.addView(textView);
                }
            }
        } catch (NumberFormatException e) {
            // TODO: handle exception
        } catch (JSONException e) {
            // TODO: handle exception
        }
        
        
    }

    public class RefreshAvatarThread extends Thread {
        public Integer contactId;
        public ImageView avatarImageView;

        public RefreshAvatarThread(Integer contactID, ImageView avatarImageView) {
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
                        Toast.makeText(DaqiaoChatActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(DaqiaoChatActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(DaqiaoChatActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (FException e) {
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
        titleTextView.setText(content);
        ActionBar.LayoutParams lp = new LayoutParams(android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        mActionBar.setCustomView(view, lp);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_list, menu);
        return true;
    }

    public class DownloadVoiceThread extends Thread {
        private String url;
        private String path;

        public DownloadVoiceThread(String url, String path) {
            this.url = url;
            this.path = path;
        }

        @Override
        public void run() {
            FClient client = new FClient();
            client.setUserToken(MainApplication.userToken);
            client.DownloadVoice(url, path);
        }
    }

    public class RefreshChatAvatarThread extends Thread {
        private String contactId;
        private int type;
        public RefreshChatAvatarThread(String contactId, int type) { //0 left, 1 right
            this.contactId = contactId;
            this.type = type;
        }

        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                String response = client.getImageUrl(contactId);
                if (type == 0) {
                    mAvatarUrlLeft = Constans.API_HOST + response.substring(1, response.length() - 1);
                } else if (type == 1) {
                    mAvatarUrlRight = Constans.API_HOST + response.substring(1, response.length() - 1);
                }
                

                runOnUiThread(new Runnable() {
                    public void run() {
                        mListAdapter.notifyDataSetChanged();
                    }
                });
                // mAvatarUrl.set(position, imageUrl);
            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                final InvalidAccessToken temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(DaqiaoChatActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(DaqiaoChatActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(DaqiaoChatActivity.this, temp.message, Toast.LENGTH_SHORT).show();
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
            final Chat item = mCurrentList.get(mCurrentList.size() - position - 1);
            View view = null;
            if (!String.valueOf(item.publisherId).equals(MainApplication.userId)) {
                view = getLayoutInflater().inflate(R.layout.chatting_item_msg_text_left, null);
                ImageView avatarImageView = (ImageView) view.findViewById(R.id.iv_userhead);
                if (mAvatarUrlLeft != null) {
                    MainApplication.imageLoader.displayImage(mAvatarUrlLeft, avatarImageView);
                } else {
                    new RefreshChatAvatarThread(String.valueOf(item.publisherId), 0).start();
                }
            } else {
                view = getLayoutInflater().inflate(R.layout.chatting_item_msg_text_right, null);
                ImageView avatarImageView = (ImageView) view.findViewById(R.id.iv_userhead);
                if (mAvatarUrlRight != null) {
                    MainApplication.imageLoader.displayImage(mAvatarUrlRight, avatarImageView);
                } else {
                    new RefreshChatAvatarThread(String.valueOf(item.publisherId), 1).start();
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
                new DownloadVoiceThread(item.content, filePath).start();
                view.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (!playState) {

                            mMediaPlayer = new MediaPlayer();
                            String url = MainApplication.voicesPath.getAbsolutePath() + "/" + filePath;
                            try {
                                // 模拟器里播放传url，真机播放传getAmrPath()
                                mMediaPlayer.setDataSource(url);
                                // mediaPlayer.setDataSource(getAmrPath());
                                mMediaPlayer.prepare();
                                mMediaPlayer.start();
                                playState = true;
                                // 设置播放结束时监听
                                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        if (playState) {
                                            playState = false;
                                        }
                                    }
                                });
                            } catch (IllegalArgumentException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IllegalStateException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        } else {
                            if (mMediaPlayer.isPlaying()) {
                                mMediaPlayer.stop();
                                playState = false;
                            } else {
                                playState = false;
                            }
                        }

                    }
                });
            } else if (item.type == Chat.CHAT_TYPE_FILE) {
                playerImageView.setVisibility(View.GONE);
                tvContent.setVisibility(View.GONE);
                thumbImageView.setVisibility(View.VISIBLE);
                Log.e(DEBUG_TAG, "xxx: " + item.content);
                MainApplication.imageLoader.displayImage(item.content, thumbImageView);
                thumbImageView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(DaqiaoChatActivity.this, BrowseImageActivity.class);
                        intent.putExtra(Constans.URL, item.content);
                        startActivity(intent);
                    }
                });
            } else if (item.type == Chat.CHAT_TYPE_CONTACT) {
                //TODO:
                
            }
            tvSendTime.setText(item.createdAtStr.substring(0, 10));
            // tvUserName.setText(item.);

            return view;
        }

    }

    // 录音时显示Dialog
    void showVoiceDialog() {
        mRecordDialog = new Dialog(DaqiaoChatActivity.this, R.style.DialogStyle);
        mRecordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mRecordDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mRecordDialog.setContentView(R.layout.record_voice_dialog_layout);
        mDialogImageView = (ImageView) mRecordDialog.findViewById(R.id.dialog_img);
        mRecordDialog.show();
    }

    // 录音时间太短时Toast显示
    void showWarnToast() {
        Toast toast = new Toast(DaqiaoChatActivity.this);
        LinearLayout linearLayout = new LinearLayout(DaqiaoChatActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 20, 20, 20);

        // 定义一个ImageView
        ImageView imageView = new ImageView(DaqiaoChatActivity.this);
        imageView.setImageResource(R.drawable.voice_to_short); // 图标

        TextView mTv = new TextView(DaqiaoChatActivity.this);
        mTv.setText("时间太短   录音失败");
        mTv.setTextSize(14);
        mTv.setTextColor(Color.WHITE);// 字体颜色
        // mTv.setPadding(0, 10, 0, 0);

        // 将ImageView和ToastView合并到Layout中
        linearLayout.addView(imageView);
        linearLayout.addView(mTv);
        linearLayout.setGravity(Gravity.CENTER);// 内容居中
        linearLayout.setBackgroundResource(R.drawable.record_bg);// 设置自定义toast的背景

        toast.setView(linearLayout);
        toast.setGravity(Gravity.CENTER, 0, 0);// 起点位置为中间 100为向下移100dp
        toast.show();
    }

    // 录音计时线程
    void mythread() {
        mRecordThread = new Thread(ImgThread);
        mRecordThread.start();
    }

    // 录音Dialog图片随声音大小切换
    void setDialogImage() {
        if (voiceValue < 200.0) {
            mDialogImageView.setImageResource(R.drawable.record_animate_01);
        } else if (voiceValue > 200.0 && voiceValue < 400) {
            mDialogImageView.setImageResource(R.drawable.record_animate_02);
        } else if (voiceValue > 400.0 && voiceValue < 800) {
            mDialogImageView.setImageResource(R.drawable.record_animate_03);
        } else if (voiceValue > 800.0 && voiceValue < 1600) {
            mDialogImageView.setImageResource(R.drawable.record_animate_04);
        } else if (voiceValue > 1600.0 && voiceValue < 3200) {
            mDialogImageView.setImageResource(R.drawable.record_animate_05);
        } else if (voiceValue > 3200.0 && voiceValue < 5000) {
            mDialogImageView.setImageResource(R.drawable.record_animate_06);
        } else if (voiceValue > 5000.0 && voiceValue < 7000) {
            mDialogImageView.setImageResource(R.drawable.record_animate_07);
        } else if (voiceValue > 7000.0 && voiceValue < 10000.0) {
            mDialogImageView.setImageResource(R.drawable.record_animate_08);
        } else if (voiceValue > 10000.0 && voiceValue < 14000.0) {
            mDialogImageView.setImageResource(R.drawable.record_animate_09);
        } else if (voiceValue > 14000.0 && voiceValue < 17000.0) {
            mDialogImageView.setImageResource(R.drawable.record_animate_10);
        } else if (voiceValue > 17000.0 && voiceValue < 20000.0) {
            mDialogImageView.setImageResource(R.drawable.record_animate_11);
        } else if (voiceValue > 20000.0 && voiceValue < 24000.0) {
            mDialogImageView.setImageResource(R.drawable.record_animate_12);
        } else if (voiceValue > 24000.0 && voiceValue < 28000.0) {
            mDialogImageView.setImageResource(R.drawable.record_animate_13);
        } else if (voiceValue > 28000.0) {
            mDialogImageView.setImageResource(R.drawable.record_animate_14);
        }
    }

    // 录音线程
    private Runnable ImgThread = new Runnable() {

        @Override
        public void run() {
            recodeTime = 0.0f;
            while (RECODE_STATE == RECORD_ING) {
                if (recodeTime >= MAX_TIME && MAX_TIME != 0) {
                    imgHandle.sendEmptyMessage(0);
                } else {
                    try {
                        Thread.sleep(200);
                        recodeTime += 0.2;
                        if (RECODE_STATE == RECORD_ING) {
                            voiceValue = mAudioRecorder.getAmplitude();
                            imgHandle.sendEmptyMessage(1);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        Handler imgHandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what) {
                case 0:
                    // 录音超过15秒自动停止
                    if (RECODE_STATE == RECORD_ING) {
                        RECODE_STATE = RECODE_ED;
                        if (mRecordDialog.isShowing()) {
                            mRecordDialog.dismiss();
                        }
                        try {
                            mAudioRecorder.stop();
                            voiceValue = 0.0;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (recodeTime < 1.0) {
                            showWarnToast();
                            // record.setText("按住开始录音");
                            RECODE_STATE = RECORD_NO;
                        } else {
                            // record.setText("录音完成!点击重新录音");
                            // luyin_txt.setText("录音时间："+((int)recodeTime));
                            // luyin_path.setText("文件路径："+getAmrPath());
                        }
                    }
                    break;
                case 1:
                    setDialogImage();
                    break;
                default:
                    break;
                }

            }
        };
    };

    enum MessageType {
        TEXT, VOICE, IMAGE
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        ContentResolver resolver = getContentResolver();
        if (data != null) {
            if (requestCode == 1) {

                try {
                    Uri originalUri = data.getData();
                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String sendTime = sDateFormat.format(new java.util.Date());
                    File file = new File(Uri2FilePath(originalUri));
                    new UploadFileThread(file, 1).start();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            } else if (requestCode == 0) {

                String sdStatus = Environment.getExternalStorageState();
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                    return;
                }
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                Bitmap bitmap = (Bitmap) bundle.get("data");
                FileOutputStream b = null;
                String str = null;
                Date date = null;
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                date = new Date();
                str = format.format(date);
                String fileName = MainApplication.picturesPath.getAbsolutePath() + "/" + str + ".jpg";
                try {
                    b = new FileOutputStream(fileName);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        b.flush();
                        b.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    new UploadFileThread(new File(fileName), 1).start();
                }
            }
        }
    }

    public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    public class AddChatThread extends Thread {
        private Chat chat;
        
        public AddChatThread(Chat chat) {
            this.chat = chat;
        }
        
        @Override
        public void run() {
            
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                String response = client.messageAddChat(chat);
                Log.e(DEBUG_TAG, "add chat: " + response);
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
                        Toast.makeText(DaqiaoChatActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(DaqiaoChatActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(DaqiaoChatActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (FException e) {
                // TODO: handle exception
            }
            
        }
    }

    public class UploadFileThread extends Thread {
        private File file;
        private int type;

        public UploadFileThread(File file, int type) { // audio 0 pic 1 contact 2
            this.file = file;
            this.type = type;
        }

        @Override
        public void run() {
            FClient client = new FClient();
            client.setUserToken(MainApplication.userToken);
            String response = client.uploadFile(file);
            if (response == null || response.equals("")) {
                return ;
            }
            response = response.replaceAll("\"", "");
            
            Chat chat = new Chat();
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d2 = df.parse("2001-01-01 00:00:00");
                chat.chatId = System.currentTimeMillis() - d2.getTime();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            chat.content = Constans.API_HOST + response;
            if (type == 0) {
                chat.type = Chat.CHAT_TYPE_AUDIO;
            } else if (type == 1) {
                chat.type = Chat.CHAT_TYPE_FILE;
            } else if (type == 2) {
                chat.type = Chat.CHAT_TYPE_CONTACT;
            }
            chat.communicationId = Long.parseLong(mCommunication.communicationId);
            chat.publisherId = Long.parseLong(MainApplication.userId);
            chat.otherRMId = Long.parseLong(rewardMessageId);
            new AddChatThread(chat).start();
        }
    }

    public String Uri2FilePath(Uri uri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor.getString(actual_image_column_index);
        return img_path;
    }

}
