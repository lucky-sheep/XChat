package cn.tim.xchat.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mmkv.MMKV;

import cn.tim.xchat.common.constans.StorageKey;
import cn.tim.xchat.common.utils.StatusBarUtil;
import cn.tim.xchat.login.module.UserInfo;
import cn.tim.xchat.network.model.ResponseModule;

@Route(path = "/login/main")
public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";

    MMKV mmkv = MMKV.defaultMMKV();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        // 设置沉浸式状态栏
        StatusBarUtil.setStatusBarFullTransparent(this);
        StatusBarUtil.setDarkStatusIcon(getWindow(),true);

        Fragment loginFragment = (Fragment) ARouter.getInstance().build("/login/register").navigation();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.login_main_content, loginFragment)
                .commit();
    }

    public void successAuthHandle(ResponseModule responseModule) {
        JSONObject data = responseModule.getData();

        String token = data.getString("token");
        mmkv.putString(StorageKey.TOKEN_KEY, token);

        UserInfo userInfo = data.getObject("userInfo", UserInfo.class);
        if(userInfo != null) {
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
                    .navigation();
        }else {
            Log.e(LoginActivity.TAG, "userInfo == null!!!");
        }
    }
}