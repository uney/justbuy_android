package com.beehivesnetwork.justword.ui.autocomplete;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class SchoolListAdapter extends AutoCompleteAdapter {
    private ArrayList<String> resultList;
    JSONArray schoolJsonArray =null;

    public SchoolListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }


    private static final String LOG_TAG = "ExampleApp";

    private static final String API_BASE = "http://md.avrio.hk/api";
    private static final String INFO_TYPE = "/schools.php";


    @Override
    public ArrayList<String> autoComplete(String input) {
        ArrayList<String> resultList = new ArrayList<String>();

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(API_BASE + INFO_TYPE);
            sb.append("?term=" + URLEncoder.encode(input, "utf8"));
            sb.append("&lang=" + URLEncoder.encode("tc", "utf8"));


            Log.i("AutoComplete", "AutoComplete url check: " + sb.toString());
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            Log.i("AutoComplete", "AutoComplete" + jsonResults.toString());
            JSONArray predsJsonArray = null;
            if(jsonResults.toString().contains("data")){
                predsJsonArray = new JSONObject(jsonResults.toString()).getJSONArray("data");
            }
            if (predsJsonArray!=null&&predsJsonArray.length()>0){
                schoolJsonArray = predsJsonArray;
                // Extract the Place descriptions from the results
                resultList = new ArrayList<String>(predsJsonArray.length());
                for (int i = 0; i < predsJsonArray.length(); i++){
                    if(predsJsonArray.getJSONObject(i).has("name")){
                        resultList.add(predsJsonArray.getJSONObject(i).getString("name")+" ("+i+")");
                    }
                    if(predsJsonArray.getJSONObject(i).has("ENGLISH_NAME_EN")){
                        resultList.add(predsJsonArray.getJSONObject(i).getString("ENGLISH_NAME_EN")+" ("+i+")");
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }
        return resultList;
    }

    public JSONObject getSchool(int index) {
        try{
            return schoolJsonArray.getJSONObject(index);
        }catch (JSONException e){
            return null;
        }
    }


    public JSONArray getSchoolJsonArray(){
        return schoolJsonArray;
    }
}