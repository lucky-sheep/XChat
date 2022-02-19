package cn.tim.xchat.chat.netty;

import java.net.URI;
import java.net.URISyntaxException;

import cn.tim.xchat.core.model.DataContentSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyClientHelper {
    static EventLoopGroup group = new NioEventLoopGroup();

    public static void launchNettyClient(){
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
//                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true);
        ChatMsgHandler msgHandler = new ChatMsgHandler();

        bootstrap.handler(new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                //HTTP编解码器
                pipeline.addLast(new HttpClientCodec());
                //HTTP消息聚合,使用FullHttpResponse和FullHttpRequest到ChannelPipeline中的下一个ChannelHandler，这就消除了断裂消息，保证了消息的完整。
                pipeline.addLast(new HttpObjectAggregator(65536));

                pipeline.addLast(new ProtobufVarint32FrameDecoder());
                pipeline.addLast(new ProtobufDecoder(DataContentSerializer.DataContent.getDefaultInstance()));

                pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
                pipeline.addLast(new ProtobufEncoder());

                pipeline.addLast("client_handler", new ClientHandler());
                pipeline.addLast(msgHandler);
            }
        });


        try {
            URI webSocketURI = new URI("ws://192.168.31.156:8088/ws");
            HttpHeaders httpHeaders = new DefaultHttpHeaders();
            WebSocketClientHandshaker handShaker = WebSocketClientHandshakerFactory.newHandshaker(
                    webSocketURI,
                    WebSocketVersion.V08, null, true, httpHeaders);

            Channel channel = bootstrap.connect(webSocketURI.getHost(),
                    webSocketURI.getPort()).sync().channel();

            ClientHandler handler = (ClientHandler)channel.pipeline().get("client_handler");
            handler.setHandShaker(handShaker);

            handShaker.handshake(channel);

            //阻塞等待是否握手成功
            ChannelFuture future = handler.handshakeFuture().sync();
            System.out.println("----channel:" + future.channel());

            System.out.println("发送 dataContent");
            DataContentSerializer.DataContent dataContent = DataContentSerializer.DataContent.newBuilder()
                    .setAction(0)
                    .build();
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
