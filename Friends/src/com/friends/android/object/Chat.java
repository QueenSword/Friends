package com.friends.android.object;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Date;

import org.json.JSONObject;

import android.R.integer;
import android.util.Log;


public class Chat {
    
    public static final Integer CHAT_TYPE_TEXT = 10;
    public static final Integer CHAT_TYPE_AUDIO = 20;
    public static final Integer CHAT_TYPE_FILE = 30;
    public static final Integer CHAT_TYPE_CONTACT = 40;
    
    public static final String DEBUG_TAG = "Chat Object";
    
    public Long chatId;
    public String content;
    public Integer type;
    public Long communicationId;
    public Long otherRMId;
    public Long publisherId;
    public Integer status;
    public Date createdAt;
    public String localFileName;
    public String createdAtStr;
    public Contact contact;

    public Integer senderStatus;
    
    public void fromJSON(JSONObject s){
        try{
            if(s == null) return;
            this.chatId = s.getLong("chat_id");
            this.content = URLDecoder.decode(s.getString("content"), "utf-8");
            this.publisherId = s.getLong("publisher_id");
            this.communicationId = s.getLong("communication_id");
            this.type = s.getInt("type");
            this.status = s.getInt("status");
            if(!s.isNull("created_at")) {
                this.createdAtStr = (String)s.get("created_at");
                //this.createdAt = Date.valueOf(s.getString("created_at"));
            } else if (!s.isNull("created")) {
                this.createdAtStr = (String)s.get("created");
                //this.createdAt = Date.valueOf(s.getString("created"));
            } else if (!s.isNull("create_at")) {
                this.createdAtStr = (String)s.get("create_at");
                //this.createdAt = Date.valueOf(s.getString("create_at"));
            }


        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public JSONObject toJSON(){
        JSONObject json = null;
        try{
            json = new JSONObject();
            json.put("chat_id", this.chatId);
            json.put("content", URLEncoder.encode(this.content, "utf-8"));
            json.put("communication_id", this.communicationId);
            json.put("publisher_id", this.publisherId);
            json.put("type", this.type);
            
        }catch(Exception e){
            e.printStackTrace();
        }    
        return json;
    }

}

