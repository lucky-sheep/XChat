package cn.tim.xchat;

import android.app.Application;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.tencent.mmkv.MMKV;

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
    }
}
