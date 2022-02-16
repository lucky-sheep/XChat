package cn.tim.xchat.personal;


import android.os.Bundle;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;

import cn.tim.xchat.common.XChatBaseActivity;

@Route(path = "/personal/detail")
public class PersonalDetailActivity extends XChatBaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_personal_detail_activity);
    }

    @Override
    protected ViewGroup getContentView() {
        return findViewById(android.R.id.content);
    }
}