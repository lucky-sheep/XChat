package cn.tim.xchat.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.textfield.TextInputLayout;

import cn.tim.xchat.common.utils.InputFilterUtil;
import cn.tim.xchat.common.utils.RegexUtil;
import cn.tim.xchat.common.widget.toast.XChatToast;
import cn.tim.xchat.login.adapter.MyEditTextWatcher;
import cn.tim.xchat.network.OkHttpUtils;
import okhttp3.OkHttpClient;

@Route(path = "/login/login")
public class LoginFragment extends Fragment {
    private EditText usernameEdit;
    private EditText passwordEdit;
    private Button loginBtn;
    private TextView goToRegisterBtn;

    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_login_main, container, false);
        initView();
        return view;
    }

    private void initView() {
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
            String username = usernameEdit.getText().toString();
            String password = passwordEdit.getText().toString();
            if(TextUtils.isEmpty(username)
                    || TextUtils.isEmpty(username)
                    || TextUtils.isEmpty(password)) {
                XChatToast.INSTANCE.showToast(getContext(), "请正确填写登录信息", Gravity.TOP, 50);
                return;
            }
            OkHttpClient instance = OkHttpUtils.getInstance();
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

    @Override
    public void onStop() {
        super.onStop();
    }
}