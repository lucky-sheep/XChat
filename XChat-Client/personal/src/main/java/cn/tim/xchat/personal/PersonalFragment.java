package cn.tim.xchat.personal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.imageview.ShapeableImageView;
import com.tencent.mmkv.MMKV;

import cn.tim.xchat.common.constans.StorageKey;


@Route(path = "/personal/main")
public class PersonalFragment extends Fragment {
    MMKV mmkv = MMKV.defaultMMKV();
    // 创建视图
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_personal_main_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        ShapeableImageView headerIv = view.findViewById(R.id.personal_main_header_iv);
        TextView userIdTv = view.findViewById(R.id.personal_main_userid);
        TextView userNameTv = view.findViewById(R.id.personal_main_username);
        ImageView qrCodeBtn = view.findViewById(R.id.personal_main_qrcode_btn);
        ImageView detailBtn = view.findViewById(R.id.personal_main_detail_btn);

        userNameTv.setText(mmkv.getString(StorageKey.USERNAME_KEY, ""));
        userIdTv.setText(mmkv.getString(StorageKey.USERID_KEY, ""));
        qrCodeBtn.setOnClickListener(v -> {

        });

        detailBtn.setOnClickListener(v -> {

        });
    }
}