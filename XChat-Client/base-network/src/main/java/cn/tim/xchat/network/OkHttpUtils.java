package cn.tim.xchat.network;

import android.content.Context;

import com.readystatesoftware.chuck.ChuckInterceptor;

import okhttp3.OkHttpClient;

public class OkHttpUtils {
    // 全局Application对象不影响，不存在内存泄漏
    private static Context context;

    private static OkHttpClient client;

    public static OkHttpClient getInstance(){
        if(client == null) {
            synchronized (OkHttpClient.class) {
                if(client == null){
                    client = new OkHttpClient.Builder()
                            .addInterceptor(new LogInterceptor())
                            .addInterceptor(new TokenInterceptor(context))
                            .addInterceptor(new ChuckInterceptor(context))
                            .build();
                }
            }
        }
        return client;
    }

    public static void initHttpService(Context context){
        OkHttpUtils.context = context;
    }
}
