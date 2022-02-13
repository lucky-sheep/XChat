package cn.tim.xchat.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebSocketServer {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;
    private ChannelFuture channelFuture;

    private static class SingleWebSocketServer {
        static final WebSocketServer instance = new WebSocketServer();
    }

    public static WebSocketServer getInstance(){
        return SingleWebSocketServer.instance;
    }

    public WebSocketServer() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new WSServerInitializer());
    }

    public void start() {
        channelFuture = serverBootstrap.bind(8088);
        log.warn("netty websocket server start success...");
    }

    public void stop() {
        if(channelFuture != null) {
            channelFuture.channel().closeFuture();
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
