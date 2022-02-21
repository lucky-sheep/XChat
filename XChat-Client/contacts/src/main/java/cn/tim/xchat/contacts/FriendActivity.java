package cn.tim.xchat.contacts;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;

import org.litepal.LitePal;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import cn.tim.xchat.common.XChatBaseActivity;
import cn.tim.xchat.common.module.FriendInfo;
import cn.tim.xchat.common.widget.titlebar.BaseTitleBar;
import cn.tim.xchat.common.widget.titlebar.TitleBarType;

@Route(path = "/contacts/friend")
public class FriendActivity extends XChatBaseActivity {
    private static final String TAG = "FriendActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        BaseTitleBar baseTitleBar = findViewById(R.id.contact_friend_title_bar);
        baseTitleBar.autoChangeByType(TitleBarType.CONTACTS_FRIEND_INFO);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        Log.i(TAG, "onCreate: friendInfo = " + username);
        FriendInfo friendInfo = LitePal.where("username = ?", username)
                .findFirst(FriendInfo.class);

        AvatarImageView avatarImageView = findViewById(R.id.contact_friend_header_iv);
        TextView noteNameTv = findViewById(R.id.contact_friend_notesname);
        TextView userNameTv = findViewById(R.id.contact_friend_username);
        TextView userIdTv = findViewById(R.id.contact_friend_userid_tv);

        String notesName = friendInfo.getNotes(); // 备注名
        String nickname = friendInfo.getNickname(); // 昵称

        noteNameTv.setText(TextUtils.isEmpty(notesName)?
                (TextUtils.isEmpty(nickname)? "未设置备注名" : nickname): notesName);
        userNameTv.setText(username);
        avatarImageView.setTextAndColor(username.substring(0, 1), Color.BLACK);

        userIdTv.setText(String.valueOf(friendInfo.getUserId()));
        TextView setNoteNameBtn = findViewById(R.id.contact_friend_set_notesname_btn);


        Glide.with(this)
                .load(friendInfo.getFaceImage())
                .centerCrop()
                .into(avatarImageView);
        setNoteNameBtn.setOnClickListener(v -> {
            // TODO 设置备注名
        });
    }

    @Override
    protected ViewGroup getContentView() {
        return findViewById(android.R.id.content);
    }
}