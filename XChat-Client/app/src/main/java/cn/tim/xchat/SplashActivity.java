package cn.tim.xchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.tencent.mmkv.MMKV;

import cn.tim.xchat.common.constans.StorageKey;
import cn.tim.xchat.common.utils.MD5Utils;

public class SplashActivity extends AppCompatActivity {

    Handler handler = new Handler(Looper.getMainLooper());
    MMKV mmkv = MMKV.defaultMMKV();

    boolean isDebugUI = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepVisibleCondition(() -> true);
        handler.postDelayed(() -> {
            splashScreen.setKeepVisibleCondition(() -> false);
        }, 1000);

        // 检测登录Token
        String userToken = mmkv.getString(StorageKey.TOKEN_KEY, null);
        if(TextUtils.isEmpty(userToken)) {
            if(isDebugUI) {
                ARouter.getInstance().build("/home/main").navigation();
//                ARouter.getInstance().build("/chat/list").navigation();
                mmkv.putString(StorageKey.USERID_KEY, "37901789");
                mmkv.putString(StorageKey.USERNAME_KEY, "zouchanglin");
                mmkv.putString(StorageKey.EMAIL_KEY, "zchanglin@163.com");
                mmkv.putString(StorageKey.PASSWORD_KEY, MD5Utils.string2MD5("123456"));
            }else {
                ARouter.getInstance().build("/login/main").navigation();
            }
        }else {
            ARouter.getInstance().build("/home/main").navigation();
        }
        finish();
    }
}