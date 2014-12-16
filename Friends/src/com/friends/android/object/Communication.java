package com.friends.android.object;

import java.sql.Date;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Communication {
    public static final Integer COMMUNICATION_STATUS_ALL = 0;
    public static final Integer COMMUNICATION_STATUS_PENDING = 10;
    public static final Integer COMMUNICATION_STATUS_ACCEPT = 20;
    public static final Integer COMMUNICATION_STATUS_DECLINE = 30;

    public String communicationId;
    public BaseContact target;
    public RewardMessage rm;
    public BaseContact builder;
    public Integer status;
    public Integer unreadChatCount;
    public Integer payCount;
    public Boolean isClosed;
    public Date createdAt;
    public String createdAtStr;
    public ArrayList<Chat> chats;

    public void fromJSON(JSONObject s) {
        try {
            if (s == null)
                return;
            this.communicationId = s.getString("communication_id");
            this.status = s.getInt("status");
            if (!s.isNull("created_at")) {
                this.createdAtStr = (String) s.get("created_at");
                // this.createdAt = Date.valueOf(s.getString("created_at"));
            } else if (!s.isNull("created")) {
                this.createdAtStr = (String) s.get("created");
                // this.createdAt = Date.valueOf(s.getString("created"));
            }

            if (!s.isNull("builder")) {
                this.builder = new BaseContact();
                builder.fromJSON(s.getJSONObject("builder"));
            }
            this.target = new BaseContact();
            target.fromJSON(s.getJSONObject("professor"));
            this.rm = new RewardMessage();
            this.rm.fromJSON(s.getJSONObject("reward_message"));
            if (!s.isNull("chats")) {
                JSONArray chatArray = s.getJSONArray("chats");
                if (chatArray != null) {
                    this.chats = new ArrayList<Chat>();
                    for (int i = 0; i < chatArray.length(); i++) {
                        Chat chat = new Chat();
                        chat.fromJSON(chatArray.getJSONObject(i));
                        this.chats.add(chat);
                    }
                }
            }
            
            this.unreadChatCount = s.getInt("unread_chat_count");
            this.payCount = s.getInt("pay_count");
            this.isClosed = s.getBoolean("is_closed");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
