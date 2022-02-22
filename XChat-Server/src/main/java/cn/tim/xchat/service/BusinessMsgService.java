package cn.tim.xchat.service;

import cn.tim.xchat.core.model.DataContentSerializer;
import cn.tim.xchat.entity.FriendRequest;
import io.netty.channel.Channel;

public interface BusinessMsgService {
    DataContentSerializer.DataContent getUserFriendRequest(String userId);

    void handleFriendRequestMsg(DataContentSerializer.DataContent dataContent, Channel currentChannel);

    void sendNewFriendRequest(FriendRequest friendRequest);
}
