package com.friends.android;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.friends.android.internal.Constans;
import com.friends.android.internal.FClient;
import com.friends.android.internal.FClient.FException;
import com.friends.android.internal.FClient.InvalidAccessToken;
import com.friends.android.internal.FClient.UnknowServerError;
import com.friends.android.object.Contact;

import android.R.bool;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LocalContactActivity extends Activity {
    public static final String DEBUG_TAG = "LocalContactActivity";
    
    private ListView mListView;
    private ListAdapter mListAdapter;
    private List<String> mCurrentNameList = new ArrayList<String>();
    private List<String> mCurrentNumList = new ArrayList<String>();
    private List<Contact> mFriendsList = new ArrayList<Contact>();
    private List<Contact> mRegisterList = new ArrayList<Contact>();
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_contact);
        mListView = (ListView) findViewById(R.id.listview);
        mListAdapter = new ListAdapter();
        mListView.setAdapter(mListAdapter);
        mListAdapter.notifyDataSetChanged();
        
        new GetLocalContactThread().start();
    }
    
    public class RefreshDataThread extends Thread {
        
        @Override
        public void run() {
            try {
                Log.e(DEBUG_TAG, "refresh data");
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                JSONObject jsonObject = new JSONObject(client.userGetFriends(1));
                JSONArray jsonArray = jsonObject.getJSONArray("contacts");
                mFriendsList.clear();
                for (int i = 0; i < jsonArray.length(); i ++) {
                    Contact contact = new Contact();
                    contact.fromJSON(jsonArray.getJSONObject(i));
                    mFriendsList.add(contact);
                }
                
                jsonObject = new JSONObject(client.userSearchByPhonenum(mCurrentNumList));
                jsonArray = jsonObject.getJSONArray("contacts");
                mRegisterList.clear();
                for (int i = 0; i < jsonArray.length(); i ++) {
                    Contact contact = new Contact();
                    contact.fromJSON(jsonArray.getJSONObject(i));
                    mRegisterList.add(contact);
                }
                Log.e(DEBUG_TAG, "size: " + mRegisterList.size() + " " + mFriendsList.size());
                for (int i = 0; i < mRegisterList.size(); i ++) {
                    Boolean isFriend = false;
                    for (int j = 0; j < mFriendsList.size(); j ++) {
                        if (mFriendsList.get(j).contactId.equals(mRegisterList.get(i).contactId)) {
                            isFriend = true;
                            mRegisterList.get(i).status = Contact.CONTACT_STATUS_FRIEND;
                        }
                    }
                    if (!isFriend) {
                        if (MainApplication.prefs.contains(mRegisterList.get(i).phoneNumber + "add")) {
                            mRegisterList.get(i).status = Contact.CONTACT_STATUS_ASKED;
                        } else {
                            if (MainApplication.prefs.contains(mRegisterList.get(i).phoneNumber + "invite")) {
                                mRegisterList.get(i).status = Contact.CONTACT_STATUS_INVITED;
                            }
                        }
                    }
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
                        Toast.makeText(LocalContactActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(LocalContactActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(LocalContactActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (FException e) {
                // TODO: handle exception
            } catch (JSONException e) {
                // TODO: handle exception
            }
        }
    }
    
    public class GetLocalContactThread extends Thread {
        @Override
        public void run() {
            getContact();
            Log.e(DEBUG_TAG, "" + mCurrentNameList.size() + mCurrentNumList.size());
            runOnUiThread(new Runnable() {
                public void run() {
                    
                    mListAdapter.notifyDataSetChanged();
                    new RefreshDataThread().start();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.local_contact, menu);
        return true;
    }

    public class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mCurrentNameList.size();
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
                convertView = getLayoutInflater().inflate(R.layout.local_contact_list_item_layout, null);
            }
            
            if (position >= mCurrentNameList.size()) {
                return null;
            }
            TextView nameTextView = (TextView) convertView.findViewById(R.id.name_textview);
            TextView numTextView = (TextView) convertView.findViewById(R.id.num_textview);
            TextView addFriendTextView = (TextView) convertView.findViewById(R.id.addfriend_textview);
            nameTextView.setText(mCurrentNameList.get(position));
            numTextView.setText(mCurrentNumList.get(position));
            Boolean hasRegister = false;
            
            for (int i = 0; i < mRegisterList.size(); i ++) {
                if (mRegisterList.get(i).phoneNumber.equals(mCurrentNumList.get(position))) {
                    hasRegister = true;
                    Contact contact = mRegisterList.get(i);
                    if (contact.status == Contact.CONTACT_STATUS_FRIEND) {
                        addFriendTextView.setText("通过验证");
                        addFriendTextView.setOnClickListener(null);
                    } else if (contact.status == Contact.CONTACT_STATUS_ASKED) {
                        addFriendTextView.setText("等待验证");
                        addFriendTextView.setOnClickListener(null);
                    } else {
                        addFriendTextView.setText("添加好友");
                        addFriendTextView.setOnClickListener(new OnClickListener() {
                            
                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                
                            }
                        });
                    }
                    break;
                }
            }
            final Integer pos = position;
            if (!hasRegister) {
                if (MainApplication.prefs.contains(mCurrentNumList.get(position) + "invite")) {
                    addFriendTextView.setText("已邀请");
                    addFriendTextView.setOnClickListener(null);
                } else {
                    addFriendTextView.setText("发送邀请");
                    addFriendTextView.setOnClickListener(new OnClickListener() {
                        
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            Uri uri = Uri.parse("smsto:" + mCurrentNumList.get(pos));
                            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                            intent.putExtra("sms_body", "二度空间可以轻松和你的朋友一起解决问题， 推荐你用一下。");
                            startActivity(intent);
                        }
                    });
                }
                
            }
            return convertView;
        }
        
    }
    
    public void getContact() {
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        String content = "";
        List<String> mCurrentNameList1 = new ArrayList<String>();
        List<String> mCurrentNumList1 = new ArrayList<String>();
        mCurrentNumList1.clear();
           /* File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myContact.txt");
            if (!file.exists()) {
                file.createNewFile();
            }*/
            /*RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(file.length());
*/
            while (cursor.moveToNext()) {

                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                mCurrentNameList1.add(name);
                //Log.e(DEBUG_TAG, name);
                String phoneNumber = "";
                if (hasPhone.equalsIgnoreCase("1")) {
                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                    while (phones.moveToNext()) {
                        phoneNumber = phones.getString(phones
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //Log.e(DEBUG_TAG, "…phoneNumber…  " + phoneNumber);
                        //raf.write((phoneNumber).getBytes());
                        //mCurrentNumList1.add(phoneNumber);
                        break;
                    }
                    phones.close();
                    
                }
                    
                else {
                    
                }
                mCurrentNumList1.add(phoneNumber);
                
                    

            }
            cursor.close();

            // raf.write(content.getBytes());
            //raf.close();
            mCurrentNameList = mCurrentNameList1;
            mCurrentNumList = mCurrentNumList1;

    }
}
