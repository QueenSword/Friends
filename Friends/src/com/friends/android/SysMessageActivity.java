package com.friends.android;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.friends.android.internal.Constans;
import com.friends.android.internal.FClient;
import com.friends.android.internal.FClient.FException;
import com.friends.android.internal.FClient.InvalidAccessToken;
import com.friends.android.internal.FClient.UnknowServerError;
import com.friends.android.object.SystemMessage;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SysMessageActivity extends Activity {
    public static final String DEBUG_TAG = "SysMessageActivity";
    
    private ListView mListView;
    private ListAdapter mListAdapter;
    private EditText mEditText;
    private Button mButton;
    
    private List<SystemMessage> mCurrentList = new ArrayList<SystemMessage>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_message);
        initActionBar();
        
        mListView = (ListView) findViewById(R.id.listview);
        mListAdapter = new ListAdapter();
        mListView.setAdapter(mListAdapter);
        
        mEditText = (EditText) findViewById(R.id.edittext);
        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                SystemMessage systemMessage = new SystemMessage();
                systemMessage.content = mEditText.getText().toString();
                
                mCurrentList.add(systemMessage);
                mListAdapter.notifyDataSetChanged();
                new AddDataThread(systemMessage).start();
            }
        });
    }
    
    
    public class AddDataThread extends Thread {
        private SystemMessage systemMessage;
        
        public AddDataThread(SystemMessage systemMessage) {
            this.systemMessage = systemMessage;
        }
        
        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                /*qsword*/
                String response = client.messageAddSystemMessage(systemMessage);
                mCurrentList.add(systemMessage);
                runOnUiThread(new Runnable() {
                    public void run() {
                        mListAdapter.notifyDataSetChanged();
                    }
                });
                Log.e(DEBUG_TAG, "add sys: " + response);
            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                final InvalidAccessToken temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SysMessageActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(SysMessageActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SysMessageActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (FException e) {
                // TODO: handle exception
            }
            
        }
    }
    /*qsword*/
    public class RefreshDataThread extends Thread {
        
        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                long time=System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();  
                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");  
                Date d1=new Date(time);  
                String t1=format.format(d1);  
                
                
                String response = client.messageGetSystemMessage(t1);
                
                Log.e(DEBUG_TAG, response);
            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                final InvalidAccessToken temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SysMessageActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(SysMessageActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SysMessageActivity.this, temp.message, Toast.LENGTH_SHORT).show();
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
            return mCurrentList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SystemMessage systemMessage = mCurrentList.get(position);
            View view = getLayoutInflater().inflate(R.layout.chatting_item_msg_text_right, null);
            TextView contentTextView = (TextView) view.findViewById(R.id.tv_chatcontent);
            contentTextView.setText(systemMessage.content);
            TextView timeTextView = (TextView) view.findViewById(R.id.tv_sendtime);
            timeTextView.setText(systemMessage.createdAtStr);
            return view;
        }
        
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sys_message, menu);
        return true;
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
        rightImageView.setVisibility(View.INVISIBLE);
        TextView titleTextView = (TextView) view.findViewById(R.id.title_textview);
        titleTextView.setText("系统信息");
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

}
