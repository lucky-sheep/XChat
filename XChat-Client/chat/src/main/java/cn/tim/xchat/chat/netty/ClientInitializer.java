package cn.tim.xchat.chat.netty;

import java.net.URI;

import cn.tim.xchat.chat.BuildConfig;
import cn.tim.xchat.core.model.DataContentSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.EmptyHeaders;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class ClientInitializer extends ChannelInitializer<NioSocketChannel> {
    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        DefaultHttpHeaders headers = new DefaultHttpHeaders();

//        pipeline.addLast(new WebSocketClientProtocolHandler(
//                URI.create(BuildConfig.WEBSOCKET_URL),
//                WebSocketVersion.V08,
//                "",
//                true,
//                headers,
//                1024 * 1024
//                ));

        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        pipeline.addLast(new ProtobufDecoder(DataContentSerializer.DataContent.getDefaultInstance()));

        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast(new ProtobufEncoder());

        pipeline.addLast(new ChatMsgHandler());
    }
}
