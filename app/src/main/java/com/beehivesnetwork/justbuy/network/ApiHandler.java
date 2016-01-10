package com.beehivesnetwork.justbuy.network;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.beehivesnetwork.justbuy.object.Ads;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by davidtang on 2015-11-18.
 */
public class ApiHandler {
    private Thread currentApiTread;
    public void getApiResult(final String url, final Handler handler) {
        if (currentApiTread != null) {
            currentApiTread = null;
        }
        currentApiTread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                try {
                    ArrayList<Ads> list = AdsPhaser(CallApi.callApi(url));
                    data.putSerializable("result", list);
                } catch (Exception e) {
                    data.putSerializable("exception", e);
                    e.printStackTrace();
                } finally {
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            }
        });
        currentApiTread.start();
    }

    public static ArrayList<Ads> AdsPhaser(String jsonStr){
        ArrayList<Ads> phaserList = new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray jsonArray = jsonObject.getJSONArray("ads");
            for(int i=0;i<jsonArray.length();i++){
                try{
                    JSONObject adsObject = jsonArray.getJSONObject(i);
                    double price = adsObject.getJSONObject("price").getJSONObject("amount").getDouble("value");
                    String name = adsObject.getJSONObject("title").getString("value");
                    String id = adsObject.getString("id");
                    String description = adsObject.getJSONObject("description").getString("value");
                    String address = adsObject.getJSONObject("ad-address").getJSONObject("full-address").getString("value");
//                    double lat = adsObject.getJSONObject("locations").getJSONObject("latitude").getDouble("value");
//                    double lng = adsObject.getJSONObject("locations").getJSONObject("longitude").getDouble("value");                    double lat = adsObject.getJSONObject("locations").getJSONObject("latitude").getDouble("value");
                    double lat = 0;
                    double lng = 0;
                    String email = adsObject.getJSONObject("email").getString("value");
                    String phone = adsObject.getJSONObject("phone").getString("value");
                    String category = adsObject.getJSONObject("category").getJSONObject("id-name").getString("value");
                    JSONArray array = adsObject.getJSONObject("pictures").getJSONArray("picture");
                    String image = "";
                    for(int j=0; j<array.length(); j++){
                        JSONArray secondArray = array.getJSONObject(j).getJSONArray("link");
                        for(int k=0; k<secondArray.length(); k++){
                            if(secondArray.getJSONObject(k).getString("rel").equalsIgnoreCase("large")){
                                image = secondArray.getJSONObject(k).getString("href");
                            }
                        }
                    }
                    Ads ads =new Ads(price, name, image, id, description, category, email, phone, address, lat, lng);
                    ads.setTime(System.currentTimeMillis());
                    int min = (int)(price/100);
                    int max = (int)(price/10);
                    Random r = new Random();
                    int i1 = r.nextInt(max - min + 1) + min;
                    ads.setCountDown(i1*1000);
                    phaserList.add(ads);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return phaserList;
    }


}
