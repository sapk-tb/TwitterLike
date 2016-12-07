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
    String token  = null;
    String result = "";


    public Request(String method, String uri, Message data, String token) {
        this.uri = uri;
        this.method = method;
        if (data != null){
            this.data = data.toJSONString();
        }
        this.token = token;
    }
    public Request(String method, String uri, Message data) {
        this.uri = uri;
        this.method = method;
        if (data != null){
            this.data = data.toJSONString();
        }
    }

    public JSONObject send() throws Exception {
        String output = "";
        Log.d("REST", "Start method:" + this.method + " uri:" + this.uri + " data:" + this.data+ " token:" + this.token);
        URL url = new URL(this.uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        Log.d("REST", "Request init");
        if ("PUT".equals(this.method) || this.method.equals("POST")) {
            conn.setDoOutput(true);
        }
        Log.d("REST", "Request output params set");
        if(this.token != null){
            Log.d("REST", "Token set: " + this.token);
            conn.addRequestProperty("X-secure-Token", token);
        }
        Log.d("REST", "Request token set");

        conn.setInstanceFollowRedirects(false);
        conn.setDoInput(true);
        conn.setRequestMethod(this.method);
        conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        //conn.setRequestProperty("Accept", "application/json");
        Log.d("REST", "Request ready");
        conn.connect();
        Log.d("REST", "Request open");

        //Write data
        if ((this.method.equals("PUT") || this.method.equals("POST")) && !this.data.equals("")) {
            Log.d("REST", "Writing data: " + this.data);
            OutputStream buffer = conn.getOutputStream();
            buffer.write(this.data.getBytes());
            buffer.flush();
        }else{
            Log.d("REST", "No data to send");
        }

        Log.d("REST", "Response code: "+conn.getResponseCode());

        if (conn.getResponseCode() < 200 || conn.getResponseCode() > 300) {
            Log.d("REST", "Error code detected : " + conn.getResponseCode());
            String out;
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            while ((out = br.readLine()) != null) {
                output += out; //Exemple of error to handle {"timestamp":616870112898060,"status":500,"error":"Internal Server Error","path":"/api/1/user/register","exception":"org.giwi.giwitter.exception.BusinessException","message":"Login déjà utilisé"}
            }
            throw new Exception("Failed : HTTP error code : " + conn.getResponseCode() + "####" + output);
        }

        //Read data
        //*
        String out;
        Log.d("REST", "response code: " + conn.getResponseCode());
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while ((out = br.readLine()) != null) {
            Log.d("REST", "output tmp: "+out);
            output += out;
        }
        Log.d("REST", "output: "+output);
        conn.disconnect();
        //*/
        /*
        String out = readStream(new BufferedInputStream(conn.getInputStream()));
        conn.disconnect();
        //*/
        return new JSONObject(output);
    }
    /*
    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
    */
}
