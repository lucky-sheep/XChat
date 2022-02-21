package cn.tim.xchat.netty;

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
}
