package cn.tim.xchat.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.textfield.TextInputLayout;
import com.tencent.mmkv.MMKV;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.tim.xchat.common.constans.StorageKey;
import cn.tim.xchat.common.utils.InputFilterUtil;
import cn.tim.xchat.common.utils.MD5Utils;
import cn.tim.xchat.common.utils.RegexUtil;
import cn.tim.xchat.common.widget.toast.XChatToast;
import cn.tim.xchat.login.adapter.MyEditTextWatcher;
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

@Route(path = "/login/register")
public class RegisterFragment extends Fragment {
    private EditText usernameEdit;
    private EditText passwordEdit;
    private EditText emailEdit;

    private View view;

    MMKV mmkv = MMKV.defaultMMKV();
    private LoginActivity bll;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_register_main, container, false);
        initView();
        return view;
    }

    private void initView() {
        bll = (LoginActivity) getActivity();
        passwordEdit = view.findViewById(R.id.login_password_et);
        usernameEdit = view.findViewById(R.id.login_username_et);
        emailEdit = view.findViewById(R.id.login_email_et);
        Button registerBtn = view.findViewById(R.id.login_register_btn);
        TextView goToLoginBtn = view.findViewById(R.id.login_goto_login_btn);


        TextInputLayout usernameTIL = view.findViewById(R.id.login_username_et_til);
        TextInputLayout emailTIL = view.findViewById(R.id.login_email_et_til);
//        TextInputLayout passwordTIL = view.findViewById(R.id.login_password_et_til);

        goToLoginBtn.setOnClickListener(v -> {
            Fragment regFragment = (Fragment) ARouter.getInstance()
                    .build("/login/login")
                    .navigation();
            XChatToast.INSTANCE.cancelToast();
            getParentFragmentManager().beginTransaction()
                    .remove(this)
                    .add(R.id.login_main_content, regFragment)
                    .commit();
        });

        registerBtn.setOnClickListener(v -> {
            String email = emailEdit.getText().toString();
            String username = usernameEdit.getText().toString().trim();
            String password = passwordEdit.getText().toString().trim();
            if(TextUtils.isEmpty(username)
                    || TextUtils.isEmpty(username)
                    || TextUtils.isEmpty(password)
                    || !RegexUtil.isEmail(email)) {
                bll.showToast("请正确填写注册信息");
                return;
            }
            String deviceIdMd5 = mmkv.getString(StorageKey.DEVICE_ID_KEY, "");
            Log.i(LoginActivity.TAG, "initView: deviceId = " + deviceIdMd5);

            String pwdMd5 = MD5Utils.string2MD5(password);
            registerCall(username, deviceIdMd5, pwdMd5, email);
        });

        usernameEdit.setFilters(new InputFilter[]{InputFilterUtil.englishAndNumberFilter});
        usernameEdit.addTextChangedListener(new MyEditTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                String text = usernameEdit.getText().toString();
                usernameTIL.setError(text.length() >
                        usernameTIL.getCounterMaxLength() ? "输入过长":null);
            }
        });

        emailEdit.addTextChangedListener(new MyEditTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                Editable text = emailEdit.getText();
                emailTIL.setError(text.length() >
                        emailTIL.getCounterMaxLength() ? "输入过长":null);
                if(!RegexUtil.isEmail(text.toString())) {
                    emailTIL.setError("邮箱格式不正确");
                }
            }
        });
    }

    private void registerCall(String username, String deviceIdMd5,
                              String pwdMd5, String email) {
        OkHttpClient client = OkHttpUtils.getInstance();
        Map<String, Object> map = new HashMap<>();
        map.put("deviceId", deviceIdMd5);
        map.put("password", pwdMd5);
        map.put("username", username);
        map.put("email", email);

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                JSONObject.toJSONString(map));
        Request request = new Request.Builder()
                .post(requestBody)
                .url(NetworkConfig.baseUrl + NetworkConfig.USER_REGISTER_URL)
                .build();
        bll.loadingComponent.start();
        client.newCall(request)
            .enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    bll.showResultUITask("请检查网络连接");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response)
                        throws IOException {
                    ResponseBody responseBody = response.body();
                    assert responseBody != null;
                    String responseBodyStr = responseBody.string();
                    try {
                        ResponseModule responseModule = JSON.parseObject(
                                responseBodyStr, ResponseModule.class);
                        if(responseModule != null) {
                            bll.showResultUITask(responseModule.getMessage());
                            if (responseModule.getSuccess()) {
                                bll.successAuthHandle(responseModule);
                            }
                        }else {
                            bll.loadingComponent.stop();
                            bll.showToast("服务器正忙，请联系管理员");
                        }
                    } catch (JSONException e){
                        bll.showResultUITask("服务器正忙，请联系管理员");
                    }
                }
            });
    }
}