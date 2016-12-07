package fr.sapk.twitterlike.api.message;

import android.util.Log;

/**
 * Created by sapk on 21/11/16.
 */

public class WriteRequest implements Message {
    String user_id;
    String content;

    public WriteRequest(String userId, String content) {
        this.user_id = userId;
        this.content = content;
        Log.d("TwitterLike","new"+this);
    }

    @Override
    public String toJSONString() {
        return "{" +
                "\"user_id\":\"" + user_id + "\"," +
                "\"content\":\"" + content + "\"" + //TODO escape JSON
                '}';
    }
    @Override
    public String toString() {
        return "LoginRequest{" +
                "user_id='" + user_id + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
