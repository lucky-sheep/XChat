package cn.tim.xchat.chat.handler;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import cn.tim.xchat.chat.msg.MsgTypeEnum;
import cn.tim.xchat.common.event.AppEvent;
import cn.tim.xchat.common.module.FriendInfo;
import cn.tim.xchat.common.module.FriendRequest;
import cn.tim.xchat.core.model.DataContentSerializer;

public class BusinessHandler {
    public static void hand(DataContentSerializer.DataContent dataContent) {
        DataContentSerializer.DataContent.ChatMessage chatMessage = dataContent.getChatMessage();
        int type = chatMessage.getType();
        if(type == MsgTypeEnum.FRIEND_REQUEST.ordinal()){
            List<FriendRequest> reqFriends = JSON.parseArray(chatMessage.getText(), FriendRequest.class);
            if(reqFriends != null) {
                for(FriendRequest friendRequest: reqFriends) {
                    FriendRequest findRet = LitePal.where("itemId = ?", friendRequest.getItemId())
                            .findFirst(FriendRequest.class);
                    if(findRet != null) findRet.delete();
                    friendRequest.save();
                }

                HashMap<String, Object> data = new HashMap<>();
                data.put(AppEvent.Type.NEW_FRIENDS_REQUEST.name(), reqFriends.size());
                EventBus.getDefault().post(new AppEvent(AppEvent.Type.NEW_FRIENDS_REQUEST, data));
            }
        }
    }
}
