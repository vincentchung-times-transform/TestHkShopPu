package com.HKSHOPU.hk.net;

import android.util.Log;

import com.HKSHOPU.hk.data.bean.ShopLogisticBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class Web {
    public static final String TAG = Web.class.getSimpleName();
    public WebListener listener;
    private OkHttpClient okHttpClient;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public Web(WebListener weblistener) {
        listener = weblistener;
        okHttpClient = getUnsafeOkHttpClient();

    }

    public void InsertAuditLog(String url, String action, String parameter_in, String parameter_out) {


        RequestBody formBody = new FormBody.Builder()
                .add("action", action)
                .add("parameter_in", parameter_in)
                .add("parameter_out", parameter_out)
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }
    public void doBackendUserIDValidation(String url, String user_id) {


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }
    public void doGetLatestAppVersion(String url, String app_type) {


        RequestBody formBody = new FormBody.Builder()
                .add("app_type", app_type)
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }
    public void Get_Data(String url) {

        Request request = new Request.Builder().url(url).get().build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }

    public void Delete_Data(String url) {

        Request request = new Request.Builder().url(url).delete().build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }

    public void Delete_Product(String url) {

        RequestBody formBody = new FormBody.Builder()
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }


    public void Do_SocialLogin(String url,String email , String facebook_account, String google_account,String apple_account) {

        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("facebook_account", facebook_account)
                .add("google_account", google_account)
                .add("apple_account", apple_account)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
//                response.close();
//                Log.d(TAG, "Return Content ＝ " + response.body().string());
            }
        });
    }

    public void Do_Login(String url,String phone , String password ) {

        RequestBody formBody = new FormBody.Builder()
                .add("email", phone)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
//                response.close();
//                Log.d(TAG, "Return Content ＝ " + response.body().string());
            }
        });
    }

    public void Do_verifyCode(String url,String email ) {

        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
//                response.close();
//                Log.d(TAG, "Return Content ＝ " + response.body().string());
            }
        });
    }


    public void Do_Register(String url,String account_name ,String email, String password,String confirm_password ,String first_name,String last_name,String gender,String birthday,String phone,String address,String region,String district,String street_name,String street_no,String floor,String room ) {

        RequestBody formBody = new FormBody.Builder()
                .add("account_name", account_name)
                .add("email", email)
                .add("password", password)
                .add("confirm_password", confirm_password)
                .add("first_name", first_name)
                .add("last_name", last_name)
                .add("gender", gender)
                .add("birthday", birthday)
                .add("phone", phone)
                .add("address", address)
                .add("region", region)
                .add("district", district)
                .add("street_name", street_name)
                .add("street_no", street_no)
                .add("floor", floor)
                .add("room", room)


                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
//                response.close();
//                Log.d(TAG, "Return Content ＝ " + response.body().string());
            }
        });
    }

    public void Do_ShopAdd(String url, final String shop_title, String user_id, List<String> shop_category_id,final String bank_code, String bank_name,final String bank_account_name, String bank_account,final String address_name, String address_country_code,final String address_phone, String address_is_phone_show,final String address_area, String address_district,final String address_road, String address_number,final String address_other, String address_floor , String address_room,File postImg) {
        Log.d(TAG, "Do_ShopAdd Url ＝ " + url);

        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), postImg);
        MultipartBody.Builder requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("shop_title", shop_title)
                .addFormDataPart("user_id", String.valueOf(user_id))
                .addFormDataPart("bank_code", bank_code)
                .addFormDataPart("bank_name", bank_name)
                .addFormDataPart("bank_account_name", bank_account_name)
                .addFormDataPart("bank_account", bank_account)
                .addFormDataPart("address_name", address_name)
                .addFormDataPart("address_country_code", address_country_code)
                .addFormDataPart("address_phone", address_phone)
                .addFormDataPart("address_is_phone_show", address_is_phone_show)
                .addFormDataPart("address_area", address_area)
                .addFormDataPart("address_district", address_district)
                .addFormDataPart("address_road", address_road)
                .addFormDataPart("address_number", address_number)
                .addFormDataPart("address_other", address_other)
                .addFormDataPart("address_floor", address_floor)
                .addFormDataPart("address_room", address_room)
                .addFormDataPart("shop_icon", postImg.getName(), fileBody);

        for(int i=0; i< shop_category_id.size(); i++){
            requestBody.addFormDataPart("shop_category_id", String.valueOf(shop_category_id.get(i)));
        };

        MultipartBody multipartBody = requestBody.build();

//        RequestBody requestBody = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build();
//        Log.d(TAG, "Request ＝ " + requestBody);
//        Log.d(TAG, "Content ＝ " + bodyToString(request));

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }

    public void Do_ShopAddressPreset(String url,String shopid,String presetid) {

        RequestBody formBody = new FormBody.Builder()
                .add("shop_id", String.valueOf(shopid))
                .add("shop_address_id", String.valueOf(presetid))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
//                response.close();
//                Log.d(TAG, "Return Content ＝ " + response.body().string());
            }
        });
    }
    public void Do_ShopLogisticSetup(String url, ArrayList<ShopLogisticBean> mData) {
        List<JSONObject> myJSONObjects = new  ArrayList<JSONObject> (mData.size());
        JSONArray jsonArray=new JSONArray();
        try {
            for(int i=0; i<mData.size()-1; i++) {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("shipment_desc", mData.get(i).getShipmentDesc());
                jsonObject.put("onoff", mData.get(i).getOnOff());
                Log.d(TAG, "Content ＝ " + mData.get(i).getShipmentDesc());
//                myJSONObjects.add(jsonObject);
                jsonArray.put(jsonObject);
            }


            Log.d(TAG, "Content ＝ " + jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("shipment_settings",jsonArray.toString())
                .build();

//        RequestBody requestBody = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
//                response.close();
//                Log.d(TAG, "Return Content ＝ " + response.body().string());
            }
        });
    }


    public void Do_ShopDesUpdate(String url,String addressid,String description) {

        RequestBody formBody = new FormBody.Builder()
                .add("address_id", String.valueOf(addressid))
                .add("long_description", description)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
//                response.close();
//                Log.d(TAG, "Return Content ＝ " + response.body().string());
            }
        });
    }


    public void Do_AddNewShopBankAccount(String url,String code,String name,String account,String account_name) {

        RequestBody formBody = new FormBody.Builder()
                .add("code", code)
                .add("name", name)
                .add("account", account)
                .add("account_name", account_name)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
//                response.close();
//                Log.d(TAG, "Return Content ＝ " + response.body().string());
            }
        });
    }


    public void Do_ProductAdd(String url, String shop_id, String product_category_id, String product_sub_category_id, String product_title, int quantity, String product_description, int product_price, int shipping_fee, int weight, String new_secondhand,int product_pic_list_size ,ArrayList<File> product_pic_list, String product_spec_list, String user_id, int length, int width, int height, String shipment_method, int longterm_stock_up, String product_status, String product_spec_on) {
        Log.d(TAG, "Do_ProductAdd Url ＝ " + url);

        List<MultipartBody.Part> images = new ArrayList<>();

        for (int i=0; i < product_pic_list.size(); i++){

            images.add(prepareFilePart("provider_documents["+i+"][document]", product_pic_list.get(i)));

        }


        MultipartBody.Builder requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("shop_id", String.valueOf(shop_id))
                .addFormDataPart("product_category_id", String.valueOf(product_category_id))
                .addFormDataPart("product_sub_category_id", String.valueOf(product_sub_category_id))
                .addFormDataPart("product_title", String.valueOf(product_title))
                .addFormDataPart("quantity", String.valueOf(quantity))
                .addFormDataPart("product_description", String.valueOf(product_description))
                .addFormDataPart("product_price", String.valueOf(product_price))
                .addFormDataPart("shipping_fee", String.valueOf(shipping_fee))
                .addFormDataPart("weight", String.valueOf(weight))
                .addFormDataPart("new_secondhand", String.valueOf(new_secondhand))
                .addFormDataPart("product_pic_list", String.valueOf(product_pic_list))
                .addFormDataPart("product_spec_list", String.valueOf(product_spec_list))
                .addFormDataPart("user_id", String.valueOf(user_id))
                .addFormDataPart("length", String.valueOf(length))
                .addFormDataPart("width", String.valueOf(width))
                .addFormDataPart("height", String.valueOf(height))
                .addFormDataPart("shipment_method", String.valueOf(shipment_method))
                .addFormDataPart("longterm_stock_up", String.valueOf(longterm_stock_up))
                .addFormDataPart("product_status", String.valueOf(product_status))
                .addFormDataPart("product_pic_list_size", String.valueOf(product_pic_list_size))
                .addFormDataPart("product_spec_on", String.valueOf(product_spec_on));

        for(int i=0; i< product_pic_list.size(); i++){

            requestBody.addFormDataPart("product_pic"+i, product_pic_list.get(i).getName(), RequestBody.create(MediaType.parse("image/jpeg"), product_pic_list.get(i)));

        }

        MultipartBody multipartBody = requestBody.build();

        for(int i=0; i<multipartBody.parts().size(); i++){

            Log.d("requestBody_test", multipartBody.part(i).body().toString());

        }

//        RequestBody requestBody = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build();
//        Log.d(TAG, "Request ＝ " + requestBody);
//        Log.d(TAG, "Content ＝ " + bodyToString(request));

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }
    public void Do_ProductUpdate(String url, String product_id, String product_category_id, String product_sub_category_id, String product_title, int quantity, String product_description, int product_price, int shipping_fee, int weight, String new_secondhand,int product_pic_list_size ,ArrayList<File> product_pic_list, String product_spec_list, String user_id, int length, int width, int height, String shipment_method, int longterm_stock_up, String product_status, String product_spec_on) {
        Log.d(TAG, "Do_ProductAdd Url ＝ " + url);

        List<MultipartBody.Part> images = new ArrayList<>();

        for (int i=0; i < product_pic_list.size(); i++){

            images.add(prepareFilePart("provider_documents["+i+"][document]", product_pic_list.get(i)));

        }


        MultipartBody.Builder requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("product_id", String.valueOf(product_id))
                .addFormDataPart("product_category_id", String.valueOf(product_category_id))
                .addFormDataPart("product_sub_category_id", String.valueOf(product_sub_category_id))
                .addFormDataPart("product_title", String.valueOf(product_title))
                .addFormDataPart("quantity", String.valueOf(quantity))
                .addFormDataPart("product_description", String.valueOf(product_description))
                .addFormDataPart("product_price", String.valueOf(product_price))
                .addFormDataPart("shipping_fee", String.valueOf(shipping_fee))
                .addFormDataPart("weight", String.valueOf(weight))
                .addFormDataPart("new_secondhand", String.valueOf(new_secondhand))
                .addFormDataPart("product_pic_list", String.valueOf(product_pic_list))
                .addFormDataPart("product_spec_list", String.valueOf(product_spec_list))
                .addFormDataPart("user_id", String.valueOf(user_id))
                .addFormDataPart("length", String.valueOf(length))
                .addFormDataPart("width", String.valueOf(width))
                .addFormDataPart("height", String.valueOf(height))
                .addFormDataPart("shipment_method", String.valueOf(shipment_method))
                .addFormDataPart("longterm_stock_up", String.valueOf(longterm_stock_up))
                .addFormDataPart("product_status", String.valueOf(product_status))
                .addFormDataPart("product_pic_list_size", String.valueOf(product_pic_list_size))
                .addFormDataPart("product_spec_on", String.valueOf(product_spec_on));

        for(int i=0; i< product_pic_list.size(); i++){

            requestBody.addFormDataPart("product_pic"+i, product_pic_list.get(i).getName(), RequestBody.create(MediaType.parse("image/jpeg"), product_pic_list.get(i)));

        }

        MultipartBody multipartBody = requestBody.build();

        for(int i=0; i<multipartBody.parts().size(); i++){

            Log.d("requestBody_test", multipartBody.part(i).body().toString());

        }

//        RequestBody requestBody = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build();
//        Log.d(TAG, "Request ＝ " + requestBody);
//        Log.d(TAG, "Content ＝ " + bodyToString(request));

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }




    private MultipartBody.Part prepareFilePart(String partName, File file){

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestBody);

    }


    public void Do_ShopBankAccountUpdate(String url,String code,String name, String account_name,String account) {

        RequestBody formBody = new FormBody.Builder()
                .add("code",code)
                .add("name",name)
                .add("account_name",account_name)
                .add("account",account)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
//                response.close();
//                Log.d(TAG, "Return Content ＝ " + response.body().string());
            }
        });
    }



    public void Do_ShopAddAddress(String url, final String address_name, String address_country_code,final String address_phone, String address_is_phone_show,final String address_area, String address_district,final String address_road, String address_number,final String address_other, String address_floor , String address_room) {
        Log.d(TAG, "Do_ShopAdd Url ＝ " + url);
//        ArrayList<String> shop_category_id = new ArrayList<>();
//        shop_category_id.add(shop_category_id1);
//        shop_category_id.add(shop_category_id2);
//        shop_category_id.add(shop_category_id3);
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)

                .addFormDataPart("address_name", address_name)
                .addFormDataPart("address_country_code", address_country_code)
                .addFormDataPart("address_phone", address_phone)
                .addFormDataPart("address_is_phone_show", address_is_phone_show)
                .addFormDataPart("address_area", address_area)
                .addFormDataPart("address_district", address_district)
                .addFormDataPart("address_road", address_road)
                .addFormDataPart("address_number", address_number)
                .addFormDataPart("address_other", address_other)
                .addFormDataPart("address_floor", address_floor)
                .addFormDataPart("address_room", address_room)
                .build();
//        RequestBody requestBody = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
//        Log.d(TAG, "Request ＝ " + requestBody);
//        Log.d(TAG, "Content ＝ " + bodyToString(request));

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }

    public void Do_ShopAddresspresetShow(String url,String shop_id,String show_status) {

        RequestBody formBody = new FormBody.Builder()
                .add("shop_id",String.valueOf(shop_id))
                .add("show_status", show_status)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
//                response.close();
//                Log.d(TAG, "Return Content ＝ " + response.body().string());
            }
        });
    }

    public void Do_ShopBgUpdate(String url,String addressid,File postImg) {

        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), postImg);
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("address_id", String.valueOf(addressid))
                .addFormDataPart("background_pic", postImg.getName(), fileBody)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
//                response.close();
//                Log.d(TAG, "Return Content ＝ " + response.body().string());
            }
        });
    }

    public void Patch_Data(String url) {
        RequestBody formBody = new FormBody.Builder()
                .build();

        Request request = new Request.Builder().url(url).patch(formBody).build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }

    public void Do_ShopEmailUpdate(String url,String addressid,String email,String is_email_show) {

        RequestBody formBody = new FormBody.Builder()
                .add("address_id", String.valueOf(addressid))
                .add("shop_email", email)
                .add("email_on", is_email_show)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
//                response.close();
//                Log.d(TAG, "Return Content ＝ " + response.body().string());
            }
        });
    }

    public void Do_ShopPhoneUpdate(String url,String addressid,String countrycode,String phone,String is_phone_show) {

        RequestBody formBody = new FormBody.Builder()
                .add("address_id", String.valueOf(addressid))
                .add("address_country_code", countrycode)
                .add("shop_phone", phone)
                .add("shop_is_phone_show", is_phone_show)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
//                response.close();
//                Log.d(TAG, "Return Content ＝ " + response.body().string());
            }
        });
    }

    public void Do_ShopAddressDel(String url,ArrayList<String> addressid) {

        JSONArray jsonArray=new JSONArray();
        for(int i=0; i< addressid.size(); i++) {
            jsonArray.put(addressid.get(i));

        }
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("address_id",jsonArray.toString())
                .build();

//        RequestBody requestBody = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
//                response.close();
//                Log.d(TAG, "Return Content ＝ " + response.body().string());
            }
        });
    }


    public void Do_ShopIconUpdate(String url,String addressid,File postImg) {

        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), postImg);
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("address_id", String.valueOf(addressid))
                .addFormDataPart("shop_icon", postImg.getName(), fileBody)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
//                response.close();
//                Log.d(TAG, "Return Content ＝ " + response.body().string());
            }
        });
    }

    public void Do_ShopPicUpdate(String url,String addressid,File postImg) {

        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), postImg);
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("address_id", String.valueOf(addressid))
                .addFormDataPart("shop_pic", postImg.getName(), fileBody)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
//                response.close();
//                Log.d(TAG, "Return Content ＝ " + response.body().string());
            }
        });
    }
    public void Do_ShopTitleUpdate(String url,String addressid,String shopTitle) {

        RequestBody formBody = new FormBody.Builder()
                .add("address_id", String.valueOf(addressid))
                .add("shop_title", shopTitle)

                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
//                response.close();
//                Log.d(TAG, "Return Content ＝ " + response.body().string());
            }
        });
    }


    public void Do_ShopFaceBookOnOff(String url,String addressid,String facebook_on) {

        RequestBody formBody = new FormBody.Builder()
                .add("address_id", String.valueOf(addressid))
                .add("facebook_on", facebook_on)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
//                response.close();
//                Log.d(TAG, "Return Content ＝ " + response.body().string());
            }
        });
    }

    public void Do_ShopCategoryUpdate(String url,ArrayList<String> category_id_list) {
        JSONArray jsonArray=new JSONArray();
        for(int i=0; i< category_id_list.size(); i++) {
            jsonArray.put(category_id_list.get(i));

            Log.d(TAG, "category_id_list ＝ " + category_id_list.get(i));
        }
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("shop_category_id",jsonArray.toString())
                .build();

//        RequestBody requestBody = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
//                response.close();
//                Log.d(TAG, "Return Content ＝ " + response.body().string());
            }
        });
    }

    private static String bodyToString(final Request request){

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            }};

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            builder.connectTimeout(15, TimeUnit.SECONDS).build();

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void getSimilarProducts(String url, String user_id, String product_id) {


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", String.valueOf(user_id))
                .add("product_id", String.valueOf(product_id))
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }

    public void doLikeProductForBuyer(String url, String user_id, String product_id, String like) {


        RequestBody formBody = new FormBody.Builder()
                .add("user_id", String.valueOf(user_id))
                .add("product_id", String.valueOf(product_id))
                .add("like", String.valueOf(like))
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }

    public void doFollowShopForBuyer(String url, String user_id, String shop_id, String follow) {

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", String.valueOf(user_id))
                .add("shop_id", String.valueOf(shop_id))
                .add("follow", String.valueOf(follow))
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }

    public void doAddItemsToShoppingCart(String url, String user_id, String product_id, String product_spec_id, int quantity, String shop_id) {
        RequestBody formBody = new FormBody.Builder()
                .add("user_id", String.valueOf(user_id))
                .add("product_id", String.valueOf(product_id))
                .add("product_spec_id", String.valueOf(product_spec_id))
                .add("quantity", String.valueOf(quantity))
                .add("shop_id", String.valueOf(shop_id))
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();

//        Log.d("dsdsdsdssds", "quantity : "+String.valueOf(quantity));

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }

    public void doDeleteShoppingCartitems(String url, String user_id, String shop_id, String shopping_cart_item_id) {

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", String.valueOf(user_id))
                .add("shop_id", String.valueOf(shop_id))
                .add("shopping_cart_item_id", String.valueOf(shopping_cart_item_id))
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }

    public void doUpdateShoppingCartitems(String url, String shopping_cart_item_id, String new_quantity, String selected_shipment_id, String selected_user_address_id, String selected_payment_id) {


        RequestBody formBody = new FormBody.Builder()
                .add("shopping_cart_item_id", String.valueOf(shopping_cart_item_id))
                .add("new_quantity", String.valueOf(new_quantity))
                .add("selected_shipment_id", String.valueOf(selected_shipment_id))
                .add("selected_user_address_id", String.valueOf(selected_user_address_id))
                .add("selected_payment_id", String.valueOf(selected_payment_id))
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }

    public void Do_BuyerAddAddress(String url, final  String user_id, final String address_name, String address_country_code,final String address_phone,final String address_area, String address_district,final String address_road, String address_number,final String address_other, String address_floor , String address_room) {
        Log.d(TAG, "Do_BuyerAddAddress Url ＝ " + url);
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", String.valueOf(user_id))
                .addFormDataPart("name", String.valueOf(address_name))
                .addFormDataPart("country_code", String.valueOf(address_country_code))
                .addFormDataPart("phone", String.valueOf(address_phone))
                .addFormDataPart("area ", String.valueOf(address_area))
                .addFormDataPart("district", String.valueOf(address_district))
                .addFormDataPart("road ", String.valueOf(address_road))
                .addFormDataPart("number ", String.valueOf(address_number))
                .addFormDataPart("other", String.valueOf(address_other))
                .addFormDataPart("floor", String.valueOf(address_floor))
                .addFormDataPart("room", String.valueOf(address_room))
                .build();
//        RequestBody requestBody = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
//        Log.d(TAG, "Request ＝ " + requestBody);
//        Log.d(TAG, "Content ＝ " + bodyToString(request));

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }

    public void Do_UserInfoUpdate(String url, final String user_id, String user_name,final String gender, String birthday,final String phone, String facebook_on_off,final String old_password, String new_password) {

        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)

                .addFormDataPart("user_id", user_id)
                .addFormDataPart("user_name", user_name)
                .addFormDataPart("gender", gender)
                .addFormDataPart("birthday", birthday)
                .addFormDataPart("phone", phone)
                .addFormDataPart("facebook_on_off", facebook_on_off)
                .addFormDataPart("old_password", old_password)
                .addFormDataPart("new_password", new_password)
                .build();
//        RequestBody requestBody = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
//        Log.d(TAG, "Request ＝ " + requestBody);
//        Log.d(TAG, "Content ＝ " + bodyToString(request));

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }

    public void Do_UserAddAddress(String url, final String user_id, final String name, String country_code,final String phone,final String area, String district,final String road, String number,final String other, String floor , String room) {
        Log.d(TAG, "Do_ShopAdd Url ＝ " + url);
//        ArrayList<String> shop_category_id = new ArrayList<>();
//        shop_category_id.add(shop_category_id1);
//        shop_category_id.add(shop_category_id2);
//        shop_category_id.add(shop_category_id3);
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", user_id)
                .addFormDataPart("name", name)
                .addFormDataPart("country_code", country_code)
                .addFormDataPart("phone", phone)
                .addFormDataPart("area", area)
                .addFormDataPart("district", district)
                .addFormDataPart("road", road)
                .addFormDataPart("number", number)
                .addFormDataPart("other", other)
                .addFormDataPart("floor", floor)
                .addFormDataPart("room", room)
                .build();
//        RequestBody requestBody = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
//        Log.d(TAG, "Request ＝ " + requestBody);
//        Log.d(TAG, "Content ＝ " + bodyToString(request));

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }

    public void Do_BuyerAddressPreset(String url,String userid,String presetid) {

        RequestBody formBody = new FormBody.Builder()
                .add("user_id",userid)
                .add("user_address_id", presetid)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
//                response.close();
//                Log.d(TAG, "Return Content ＝ " + response.body().string());
            }
        });
    }

    public void Do_UserImgUpdate(String url,String userId,File postImg) {

        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), postImg);
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id",userId)
                .addFormDataPart("user_pic", postImg.getName(), fileBody)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
//                response.close();
//                Log.d(TAG, "Return Content ＝ " + response.body().string());
            }
        });
    }

    public void Do_GetSearchProduct(String url, String userId, String category_id, String sub_category_id, String max_seq, String keyword) {

        RequestBody formBody = new FormBody.Builder()
                .add("user_id",  String.valueOf(userId))
                .add("category_id", String.valueOf(category_id))
                .add("sub_category_id", String.valueOf(sub_category_id))
                .add("max_seq", max_seq)
                .add("key_word", String.valueOf(keyword))
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }

    public void Do_GetShopProduct(String url,String userId) {

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", userId)
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }

    public void Do_GetShopTopProduct(String url,String userId,int max_seq) {

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", userId)
                .add("max_seq", String.valueOf(max_seq))
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }

    public void Do_GetShopRecommend(String url,String userId,String mode,int max_seq) {

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", userId)
                .add("mode", mode)
                .add("max_seq", String.valueOf(max_seq))
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }

    public void Do_GetSearchStore(String url,String userId,String mode,String max_seq,String product_category_id, String product_sub_category_id, String keyword) {

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", userId)
                .add("mode", mode)
                .add("max_seq", max_seq)
                .add("product_category_id", String.valueOf(product_category_id))
                .add("product_sub_category_id", String.valueOf(product_sub_category_id))
                .add("keyword", keyword)
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }

    public void Do_GetRecommendedShops(String url,String userId ) {

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", userId)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
//                response.close();
//                Log.d(TAG, "Return Content ＝ " + response.body().string());
            }
        });
    }

    public void Product_Like(String url,String product_Id,String userId,String like ) {

        RequestBody formBody = new FormBody.Builder()
                .add("product_id", product_Id)
                .add("user_id", userId)
                .add("like", like)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
//                response.close();
//                Log.d(TAG, "Return Content ＝ " + response.body().string());
            }
        });
    }


    public void Store_Follow(String url,String follow) {

        RequestBody formBody = new FormBody.Builder()
                .add("follow", follow)
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }

    public void doShoppingCartQuantityDoubleChecking(String url, String user_id, String new_quantity) {

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("new_quantity", new_quantity)
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onErrorResponse(e);
                Log.d(TAG, "Return error ＝ " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onResponse(response);
                response.close();
                call.cancel();
            }
        });
    }

}
