package cn.tim.xchat.chat.send.impl;

import android.util.Log;

import java.time.Instant;

import cn.tim.xchat.chat.send.CallBackAsync;
import cn.tim.xchat.chat.send.SendMsgAction;
import cn.tim.xchat.common.core.WebSocketHelper;
import cn.tim.xchat.common.msg.MsgActionEnum;
import cn.tim.xchat.common.msg.MsgTypeEnum;
import cn.tim.xchat.common.utils.ext.UserUtil;
import cn.tim.xchat.core.model.DataContentSerializer;

public class SendMsgActionImpl implements SendMsgAction {
    private static final String TAG = "SendMsgAction";
    WebSocketHelper helper = WebSocketHelper.getInstance();

    @Override
    public boolean sendTextMsg(String acceptUserId, String msg, CallBackAsync callBackAsync) {
        // 打包
        DataContentSerializer.DataContent.ChatMessage chatMessage = DataContentSerializer.DataContent.ChatMessage
                .newBuilder()
                .setText(msg)
                .setType(MsgTypeEnum.TEXT.getCode())
                .build();

        DataContentSerializer.DataContent dataContent = DataContentSerializer.DataContent
                .newBuilder()
                .setSenderId(UserUtil.get().getId())
                .setTimestamp((int) (Instant.now().toEpochMilli()/ 1000))
                .setReceiveId(acceptUserId)
                .setAction(MsgActionEnum.CHAT.getCode())
                .setChatMessage(chatMessage)
                .build();

        // TODO -> callback
        Log.i(TAG, "sendTextMsg: dataContent = " + dataContent);
        return helper.sendMsg(dataContent);
    }

    @Override
    public boolean sendVoiceMSg(String acceptUserId) {
        //TODO do nothing
        return false;
    }
}
