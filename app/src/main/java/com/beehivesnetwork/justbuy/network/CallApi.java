package com.beehivesnetwork.justbuy.network;

import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by davidtang on 2015-11-18.
 */
public class CallApi {

    public static String callApi(String url) throws JSONException {

        String URL = url;
        try {
            HttpURLConnection httpConnection;
            httpConnection = (HttpURLConnection) new URL(URL).openConnection();

            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Content-type", "text/json; charset=utf-8");

            // HMAC encryption
            Long tsLong = System.currentTimeMillis() / 1000;
            String currTs = tsLong.toString();
            httpConnection.addRequestProperty("X-MICROTIME", currTs);


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
                return response.toString();
            } else {
                Log.d("callMDAPI API request", "fail sent" + responseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
