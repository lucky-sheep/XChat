package cn.tim.xchat;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

import cn.tim.xchat.common.utils.StatusBarUtil;

public class MainActivity extends AppCompatActivity implements ActivityProvider {

    private final Handler handler = new MainHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // 设置沉浸式状态栏
        StatusBarUtil.setStatusBarFullTransparent(MainActivity.this);
        StatusBarUtil.setDarkStatusIcon(getWindow(),true);

//        splashScreen.setKeepVisibleCondition(() -> true);
        MainActivityLogic activityLogic = new MainActivityLogic(this, savedInstanceState);
        getLifecycle().addObserver(activityLogic);

//        // 启动页延迟1S
//        postDelayRunnable(() -> {
//            splashScreen.setKeepVisibleCondition(() -> false);
//        }, 1000);
    }


    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void postRunnable(Runnable runnable) {
        runOnUiThread(runnable);
    }

    @Override
    public void postDelayRunnable(Runnable runnable, long delayMs) {
        handler.postDelayed(runnable, delayMs);
    }

    public static class MainHandler extends Handler {
        private final WeakReference<MainActivity> mActivityWR;
        // 在构造方法中传入需持有的Activity实例
        public MainHandler(MainActivity activity) {
            // 使用WeakReference弱引用持有Activity实例
            mActivityWR = new WeakReference<>(activity);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = mActivityWR.get();
        }
    }


    protected void onDestroy() {
        super.onDestroy();
        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}