package fr.sapk.twitterlike.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONObject;

import fr.sapk.twitterlike.api.message.LoginRequest;
import fr.sapk.twitterlike.api.message.LoginResponse;
import fr.sapk.twitterlike.api.message.MessagesResponse;
import fr.sapk.twitterlike.api.message.RegisterRequest;
import fr.sapk.twitterlike.api.message.RegisterResponse;
import fr.sapk.twitterlike.api.message.UserResponse;
import fr.sapk.twitterlike.api.message.UsersResponse;
import fr.sapk.twitterlike.api.message.WriteRequest;
import fr.sapk.twitterlike.api.message.WriteResponse;


/**
 * Created by sapk on 21/11/16.
 */

public class Api {

    static private String APIUrl = "http://vps288382.ovh.net/api/1/";

    /**
     * Is api (internetfro now) available boolean.
     *
     * @param context the context
     * @return the boolean
     */
    public static boolean isAvailable(Context context) {
        try {
            ConnectivityManager cm
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } catch (Exception e) {
            Log.e("TwitterLike", "Error on checking internet:", e);
        }
        //default allowed to access internet
        return true;
    }

    public static UserResponse CurrentUser(String token) throws Exception {

        Request req = new Request("GET",
                APIUrl+"private/user",
                null,
                token
        );
        JSONObject obj = req.send();
        return new UserResponse(obj);
    }
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

    public static WriteResponse Write(String userId, String content, String token) throws Exception {
        Request req = new Request("POST",
                APIUrl+"private/message",
                new WriteRequest(userId,content),
                token
        );
        JSONObject obj = req.send();
        return new WriteResponse(obj);
    }

    public static MessagesResponse GetMessages(String token) throws Exception {
        Request req = new Request("GET",
                APIUrl+"private/message",
                null,
                token
        );
        JSONObject obj = req.send();
        return new MessagesResponse(obj);
    }
    public static UsersResponse GetUsers(String token) throws Exception {

        Request req = new Request("GET",
                APIUrl+"private/user/list",
                null,
                token
        );
        JSONObject obj = req.send();
        return new UsersResponse(obj);
    }
}


