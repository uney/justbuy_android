package com.beehivesnetwork.justword.network;

import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by davidtang on 2015-11-18.
 */
public class CallApi {

    public static JSONArray callApi(JSONObject jsonParams) throws JSONException {

        String URL = jsonParams.getString("api");
        try {
            if (URL.toLowerCase().contains("https")) {
                HttpsURLConnection httpsConnection;
                httpsConnection = (HttpsURLConnection) new URL(URL).openConnection();
                String json = jsonParams.toString();
                String json64 = Base64.encodeToString(json.getBytes("UTF-8"), Base64.URL_SAFE);

                httpsConnection.setRequestMethod("POST");
                httpsConnection.setRequestProperty("Content-type", "text/json; charset=utf-8");

                // HMAC encryption
                Long tsLong = System.currentTimeMillis() / 1000;
                String currTs = tsLong.toString();
                httpsConnection.addRequestProperty("X-MICROTIME", currTs);

                DataOutputStream os = new DataOutputStream(httpsConnection.getOutputStream());
                os.writeBytes(json64);
                os.flush();
                os.close();

                int responseCode = httpsConnection.getResponseCode();
                if (responseCode == 200) {

                    // Get Response
                    InputStream is = httpsConnection.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while ((line = rd.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    rd.close();
                    // convert response string to JSONObject
                    JSONArray reJson = new JSONArray(response.toString());
                    return reJson;


                } else {
                    Log.d("callMDAPI API request", "fail sent: " + responseCode);
                    InputStream is = httpsConnection.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while ((line = rd.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    rd.close();
                    Log.d("callMDAPI API response", response.toString());
                }
            } else {
                HttpURLConnection httpConnection;
                httpConnection = (HttpURLConnection) new URL(URL).openConnection();
                String json = jsonParams.toString();
                String json64 = Base64.encodeToString(json.getBytes("UTF-8"), Base64.URL_SAFE);
                Log.d("call api result", "json: " + json);
                Log.d("call api result", "json: " + json64);

                httpConnection.setRequestMethod("POST");
                httpConnection.setRequestProperty("Content-type", "text/json; charset=utf-8");

                // HMAC encryption
                Long tsLong = System.currentTimeMillis() / 1000;
                String currTs = tsLong.toString();
                httpConnection.addRequestProperty("X-MICROTIME", currTs);

                DataOutputStream os = new DataOutputStream(httpConnection.getOutputStream());
                os.writeBytes(json64);
                os.flush();
                os.close();

                int responseCode = httpConnection.getResponseCode();
                Log.d("call api result", "responseCode: " + responseCode);

                if (responseCode == 200) {

                    // Get Response
                    InputStream is = httpConnection.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuffer response = new StringBuffer();
                    while ((line = rd.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    rd.close();
                    // convert response string to JSONObject
                    Log.d("call api result", "response: " + response.toString());

                    JSONArray reJson = new JSONArray(response.toString());
                    return reJson;
                } else {
                    Log.d("callMDAPI API request", "fail sent" + responseCode);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
