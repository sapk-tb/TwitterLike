package fr.sapk.twitterlike.api;

import org.json.JSONObject;

import fr.sapk.twitterlike.api.message.LoginRequest;
import fr.sapk.twitterlike.api.message.LoginResponse;
import fr.sapk.twitterlike.api.message.RegisterRequest;
import fr.sapk.twitterlike.api.message.RegisterResponse;


/**
 * Created by sapk on 21/11/16.
 */

public class Api {

    static private String APIUrl = "http://vps288382.ovh.net/api/1/";

    public static LoginResponse Login(String username, String password) throws Exception {

        Request req = new Request("POST",
                APIUrl+"user/login",
                new LoginRequest(username,password)
        );
        JSONObject obj = req.send();
        return new LoginResponse(obj); //return {"secureToken":"050ed50d-9e50-43da-83ff-fd512e196e04","user_id":"58334c24b69bfd002cfead9f","status":true}
    }
    public static RegisterResponse Register(String username, String password, String name, String firstname) throws Exception {
        Request req = new Request("PUT",
                APIUrl+"user/register",
                new RegisterRequest(username,password,name,firstname)
        );
        JSONObject obj = req.send();
        return new RegisterResponse(obj);
    }
}
