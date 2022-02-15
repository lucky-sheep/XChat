package cn.tim.xchat.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;

import cn.tim.xchat.common.utils.KeyUtil;
import cn.tim.xchat.core.model.DataContentSerializer;

@Route(path = "/chat/list")
public class ChatActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String testId = KeyUtil.genUniqueKey();
        String url = "https://zouchanglin.cn/" + KeyUtil.genUniqueKey();
        long start = System.currentTimeMillis();
        DataContentSerializer.DataContent.Builder builder = DataContentSerializer.DataContent.newBuilder()
                .setSenderId(testId)
                .setReceiveId(testId)
                .setAction(1)
                .setTimestamp((int) (System.currentTimeMillis() / 1000));
        DataContentSerializer.DataContent dataContent = builder.build();
        byte[] protoBytes = dataContent.toByteArray();


    }
}