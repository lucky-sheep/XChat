package cn.tim.xchat;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.didichuxing.doraemonkit.DoKit;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.UUID;

import cn.tim.xchat.chat.core.WebSocketService;
import cn.tim.xchat.common.constans.StorageKey;
import cn.tim.xchat.common.event.TokenEvent;
import cn.tim.xchat.common.utils.MD5Utils;
import cn.tim.xchat.network.OkHttpUtils;
import cn.tim.xchat.network.TokenInterceptor;

public class XChatApplication extends Application {
    private WebSocketService.WebSocketClientBinder webSocketClientBinder;
    private static final String TAG = "XChatApplication";
    private MMKV mmkv;
    private TokenInterceptor tokenInterceptor;


    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(XChatApplication.this);

        // 初始化MMKV
        String rootDir = MMKV.initialize(this);
        Log.i(ActivityProvider.TAG, "mmkv root: " + rootDir);

        // 初始化网络组件
        OkHttpUtils.initHttpService(this);

        saveDeviceId();

        if(BuildConfig.DEBUG) {
            new DoKit.Builder(this)
                    .productId("4b16245fb438845e09386178c9dda449")
                    .build();
        }

        EventBus.getDefault().register(this);
        new Thread(this::startWebSocketService).start();
    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(TokenEvent tokenEvent){
        if(TokenEvent.TokenType.TOKEN_OVERDUE.equals(tokenEvent.getType())) {
            Log.e(TAG, "刷新Token");
            if(tokenInterceptor == null) {
                synchronized (TokenInterceptor.class) {
                    if (tokenInterceptor == null)
                        tokenInterceptor = new TokenInterceptor(this);
                }
            }
            tokenInterceptor.flushToken();
        }
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            webSocketClientBinder = (WebSocketService.WebSocketClientBinder) service;
            webSocketClientBinder.startForeground();
        }

        public void onServiceDisconnected(ComponentName name) {
            webSocketClientBinder.cancel();
        }
    };

    /**
     * 开启并绑定WebSocket服务
     */
    public void startWebSocketService() {
        Intent bindIntent = new Intent(this, WebSocketService.class);
        startForegroundService(bindIntent);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    private void saveDeviceId() {
        String deviceId = Settings.System.getString(getContentResolver(),
                Settings.System.ANDROID_ID);
        if(TextUtils.isEmpty(deviceId)) {
            deviceId = UUID.randomUUID().toString();
        }
        Log.i(ActivityProvider.TAG, "deviceId = " + deviceId);
        String deviceIdMd5 = MD5Utils.string2MD5(deviceId);
        MMKV.defaultMMKV().putString(StorageKey.DEVICE_ID_KEY, deviceIdMd5);
    }
}
