package com.beehivesnetwork.justword.network;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by davidtang on 2015-11-18.
 */
public class CustomApiConstructor {
    final static String domain = "http://sooryehan.beehivesnetwork.com";
    final static String imageUrl = "http://sooryehan.beehivesnetwork.com/images";
    final static int ITEMS_PER_PAGE = 10;

    public static String getDomain(){
        return domain;
    }

    public static String getImageUrl(){
        return imageUrl;
    }

    public static String getApi(){
        return domain+"/api/index.php";
    }
    public static String getProductApi(){
        return getApi()+"/get_product_list.php";
    }
    public static String getProductDetail(){
        return getApi()+"/get_product_detail.php";
    }
    public static String getCategories(){
        return getApi()+"/get_category.php";
    }
    public static String getPromotion(){
        return getApi()+"/get_promotion.php";
    }

    public static JSONObject makeProductListRequest(JSONObject json){
        int page;
        JSONObject jsonObject = json;
        try {
            page = json.getInt("page");
            jsonObject.put("requestType", "get_product_list");
            jsonObject.put("page", page);
            jsonObject.put("api", getApi());
            jsonObject.put("items_per_page", ITEMS_PER_PAGE);
            jsonObject.put("category", json.getString("category"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject makeBrandListRequest(JSONObject json){
        int page;
        JSONObject jsonObject = json;
        try {
            page = json.getInt("page");
            jsonObject.put("requestType", "get_brand_list");
            jsonObject.put("page", page);
            jsonObject.put("api", getApi());
            jsonObject.put("items_per_page", 100);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject makeCategoryListRequest(JSONObject json){
        int page;
        JSONObject jsonObject = json;
        try {
            page = json.getInt("page");
            String bid = json.getString("bid");
            jsonObject.put("requestType", "get_category_list");
            jsonObject.put("page", page);
            jsonObject.put("bid", bid);
            jsonObject.put("api", getApi());
            jsonObject.put("items_per_page", 100);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public static JSONObject makePromotionRequest(JSONObject json){
        JSONObject jsonObject = json;
        try {
            jsonObject.put("requestType", "get_promotion");
            jsonObject.put("api", getApi());
            jsonObject.put("items_per_page", 100);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject makeLoginRequest(JSONObject json){
        JSONObject jsonObject = json;
        try {
            jsonObject.put("requestType", "user_login");
            jsonObject.put("api", getApi());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject makeVerifyCode(JSONObject json){
        JSONObject jsonObject = json;
        try {
            jsonObject.put("requestType", "user_verify");
            jsonObject.put("api", getApi());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject makeUserRequest(JSONObject json){
        JSONObject jsonObject = json;
        try {
            jsonObject.put("requestType", "get_user_info");
            jsonObject.put("phone", json.getString("phone"));
            jsonObject.put("api", getApi());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject makeUpdateRequest(JSONObject json){
        JSONObject jsonObject = json;
        try {
            jsonObject.put("requestType", "set_user_info");
            jsonObject.put("api", getApi());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject makeGiftRequest(JSONObject json){
        JSONObject jsonObject = json;
        try {
            jsonObject.put("api", getApi());
            jsonObject.put("requestType", "get_gift_list");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject makeDiscountRequest(JSONObject json){
        JSONObject jsonObject = json;
        try {
            jsonObject.put("api", getApi());
            jsonObject.put("requestType", "get_discount");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject makePaymentRequest(JSONObject json){
        JSONObject jsonObject = json;
        try {
            jsonObject.put("api", getApi());
            jsonObject.put("requestType", "check_out");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
