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
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.web.HttpRequestHandler;

import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;

public class WSServerInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		// websocket 基于HTTP协议，需要HTTP编解码器
		pipeline.addLast(new HttpServerCodec());
		// 对写大数据流的支持
//		pipeline.addLast(new ChunkedWriteHandler());
		// 对httpMessage进行聚合，聚合成FullHttpRequest或FullHttpResponse
		pipeline.addLast(new HttpObjectAggregator(1024 * 128));
		// 针对客户端，如果在30S时没有向服务端发送读写心跳(ALL)，则主动断开

//		pipeline.addLast(new IdleStateHandler(8, 10, 12));
		// 自定义的空闲状态检测
//		pipeline.addLast(new HeartBeatHandler());

		/*
		 * websocket 服务器处理的协议，用于指定给客户端连接访问的路由 : /ws
		 * 本handler会帮你处理一些繁重的复杂的事
		 * 会帮你处理握手动作： handshaking（close, ping, pong） ping + pong = 心跳
		 * 对于websocket来讲，都是以frames进行传输的，不同的数据类型对应的frames也不同
		 */
//		pipeline.addLast(new AuthHandler());

//		pipeline.addLast(new AuthHandler());

		pipeline.addLast(new ProtobufVarint32FrameDecoder());
		pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
		//处理FullHttpRequest
		pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));


		// 协议包解码
		pipeline.addLast(new MessageToMessageDecoder<WebSocketFrame>() {
			@Override
			protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
				ByteBuf buf = frame.content();
				out.add(buf);
				buf.retain();
			}
		});

		pipeline.addLast("decoder" +
				"", new ProtobufDecoder(DataContentSerializer.DataContent.getDefaultInstance()));

		pipeline.addLast(new MessageToMessageEncoder<MessageLiteOrBuilder>() {
			@Override
			protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out) throws Exception {
				ByteBuf result = null;
				if (msg instanceof MessageLite) {
					System.out.println("msg instanceof MessageLite");
					result = wrappedBuffer(((MessageLite) msg).toByteArray());
				}
				if (msg instanceof MessageLite.Builder) {
					System.out.println("msg instanceof MessageLite.Builder");
					result = wrappedBuffer(((MessageLite.Builder) msg).build().toByteArray());
				}
				if(result == null) throw new RuntimeException("pipeline encode error");
				WebSocketFrame frame = new BinaryWebSocketFrame(result);
				out.add(frame);
			}
		});
		pipeline.addLast(new ChatHandlerByProtobuf());

		// 如果是读空闲或者写空闲，不处理
//		pipeline.addLast(new IdleStateHandler(4, 8, 12));
		// 自定义的空闲状态检测
//		pipeline.addLast(new HeartBeatHandler());

		// 聊天消息处理的Handler
//		pipeline.addLast(new ChatHandler());

	}
}
