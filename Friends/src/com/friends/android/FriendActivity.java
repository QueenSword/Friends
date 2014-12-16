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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.friends.android.object.Contact;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class FriendActivity extends Activity {

    public static final String DEBUG_TAG = "FriendActivity";

    private SlidingMenu mSlidingMenu;
    private View mFriendMenuView;
    private Contact contact;
    private ListView mSlidingListView;
    private SlidingAdapter mSlidingAdapter;
    
    private ListView mListView;
    private ListAdapter mListAdapter;
    private List<Contact> mCurrentList = new ArrayList<Contact>();
    private List<String> mAvatarList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        initSlidingMenu();
        initActionBar();

        mListView = (ListView) findViewById(R.id.listview);
        mListAdapter = new ListAdapter();
        mListView.setAdapter(mListAdapter);

        LinearLayout newLayout = (LinearLayout) findViewById(R.id.new_linearlayout);
        newLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(FriendActivity.this, NewFriendListActivity.class);
                startActivity(intent);
            }
        });
        new RefreshDataThread().start();
    }

    public void initSlidingMenu() {
        mSlidingMenu = new SlidingMenu(FriendActivity.this);
        mSlidingMenu.setMode(SlidingMenu.RIGHT);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        mSlidingMenu.setBehindWidth(4 * MainApplication.width / 5);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.attachToActivity(FriendActivity.this, SlidingMenu.SLIDING_WINDOW);
        mFriendMenuView = getLayoutInflater().inflate(R.layout.sliding_menu_friend_detail_layout, null);
        mSlidingListView = (ListView) mFriendMenuView.findViewById(R.id.detail_listview);
        
        ImageView hideImageView = (ImageView) mFriendMenuView.findViewById(R.id.hide_imageview);
        hideImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mSlidingMenu.isMenuShowing()) {
                    mSlidingMenu.showContent();
                }
            }
        });

        mSlidingMenu.setMenu(mFriendMenuView);
    }

public class BlockContactThread extends Thread {
        
        private List<String> contactIds = new ArrayList<String>();;
        
        public BlockContactThread(List<String> contactIds) {
            this.contactIds = contactIds;
        }
        
        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                String response = client.userBlockContacts(contactIds);
                runOnUiThread(new Runnable() {
                    public void run() {
                        new RefreshFriendThread(contactIds.get(0)).start();
                    }
                });
            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                final InvalidAccessToken temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(FriendActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(FriendActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(FriendActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (FException e) {
                // TODO: handle exception
            }
        }
    }

public void RefreshFriendView() {
    TextView rmCountTextView = (TextView) mFriendMenuView.findViewById(R.id.rmcount_textview);
    TextView solutionCountTextView = (TextView) mFriendMenuView.findViewById(R.id.solutioncount_textview);
    TextView complaintTextView = (TextView) mFriendMenuView.findViewById(R.id.complaintcount_textview);
    rmCountTextView.setText("共发信息" + contact.rmCount);
    solutionCountTextView.setText("搭桥成功" + contact.solutionCount);
    complaintTextView.setText("被拉黑" + contact.complaintCount);
    mSlidingAdapter = new SlidingAdapter();
    mSlidingListView.setAdapter(mSlidingAdapter);
    ImageView callImageView = (ImageView) mFriendMenuView.findViewById(R.id.call_imageview);
    ImageView smsImageView = (ImageView) mFriendMenuView.findViewById(R.id.sms_imageview);
    callImageView.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact.phoneNumber));
            startActivity(intent);
        }
    });
    smsImageView.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(View v) {
            Uri uri = Uri.parse("smsto:" + contact.phoneNumber);
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            intent.putExtra("sms_body", "");
            startActivity(intent);
        }
    });
    
    TextView blockTextView = (TextView) mFriendMenuView.findViewById(R.id.block_textview);
    blockTextView.setOnClickListener(new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            new AlertDialog.Builder(FriendActivity.this) 
            .setTitle("警告")
            .setMessage("确定拉黑吗？")
            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    List<String> contactIds = new ArrayList<String>();
                    contactIds.add("" + contact.contactId);
                    new BlockContactThread(contactIds).start();
                }
            })
            .setNegativeButton("否", null)
            .show();
        }
    });
    
    TextView addFriendTextView = (TextView) mFriendMenuView.findViewById(R.id.addfriend_textview);
    addFriendTextView.setOnClickListener(new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            List<String> contactIds = new ArrayList<String>();
            contactIds.add("" + contact.contactId);
            new AddFriendThread(contactIds).start();
        }
    });
    
}

public class AddFriendThread extends Thread {
    
    private List<String> contactIds = new ArrayList<String>();;
    
    public AddFriendThread(List<String> contactIds) {
        this.contactIds = contactIds;
    }
    
    @Override
    public void run() {
        try {
            FClient client = new FClient();
            client.setUserToken(MainApplication.userToken);
            String response = client.userAddFriend(contactIds);
            /*runOnUiThread(new Runnable() {
                public void run() {
                    new RefreshFriendThread(contactIds.get(0)).start();
                }
            });*/
        } catch (InvalidAccessToken e) {
            // TODO: handle exception
            final InvalidAccessToken temp = e;
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(FriendActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                }
            });
            
            finish();
            MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
            MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
            MainApplication.userId = null;
            MainApplication.userToken = null;
            MainApplication.imageHeaders = null;
            Intent intent = new Intent(FriendActivity.this, LogingWebActivity.class);
            startActivity(intent);
        } catch (UnknowServerError e) {
            // TODO: handle exception
            final UnknowServerError temp =e;
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(FriendActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                }
            });
            
        } catch (FException e) {
            // TODO: handle exception
        }
    }
}

public class RefreshFriendThread extends Thread {
    private String contactId;
    
    public RefreshFriendThread(String contactId) {
        this.contactId = contactId;
    }
    @Override
    public void run() {
        try {
            FClient client = new FClient();
            client.setUserToken(MainApplication.userToken);
            JSONObject jsonObject = new JSONObject(client.getUserOther(contactId));
            contact = new Contact();
            Log.e(DEBUG_TAG, "contact content: " + jsonObject.toString());
            if (!jsonObject.isNull("contact_id")) {
                contact.fromJSON(jsonObject);
            }
            
            runOnUiThread(new Runnable() {
                public void run() {
                    RefreshFriendView();
                }
            });
            
        } catch (InvalidAccessToken e) {
            // TODO: handle exception
            final InvalidAccessToken temp = e;
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(FriendActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                }
            });
            
            finish();
            MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
            MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
            MainApplication.userId = null;
            MainApplication.userToken = null;
            MainApplication.imageHeaders = null;
            Intent intent = new Intent(FriendActivity.this, LogingWebActivity.class);
            startActivity(intent);
        } catch (UnknowServerError e) {
            // TODO: handle exception
            final UnknowServerError temp =e;
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(FriendActivity.this, temp.message, Toast.LENGTH_SHORT).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.friend, menu);
        return true;
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
        rightImageView.setImageResource(R.drawable.icon_add_gray);
        rightImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendActivity.this, InviteFriendActivity.class);
                startActivity(intent);
            }
        });
        TextView titleTextView = (TextView) view.findViewById(R.id.title_textview);
        titleTextView.setText("我的朋友");
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
                            MainApplication.imageLoader.displayImage(imageUrl, avatarImageView);
                        }
                    }
                });
            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                final InvalidAccessToken temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(FriendActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(FriendActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(FriendActivity.this, temp.message, Toast.LENGTH_SHORT).show();
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
            View view = getLayoutInflater().inflate(R.layout.friend_list_item_layout, null);
            final Contact item = mCurrentList.get(position);
            TextView nameTextView = (TextView) view.findViewById(R.id.name_textview);
            TextView contentTextView = (TextView) view.findViewById(R.id.content_textview);
            nameTextView.setText(item.displayName);
            contentTextView.setText(item.currentTitle);
            ImageView avatarImageView = (ImageView) view.findViewById(R.id.avatar_imageview);
            if (!mAvatarList.get(position).equals("") && !mAvatarList.get(position).endsWith("/")) {
                MainApplication.imageLoader.displayImage(mAvatarList.get(position), avatarImageView);
            } else {
                new RefreshAvatarThread(position, item.contactId, avatarImageView).start();
            }
            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mSlidingMenu.isMenuShowing()) {
                        mSlidingMenu.showContent();
                    } else {
                        mSlidingMenu.showMenu();
                    }
                    contact = item;
                    new RefreshFriendThread("" + contact.contactId).start();
                }
            });
            return view;
        }
    }
    
    public class RefreshDataThread extends Thread {
        
        @Override
        public void run() {
            
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                JSONObject jsonObject = new JSONObject(client.userGetFriends(1));
                JSONArray jsonArray = jsonObject.getJSONArray("contacts");
                mCurrentList.clear();
                mAvatarList.clear();
                for (int i = 0; i < jsonArray.length(); i ++) {
                    Contact contact = new Contact();
                    contact.fromJSON(jsonArray.getJSONObject(i));
                    mCurrentList.add(contact);
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
                        Toast.makeText(FriendActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(FriendActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp =e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(FriendActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (FException e) {
                // TODO: handle exception
            } catch (JSONException e) {
                // TODO: handle exception
            }
        }
    }

    public class SlidingAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if (contact != null && contact.titles != null) {
                return 4 + contact.titles.length();
            }
            return 4;
            
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
            View view = getLayoutInflater().inflate(R.layout.sliding_menu_friend_detail_list_item_layout, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
            TextView textView = (TextView) view.findViewById(R.id.textview);
            if (position == 0) {
                imageView.setImageResource(R.drawable.icon_name);
                textView.setText(contact.displayName);
                
            } else if (position == 1) {
                imageView.setImageResource(R.drawable.icon_phone_small);
                textView.setText(contact.phoneNumber);
                
            } else if (position == 2) {
                imageView.setImageResource(R.drawable.icon_title);
                textView.setText(contact.currentTitle);
                
            } else if (position < 3 + contact.titles.length()) {
                imageView.setImageResource(R.drawable.icon_title);
                try {
                    textView.setText(contact.titles.getString(position - 3));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (position == 3 + contact.titles.length()) {
                imageView.setVisibility(View.GONE);
                textView.setText(contact.comment);
            }
            return view;
        }
        
    }

}
