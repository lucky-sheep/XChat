package cn.tim.xchat.service;

import cn.tim.xchat.core.enums.MsgActionEnum;
import cn.tim.xchat.core.enums.MsgSignEnum;
import cn.tim.xchat.core.enums.MsgTypeEnum;
import cn.tim.xchat.core.model.DataContentSerializer;
import cn.tim.xchat.entity.ChatMsg;
import io.netty.channel.Channel;

import java.util.List;

public interface ChatMsgService {
    /**
     * 用户上线之后发送积压消息
     * @param userId acceptUserId
     * @param currentChannel channel
     */
    void sendUserNewMsgBeforeUserOnline(String userId, Channel currentChannel);

    /**
     * 处理服务器收到的消息（日常聊天消息）
     * @param dataContent
     */
    void handleReceivedMsg(DataContentSerializer.DataContent dataContent);
}
