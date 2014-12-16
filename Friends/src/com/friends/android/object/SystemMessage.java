package com.friends.android.object;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class SystemMessage {
    public String smId;
    public String content;
    public Integer type;
    public Boolean isSystem;

    public String createdAtStr;
    public Date createdAt;

    public void fromJSON(JSONObject s) {
        try {
            if (s == null)
                return;
            if (!s.isNull("system_message_id")) {
                this.smId = s.getString("system_message_id");
            } else if (!s.isNull("sm_id")) {
                this.smId = s.getString("sm_id");
            }
            
            this.content = URLDecoder.decode(s.getString("content"), "utf-8");
            this.type = s.getInt("type");
            if (!s.isNull("created_at")) {
                this.createdAtStr = (String) s.get("created_at");
                this.createdAt = Date.valueOf(s.getString("created_at"));
            } else if (!s.isNull("created")) {
                this.createdAtStr = (String) s.get("created");
                this.createdAt = Date.valueOf(s.getString("created"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject toJson() {
        try {
            JSONObject object = new JSONObject();
            if (this.smId != null) {
                object.put("system_message_id", this.smId);
            }
            if (this.content != null) {
                object.put("content", URLEncoder.encode(this.content, "utf-8"));
            }
            if (this.type != null) {
                object.put("type", this.type);
            }
            if (this.createdAt != null) {
                object.put("created_at", this.createdAt);
            }
            return object;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

}
