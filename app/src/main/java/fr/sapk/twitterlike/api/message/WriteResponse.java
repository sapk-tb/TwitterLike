package fr.sapk.twitterlike.api.message;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.sapk.twitterlike.model.MessageModel;

/**
 * Created by sapk on 21/11/16.
 */

public class WriteResponse implements Response {
    boolean status;
    MessageModel message = null;

    public boolean isOk() {
        return status;
    }
    public MessageModel getMessage() {
        return message;
    }

    public WriteResponse(JSONObject obj) throws JSONException { //TODO force existence of this interface
        this.status = obj.getBoolean("status");
        JSONObject m = obj.getJSONObject("message");
        this.message = new MessageModel(m.getJSONObject("author").getString("username"), m.getString("content"), m.getLong("date"));
        Log.d("TwitterLike","new"+this);
    }
    public WriteResponse(MessageModel message) {
        this.message = message;
    }

    @Override
    public String toJSONString() {
        return "{" +
                "\"message\":\"" + message + "\"," +
                '}';
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "message='" + message + '\'' +
                '}';
    }
}
