package com.friends.android;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.friends.android.object.Contact;

//需要实现SensorEventListener接口  

public class YaoYiYaoActivity extends Activity implements SensorEventListener {
    
    public static final String DEBUG_TAG = "YaoYiYaoActivity";
    
    private SensorManager mSensorManager;
    public TextView mTextView;
    private ListAdapter mListAdapter;
    private ListView mListView;
    private List<Contact> mCurrentList = new ArrayList<Contact>();
    private List<Boolean> mHasAddList = new ArrayList<Boolean>();
    // 震动

    private Vibrator vibrator;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yao_yi_yao);
        initActionBar();
        
        mTextView = (TextView) findViewById(R.id.count_textview);
        // 获取传感器管理服务

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // 震动
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        
        mListView = (ListView) findViewById(R.id.listview);
        mListAdapter = new ListAdapter();
        mListView.setAdapter(mListAdapter);
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
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.yaoyiyao_list_item_layout, null);
            }
            final Contact contact = mCurrentList.get(position);
            TextView nameTextView = (TextView) convertView.findViewById(R.id.name_textview);
            TextView contentTextView = (TextView) convertView.findViewById(R.id.content_textview);
            ImageView addImageView = (ImageView) convertView.findViewById(R.id.add_imageview);
            nameTextView.setText(contact.displayName);
            contentTextView.setText(contact.currentTitle);
            if (mHasAddList.get(position)) {
                addImageView.setImageResource(R.drawable.icon_add_dgray);
                addImageView.setOnClickListener(null);
            } else {
                addImageView.setImageResource(R.drawable.icon_add_gray);
                final Integer pos = position;
                addImageView.setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        List<String> contactIds = new ArrayList<String>();
                        contactIds.add("" + contact.contactId);
                        mHasAddList.set(pos, true);
                        new AddFriendThread(contactIds).start();
                    }
                });
        
            }
                    return convertView;
        }
        
    }
    
    public class AddFriendThread extends Thread {
        
        private List<String> contactIds;
        
        public AddFriendThread(List<String> contactIds) {
            this.contactIds = contactIds;
        }
        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                JSONObject jsonObject = new JSONObject(client.userAddFriend(contactIds));
                Log.e(DEBUG_TAG, jsonObject.toString());
            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                final InvalidAccessToken temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(YaoYiYaoActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(YaoYiYaoActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(YaoYiYaoActivity.this, temp.message, Toast.LENGTH_SHORT).show();
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
        
        /*private List<String> contactIds;
        
        public AddFriendThread(List<String> contactIds) {
            this.contactIds = contactIds;
        }*/
        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                JSONObject jsonObject = new JSONObject(client.userWave());
                Log.e(DEBUG_TAG, jsonObject.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("contacts");
                mCurrentList.clear();
                mHasAddList.clear();
                for (int i = 0; i < jsonArray.length(); i ++) {
                    Contact contact = new Contact();
                    contact.fromJSON(jsonArray.getJSONObject(i));
                    mCurrentList.add(contact);
                    mHasAddList.add(false);
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        mTextView.setText("找到" + mCurrentList.size() + "位好友");
                        mListAdapter.notifyDataSetChanged();
                    }
                });
            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                final InvalidAccessToken temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(YaoYiYaoActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(YaoYiYaoActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(YaoYiYaoActivity.this, temp.message, Toast.LENGTH_SHORT).show();
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
        rightImageView.setVisibility(View.INVISIBLE);
        TextView titleTextView = (TextView) view.findViewById(R.id.title_textview);
        titleTextView.setText("摇一摇");
        ActionBar.LayoutParams lp = new LayoutParams(android.app.ActionBar.LayoutParams.MATCH_PARENT,
                android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        mActionBar.setCustomView(view, lp);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    protected void onResume() {

        super.onResume();

        // 加速度传感器

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
        // 还有SENSOR_DELAY_UI、SENSOR_DELAY_FASTEST、SENSOR_DELAY_GAME等，

                // 根据不同应用，需要的反应速率不同，具体根据实际情况设定

                SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onStop() {

        mSensorManager.unregisterListener(this);

        super.onStop();

    }

    @Override
    protected void onPause() {

        mSensorManager.unregisterListener(this);

        super.onPause();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

        // TODO Auto-generated method stub

        // 当传感器精度改变时回调该方法，Do nothing.

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // TODO Auto-generated method stub

        int sensorType = event.sensor.getType();

        // values[0]:X轴，values[1]：Y轴，values[2]：Z轴

        float[] values = event.values;

        if (sensorType == Sensor.TYPE_ACCELEROMETER) {

            /*
             * 因为一般正常情况下，任意轴数值最大就在9.8~10之间，只有在你突然摇动手机
             * 
             * 的时候，瞬时加速度才会突然增大或减少。
             * 
             * 所以，经过实际测试，只需监听任一轴的加速度大于14的时候，改变你需要的设置
             * 
             * 就OK了~~~
             */
            int threshold = 14;
            if ((Math.abs(values[0]) > threshold || Math.abs(values[1]) > threshold || Math.abs(values[2]) > threshold)) {
                new RefreshDataThread().start();
                // 摇动手机后，设置button上显示的字为空

                // 摇动手机后，再伴随震动提示~~
                vibrator.vibrate(1000);

            }

        }

    }

}