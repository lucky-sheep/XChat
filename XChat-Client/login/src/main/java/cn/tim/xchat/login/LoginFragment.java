package cn.tim.xchat.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.textfield.TextInputLayout;
import com.tencent.mmkv.MMKV;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cn.tim.xchat.common.constans.StorageKey;
import cn.tim.xchat.common.utils.InputFilterUtil;
import cn.tim.xchat.common.utils.MD5Utils;
import cn.tim.xchat.common.utils.RegexUtil;
import cn.tim.xchat.common.widget.toast.XChatToast;
import cn.tim.xchat.login.adapter.MyEditTextWatcher;
import cn.tim.xchat.login.module.UserInfo;
import cn.tim.xchat.network.OkHttpUtils;
import cn.tim.xchat.network.config.NetworkConfig;
import cn.tim.xchat.network.model.ResponseModule;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Route(path = "/login/login")
public class LoginFragment extends Fragment {
    private EditText usernameEdit;
    private EditText passwordEdit;
    private Button loginBtn;
    private TextView goToRegisterBtn;

    private View view;
    private LoginActivity activity;

    MMKV mmkv = MMKV.defaultMMKV();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_login_main, container, false);
        initView();
        return view;
    }

    private void initView() {
        activity = (LoginActivity) getActivity();
        passwordEdit = view.findViewById(R.id.login_password_et);
        usernameEdit = view.findViewById(R.id.login_username_et);
        loginBtn = view.findViewById(R.id.login_login_btn);
        goToRegisterBtn = view.findViewById(R.id.login_goto_register_btn);

        TextInputLayout usernameTIL = view.findViewById(R.id.login_username_et_til);
        TextInputLayout passwordTIL = view.findViewById(R.id.login_password_et_til);

        usernameEdit.setFilters(new InputFilter[]{InputFilterUtil.englishAndNumberFilter});
        usernameEdit.addTextChangedListener(new MyEditTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                usernameTIL.setError(usernameEdit.getText().length() >
                        usernameTIL.getCounterMaxLength() ? "输入过长":null);
            }
        });

        loginBtn.setOnClickListener(v -> {
            String username = usernameEdit.getText().toString().trim();
            String password = passwordEdit.getText().toString().trim();
            if(TextUtils.isEmpty(username)
                    || TextUtils.isEmpty(username)
                    || TextUtils.isEmpty(password)) {
                XChatToast.INSTANCE.showToast(getContext(), "请正确填写登录信息", Gravity.TOP, 50);
                return;
            }
            loginCall(username, password);
        });

        goToRegisterBtn.setOnClickListener(v -> {
            Fragment regFragment = (Fragment) ARouter.getInstance()
                    .build("/login/register")
                    .navigation();
            XChatToast.INSTANCE.cancelToast();
            getParentFragmentManager().beginTransaction()
                    .remove(this)
                    .add(R.id.login_main_content, regFragment)
                    .commit();
        });
    }

    private void loginCall(String username, String password) {
        OkHttpClient client = OkHttpUtils.getInstance();
        Map<String, Object> map = new HashMap<>();
        map.put("password", MD5Utils.string2MD5(password));
        map.put("username", username);
        map.put("deviceId", mmkv.getString(StorageKey.DEVICE_ID_KEY, UUID.randomUUID().toString()));

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                JSONObject.toJSONString(map));
        Request request = new Request.Builder()
                .post(requestBody)
                .url(NetworkConfig.baseUrl + NetworkConfig.USER_LOGIN_URL)
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        activity.runOnUiThread(() -> {
                            XChatToast.INSTANCE.showToast(getContext(), "请检查网络连接", Gravity.TOP, 50);
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response)
                            throws IOException {
                        ResponseBody responseBody = response.body();
                        assert responseBody != null;
                        ResponseModule responseModule = JSON.parseObject(
                                responseBody.string(), ResponseModule.class);

                        activity.runOnUiThread(()-> XChatToast.INSTANCE.showToast(
                                getContext(), responseModule.getMessage(), Gravity.TOP, 50));
                        if(responseModule.getSuccess()) {
                            activity.successAuthHandle(responseModule);
                        }
                    }
                });
    }

}