package cn.tim.xchat.netty;

import cn.tim.xchat.core.model.DataContentSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

public class TestClient {
    public static void main(String[] args) throws InterruptedException {
        URI uri = URI.create("ws://127.0.0.1:8899/ws");
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        ClientInitializer initializer = new ClientInitializer();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(initializer);

        Channel channel = bootstrap.connect(uri.getHost(), uri.getPort())
                .sync()
                .channel();
        System.err.println("=========");

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                DataContentSerializer.DataContent dataContent =
                        DataContentSerializer.DataContent.newBuilder()
                                .setAction(11111)
                                .build();
                if(channel.isWritable()) {
                    System.out.println("Timer发送消息");
                    channel.writeAndFlush(dataContent);
                }
            }
        }, 0, 1500);
        initializer.handler.handshakeFuture().sync();
    }
}