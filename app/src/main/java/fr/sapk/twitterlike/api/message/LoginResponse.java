package fr.sapk.twitterlike.api.message;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sapk on 21/11/16.
 */

public class LoginResponse implements Response {
    String secureToken;
    boolean status;

    public String getSecureToken() {
        return secureToken;
    }

    public boolean isOk() {
        return status;
    }

    public LoginResponse(JSONObject obj) throws JSONException { //TODO force existence of this interface
        this.secureToken = obj.getString("secureToken");
        this.status = obj.getBoolean("status");
    }
    public LoginResponse(String secureToken, boolean status) {
        this.secureToken = secureToken;
        this.status = status;
    }

    @Override
    public String toJSONString() {
        return "{" +
                "\"status\":\"" + status + "\"," +
                "\"secureToken\":\"" + secureToken + "\"" +
                '}';
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "secureToken='" + secureToken + '\'' +
                ", status=" + status +
                '}';
    }
}
