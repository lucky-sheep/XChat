//package cn.tim.xchat.netty;
//
//import cn.tim.xchat.core.enums.MsgActionEnum;
//import cn.tim.xchat.core.model.DataContent;
//import com.alibaba.fastjson.JSON;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import io.netty.channel.group.ChannelGroup;
//import io.netty.channel.group.DefaultChannelGroup;
//import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
//import io.netty.util.concurrent.GlobalEventExecutor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
///**
// * 处理消息的handler
// * TextWebSocketFrame： 在netty中，是用于为websocket专门处理文本的对象，frame是消息的载体
// */
//@Slf4j
//@Component
//public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
//
//	// 用于记录和管理所有客户端的Channel
//	public static ChannelGroup users =
//			new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
//
//	@Override
//	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg)
//			throws Exception {
//		Channel currentChannel = ctx.channel();
//
//		// 获取客户端传输过来的消息
//		DataContent dataContent = JSON.parseObject(msg.text(), DataContent.class);
//		if(dataContent != null) {
//			int action = dataContent.getAction();
//
//			// 判断消息类型, 根据不同的类型来处理不同的业务
//			if (action == MsgActionEnum.CONNECT.type) {
//				UserChannelHelper.put(dataContent.getSenderId(), currentChannel);
//				// 写回Success
//				currentChannel.writeAndFlush(new TextWebSocketFrame("ok"));
//			} else if (action == MsgActionEnum.CHAT.type) {
//			} else if (action == MsgActionEnum.SIGNED.type) {
//			} else if (action == MsgActionEnum.KEEPALIVE.type) {
//				log.info("KEEPALIVE MSG = ");
//			}
//		}
//	}
//
//	/**
//	 * 当客户端连接服务端之后（打开连接）
//	 * 获取客户端的channel，并且放到ChannelGroup中去进行管理
//	 */
//	@Override
//	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
//		users.add(ctx.channel());
//		log.info("ChannelGroup:users -> add channel, id = " + ctx.channel().id());
//	}
//
//	@Override
//	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
//		String channelId = ctx.channel().id().asShortText();
//		log.info("remove channel id = " + channelId);
//		// 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
//		users.remove(ctx.channel());
//	}
//
//	@Override
//	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//		cause.printStackTrace();
//		// 发生异常之后关闭channel，随后从ChannelGroup中移除
//		ctx.channel().close();
//		users.remove(ctx.channel());
//	}
//}
