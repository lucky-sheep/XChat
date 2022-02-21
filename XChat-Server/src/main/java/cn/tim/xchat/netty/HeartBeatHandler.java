package cn.tim.xchat.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于检测channel的心跳handler
 * 用于捕获ChannelInboundHandlerAdapter的事件
 */
@Slf4j
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		
		// 判断evt是否是IdleStateEvent（用于触发用户事件，包含读空闲/写空闲/读写空闲 ）
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent)evt;
			if (event.state() == IdleState.READER_IDLE) {
				log.info("进入读空闲...");
			} else if (event.state() == IdleState.WRITER_IDLE) {
				log.info("进入写空闲...");
			} else if (event.state() == IdleState.ALL_IDLE) {
				Channel channel = ctx.channel();
				log.info("关闭无用的Channel, Id = " + channel.id());
				channel.close();
			}
		}
	}
}