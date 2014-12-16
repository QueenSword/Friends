package com.friends.android.object;

import java.sql.Date;

import org.json.JSONObject;

public class Account {
    public String accountId;
    public Integer type;
    public Float amount;
    public String targetContactId;
    public Contact target;
    public String createdAtStr;
    public Date createdAt;
    
    public void fromJSON(JSONObject s){
        try{
            if(s == null) return;
            this.accountId = (String)s.get("account_id");
            this.targetContactId = (String)s.get("contact_id");
            this.type = (Integer)s.get("type");
            this.amount = (Float)s.get("amount");
            if(s.get("created_at") != null){
                this.createdAtStr = (String)s.get("created_at");
                this.createdAt = Date.valueOf(s.getString("created_at"));
            }else{
                this.createdAtStr = (String)s.get("created");
                this.createdAt = Date.valueOf(s.getString("created"));
            }


        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
