package cn.tim.xchat.personal;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.tencent.mmkv.MMKV;

import org.litepal.LitePal;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import cn.tim.xchat.common.constans.StorageKey;
import cn.tim.xchat.common.module.FriendInfo;
import cn.tim.xchat.common.module.FriendRequest;


@Route(path = "/personal/main")
public class PersonalFragment extends Fragment {
    MMKV mmkv = MMKV.defaultMMKV();
    // 创建视图
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_personal_main_fragment, container, false);

        Button clearDB = view.findViewById(R.id.debug_clear_databases);
        clearDB.setOnClickListener(v -> {
            LitePal.deleteAll(FriendInfo.class);
            LitePal.deleteAll(FriendRequest.class);
        });
        initView(view);
        return view;
    }

    private void initView(View view) {
        String username = mmkv.getString(StorageKey.USERNAME_KEY, "XX");
        AvatarImageView headerIv = view.findViewById(R.id.personal_main_header_iv);
        TextView userIdTv = view.findViewById(R.id.personal_main_userid);
        TextView userNameTv = view.findViewById(R.id.personal_main_username);
        ImageView qrCodeBtn = view.findViewById(R.id.personal_main_qrcode_btn);
        ImageView detailBtn = view.findViewById(R.id.personal_main_detail_btn);

        assert username != null;
        userNameTv.setText(username);
        userIdTv.setText(mmkv.getString(StorageKey.USERID_KEY, ""));
        headerIv.setTextAndColor(username.substring(0, 1), Color.BLACK);
        qrCodeBtn.setOnClickListener(v -> {

        });

        detailBtn.setOnClickListener(v -> {
            ARouter.getInstance()
                    .build("/personal/detail")
                    .navigation(getContext());
        });
    }
}