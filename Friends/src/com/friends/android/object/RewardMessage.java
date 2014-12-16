package com.friends.android.object;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RewardMessage {

    public static final Integer RM_TYPE_NORMAL = 10;
    public static final Integer RM_TYPE_DIRECTCONTACT = 20;

    public String rmId;
    public String content;
    public Integer type;
    public Integer reward;
    public Integer flags;
    public Integer isPrivacy;
    public JSONArray fields;
    public JSONArray contactIds;
    public JSONArray contacts;
    public JSONArray inviteIds;
    public BaseContact publisher;
    public BaseContact communicator;
    public Integer replyCount;
    public String createdAtStr;
    public String updateAtStr;
    public Date createdAt;
    public Date updateAt;
    public Integer isClosed;

    public Integer friend2Count;
    public Integer friendCount;

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (this.rmId != null) {
                jsonObject.put("reward_message_id", this.rmId);
            }
            if (this.content != null) {
                jsonObject.put("content", URLEncoder.encode(this.content, "utf-8"));

            }
            if (this.type != null) {
                jsonObject.put("type", this.type);
            }
            if (this.reward != null) {
                jsonObject.put("reward", this.reward);
            }
            if (this.isPrivacy != null) {
                jsonObject.put("is_privacy", this.isPrivacy);
            }
            if (this.fields != null) {
                jsonObject.put("fields", this.fields);
            }
            if (this.inviteIds != null) {
                jsonObject.put("inviter_ids", this.inviteIds);
            }
            if (this.contactIds != null) {
                jsonObject.put("contact_ids", this.contactIds);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return jsonObject;
    }

    public void fromJSON(JSONObject s) {
        try {
            if (s == null)
                return;
            if (!s.isNull("reward_message_id")) {
                this.rmId = s.getString("reward_message_id");
            } else if (!s.isNull("rm_id")) {
                this.rmId = s.getString("rm_id");
            }

            this.content = URLDecoder.decode(s.getString("content"), "utf-8");
            this.type = s.getInt("type");
            this.reward = s.getInt("reward");
            this.friend2Count = s.getInt("two_dimensional_friend_count");
            this.friendCount = s.getInt("friend_count");
            if (!s.isNull("is_privacy")) {
                this.isPrivacy = s.getInt("is_privacy");
            }

            if (s.get("fields") instanceof JSONArray) {
                this.fields = s.getJSONArray("fields");
            } else if (s.get("fields") instanceof String) {
                String mFields = s.getString("fields");
                if (mFields != null && mFields.length() > 0) {
                    String[] mArray = mFields.split(",");
                    for (int i = 0; i < mArray.length; i++) {
                        this.fields.put(i, mArray[i]);
                    }
                }
            }
            if (!s.isNull("contact_ids")) {
                this.contactIds = s.getJSONArray("contact_ids");
            }
            if (!s.isNull("inviter_ids")) {
                this.inviteIds = s.getJSONArray("inviter_ids");
            }

            this.publisher = new BaseContact();
            if (!s.isNull("publisher")) {

                this.publisher.fromJSON(s.getJSONObject("publisher"));
            } else {
                this.publisher.fromJSON(s.getJSONObject("publisher_id"));
            }
            if (!s.isNull("reply_count")) {
                this.replyCount = s.getInt("reply_count");
            }

            this.flags = s.getInt("flags");

            if (!s.isNull("created_at")) {
                this.createdAtStr = (String) s.get("created_at");
                // this.createdAt = Date.valueOf(s.getString("created_at"));
            } else {
                this.createdAtStr = (String) s.get("created");
                // this.createdAt = Date.valueOf(s.getString("created"));
            }

            if (!s.isNull("updated_at")) {
                this.updateAtStr = (String) s.get("updated_at");
                // this.updateAt = Date.valueOf(s.getString("updated_at"));
            } else if (!s.isNull("updated")) {
                this.updateAtStr = (String) s.get("updated");
                // this.updateAt = Date.valueOf(s.getString("updated"));
            }
            if (!s.isNull("is_closed")) {
                this.isClosed = s.getInt("is_closed");

                if (isClosed == 1) {
                    // this.flags = this.flags | RM_FLAG_CLOSE;
                    // enum {
                    // RM_FLAG_NEW = 1 << 0,
                    // RM_FLAG_SEEN = 1 << 1,
                    // RM_FLAG_FLAGGED = 1 << 2,
                    // RM_FLAG_CLOSE = 1 << 3,
                    // };
                }
            }

            if (this.updateAtStr == null) {
                this.updateAtStr = this.createdAtStr;
                this.updateAt = this.createdAt;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
