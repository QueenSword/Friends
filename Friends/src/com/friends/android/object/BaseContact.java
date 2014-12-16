package com.friends.android.object;

import java.sql.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class BaseContact {
    public Integer contactId;
    public String displayName;
    public String currentTitle;
    public String createdAt;
    public String updatedAt;
    public String profileUrl;
    public String modified;
    public Integer rmCount;
    public Integer complaintCount;
    public Integer solutionCount;
    public Integer type;
    public Integer friendCount;

    public Boolean isFriend;
    public Boolean isHasiFriend;
    
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (this.contactId != null) {
                jsonObject.put("contact_id", this.contactId);
            }
            if (this.displayName != null) {
                jsonObject.put("display_name", this.displayName);
            }
            if (this.currentTitle != null) {
                jsonObject.put("current_title", this.currentTitle);
            }
            
            
        } catch (JSONException e) {
            // TODO: handle exception
        }
        return jsonObject;
        
    }

    public void fromJSON(JSONObject s) {
        try {
            if (s == null)
                return;
            if (!s.isNull("contact_id")) {
                this.contactId = s.getInt("contact_id");
            }
            this.displayName = s.getString("display_name");
            if (!s.isNull("current_title")) {
                this.currentTitle = (String) s.get("current_title");
            } else if (!s.isNull("title")) {
                this.currentTitle = (String) s.getString("title");
            }

            if (!s.isNull("created_at")) {
                this.modified = s.getString("created_at");
                this.createdAt = s.getString("created_at");//Date.valueOf(s.getString("created_at"));
            } else if (!s.isNull("created")) {
                this.modified = s.getString("created");
                this.createdAt = s.getString("created");
            }
            if (!s.isNull("updated_at")) {
                this.modified = s.getString("updated_at");
                this.updatedAt = s.getString("updated_at");
            } else if (!s.isNull("updated")) {
                this.modified = s.getString("updated");
                this.updatedAt = s.getString("updated");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
