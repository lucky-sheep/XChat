package cn.tim.xchat.network;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cn.tim.xchat.common.constans.StorageKey;
import cn.tim.xchat.common.event.TokenEvent;
import cn.tim.xchat.network.config.NetworkConfig;
import cn.tim.xchat.network.model.ResponseModule;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

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
                context.getMainExecutor().execute(()->
                        ARouter.getInstance()
                                .build("/login/main")
                                .navigation(context));
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
//        return chain.proceed(request);

        // 下面为了应对服务端Token失效
        Response response = chain.proceed(request);
        BufferedSource source = response.body().source();
        Buffer buffer = source.getBuffer();
        String readString = buffer.clone().readString(StandardCharsets.UTF_8);
        ResponseModule responseModule = JSON.parseObject(readString, ResponseModule.class);
        if(responseModule != null && !responseModule.getSuccess()) {
            if(responseModule.getCode() == 50001) {
                // 说明Token过期
                token = flushToken();

                request = chain.request().newBuilder()
                        .removeHeader("token")
                        .addHeader("token", token)
                        .build();

                return chain.proceed(request);
            }

            // 其他需要在拦截器里面处理的情况 ADD THIS
        }
        return response;
    }

    public String flushToken() {
        String password = mmkv.getString(StorageKey.PASSWORD_KEY, null);
        String username = mmkv.getString(StorageKey.USERNAME_KEY, null);
        Map<String, Object> map = new HashMap<>();
        map.put("password", password);
        map.put("username", username);
        map.put("deviceId", mmkv.getString(StorageKey.DEVICE_ID_KEY, UUID.randomUUID().toString()));

        if(password == null || username == null){
            // 由TokenInterceptor内部处理
            return null;
        }

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
            String responseStr = responseBody.string();
            Log.e(TAG, "responseStr = " + responseStr);
            ResponseModule responseModule = JSON.parseObject(responseStr, ResponseModule.class);
            if(responseModule.getSuccess()) {
                String token = responseModule.getData().getString("token");
                mmkv.putString(StorageKey.TOKEN_KEY, token);
                EventBus.getDefault().post(new TokenEvent(TokenEvent.TokenType.TOKEN_REFRESH));
                Log.e(TAG, "========= 自动获取Token成功 =========");
                return token;
            }
        } catch (IOException | JSONException e) {
            EventBus.getDefault().post(new TokenEvent(TokenEvent.TokenType.SERVER_ERROR));
            return "SERVER_ERROR";
        }
        return null;
    }
}
