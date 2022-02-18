package cn.tim.xchat.common;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import cn.tim.xchat.common.utils.DensityUtil;
import cn.tim.xchat.common.utils.StatusBarUtil;

public abstract class XChatBaseActivity extends AppCompatActivity {
    public String TAG = "XChat-dev";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置沉浸式状态栏
        StatusBarUtil.setStatusBarFullTransparent(this);
        StatusBarUtil.setDarkStatusIcon(getWindow(),true);
    }

    @Override
    public void setContentView(int layoutResID) {
        setTheme(R.style.Theme_MaterialComponents_DayNight_NoActionBar);
        super.setContentView(layoutResID);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getNotchParams();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void getNotchParams() {
        final View decorView = getWindow().getDecorView();
        ViewGroup contentView = getContentView();
        if(contentView instanceof FrameLayout) {
            FrameLayout frameLayout = (FrameLayout) contentView;
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();

            decorView.post(() -> {
                WindowInsets rootWindowInsets = decorView.getRootWindowInsets();
                DisplayCutout displayCutout = rootWindowInsets.getDisplayCutout();
                if (displayCutout != null) {
                    Log.v(TAG, "Safe top of the screen" + displayCutout.getSafeInsetTop());
                    layoutParams.topMargin = displayCutout.getSafeInsetTop();
                } else {
                    // 默认设置为0即可
                    layoutParams.topMargin = DensityUtil.dp2px(0);
                }
                frameLayout.setLayoutParams(layoutParams);
            });
        }else {
            Log.e(TAG, "ContentView is not FrameLayout -> " + getPackageName());
        }
    }

    protected abstract ViewGroup getContentView();
}
