package cn.tim.xchat.login;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;

import cn.tim.xchat.common.XChatBaseActivity;
import cn.tim.xchat.common.constans.StorageKey;
import cn.tim.xchat.common.event.TokenEvent;
import cn.tim.xchat.common.widget.loading.LoadingComponent;
import cn.tim.xchat.common.widget.toast.XChatToast;
import cn.tim.xchat.login.module.UserInfo;
import cn.tim.xchat.network.model.ResponseModule;

@Route(path = "/login/main")
public class LoginActivity extends XChatBaseActivity {
    public static final String TAG = "LoginActivity";

    MMKV mmkv = MMKV.defaultMMKV();

    public static final int Y_OFFSET = 50;
    public LoadingComponent loadingComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Fragment loginFragment = (Fragment) ARouter.getInstance().build("/login/register").navigation();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.login_main_content, loginFragment)
                .commit();
        loadingComponent = new LoadingComponent(this, getContentView());
    }

    @Override
    protected ViewGroup getContentView() {
        return findViewById(android.R.id.content);
    }

    public void successAuthHandle(ResponseModule responseModule) {
        JSONObject data = responseModule.getData();

        String token = data.getString("token");
        mmkv.putString(StorageKey.TOKEN_KEY, token);

        UserInfo userInfo = data.getObject("userInfo", UserInfo.class);
        if(userInfo != null) {
            EventBus.getDefault().post(new TokenEvent(TokenEvent.TokenType.TOKEN_REFRESH));
            mmkv.putString(StorageKey.USERID_KEY, userInfo.getId());
            mmkv.putString(StorageKey.USERNAME_KEY, userInfo.getUsername());
            mmkv.putString(StorageKey.EMAIL_KEY, userInfo.getEmail());
            mmkv.putString(StorageKey.PASSWORD_KEY, userInfo.getPassword());

            String deviceId = mmkv.getString(StorageKey.DEVICE_ID_KEY, "");
            Log.i(LoginActivity.TAG, "MMKV deviceId = " + deviceId
                    + ", UserInfo deviceId = " + userInfo.getClientId());
            ARouter.getInstance()
                    .build("/home/main")
                    .withString("token", token)
                    .withString("tab", "personal")
                    .navigation();
        }else {
            Log.e(LoginActivity.TAG, "userInfo == null!!!");
        }
    }

    public void showToast(String error) {
        XChatToast.INSTANCE.showToast(this, error,
                Gravity.TOP, LoginActivity.Y_OFFSET);
    }

    public void showResultUITask(String msg) {
        runOnUiThread(() -> {
            loadingComponent.stop();
            showToast(msg);
        });
    }
}