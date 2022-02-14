package cn.tim.xchat.netty;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理用户的Channel
 */
public class UserChannelHelper {
    private static ConcurrentHashMap<String, Channel> userChannelMap = new ConcurrentHashMap<>();

    public static void put(String userId, Channel channel) {
        userChannelMap.put(userId, channel);
    }


    public static Channel get(String userId) {
        return userChannelMap.get(userId);
    }

    public static void remove(String userId){
        userChannelMap.remove(userId);
    }
}
