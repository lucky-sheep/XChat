package cn.tim.xchat.netty;

import cn.tim.xchat.core.model.DataContentSerializer;
import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.URI;
import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    URI uri = URI.create("ws://127.0.0.1:8899/ws");
    WebSocketClientHandler handler =
            new WebSocketClientHandler(
                    WebSocketClientHandshakerFactory.newHandshaker(
                            uri,
                            WebSocketVersion.V08,
                            null,
                            false,
                            new DefaultHttpHeaders()));

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addFirst(new LoggingHandler(LogLevel.INFO));
        pipeline.addLast(new HttpClientCodec());
        pipeline.addLast(new HttpObjectAggregator(1024 * 128));
        pipeline.addLast(WebSocketClientCompressionHandler.INSTANCE);
        pipeline.addLast(handler);


        // 协议包解码
        pipeline.addLast(new MessageToMessageDecoder<WebSocketFrame>() {
            @Override
            protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
                ByteBuf buf = frame.content();
                out.add(buf);
                buf.retain();
            }
        });

        pipeline.addLast(new MessageToMessageEncoder<MessageLiteOrBuilder>() {
            @Override
            protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) throws Exception {
                ByteBuf result = null;
                if (msg instanceof MessageLite) {
                    result = wrappedBuffer(((MessageLite) msg).toByteArray());
                }
                if (msg instanceof MessageLite.Builder) {
                    result = wrappedBuffer(((MessageLite.Builder) msg).build().toByteArray());
                }
                if(result == null) throw new RuntimeException("pipeline encode error");
                WebSocketFrame frame = new BinaryWebSocketFrame(result);
                out.add(frame);
            }
        });
        pipeline.addLast(new ProtobufVarint32FrameDecoder());

        pipeline.addLast(new ProtobufDecoder(DataContentSerializer.DataContent.getDefaultInstance()));
        pipeline.addLast(new ProtobufEncoder());
        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());

        pipeline.addLast(new ClientHandler());
    }
}
