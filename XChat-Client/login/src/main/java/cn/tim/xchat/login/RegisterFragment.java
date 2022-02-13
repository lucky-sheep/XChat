package cn.tim.xchat.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import java.util.regex.Pattern;

import cn.tim.xchat.common.utils.RegexUtil;
import cn.tim.xchat.common.widget.toast.XChatToast;
import cn.tim.xchat.login.adapter.MyEditTextWatcher;
import cn.tim.xchat.network.OkHttpUtils;
import okhttp3.OkHttpClient;

@Route(path = "/login/register")
public class RegisterFragment extends Fragment {
    private EditText usernameEdit;
    private EditText passwordEdit;
    private EditText emailEdit;
    private Button registerBtn;
    private TextView goToLoginBtn;

    private View view;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_register_main, container, false);
        initView();
        return view;
    }

    private void initView() {
        passwordEdit = view.findViewById(R.id.login_password_et);
        usernameEdit = view.findViewById(R.id.login_username_et);
        emailEdit = view.findViewById(R.id.login_email_et);
        registerBtn = view.findViewById(R.id.login_register_btn);
        goToLoginBtn = view.findViewById(R.id.login_goto_login_btn);


        TextInputLayout usernameTIL = view.findViewById(R.id.login_username_et_til);
        TextInputLayout emailTIL = view.findViewById(R.id.login_email_et_til);
        TextInputLayout passwordTIL = view.findViewById(R.id.login_password_et_til);

        goToLoginBtn.setOnClickListener(v -> {
            Fragment regFragment = (Fragment) ARouter.getInstance()
                    .build("/login/login")
                    .navigation();
            getParentFragmentManager().beginTransaction()
                    .remove(this)
                    .add(R.id.login_main_content, regFragment)
                    .commit();
        });

        registerBtn.setOnClickListener(v -> {
            String email = emailEdit.getText().toString();
            String username = usernameEdit.getText().toString();
            String password = passwordEdit.getText().toString();
            if(TextUtils.isEmpty(username)
                    || TextUtils.isEmpty(username)
                    || TextUtils.isEmpty(password)
                    || !RegexUtil.isEmail(email)) {
                XChatToast.INSTANCE.showToast(getContext(), "请正确填写注册信息", Gravity.TOP, 50);
                return;
            }
            OkHttpClient instance = OkHttpUtils.getInstance();
        });


        usernameEdit.addTextChangedListener(new MyEditTextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                usernameTIL.setError(usernameEdit.getText().length() >
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


}