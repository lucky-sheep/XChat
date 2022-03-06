package cn.tim.xchat.common.handler;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.HashMap;
import java.util.List;

import cn.tim.xchat.common.enums.business.MsgSignEnum;
import cn.tim.xchat.common.event.ChatEvent;
import cn.tim.xchat.common.module.chat.ChatMsg;
import cn.tim.xchat.common.msg.MsgActionEnum;
import cn.tim.xchat.common.msg.MsgTypeEnum;
import cn.tim.xchat.common.event.AppEvent;
import cn.tim.xchat.common.module.FriendRequest;
import cn.tim.xchat.common.utils.KeyUtil;
import cn.tim.xchat.common.utils.ext.UserUtil;
import cn.tim.xchat.core.model.DataContentSerializer;

/**
 * 1V1
 */
public class ChatMsgHandler {
    public static void handCommonMsg(DataContentSerializer.DataContent dataContent) {
        if(dataContent.getAction() != MsgActionEnum.CHAT.type){
            return;
        }

        DataContentSerializer.DataContent.ChatMessage chatMessage = dataContent.getChatMessage();
        int type = chatMessage.getType();
        if(type == MsgTypeEnum.TEXT.getCode()){
            handleTextMsg(dataContent);
        }
    }

    /**
     * 处理recv
     * @param dataContent
     */
    private static void handleTextMsg(DataContentSerializer.DataContent dataContent) {
        ChatMsg chatMsg = new ChatMsg();
        DataContentSerializer.DataContent.ChatMessage chatMessage = dataContent.getChatMessage();
        String msgItemId = KeyUtil.genUniqueKey();
        chatMsg.setItemId(msgItemId);
        chatMsg.setAcceptUserId(UserUtil.get().getId());
        chatMsg.setSendUserId(dataContent.getSenderId());
        chatMsg.setCreateTime(dataContent.getTimestamp() * 1000L);
        chatMsg.setType(chatMessage.getType());
        chatMsg.setSignFlag(MsgSignEnum.UN_READ.getCode());
        chatMsg.setMsg(chatMessage.getText()); // text type

        chatMsg.saveAsync();

        HashMap<String, Object> data = new HashMap<>();
        data.put(ChatEvent.SENDER_ID, dataContent.getSenderId());
        data.put(ChatEvent.MSG_ITEM_ID, msgItemId);

        // send msg
        EventBus.getDefault().post(new ChatEvent(ChatEvent.Type.RECEIVE_MSG, data));
    }
}
