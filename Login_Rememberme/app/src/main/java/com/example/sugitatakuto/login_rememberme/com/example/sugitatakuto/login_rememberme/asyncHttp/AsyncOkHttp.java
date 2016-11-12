package com.example.sugitatakuto.login_rememberme.com.example.sugitatakuto.login_rememberme.asyncHttp;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by sugitatakuto on 2016/11/08.
 */
public class AsyncOkHttp extends AsyncTask<String, Void, JSONObject>  {
    public AsyncResponse delegate = null;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    public AsyncOkHttp(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    public JSONObject doInBackground(String... params) {
        JSONObject jsonLoginObject = new JSONObject();
        JSONObject jsonData = null;
        String result = null;
        try {

            jsonLoginObject.put("email", params[0]);
            jsonLoginObject.put("password", params[1]);

        }catch(JSONException e) {
            e.printStackTrace();
        }

        String url = "http://10.0.2.2/db_connection.php";

        RequestBody body = RequestBody.create(JSON, jsonLoginObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
            response.body().close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        try {
            jsonData = new JSONObject(result);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return jsonData;
    }

    @Override
    public void onPostExecute(JSONObject result) {
        delegate.processFinish(result);
    }



}
