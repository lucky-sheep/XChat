package cn.tim.xchat.netty;

import cn.tim.xchat.core.model.DataContentSerializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<DataContentSerializer.DataContent> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DataContentSerializer.DataContent msg) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        DataContentSerializer.DataContent content = DataContentSerializer.DataContent.newBuilder()
                .setAction(0)
                .setTimestamp((int) System.currentTimeMillis())
                .build();
        ctx.channel().writeAndFlush(content);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}