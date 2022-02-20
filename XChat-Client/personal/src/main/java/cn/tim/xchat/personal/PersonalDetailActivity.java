package cn.tim.xchat.personal;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.button.MaterialButton;

import org.greenrobot.eventbus.EventBus;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import cn.tim.xchat.common.XChatBaseActivity;
import cn.tim.xchat.common.event.WSEvent;
import cn.tim.xchat.common.module.UserInfo;
import cn.tim.xchat.common.utils.ext.UserUtil;
import cn.tim.xchat.common.widget.titlebar.BaseTitleBar;
import cn.tim.xchat.common.widget.titlebar.TitleBarType;

@Route(path = "/personal/detail")
public class PersonalDetailActivity extends XChatBaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_personal_detail_activity);

        AvatarImageView avatarIv = findViewById(R.id.personal_detail_header_iv);
        TextView unameTv = findViewById(R.id.personal_detail_username_tv);
        TextView emailTv = findViewById(R.id.personal_detail_email_tv);
        TextView clientTv = findViewById(R.id.personal_detail_client_tv);
        TextView userIdTv = findViewById(R.id.personal_detail_userid_tv);
        MaterialButton logoutBtn = findViewById(R.id.personal_detail_user_logout);

        BaseTitleBar titleBar = findViewById(R.id.personal_detail_titlebar);
        titleBar.autoChangeByType(TitleBarType.PERSONAL_DETAIL_PAGER);
        titleBar.getBackBtn().setOnClickListener(v -> finish());
        UserInfo userInfo = UserUtil.get();
        avatarIv.setTextAndColor(userInfo.getUsername().substring(0, 3), Color.BLACK);
        unameTv.setText(userInfo.getUsername());
        emailTv.setText(userInfo.getEmail());
        clientTv.setText(userInfo.getClientId());
        userIdTv.setText(userInfo.getId());

        logoutBtn.setOnClickListener(v -> {
            UserUtil.clear();
            EventBus.getDefault().post(new WSEvent(WSEvent.Type.DISCONNECTED_BY_USER));
            // 清除Activity栈
            ARouter.getInstance()
                    .build("/login/main")
                    .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .navigation();
            finish();
        });
    }

    @Override
    protected ViewGroup getContentView() {
        return findViewById(android.R.id.content);
    }
}