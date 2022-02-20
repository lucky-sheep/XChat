package cn.tim.xchat;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import cn.tim.xchat.common.XChatBaseActivity;

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

        PermissionX.init(this)
                .permissions(Arrays.asList(
                        Manifest.permission.CHANGE_NETWORK_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ))
                .onExplainRequestReason(new ExplainReasonCallback() {
                    @Override
                    public void onExplainReason(@NonNull ExplainScope scope, @NonNull List<String> deniedList) {
                        scope.showRequestReasonDialog(deniedList, "需要您同意以下权限才能正常使用", "允许", "拒绝");
                    }
                })
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {

                    }
                });
    }


    @Override
    public AppCompatActivity getActivity() {
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