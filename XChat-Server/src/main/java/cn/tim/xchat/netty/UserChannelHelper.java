package cn.tim.xchat.netty;

import cn.tim.xchat.core.model.DataContentSerializer;
import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理用户的Channel
 */
public class UserChannelHelper {
    private static final ConcurrentHashMap<String, Channel> userChannelMap = new ConcurrentHashMap<>();

    /**
     * 用户上线
     * @param userId
     * @param channel
     */
    public static void online(String userId, Channel channel) {
        userChannelMap.put(userId, channel);
    }

    public static Channel getChannelByUserId(String userId) {
        return userChannelMap.get(userId);
    }

    /**
     * 用户离线
     * @param userId
     */
    public static void offOnline(String userId){
        userChannelMap.remove(userId);
    }

    public static boolean isOnline(String userId){
        if(userChannelMap.containsKey(userId)){
            Channel channel = userChannelMap.get(userId);
            return channel.isOpen();
        }
        return false;
    }

    /**
     * 如果用户在线就发送发消息
     * @param userId
     * @param dataContent
     * @return
     */
    public static boolean isOnlineAndSendMsg(String userId, DataContentSerializer.DataContent dataContent){
        if(userChannelMap.containsKey(userId)){
            Channel channel = userChannelMap.get(userId);
            if(channel.isOpen()){
                channel.writeAndFlush(dataContent);
                return true;
            }
        }
        return false;
    }
}
