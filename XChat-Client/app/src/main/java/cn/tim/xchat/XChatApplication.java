package cn.tim.xchat;

import android.app.Application;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.tencent.mmkv.MMKV;

import java.util.UUID;

import cn.tim.xchat.common.constans.StorageKey;
import cn.tim.xchat.common.utils.MD5Utils;
import cn.tim.xchat.common.utils.MMKVUtil;
import cn.tim.xchat.login.LoginActivity;
import cn.tim.xchat.network.OkHttpUtils;

public class XChatApplication extends Application {

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
