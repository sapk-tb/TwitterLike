package fr.sapk.twitterlike.api.message;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.LinkedList;
import java.util.List;

import fr.sapk.twitterlike.model.MessageModel;

/**
 * Created by sapk on 21/11/16.
 */

public class MessagesResponse implements Response {
    boolean status;
    List<MessageModel> messages = new LinkedList<>();

    public boolean isOk() {
        return status;
    }
    public List<MessageModel> getMessages() {
        return messages;
    }

    public MessagesResponse(JSONObject obj) throws JSONException { //TODO force existence of this interface
        this.status = obj.getBoolean("status");
        JSONArray messArray = obj.getJSONArray("messages");
        for (int i = 0; i< messArray.length(); i++) { ///TODO use iterator ?
            JSONObject m = (JSONObject) messArray.get(i);
            messages.add(new MessageModel(m.getJSONObject("author").getString("username"), m.getString("content"), m.getLong("date")));
        }
        Log.d("TwitterLike","new"+this);
    }
    public MessagesResponse(List<MessageModel> messages) {
        this.messages = messages;
    }

    @Override
    public String toJSONString() {
        return "{" +
                "\"messages\":\"" + messages + "\"," + //TODO
                '}';
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "messages='" + messages + '\'' + //TODO
                '}';
    }
}
