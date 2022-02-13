package cn.tim.xchat.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import cn.tim.xchat.common.utils.StatusBarUtil;

@Route(path = "/login/main")
public class LoginActivity extends AppCompatActivity {

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
        initFindViewById();
    }

    private void initFindViewById() {

    }
}