package cn.tim.xchat;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;

public class XChatApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(XChatApplication.this);
    }
}
