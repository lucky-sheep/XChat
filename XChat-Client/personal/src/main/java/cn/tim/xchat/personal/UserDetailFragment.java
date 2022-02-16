package cn.tim.xchat.personal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;

@Route(path = "/personal/detail")
public class UserDetailFragment extends Fragment {
    // 创建视图
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_personal_detail_fragment, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
    }
}
