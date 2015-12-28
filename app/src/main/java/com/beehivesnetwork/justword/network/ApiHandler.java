package com.beehivesnetwork.justword.network;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

/**
 * Created by davidtang on 2015-11-18.
 */
public class ApiHandler {
    private Thread currentApiTread;
    public void getApiResult(JSONObject jsonParams, final Handler handler) {
        if (currentApiTread != null) {
            currentApiTread = null;
        }
        final JSONObject newJson = jsonParams;
        currentApiTread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                try {
                    if(newJson.getString("type").equalsIgnoreCase("get_product_list")){
//                        ArrayList<Product> dataList;
//                        JSONObject jsonObject = CustomApiConstructor.makeProductListRequest(newJson);
//                        dataList = CustomPhaser.makeProductList(CallApi.callApi(jsonObject));
//                        data.putSerializable("product_list", dataList);
                    }

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



}
