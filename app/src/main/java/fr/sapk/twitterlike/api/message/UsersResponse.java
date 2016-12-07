package fr.sapk.twitterlike.api.message;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import fr.sapk.twitterlike.model.UserModel;

/**
 * Created by sapk on 21/11/16.
 */

public class UsersResponse implements Response {
    boolean status;
    List<UserModel> users = new LinkedList<>();

    public boolean isOk() {
        return status;
    }
    public List<UserModel> getUsers() {
        return users;
    }

    public UsersResponse(JSONArray obj) throws JSONException { //TODO force existence of this interface
        this.status = true; //obj.getBoolean("status");
        for (int i = 0; i< obj.length(); i++) { ///TODO use iterator ?
            JSONObject u = (JSONObject) obj.get(i);
            //TODO users.add(new UserModel(u.getString("username")));
        }
        Log.d("TwitterLike","new "+this);
    }
    public UsersResponse(List<UserModel> users) {
        this.users = users;
    }

    @Override
    public String toJSONString() {
        return "{" +
                "\"users\":\"" + users + "\"," + //TODO
                '}';
    }

    @Override
    public String toString() {
        return "UsersResponse{" +
                "users='" + users + '\'' + //TODO
                '}';
    }
}
