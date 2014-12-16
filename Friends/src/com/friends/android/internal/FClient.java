package com.friends.android.internal;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.friends.android.MainApplication;
import com.friends.android.object.Chat;
import com.friends.android.object.Contact;
import com.friends.android.object.RewardMessage;
import com.friends.android.object.SystemMessage;

import android.net.Uri;
import android.util.Log;

public class FClient {

    public static final String DEBUG_TAG = "FClient";

    public String developerToken;
    public String userToken;
    
    public String getUserMe() throws FException{
        return doGet(Constans.USER_ME, null, null);
    }
    
    public String getUserOther(String contactId) throws FException {
        return doGet(Constans.USER_OTHER + "/" + contactId, null, null);
    }
    
    public String userAddFriend(List<String> contactIds) throws FException {
        JSONObject body = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < contactIds.size(); i ++) {
                jsonArray.put(contactIds.get(i));
            }
            body.put("contact_ids", jsonArray);
        } catch (JSONException e) {
            // TODO: handle exception
        }
        return doPost(Constans.USER_ADD_FRIEND, null, body);
    }
    
    // TODO:
    public String putUserMe(Contact contact) throws FException {
        
        Log.e(DEBUG_TAG, contact.toJSON().toString());
        return doPut(Constans.USER_ME, null, contact.toJSON());
    }
    
    public String userLogout() throws FException {
        return doPost(Constans.USER_LOGOUT, null, null);
    }
    
    //TODO:
    public String userVerifiedCode(String contactId) throws FException {
        JSONObject body = new JSONObject();
        try {
            body.put("user_token", this.userToken);
            body.put("contact_id", contactId);
        } catch (JSONException e) {
            // TODO: handle exception
        }
        return doPost(Constans.USER_VERIFIED_CODE, null, body);
    }
    
    //TODO:
    public String userChangPwd(String newPwd, String verifiedCode) throws FException {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("new_password", newPwd);
            jsonObject.put("verified_code", verifiedCode);
        } catch (JSONException e) {
            // TODO: handle exception
        }
        return doPut(Constans.USER_CHANGE_PWD, null, jsonObject);
    }
    
    //500
    public String userBlockContacts(List<String> contactIds) throws FException {
        JSONObject body = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < contactIds.size(); i ++) {
                jsonArray.put(contactIds.get(i));
            }
            body.put("contact_ids", jsonArray);
        } catch (JSONException e) {
            // TODO: handle exception
        }
        
        return doPost(Constans.USER_BLOCK_CONTACTS, null, body);
        
    }
    
    //500
    public String userUnBlockContacts(List<String> contactIds) throws FException {
        JSONObject body = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < contactIds.size(); i ++) {
                jsonArray.put(contactIds.get(i));
            }
            body.put("contact_ids", jsonArray);
        } catch (JSONException e) {
            // TODO: handle exception
        }
        
        return doPost(Constans.USER_UNBLOCK_CONTACTS, null, body);
    }
    
    //500
    public String userSearchContacts(String key) throws FException {
        JSONObject body = new JSONObject();
        try {
            body.put("key", key);
        } catch (JSONException e) {
            // TODO: handle exception
        }
        return doPost(Constans.USER_SEARCH_CONTACTS, null, body);
    }
    
    public String userWave() throws FException {
        return doGet(Constans.USER_WAVE, null, null);
    }
    
    //500
    public String userSearchByPhonenum(List<String> phonenums) throws FException {
        JSONObject body = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < phonenums.size(); i ++) {
                jsonArray.put(phonenums.get(i));
            }
            body.put("phone_numbers", jsonArray);
        } catch (JSONException e) {
            // TODO: handle exception
        }
        
        return doPost(Constans.USER_SEARCH_BY_PHONENUM, null, body);
        
    }
    
    public String userGetFriends(Integer offset) throws FException {
        String[] params = {"offset", "" + offset};
        return doGet(Constans.USER_GET_FRIENDS, params, null);
    }
    
    public String userGetNewFriends(Integer offset) throws FException {
        String[] params = {"offset", "" + offset};
        return doGet(Constans.USER_GET_NEW_FRIENDS, params, null);
    }
    
    public String userGetBlackList(Integer offset) throws FException {
        String[] params = {"offset", "" + offset};
        return doGet(Constans.USER_GET_BLOCK_CONTACTS, params, null);
    }
    
    public String userGetAddFriendAsks(Integer offset) throws FException {
        //String[] params = {"offset", "" + offset};
        return doGet(Constans.USER_GET_ADD_FRIEND_ASKS + "/" + offset, null, null);
    }
    // TODO:
    public String messageGetReferal(Integer offset, String fields) throws FException {
        String[] params = {"offset", "" + offset, "fields", fields};
        return doGet(Constans.USER_GET_REFERAL, params, null);
    }
    
    public String userGetShareInfo() throws FException {
        return doGet(Constans.USER_GET_SHARE_INTO, null, null);
    }
    
    public String richGetAccounts(Integer offset, Integer limit) throws FException {
        String[] params = {"offset", "" + offset, "limit", "" + limit};
        return doGet(Constans.RICH_GET_ACCOUNTS, params, null);
    }
    
    //500
    public String richGetBalance() throws FException {
        return doGet(Constans.RICH_GET_BALANCE, null, null);
    }
    
    
    public String richGetAlipayAccounts() throws FException {
        return doGet(Constans.RICH_GET_ALIPAY_ACCOUNTS, null, null);
    }
    
    public String richDoPay(String communicationId) throws FException {
        JSONObject body = new JSONObject();
        try {
            body.put("communication_id", communicationId);
        } catch (JSONException e) {
            // TODO: handle exception
        }
        return doPost(Constans.RICH_DO_PAY, null, body);
    }
    
    public String messageDoFeedBack(String content) throws FException {
        JSONObject body = new JSONObject();
        try {
            body.put("content", URLEncoder.encode(content, "utf-8"));
        } catch (JSONException e) {
            // TODO: handle exception
        } catch (UnsupportedEncodingException e) {
            // TODO: handle exception
        }
        return doPost(Constans.MESSAGE_DO_FEED_BACK, null, body);
    }
    
    public String messageGetRM(String rmId) throws FException {
        return doGet(Constans.MESSAGE_GET_RM + "/" + rmId, null, null);
    }
    
    public String messageGetCommunication(String communicationId) throws FException {
        String[] params = {"message_id", communicationId};
        return doGet(Constans.MESSAGE_GET_COMMUNICATION, params, null);
    }
    
    public String messageGetCommunicationById(String communicationId) throws FException {
        return doGet(Constans.MESSAGE_GET_COMMUNICATION_BY_ID + "/" + communicationId, null, null);
    }
    
    public String messagePostCommunication(String communicationId, Integer isDecline) throws FException {
        JSONObject body = new JSONObject();
        try {
            body.put("communication_id", communicationId);
            body.put("is_decline", isDecline);
        } catch (JSONException e) {
            // TODO: handle exception
        }
        return doPost(Constans.MESSAGE_GET_COMMUNICATION_BY_ID, null, body);
    }
    
    
    public String messageDoAddRm(RewardMessage rewardMessage) throws FException {
        JSONObject body = new JSONObject();
        body = rewardMessage.toJSON();
        Log.e(DEBUG_TAG, body.toString());
        return doPost(Constans.MESSAGE_ADD_RM, null, body);
    }
    
    public String messageRemoveRM(String rmId) throws FException {
        return doDelete(Constans.MESSAGE_REMOVE_RM + "/" + rmId, null, null);
    }
    
    public String messageCloseRM(String rmId) throws FException {
        return doPut(Constans.MESSAGE_CLOSE_RM + "/" + rmId, null, null);
    }
    
    public String messageEditRM(RewardMessage rewardMessage) throws FException {
        JSONObject body = new JSONObject();
        try {
            if (rewardMessage.reward != null) {
                body.put("reward", rewardMessage.reward);
            }
        } catch (JSONException e) {
            // TODO: handle exception
        }
        return doPut(Constans.MESSAGE_EDIT_RM, null, body);
    }
    
    public String messageAddReward(Integer rmId, int reward) throws FException {
        JSONObject body = new JSONObject();
        try {
            body.put("message_id", rmId);
            body.put("reward", reward);
        } catch (JSONException e) {
            // TODO: handle exception
        }
        return doPut(Constans.MESSAGE_ADD_REWARD, null, body);
    }
    
    public String messageToPublic(String rmId) throws FException {
        JSONObject body = new JSONObject();
        try {
            body.put("message_id", rmId);
        } catch (JSONException e) {
            // TODO: handle exception
        }
        return doPut(Constans.MESSAGE_TO_PUBLIC, null, body);
    }
    
    public String messageDoCommunication(String communicationId, Integer isDecline) throws FException {
        JSONObject body = new JSONObject();
        try {
            body.put("", communicationId);
            body.put("", isDecline);
        } catch (JSONException e) {
            // TODO: handle exception
        }
        return doPost(Constans.MESSAGE_DO_COMMUNICATION, null, body);
    }
    
    public String messageCloseCommunication(String communicationId) throws FException {
        return doPut(Constans.MESSAGE_DO_CLOASE_COMMUNICATION + "/" + communicationId, null, null);
    }
    
    public String messageMarkRMAsRead(String rmId) throws FException {
        return doPost(Constans.MESSAGE_DO_MARK_RM_AS_READ + "/" + rmId, null, null);
    }
    
    public String messageGetRMs(Integer offset, Integer limit) throws FException {
        String[] params = {"offset", "" + offset, "limit", "" + limit}; 
        return doGet(Constans.MESSAGE_GET_RMS, params, null);
    }
    
    public String messageGetMyRMs(Integer offset, Integer limit) throws FException {
        String[] params = {"offset", "" + offset, "limit", "" + limit}; 
        return doGet(Constans.MESSAGE_GET_MY_RMS, params, null);
    }
    
    public String messageGetRMsByContactId(String contactId, Integer offset, Integer limit) throws FException {
        String[] params = {"offset", "" + offset, "limit", "" + limit}; 
        return doGet(Constans.MESSAGE_GET_RMS_BY_CONTACTID + "/" + contactId, params, null);
    }
    
    public String messageGetCommunications(Integer offset, Integer limit, Integer status) throws FException {
        String[] params = {"offset", "" + offset, "limit", "" + limit, "status", "" + status}; 
        return doGet(Constans.MESSAGE_GET_COMMUNICATIONS, params, null);
    }
    
    public String messageGetCommunicationsForKey(Integer offset, Integer limit, Integer status, String key) throws FException {
        String[] params = {"offset", "" + offset, "limit", "" + limit, "status", "" + status, "key", key}; 
        return doGet(Constans.MESSAGE_GET_COMMUNICATIONS_FOR_KEY, params, null);
    }
    
    public String messageGetCommunicationsForRM(Integer offset, Integer limit, String rmId) throws FException {
        String[] params = {"offset", "" + offset, "limit", "" + limit, "message_id", rmId}; 
        return doGet(Constans.MESSAGE_GET_COMMUNICATIONS_FOR_RM, params, null);
    }
    
    public String messageGetChats(Integer offset, Integer limit, String communicationId) throws FException {
        String[] params = {"offset", "" + offset, "limit", "" + limit, "communication_id", communicationId}; 
        return doGet(Constans.MESSAGE_GET_COMMUNICATIONS_FOR_RM, params, null);
    }
    
    public String messageAddTimeStamp(String date, String communicationId) throws FException {
        JSONObject body = new JSONObject();
        try {
            body.put("dt", date);
            body.put("communication_id", communicationId);
        } catch (JSONException e) {
            // TODO: handle exception
        }
        return doPost(Constans.MESSAGE_ADD_TIME_STAMP, null, body);
    }
    
    public String messageSearchForDRM(Integer offset, String key) throws FException {
        String[] params = {"offset", "" + offset};
        JSONObject body = new JSONObject();
        try {
            body.put("key", key);
        } catch (JSONException e) {
            // TODO: handle exception
        }
        return doPost(Constans.MESSAGE_DO_SEARCH_FOR_RM, params, body);
    }
    
    public String messageGetFileUrl(String url) throws FException {
        String[] params = {"url", "" + url};
        return doGet(Constans.MESSAGE_GET_FILE_URL, params, null);
    }
    
    public String messageCheckIsCommunicated(String rmId, String contactId) throws FException {
        String[] params = {"contact_id", contactId};
        return doGet(Constans.MESSAGE_CHECK_IS_COMMUNICATED + "/" + rmId, params, null);
    }
    
    public String messageGetSystemMessage(String date) throws FException {
        String[] params = {"dt", date};
        return doGet(Constans.MESSAGE_GET_SYSTEM_MESSAGES, params, null);
    }
    
    public String messageAddSystemMessage(SystemMessage systemMessage) throws FException {
        JSONObject body = systemMessage.toJson();
        return doPost(Constans.MESSAGE_ADD_SYSTEM_MESSAGE, null, body);
    }
    
    public String messageAddChat(Chat chat) throws FException {
        JSONObject body = chat.toJSON();
        Log.e(DEBUG_TAG, body.toString());
        return doPost(Constans.MESSAGE_DO_ADD_CHAT, null, body);
    }
    
    
    
    
    
    public String messageHomeMessage() throws FException {
        return doGet(Constans.MESSAGE_HOME_MESSAGE, null, null);
    }

    public String messageMyCommunications(Integer offset, Integer limit) throws FException {
        List<String> paramList = new ArrayList<String>();
        if (offset != null) {
            paramList.add("offset");
            paramList.add("" + offset);
        }
        if (limit != null) {
            paramList.add("limit");
            paramList.add("" + limit);
        }
        String[] params = paramList.toArray(new String[0]);
        return doGet(Constans.MESSAGE_MY_COMMUNICATIONS, params, null);
    }

    public String messageMy() throws FException {
        return doGet(Constans.MESSAGE_MY, null, null);
    }

    public String messageMessage(int rmId) throws FException {
        return doGet(Constans.MESSAGE_MESSAGE + rmId, null, null);
    }

    public String messageCommunication(String communicationId) throws FException {
        return doGet(Constans.MESSAGE_COMMUNICATION + communicationId, null, null);
    }

    public String messageChat(Integer communicationId, Integer offset, Integer limit) throws FException {
        String[] params = { "communication_id", "" + communicationId, "offset", "" + offset, "limit", "" + limit };
        return doGet(Constans.MESSAGE_CHAT, params, null);
    }

    public String getImageUrl(String contactId) throws FException {
        try {
            String apiPath = Constans.API_HOST + Constans.USER_FACE + contactId;
            URL url = new URL(buildUrlWithParams(apiPath, null));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("X-app-token", "");
            conn.setRequestProperty("X-user-token", this.userToken);
            conn.setRequestProperty("Authorization", "Basic " + this.userToken);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                // 将输入流转换成字符串
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                String json = baos.toString();
                baos.close();
                is.close();
                return json;
            } else if (conn.getResponseCode() == 422) {
                throw new FException("逻辑错误");
            } else if (conn.getResponseCode() == 500) {
                throw new FException("服务器错误");
            }
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "network error");
            throw new FException("网络异常");
        }
        return null;
    }

    public static String encodeUrlParam(String s) {
        return Uri.encode(s, "UTF-8");
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String buildUrlWithParams(String url, String[] params) {
        StringBuilder buf = new StringBuilder();
        String sep = "";
        // if (userToken != null) {
        // buf.append(sep);
        // sep = "&";
        // buf.append("user_token=").append(userToken);
        // }
        if (params != null) {
            if (params.length % 2 != 0) {
                throw new IllegalArgumentException("'params.length' is " + params.length
                        + "; expecting a multiple of two");
            }
            for (int i = 0; i < params.length;) {
                String key = params[i++];
                String value = params[i++];
                if (value != null) {
                    buf.append(sep);
                    sep = "&";
                    buf.append(encodeUrlParam(key));
                    buf.append("=");
                    buf.append(encodeUrlParam(value));
                }
            }
        }
        return url + "?" + buf.toString();
    }

    public String doPost(String apiPath, String[] params, JSONObject body) throws FException {
        return doRequest(apiPath, params, body, "POST");
    }

    public String doGet(String apiPath, String[] params, JSONObject body) throws FException {
        return doRequest(apiPath, params, body, "GET");
    }
    
    public String doPut(String apiPath, String[] params, JSONObject body) throws FException {
        return doRequest(apiPath, params, body, "PUT");
    }
    
    public String doDelete(String apiPath, String[] params, JSONObject body) throws FException {
        return doRequest(apiPath, params, body, "DELETE");
    }

    public String doRequest(String apiPath, String[] params, JSONObject body, String method) throws FException {
        try {
            apiPath = Constans.API_HOST + apiPath;
            URL url = new URL(buildUrlWithParams(apiPath, params));
            Log.e(DEBUG_TAG, url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("X-app-token", "");
            conn.setRequestProperty("X-user-token", this.userToken);
            conn.setRequestProperty("Authorization", "Basic " + this.userToken);
            conn.setRequestMethod(method);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            if (body != null) {
                conn.setReadTimeout(TIME_OUT);
                conn.setConnectTimeout(TIME_OUT);
                conn.setDoInput(true); // 允许输入流
                conn.setDoOutput(true); // 允许输出流
                conn.setUseCaches(false); // 不允许使用缓存
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Charset", CHARSET); // 设置编码
                conn.setRequestProperty("connection", "keep-alive");
                //conn.setRequestProperty("Accept", "application/json");
                DataOutputStream out = new DataOutputStream(
                        conn.getOutputStream());
                
                out.writeBytes(body.toString());
                out.flush();
                out.close();
                
                
            }
            
            
            Log.e(DEBUG_TAG, "" + conn.getResponseCode() + url.toString());
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                // 将输入流转换成字符串
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                String json = baos.toString();
                baos.close();
                is.close();
                return json;
            } else if (conn.getResponseCode() == 401) {
                throw new InvalidAccessToken("登陆失效，请重新登陆");
            } else if (conn.getResponseCode() == 500) {
                throw new UnknowServerError("未知的服务器错误");
            }
        } catch (MalformedURLException e) {
            // TODO: handle exception
        } catch (IOException e) {
            // TODO: handle exception
        }
            
      
        return null;
    }

    public void DownloadVoice(String url, String path) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIME_OUT);
            conn.setReadTimeout(TIME_OUT);
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                File file = new File(MainApplication.voicesPath, path);
                OutputStream os = null;
                try {
                    os = new FileOutputStream(file);
                    byte buffer[] = new byte[4 * 1024];
                    while ((is.read(buffer)) != -1) {
                        os.write(buffer);
                    }
                    os.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        os.close();
                        is.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static final int TIME_OUT = 10 * 1000; // 超时时间
    private static final String CHARSET = "utf-8"; // 设置编码

    public String uploadFile(File file) {
        String result = null;
        String BOUNDARY = "----WebKitFormBoundarycC4YiaUFwM44F6rT"; // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型

        try {
            URL url = new URL(Constans.API_HOST + Constans.MESSAGE_UPLOAD);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("Authorization", "Basic " + this.userToken);
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=----WebKitFormBoundarycC4YiaUFwM44F6rT");

            if (file != null) {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(LINE_END);
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */


                sb.append("Content-Disposition:attachment; filename=" + file.getName()
                        + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    Log.e(DEBUG_TAG, "xxxxxxxx");
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功 当响应成功，获取响应的流
                 */
                
                int res = conn.getResponseCode();
                Log.e(DEBUG_TAG, "response code:" + res);
                InputStream input = conn.getInputStream();
                StringBuffer sb1 = new StringBuffer();
                int ss;
                while ((ss = input.read()) != -1) {
                    sb1.append((char) ss);
                }
                result = sb1.toString();
                Log.e(DEBUG_TAG, "result : " + result);
                return result;
                // }
                // else{
                // Log.e(TAG, "request error");
                // }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(DEBUG_TAG, "in ioexception");
            e.printStackTrace();
        }
        return null;

    }

    public class FException extends Exception {
        public String message;

        public FException(String message) {
            this.message = message;
        }
        
        

    }
    
  //401
    public class InvalidAccessToken extends FException {
        public InvalidAccessToken(String message) {
            super(message);
        }
        
    }
    
    //500
    public class UnknowServerError extends FException {
        public static final long serialVersionUID = 1L;

        public UnknowServerError(String message) {
            super(message);
        }
    }
}
