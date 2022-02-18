package cn.tim.xchat.chat;

import android.os.Bundle;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import cn.tim.xchat.common.XChatBaseActivity;

@Route(path = "/chat/list")
public class ChatActivity extends XChatBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }

    @Override
    protected ViewGroup getContentView() {
        return findViewById(android.R.id.content);
    }
}