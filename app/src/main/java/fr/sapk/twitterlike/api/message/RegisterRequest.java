package fr.sapk.twitterlike.api.message;

/**
 * Created by sapk on 21/11/16.
 */

public class RegisterRequest implements Message {
    String username;
    String password;
    String name;
    String firstname;

    public RegisterRequest(String username, String password, String name, String firstname) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.firstname = firstname;
    }

    @Override
    public String toJSONString() {
        return "{" +
                "\"username\":\"" + username + "\"," +
                "\"password\":\"" + password + "\"," +
                "\"username\":\"" + name + "\"," +
                "\"username\":\"" + firstname + "\"" +
                '}';
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", firstname='" + firstname + '\'' +
                '}';
    }
}
