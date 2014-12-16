package com.friends.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.friends.android.ChatListActivity.AddChatThread;
import com.friends.android.ChatListActivity.UploadFileThread;
import com.friends.android.internal.Constans;
import com.friends.android.internal.FClient;
import com.friends.android.internal.FClient.FException;
import com.friends.android.internal.FClient.InvalidAccessToken;
import com.friends.android.internal.FClient.UnknowServerError;
import com.friends.android.object.Chat;
import com.friends.android.object.Contact;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ProfileInfoActivity extends Activity {

    public static final String DEBUG_TAG = "ProfileInfoActivity";

    private EditText mNamEditText;
    private LinearLayout mJobLinearLayout;
    private TextView mAddJobTextView;
    private LinearLayout mSkillLinearLayout;
    private TextView mAddSkillTextView;
    private Contact contact = new Contact();
    private ImageView mAvatarImageView;
    private String[] mFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);
        initActionBar();
        mFields = getResources().getStringArray(R.array.field_array);

        mNamEditText = (EditText) findViewById(R.id.name_edittext);

        mJobLinearLayout = (LinearLayout) findViewById(R.id.job_linearlayout);
        mAddJobTextView = (TextView) findViewById(R.id.add_job_textview);
        mAddJobTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mAddJobTextView.setVisibility(View.GONE);
                final View view = getLayoutInflater().inflate(R.layout.profile_info_add_job_layout, null);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 10, 0, 0);
                mJobLinearLayout.addView(view, lp);
                final EditText nameTextView = (EditText) view.findViewById(R.id.name_edittext);
                final ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
                imageView.setImageResource(R.drawable.icon_check_gray);
                imageView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        contact.titles.put(nameTextView.getText().toString());
                        mAddJobTextView.setVisibility(View.VISIBLE);
                        imageView.setImageResource(R.drawable.icon_delete);
                        imageView.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                mJobLinearLayout.removeView(view);
                                try {
                                    JSONArray jsonArray = new JSONArray();
                                    for (int i = 0; i < contact.titles.length(); i++) {
                                        if (!contact.titles.getString(i).equals(nameTextView.getText().toString())) {
                                            jsonArray.put(contact.titles.getString(i));
                                        }
                                    }
                                    contact.titles = jsonArray;
                                } catch (JSONException e) {
                                    // TODO: handle exception
                                }

                            }
                        });
                    }
                });

            }
        });
        mSkillLinearLayout = (LinearLayout) findViewById(R.id.skill_linearlayout);
        mAddSkillTextView = (TextView) findViewById(R.id.add_skill_textview);
        mAddSkillTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(ProfileInfoActivity.this, SelectTypeActivity.class);
                    String temp = "";
                    if (contact.skills.length() != 0) {
                        for (int i = 0; i < contact.skills.length(); i++) {
                            temp += contact.skills.getString(i);
                            temp += ",";
                        }
                        temp = temp.substring(0, temp.length() - 1);
                    }

                    intent.putExtra(Constans.CONTENT, temp);
                    startActivityForResult(intent, 2);
                } catch (JSONException e) {
                    // TODO: handle exception
                }
            }
        });
        mAvatarImageView = (ImageView) findViewById(R.id.avatar_imageview);
        MainApplication.imageLoader.displayImage(MainApplication.prefs.getString(Constans.AVATAR_PATH, null),
                mAvatarImageView);

        LinearLayout albumLayout = (LinearLayout) findViewById(R.id.album_layout);
        LinearLayout captureLayout = (LinearLayout) findViewById(R.id.capture_layout);
        captureLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, 0);
            }
        });
        albumLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        new RefreshDataThread().start();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        new ModifyProfileThread().start();
        super.onBackPressed();
    }

    public class ModifyProfileThread extends Thread {
        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                String response = client.putUserMe(contact);
                Log.e(DEBUG_TAG, "modify profile response" + response);
            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                final InvalidAccessToken temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ProfileInfoActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });

                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(ProfileInfoActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ProfileInfoActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (FException e) {
                // TODO: handle exception
            }
        }
    }

    public void RefreshView() {
        Log.e(DEBUG_TAG, "refresh view titles: " + contact.titles.toString());
        mNamEditText.setText(contact.displayName);
        mSkillLinearLayout.removeAllViews();
        if (contact.skills != null) {
            for (int i = 0; i < contact.skills.length(); i++) {
                try {
                    final int ii = i;
                    final View view = getLayoutInflater().inflate(R.layout.profile_info_add_skill_layout, null);
                    EditText nameTextView = (EditText) view.findViewById(R.id.name_edittext);
                    ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
                    imageView.setImageResource(R.drawable.icon_delete);
                    nameTextView.setText(mFields[Integer.parseInt(contact.skills.getString(i)) - 1]);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 10, 0, 0);
                    mSkillLinearLayout.addView(view, lp);
                    imageView.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            mSkillLinearLayout.removeView(view);
                            try {
                                JSONArray jsonArray = new JSONArray();
                                for (int j = 0; j < contact.skills.length(); j++) {
                                    if (!contact.skills.getString(j).equals(contact.skills.getString(ii))) {
                                        jsonArray.put(contact.skills.getString(j));
                                    }
                                }
                                contact.skills = jsonArray;
                            } catch (JSONException e) {
                                // TODO: handle exception
                            }

                        }
                    });
                } catch (JSONException e) {
                    // TODO: handle exception
                }

            }

        }

        mJobLinearLayout.removeAllViews();
        if (contact.titles != null) {
            for (int i = 0; i < contact.titles.length(); i++) {
                try {
                    final int ii = i;
                    final View view = getLayoutInflater().inflate(R.layout.profile_info_add_job_layout, null);
                    EditText nameTextView = (EditText) view.findViewById(R.id.name_edittext);
                    ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
                    imageView.setImageResource(R.drawable.icon_delete);
                    nameTextView.setText(contact.titles.getString(i));
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 10, 0, 0);
                    mJobLinearLayout.addView(view, lp);
                    imageView.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            mJobLinearLayout.removeView(view);
                            try {
                                JSONArray jsonArray = new JSONArray();
                                for (int j = 0; j < contact.titles.length(); j++) {
                                    if (!contact.titles.getString(j).equals(contact.titles.getString(ii))) {
                                        jsonArray.put(contact.titles.getString(j));
                                    }
                                }
                                contact.titles = jsonArray;
                            } catch (JSONException e) {
                                // TODO: handle exception
                            }
                        }
                    });
                } catch (JSONException e) {
                    // TODO: handle exception
                }

            }

        }
    }

    public class RefreshDataThread extends Thread {

        @Override
        public void run() {
            try {
                FClient client = new FClient();
                client.setUserToken(MainApplication.userToken);
                JSONObject jsonObject = new JSONObject(client.getUserMe());
                Log.e(DEBUG_TAG, "refresh data jsonobject: " + jsonObject.toString());
                contact.fromJSON(jsonObject);
                runOnUiThread(new Runnable() {
                    public void run() {
                        RefreshView();
                    }
                });
            } catch (InvalidAccessToken e) {
                // TODO: handle exception
                final InvalidAccessToken temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ProfileInfoActivity.this, temp.message, Toast.LENGTH_SHORT).show();
                    }
                });

                finish();
                MainApplication.prefs.edit().remove(Constans.USER_ID).commit();
                MainApplication.prefs.edit().remove(Constans.USER_TOKEN).commit();
                MainApplication.userId = null;
                MainApplication.userToken = null;
                MainApplication.imageHeaders = null;
                Intent intent = new Intent(ProfileInfoActivity.this, LogingWebActivity.class);
                startActivity(intent);
            } catch (UnknowServerError e) {
                // TODO: handle exception
                final UnknowServerError temp = e;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ProfileInfoActivity.this, temp.message, Toast.LENGTH_SHORT).show();
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
        rightImageView.setVisibility(View.INVISIBLE);
        TextView titleTextView = (TextView) view.findViewById(R.id.title_textview);
        titleTextView.setText("个人信息");
        ImageView leftImageView = (ImageView) view.findViewById(R.id.left_imageview);
        leftImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new ModifyProfileThread().start();
                finish();
            }
        });
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(android.app.ActionBar.LayoutParams.MATCH_PARENT,
                android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        actionBar.setCustomView(view, lp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_info, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // 0
                                                                                    // capture
                                                                                    // 1
                                                                                    // album
                                                                                    // 2
                                                                                    // skill
        if (0 == resultCode && requestCode == 2) {
            String temp1 = data.getExtras().getString(Constans.CONTENT);
            if (temp1 == null) {
                return;
            }
            String[] temp2 = temp1.split(",");
            mSkillLinearLayout.removeAllViews();
            for (int i = 0; i < temp2.length; i++) {
                contact.skills.put(temp2[i]);
                final View view = getLayoutInflater().inflate(R.layout.profile_info_add_skill_layout, null);
                EditText nameTextView = (EditText) view.findViewById(R.id.name_edittext);
                ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
                imageView.setImageResource(R.drawable.icon_delete);
                nameTextView.setText(mFields[Integer.parseInt(temp2[i]) - 1]);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 10, 0, 0);
                mSkillLinearLayout.addView(view, lp);
                final int ii = i;
                imageView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        mSkillLinearLayout.removeView(view);
                        try {
                            JSONArray jsonArray = new JSONArray();
                            for (int j = 0; j < contact.skills.length(); j++) {
                                if (!contact.skills.getString(j).equals(contact.skills.getString(ii))) {
                                    jsonArray.put(contact.skills.getString(j));
                                }
                            }
                            contact.skills = jsonArray;
                        } catch (JSONException e) {
                            // TODO: handle exception
                        }
                    }
                });

            }
            return;
        }

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

        super.onActivityResult(requestCode, resultCode, data);
    }

    public String Uri2FilePath(Uri uri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor.getString(actual_image_column_index);
        return img_path;
    }

    public class UploadFileThread extends Thread {
        private File file;
        private int type;

        public UploadFileThread(File file, int type) { // audio 0 pic 1 contact
                                                       // 2
            this.file = file;
            this.type = type;
        }

        @Override
        public void run() {
            FClient client = new FClient();
            client.setUserToken(MainApplication.userToken);
            String response = client.uploadFile(file);
            Log.e(DEBUG_TAG, "upload response: " + response);
            response = response.replaceAll("\"", "");
            final String imageUrl = Constans.API_HOST + response;
            MainApplication.prefs.edit().putString(Constans.AVATAR_PATH, imageUrl).commit();
            runOnUiThread(new Runnable() {
                public void run() {
                    MainApplication.imageLoader.displayImage(imageUrl, mAvatarImageView);
                }
            });

        }
    }
}
