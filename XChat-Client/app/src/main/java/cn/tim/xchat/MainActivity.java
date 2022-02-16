package cn.tim.xchat;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.view.Window;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import java.lang.ref.WeakReference;

import cn.tim.xchat.common.XChatBaseActivity;
import cn.tim.xchat.common.utils.StatusBarUtil;

@Route(path = "/home/main")
public class MainActivity extends XChatBaseActivity implements ActivityProvider {

    @Autowired(name = "token", required = true)
    String token;

    @Autowired(name = "tab", required = false, desc = "chat")
    String tab;

    private final Handler handler = new MainHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ARouter.getInstance().inject(this);
        MainActivityLogic activityLogic = new MainActivityLogic(this, savedInstanceState, tab);
        getLifecycle().addObserver(activityLogic);
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

    @Override
    protected ViewGroup getContentView() {
        return findViewById(android.R.id.content);
    }

    protected void onDestroy() {
        super.onDestroy();
        if(handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}