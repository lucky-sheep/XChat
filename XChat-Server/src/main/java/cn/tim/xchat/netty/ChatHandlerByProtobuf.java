package cn.tim.xchat.netty;

import cn.tim.xchat.core.model.DataContentSerializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatHandlerByProtobuf extends SimpleChannelInboundHandler<DataContentSerializer.DataContent> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DataContentSerializer.DataContent data) throws Exception {
        String senderId = data.getSenderId();
        log.info("ChatHandlerByProtobuf senderId = {}",senderId);
    }
}
