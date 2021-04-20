package com.hkshopu.hk.net;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;
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


    public void Get_Data(String url) {

        Request request = new Request.Builder().url(url).build();


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

    public void Do_ShopAdd(String url, final String shop_title, String user_id, int shop_category_id1, int shop_category_id2, int shop_category_id3, File postImg) {
        Log.d(TAG, "Do_ShopAdd Url ＝ " + url);
//        ArrayList<String> shop_category_id = new ArrayList<>();
//        shop_category_id.add(shop_category_id1);
//        shop_category_id.add(shop_category_id2);
//        shop_category_id.add(shop_category_id3);
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), postImg);
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("shop_title", shop_title)
                .addFormDataPart("user_id", user_id)
                .addFormDataPart("shop_category_id", String.valueOf(shop_category_id1))
                .addFormDataPart("shop_category_id", String.valueOf(shop_category_id2))
                .addFormDataPart("shop_category_id", String.valueOf(shop_category_id3))
                .addFormDataPart("shop_icon", postImg.getName(), fileBody)
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
}
