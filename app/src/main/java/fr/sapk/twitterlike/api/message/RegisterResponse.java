package fr.sapk.twitterlike.api.message;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sapk on 21/11/16.
 */

public class RegisterResponse implements Response {
    boolean status;

    public boolean isOk() {
        return status;
    }

    public RegisterResponse(JSONObject obj) throws JSONException { //TODO force existence of this interface
        this.status = obj.getBoolean("status");
    }
    public RegisterResponse(String secureToken, boolean status) {
        this.status = status;
    }

    @Override
    public String toJSONString() {
        return "{" +
                "\"status\":\"" + status + "\"" +
                '}';
    }

    @Override
    public String toString() {
        return "RegisterResponse{" +
                "status=" + status +
                '}';
    }
}
