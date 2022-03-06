package cn.tim.xchat.service;

import cn.tim.xchat.core.model.DataContentSerializer;
import io.netty.channel.Channel;

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
     * @param currentChannel
     */
    void handleReceivedMsg(DataContentSerializer.DataContent dataContent, Channel currentChannel);
}
