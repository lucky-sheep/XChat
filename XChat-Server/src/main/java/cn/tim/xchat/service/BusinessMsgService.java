package cn.tim.xchat.service;

import cn.tim.xchat.core.model.DataContentSerializer;
import cn.tim.xchat.entity.FriendRequest;
import io.netty.channel.Channel;

public interface BusinessMsgService {
    /**
     * 获取未处理的好友请求
     * @param userId acceptUserID
     * @return 未处理的好友请求列表
     */
    DataContentSerializer.DataContent getUserNewFriendRequest(String userId);

    /**
     * 处理同意/拒绝好友请求的消息
     * @param dataContent itemId + state
     * @param currentChannel channel
     */
    void handleFriendRequestMsg(DataContentSerializer.DataContent dataContent, Channel currentChannel);

    /**
     * 发送未处理的新好友请求
     * @param friendRequest FriendRequest
     */
    void sendNewFriendRequest(FriendRequest friendRequest);

    /**
     * 获取好友请求的处理结果
     * @param userId sendUserId
     * @param friendRequest FriendRequest
     * @return 回应结果
     */
    DataContentSerializer.DataContent getUserFriendRequestRet(String userId, FriendRequest friendRequest);
}
