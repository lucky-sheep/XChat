package cn.tim.xchat.network;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.tencent.mmkv.MMKV;

import java.io.IOException;

import cn.tim.xchat.common.constans.StorageKey;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {
    private static final String TAG = "TokenInterceptor";
    MMKV mmkv = MMKV.defaultMMKV();
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        String token = mmkv.getString(StorageKey.TOKEN_KEY, null);
        if(TextUtils.isEmpty(token)) {
            // TODO 请求刷新Token，刷新失败走重新登录的逻辑
        }

        if(TextUtils.isEmpty(token)) {
            Log.e(TAG, "Token获取失败，检查服务器问题...");
        }

        Request request = chain.request().newBuilder()
                .addHeader("token", token)
                .build();
        return chain.proceed(request);
    }

}
