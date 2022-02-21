package cn.tim.xchat.service;

import cn.tim.xchat.core.model.DataContentSerializer;

import java.util.List;

public interface BusinessMsgService {
    DataContentSerializer.DataContent getUserFriendRequest(String userId);
}
