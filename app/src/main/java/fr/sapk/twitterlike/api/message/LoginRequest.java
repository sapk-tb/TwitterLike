package fr.sapk.twitterlike.api.message;

/**
 * Created by sapk on 21/11/16.
 */

public class LoginRequest implements Message {
    String username;
    String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String toJSONString() {
        return "{" +
                "\"username\":\"" + username + "\"," +
                "\"password\":\"" + password + "\"" +
                '}';
    }
    @Override
    public String toString() {
        return "LoginRequest{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
