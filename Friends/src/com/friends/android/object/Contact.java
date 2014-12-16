package com.friends.android.object;

import java.net.URLDecoder;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class Contact extends BaseContact {
    public static final String DEBUG_TAG = "Contact";

    public static final Integer CONTACT_STATUS_DEFAULT = 0;
    public static final Integer CONTACT_STATUS_CONTACT = 1;
    public static final Integer CONTACT_STATUS_INVITED = 2;
    public static final Integer CONTACT_STATUS_ASKED = 3;
    public static final Integer CONTACT_STATUS_FRIEND = 4;

    public String userToken;
    public String phoneNumber;
    public String email;
    public String password;
    public String comment;
    public JSONArray titles;
    public JSONArray skills;
    public Double balance;

    public BaseContact referrer;
    public JSONArray alipayAccounts;
    public Integer status;

    public JSONObject toJSON() {
        
        try {
            JSONObject jsonObject = super.toJSON();
            if (this.phoneNumber != null) {
                jsonObject.put("phone_number", this.phoneNumber);
            }
            if (this.email != null) {
                jsonObject.put("email", this.email);
            }
            /*if (this.password != null) {
                jsonObject.put("password", this.password);
            }*/
            if (this.comment != null) {
                
                jsonObject.put("comment", /*this.comment*/URLEncoder.encode(this.comment, "utf-8"));
            }
            if (this.titles != null) {
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < this.titles.length(); i++) {
                    String title = this.titles.getString(i);
                    jsonArray.put(URLEncoder.encode(title, "utf-8"));
                    
                }
                jsonObject.put("titles", jsonArray);
                //jsonObject.put("titles", this.titles/*URLEncoder.encode(this.titles.toString(), "utf-8")*/);
            }
            if (this.skills != null) {
                jsonObject.put("skills", this.skills);
            }
            return jsonObject;
        } catch (Exception e) {
            // TODO: handle exception
        } 
        return null;

    }

    public void fromJSON(JSONObject s) {
        status = CONTACT_STATUS_DEFAULT;
        try {
            if (s == null)
                return;
            super.fromJSON(s);
            if (!s.isNull("password")) {
                this.password = s.getString("password");
            }
            if (!s.isNull("phone_number")) {
                this.phoneNumber = s.getString("phone_number");
            }
            if (!s.isNull("email")) {
                this.email = s.getString("email");
            }
            if (!s.isNull("comment")) {
                this.comment = URLDecoder.decode(s.getString("comment"), "utf-8");
            }
            if (!s.isNull("titles")) {
                JSONArray jsonArray = s.getJSONArray("titles");
                this.titles = new JSONArray();
                for (int i = 0; i < jsonArray.length(); i ++) {
                    this.titles.put(URLDecoder.decode(jsonArray.getString(i), "utf-8"));
                }
                //this.titles = s.getJSONArray("titles");
                
            }
            if (!s.isNull("skills")) {
                this.skills = s.getJSONArray("skills");
            }
            if (!s.isNull("rm_count")) {
                this.rmCount = (Integer) s.get("rm_count");
            }
            if (!s.isNull("complaint_count")) {
                this.complaintCount = (Integer) s.get("complaint_count");
            }
            if (!s.isNull("solution_count")) {
                this.solutionCount = (Integer) s.get("solution_count");
            }
            if (!s.isNull("balance")) {
                this.balance = (Double) s.get("balance");
            }
            if (!s.isNull("alipay_accounts")) {
                this.alipayAccounts = s.getJSONArray("alipay_accounts");
            }
            if (!s.isNull("referrer")) {
                this.referrer = new BaseContact();
                this.referrer.fromJSON(s.getJSONObject("referrer"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
