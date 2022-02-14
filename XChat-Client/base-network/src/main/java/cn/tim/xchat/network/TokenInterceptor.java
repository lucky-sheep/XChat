package cn.tim.xchat.network;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mmkv.MMKV;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cn.tim.xchat.common.constans.StorageKey;
import cn.tim.xchat.network.config.NetworkConfig;
import cn.tim.xchat.network.model.ResponseModule;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TokenInterceptor implements Interceptor {
    private static final String TAG = "TokenInterceptor";
    MMKV mmkv = MMKV.defaultMMKV();

    private final Context context;
    public TokenInterceptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        String token = mmkv.getString(StorageKey.TOKEN_KEY, null);
        Request request = chain.request();
        String urlStr = request.url().toString();
        if(urlStr.contains(NetworkConfig.USER_REGISTER_URL) ||
                urlStr.contains(NetworkConfig.USER_LOGIN_URL)){
            return chain.proceed(request);
        }

        if(TextUtils.isEmpty(token)) {
            token = flushToken();
        }

        if(TextUtils.isEmpty(token)) {
            Log.e(TAG, "Token获取失败，检查服务器问题...");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.getMainExecutor().execute(()-> {ARouter.getInstance()
                        .build("/login/main")
                        .navigation(context);
                });
            }
            // 此时取消请求
            chain.call().cancel();
        }

        if(token == null) {
            throw new RuntimeException("Token is null！！");
        }

        request = chain.request().newBuilder()
                .addHeader("token", token)
                .build();
        return chain.proceed(request);
    }

    private String flushToken() {
        Map<String, Object> map = new HashMap<>();
        map.put("password", mmkv.getString(StorageKey.TOKEN_KEY, ""));
        map.put("username", mmkv.getString(StorageKey.USERNAME_KEY, ""));
        map.put("deviceId", mmkv.getString(StorageKey.DEVICE_ID_KEY, UUID.randomUUID().toString()));

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                JSONObject.toJSONString(map));

        Request request = new Request.Builder()
                .post(requestBody)
                .url(NetworkConfig.baseUrl + NetworkConfig.USER_LOGIN_URL)
                .build();

        try {
            ResponseBody responseBody = OkHttpUtils.getInstance()
                    .newCall(request).execute().body();
            assert responseBody != null;
            ResponseModule responseModule = JSON.parseObject(
                    responseBody.string(), ResponseModule.class);
            if(responseModule.getSuccess()) {
                String token = responseModule.getData().getString("token");
                mmkv.putString(StorageKey.TOKEN_KEY, token);
                Log.e(TAG, "========= 自动获取Token成功 =========");
                return token;
            }
        } catch (IOException e) {
            Log.e(TAG, "========= 自动获取Token失败，网络故障 =========");
        }
        return null;
    }
}
