package fr.sapk.twitterlike.api.message;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sapk on 21/11/16.
 */

public class UserResponse implements Response {

    String id = null;
    String username = null;
    List<String> roles = new ArrayList<>();
    List<String> permissions = new ArrayList<>();
    String name;
    String firstname;

    public boolean isOk() {
        return id != null && username != null;
    }

    public UserResponse(JSONObject obj) throws JSONException {
        /*
        {
            "_id": "58417637b69bfd002cfeada5",
            "username": "antoine",
            "roles": ["USER"],
            "permissions": [],
            "name": "G",
            "firstname": "Ant"
        }
         */
        //Log.d("UserResponse", " obj:" + obj);
        this.id = obj.getString("_id");
        this.username = obj.getString("username");

        JSONArray roles = obj.getJSONArray("roles");
        for (int i = 0; i< roles.length(); i++) {
            this.roles.add(roles.getString(i));
        }
        JSONArray permissions = obj.getJSONArray("permissions");
        for (int i = 0; i< permissions.length(); i++) {
            this.permissions.add(permissions.getString(i));
        }

        this.name = obj.getString("name");
        this.firstname = obj.getString("firstname");
        Log.d("TwitterLike","new "+this);
    }

    public UserResponse(String id, String username, List<String> roles, List<String> permissions, String name, String firstname) {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.permissions = permissions;
        this.name = name;
        this.firstname = firstname;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public String getName() {
        return name;
    }

    public String getFirstname() {
        return firstname;
    }
    @Override
    public String toJSONString() {
        return "{"+"}";
    } //TODO

    @Override
    public String toString() {
        return "UserResponse{" + '}';
    } //TODO
}
