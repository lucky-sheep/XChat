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

import java.util.UUID;

import cn.tim.xchat.chat.core.WebSocketService;
import cn.tim.xchat.common.constans.StorageKey;
import cn.tim.xchat.common.utils.MD5Utils;
import cn.tim.xchat.common.utils.MMKVUtil;
import cn.tim.xchat.login.LoginActivity;
import cn.tim.xchat.network.OkHttpUtils;

public class XChatApplication extends Application {
    private WebSocketService.WebSocketClientBinder webSocketClientBinder;

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

        if(!BuildConfig.DEBUG) {
            new DoKit.Builder(this)
                    .productId("4b16245fb438845e09386178c9dda449")
                    .build();
        }

        startWebSocketService();
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            webSocketClientBinder = (WebSocketService.WebSocketClientBinder) service;
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
        startService(bindIntent);
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
