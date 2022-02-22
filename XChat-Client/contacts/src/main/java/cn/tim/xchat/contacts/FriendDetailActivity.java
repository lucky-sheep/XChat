package cn.tim.xchat.contacts;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;

import org.litepal.LitePal;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import cn.tim.xchat.common.XChatBaseActivity;
import cn.tim.xchat.common.module.FriendInfo;
import cn.tim.xchat.common.module.FriendRequest;
import cn.tim.xchat.common.widget.titlebar.BaseTitleBar;
import cn.tim.xchat.common.widget.titlebar.TitleBarType;

@Route(path = "/contacts/detail")
public class FriendDetailActivity extends XChatBaseActivity {
    private static final String TAG = "FriendDetailActivity";

    @Autowired(required = false)
    FriendRequest friendRequest;

    @Autowired(required = false)
    FriendInfo friendInfo;
    private AvatarImageView avatarIv;
    private TextView noteNameTv;
    private TextView userNameTv;
    private TextView userIdTv;
    private RelativeLayout toMsgBtn;
    private RelativeLayout toVideoBtn;
    private LinearLayout editNotenameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        setContentView(R.layout.activity_friend);

        BaseTitleBar baseTitleBar = findViewById(R.id.contact_friend_title_bar);
        baseTitleBar.autoChangeByType(TitleBarType.CONTACTS_FRIEND_INFO);
        baseTitleBar.backBtn.setOnClickListener(v -> finish());

        avatarIv = findViewById(R.id.contact_friend_header_iv);
        noteNameTv = findViewById(R.id.contact_friend_notesname);
        userNameTv = findViewById(R.id.contact_friend_username);
        userIdTv = findViewById(R.id.contact_friend_userid_tv);
        toMsgBtn = findViewById(R.id.contact_friend_to_msg_btn);
        toVideoBtn = findViewById(R.id.contact_friend_to_call_btn);
        editNotenameLayout = findViewById(R.id.contact_friend_set_notesname_layout);

        if(friendInfo != null) {
            showAlreadyFriend();
        }

        if(friendRequest != null){
            showRequestFriend();
        }
    }

    private void showRequestFriend() {
        toMsgBtn.setVisibility(View.GONE);
        toVideoBtn.setVisibility(View.GONE);
        editNotenameLayout.setVisibility(View.GONE);

        userNameTv.setText(friendRequest.getUsername());
        avatarIv.setTextAndColor(friendRequest.getUsername().substring(0, 1), Color.BLACK);
        userIdTv.setText(String.valueOf(friendRequest.getUserId()));
        noteNameTv.setText(TextUtils.isEmpty(friendRequest.getNickname())?
                friendRequest.getUsername():friendRequest.getNickname());

        Glide.with(this)
                .load(friendRequest.getFaceImage())
                .centerCrop()
                .into(avatarIv);
    }

    @Override
    protected ViewGroup getContentView() {
        return findViewById(android.R.id.content);
    }


    private void showAlreadyFriend(){
        Log.i(TAG, "showAlreadyFriend: friendInfo = " + friendInfo);
        String notesName = friendInfo.getNotes(); // 备注名
        String nickname = friendInfo.getNickname(); // 昵称

        noteNameTv.setText(TextUtils.isEmpty(notesName) ?
                (TextUtils.isEmpty(nickname) ? "未设置备注名" : nickname) : notesName);
        userNameTv.setText(friendInfo.getUsername());
        avatarIv.setTextAndColor(friendInfo.getUsername().substring(0, 1), Color.BLACK);

        userIdTv.setText(String.valueOf(friendInfo.getUserId()));
        TextView setNoteNameBtn = findViewById(R.id.contact_friend_set_notesname_btn);

        Glide.with(this)
                .load(friendInfo.getFaceImage())
                .centerCrop()
                .into(avatarIv);
        setNoteNameBtn.setOnClickListener(v -> {
            // TODO 设置备注名
        });
    }
}