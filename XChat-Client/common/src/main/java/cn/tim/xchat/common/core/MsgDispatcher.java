package cn.tim.xchat.common.core;

import android.util.Log;

import cn.tim.xchat.common.handler.BusinessHandler;
import cn.tim.xchat.common.msg.MsgActionEnum;
import cn.tim.xchat.core.model.DataContentSerializer;

/**
 * 消息调度器
 */
public class MsgDispatcher {
    private static final String TAG = "MsgDispatcher";
    public static void handProtoMessage(DataContentSerializer.DataContent dataContent) {
        if(dataContent == null) {
            Log.e(TAG, "handProtoMessage: 严重错误-> dataContent is null");
            return;
        }

        int msgAction = dataContent.getAction();
        if(msgAction == MsgActionEnum.BUSINESS.type) {
            BusinessHandler.hand(dataContent);
        }
    }
}
