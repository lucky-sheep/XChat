package cn.tim.xchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.tencent.mmkv.MMKV;

import cn.tim.xchat.common.constans.StorageKey;

public class SplashActivity extends AppCompatActivity {

    Handler handler = new Handler(Looper.getMainLooper());
    MMKV mmkv = MMKV.defaultMMKV();

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
            ARouter.getInstance().build("/login/main").navigation();
        }else {
            // 1、检查Token有效性

        }
        //startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}