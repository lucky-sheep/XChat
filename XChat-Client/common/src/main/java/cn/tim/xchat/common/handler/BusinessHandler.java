package cn.tim.xchat.common.handler;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.HashMap;
import java.util.List;

import cn.tim.xchat.common.msg.MsgTypeEnum;
import cn.tim.xchat.common.event.AppEvent;
import cn.tim.xchat.common.module.FriendRequest;
import cn.tim.xchat.core.model.DataContentSerializer;

public class BusinessHandler {
    public static void hand(DataContentSerializer.DataContent dataContent) {
        DataContentSerializer.DataContent.ChatMessage chatMessage = dataContent.getChatMessage();
        int type = chatMessage.getType();
        if(type == MsgTypeEnum.FRIEND_REQUEST_NEW.getCode()){
            handleNewFriendReqMsg(chatMessage);
        }else if(type == MsgTypeEnum.FRIEND_REQUEST_RET.getCode()){
            handleFriendReqResultMsg(chatMessage);
        }
    }

    /**
     * 处理好友请求结果消息
     * @param chatMessage ChatMessage
     */
    private static void handleFriendReqResultMsg(DataContentSerializer.DataContent.ChatMessage chatMessage) {
        int modifyCount = 0;
        FriendRequest friendRequest = JSON.parseObject(chatMessage.getText(), FriendRequest.class);
        FriendRequest findRet = LitePal.where("itemId = ?", friendRequest.getItemId())
                .findFirst(FriendRequest.class);
        if(findRet != null) {
            // 判断变更是否是状态变更
            if(friendRequest.getArgeeState() != findRet.getArgeeState()) {
                modifyCount++;
            }
            findRet.delete();
        }else {
            // 如果是新加的请求也算新消息
            modifyCount++;
        }
        friendRequest.save();

        HashMap<String, Object> data = new HashMap<>();
        if(modifyCount > 0) {
            data.put(AppEvent.Type.NEW_FRIENDS_REQUEST.name(), modifyCount);
            EventBus.getDefault().post(new AppEvent(AppEvent.Type.NEW_FRIENDS_REQUEST, data));
        }
    }


    /**
     * 处理新好友请求消息
     * @param chatMessage ChatMessage
     */
    private static void handleNewFriendReqMsg(DataContentSerializer.DataContent.ChatMessage chatMessage) {
        int modifyCount = 0;
        List<FriendRequest> reqFriends = JSON.parseArray(chatMessage.getText(), FriendRequest.class);
        if(reqFriends != null) {
            for(FriendRequest friendRequest: reqFriends) {
                FriendRequest findRet = LitePal.where("itemId = ?", friendRequest.getItemId())
                        .findFirst(FriendRequest.class);
                if(findRet != null) {
                    // 判断变更是否是状态变更
                    if(friendRequest.getArgeeState() != findRet.getArgeeState()) {
                        modifyCount++;
                    }
                    findRet.delete();
                }else {
                    // 如果是新加的请求也算新消息
                    modifyCount++;
                }
                friendRequest.save();
            }

            HashMap<String, Object> data = new HashMap<>();
            if(modifyCount > 0) {
                data.put(AppEvent.Type.NEW_FRIENDS_REQUEST.name(), modifyCount);
                EventBus.getDefault().post(new AppEvent(AppEvent.Type.NEW_FRIENDS_REQUEST, data));
            }
        }
    }
}
