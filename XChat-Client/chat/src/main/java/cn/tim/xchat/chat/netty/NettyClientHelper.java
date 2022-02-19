package cn.tim.xchat.chat.netty;

import android.text.TextUtils;

import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;
import cn.tim.xchat.common.constans.StorageKey;
import cn.tim.xchat.common.event.TokenEvent;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyClientHelper {
//    static MMKV mmkv = MMKV.defaultMMKV();

    static EventLoopGroup group = new NioEventLoopGroup();

    public static void launchNettyClient(){
//        String token = mmkv.getString(StorageKey.TOKEN_KEY, null);
//
//        if(TextUtils.isEmpty(token)) {
//            EventBus.getDefault().post(new TokenEvent(TokenEvent.TokenType.TOKEN_OVERDUE));
//        }else {
//
//        }

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new ClientInitializer());
        try {
            Channel channel = bootstrap.connect("192.168.31.193", 8088)
                    .sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
