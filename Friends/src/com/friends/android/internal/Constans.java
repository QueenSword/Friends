package com.friends.android.internal;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.database.Cursor;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

public class Constans {
    public static final String SRC_ACTIVITY = "SRC_ACTIVITY";
    public static final String MAIN_ACTIVITY = "MAIN_ACTIVITY";
    public static final String MY_MESSAGE_LIST_ACTIVITY = "MY_MESSAGE_LIST_ACTIVITY";
    public static final String FRIEND_COUNT = "FRIEND_COUNT";
    public static final String FRIEND_2_COUNT = "FRIEND_2_COUNT";
    public static final String ID = "ID";
    public static final String USER_ID = "USER_ID";
    public static final String USER_TOKEN = "USER_TOKEN";
    public static final String CONTACT_ID = "CONTACT_ID";
    public static final String COMMUNICATION_ID = "COMMUNICATION_ID";
    public static final String REWARD_MESSAGE_ID = "REWARD_MESSAGE_ID";
    public static final String URL = "URL";
    public static final String CONTENT = "CONTENT";
    public static final String FIELDS = "FIELDS";
    public static final String SHOW_FEED_BACK = "SHOW_FEED_BACK";
    
    
    public static final String API_HOST = "http://114.255.159.96";
    public static final String AVATAR_PATH = "http://114.255.159.96/users/face/";
    public static final String MESSAGE_HOME_MESSAGE = "/messages/homemessage";
    public static final String MESSAGE_MY_COMMUNICATIONS = "/messages/mycommunications";
    public static final String MESSAGE_MY = "/messages/my";
    public static final String MESSAGE_MESSAGE = "/messages/messages/";
    public static final String USER_FACE = "/users/face/";
    public static final String MESSAGE_COMMUNICATION = "/messages/communication/";
    public static final String MESSAGE_CHAT = "/messages/chats";
    public static final String MESSAGE_UPLOAD = "/messages/upload";
    
    public static final String USER_ME = "/users/me";
    public static final String USER_OTHER = "/users/other";
    public static final String USER_LOGOUT = "/users/logout";
    public static final String USER_VERIFIED_CODE = "/users/verifiedcode";
    public static final String USER_CHANGE_PWD = "/users/change_password";
    public static final String USER_ADD_FRIEND = "/users/add_friends";
    public static final String USER_ACCEPT_FRIEND = "/users/accept_friends";
    public static final String USER_REJECT_FRIEND = "/users/reject_friends";
    
    public static final String USER_BLOCK_CONTACTS = "/users/block_contacts";
    public static final String USER_UNBLOCK_CONTACTS = "/users/unblock_contacts";
    public static final String USER_SEARCH_CONTACTS = "/users/search";
    public static final String USER_WAVE = "/users/waved";
    public static final String USER_SEARCH_BY_PHONENUM = "/users/search_by_phonenumbers";
    public static final String USER_GET_FRIENDS = "/users/friends";
    public static final String USER_GET_NEW_FRIENDS = "/users/new_friends";
    public static final String USER_GET_BLOCK_CONTACTS = "/users/block_contacts";
    public static final String USER_GET_ADD_FRIEND_ASKS = "/users/asks_for_friend";
    public static final String USER_GET_REFERAL = "/messages/referral";
    public static final String USER_GET_SHARE_INTO = "/users/share_urls";
    public static final String RICH_GET_ACCOUNTS = "/riches/accounts";
    public static final String RICH_GET_BALANCE = "/riches/balance";
    public static final String RICH_GET_ALIPAY_ACCOUNTS = "/riches/alipayaccounts";
    public static final String RICH_DO_PAY = "/riches/pay";
    public static final String MESSAGE_DO_FEED_BACK = "/messages/feedback";
    public static final String MESSAGE_GET_RM = "/messages/message";
    public static final String MESSAGE_GET_COMMUNICATION = "/messages/msgcomunication";
    public static final String MESSAGE_ADD_RM = "/messages/add_message";
    public static final String MESSAGE_REMOVE_RM = "/messages/delete_message";
    public static final String MESSAGE_CLOSE_RM = "/messages/close_rm";
    public static final String MESSAGE_EDIT_RM = "/messages/edit_message";
    public static final String MESSAGE_ADD_REWARD = "/messages/add_reward";
    public static final String MESSAGE_TO_PUBLIC = "/messages/to_public";
    public static final String MESSAGE_DO_COMMUNICATION = "/messages/to_public";
    public static final String MESSAGE_DO_CLOASE_COMMUNICATION = "/messages/close_communication";
    public static final String MESSAGE_DO_MARK_RM_AS_READ = "/messages/read";
    public static final String MESSAGE_GET_RMS = "/messages/messages";
    public static final String MESSAGE_GET_MY_RMS = "/messages/my";
    public static final String MESSAGE_GET_RMS_BY_CONTACTID = "/messages/messagesforme";
    public static final String MESSAGE_GET_COMMUNICATION_BY_ID = "/messages/communication";
    public static final String MESSAGE_GET_COMMUNICATIONS = "/messages/communications";
    public static final String MESSAGE_GET_COMMUNICATIONS_FOR_KEY = "/messages/communications";
    public static final String MESSAGE_GET_COMMUNICATIONS_FOR_RM = "/messages/myrmcommunications";
    public static final String MESSAGE_GET_CHATS = "/messages/chats";
    public static final String MESSAGE_ADD_TIME_STAMP = "/messages/lastcheckdate";
    public static final String MESSAGE_DO_SEARCH_FOR_RM = "/messages/search_contacts.json";
    public static final String MESSAGE_GET_FILE_URL = "/images/chat_url";
    public static final String MESSAGE_CHECK_IS_COMMUNICATED = "/messages/communication_status";
    public static final String MESSAGE_GET_SYSTEM_MESSAGES = "/messages/systems";
    public static final String MESSAGE_ADD_SYSTEM_MESSAGE = "/messages/systems/add";
    public static final String MESSAGE_DO_ADD_CHAT = "/messages/chat";
    
    
    
    
    
   
    
    
    
    /*
     // public void getContact() {
    // Cursor cursor = getContentResolver().query(
    // ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
    // while (cursor.moveToNext()) {
    // Log.e(DEBUT_TAG, "COME IN");
    // //获得通讯录中每个联系人的ID
    // String contactId = cursor.getString(cursor
    // .getColumnIndex(ContactsContract.Contacts._ID));
    //
    // //获得通讯录中联系人的名字
    // String name = cursor
    // .getString(cursor
    // .getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
    // Log.v(DEBUT_TAG, "…name…" + name);
    //
    // //查看给联系人是否有电话，返回结果是String类型，1表示有，0表是没有
    // String hasPhone = cursor
    // .getString(cursor
    // .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
    //
    // if (hasPhone.equalsIgnoreCase("1"))
    // hasPhone = "true";
    // else
    // hasPhone = "false";
    //
    // //如果有电话，根据联系人的ID查找到联系人的电话，电话可以是多个
    //
    // if (Boolean.parseBoolean(hasPhone)) {
    // Cursor phones = getContentResolver().query(
    // ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
    // null,
    // ContactsContract.CommonDataKinds.Phone.CONTACT_ID
    // + " = " + contactId, null, null);
    // while (phones.moveToNext()) {
    // String phoneNumber = phones
    // .getString(phones
    // .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    // Log.e(DEBUT_TAG, "…phoneNumber…  " + phoneNumber);
    // }
    // phones.close();
    // }
    //
    // //查找email地址，这里email也可以有多个
    //
    // Cursor emails = getContentResolver().query(
    // ContactsContract.CommonDataKinds.Email.CONTENT_URI,
    // null,
    // ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = "
    // + contactId, null, null);
    // while (emails.moveToNext()) {
    // String emailAddress = emails
    // .getString(emails
    // .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
    // Log.e(DEBUT_TAG, "…emailAddress…  " + emailAddress);
    // }
    // emails.close();
    //
    // //获得联系人的地址
    //
    // Cursor address = getContentResolver()
    // .query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
    // null,
    // ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID
    // + " = " + contactId, null, null);
    // while (address.moveToNext()) {
    // // These are all private class variables, don’t forget to create
    // // them.
    // String poBox = address
    // .getString(address
    // .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
    // String street = address
    // .getString(address
    // .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
    // String city = address
    // .getString(address
    // .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
    // String state = address
    // .getString(address
    // .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
    // String postalCode = address
    // .getString(address
    // .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
    // String country = address
    // .getString(address
    // .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
    // String type = address
    // .getString(address
    // .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
    // Log.e(DEBUT_TAG, "…city…  " + city);
    // }
    // }
    // cursor.close();
    // }
     * */
    
    /*
        public void getContact() {
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        String content = "";

        try {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myContact.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(file.length());

            while (cursor.moveToNext()) {

                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                if (hasPhone.equalsIgnoreCase("1"))
                    hasPhone = "true";
                else
                    hasPhone = "false";
                if (Boolean.parseBoolean(hasPhone)) {
                    raf.write(name.getBytes());
                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                    while (phones.moveToNext()) {
                        String phoneNumber = phones.getString(phones
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.e(DEBUT_TAG, "…phoneNumber…  " + phoneNumber);
                        raf.write((phoneNumber).getBytes());
                    }
                    phones.close();
                    raf.write("\n".getBytes());
                }

            }
            cursor.close();

            // raf.write(content.getBytes());
            raf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
     */
}
