package fr.sapk.twitterlike.api;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import fr.sapk.twitterlike.api.message.Message;

/**
 * Created by sapk on 21/11/16.
 */

public class Request {

    String uri = "";
    String method = ""; //GET,POST,PUT,...
    String data = "";
    String result = "";


    public Request(String method, String uri, String data) {
        this.uri = uri;
        this.method = method;
        this.data = data;
    }
    public Request(String method, String uri, Message data) {
        this.uri = uri;
        this.method = method;
        this.data = data.toJSONString();
    }

    public JSONObject send() throws Exception {
        String output = "";
        Log.d("REST Start", " method:" + this.method + " uri:" + this.uri + " data:" + this.data);
        URL url = new URL(this.uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (this.method.equals("PUT") || this.method.equals("POST")) {
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "text/plain;charset=UTF-8");
        }
        ;
        conn.setInstanceFollowRedirects(false);
        conn.setDoInput(true);
        conn.setRequestMethod(this.method);
        //conn.setRequestProperty("Accept", "application/json");
        conn.connect();

        //Write data
        if ((this.method.equals("PUT") || this.method.equals("POST")) && !this.data.equals("")) {
            OutputStream buffer = conn.getOutputStream();
            buffer.write(this.data.getBytes());
            buffer.flush();
        }

        if (conn.getResponseCode() < 200 || conn.getResponseCode() > 300) {
            String out;
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            while ((out = br.readLine()) != null) {
                output += out; //Exemple of error to handle {"timestamp":616870112898060,"status":500,"error":"Internal Server Error","path":"/api/1/user/register","exception":"org.giwi.giwitter.exception.BusinessException","message":"Login déjà utilisé"}
            }
            throw new Exception("Failed : HTTP error code : " + conn.getResponseCode() + "####" + output);
        }

        //Read data
        String out;
        Log.d("REST response code ", "" + conn.getResponseCode());
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while ((out = br.readLine()) != null) {
            output += out;
        }
        Log.d("REST output", output);

        conn.disconnect();
        return new JSONObject(output);
    }
}
