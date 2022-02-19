package cn.tim.xchat.netty;


import cn.tim.xchat.core.model.DataContentSerializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.UUID;

public class ServerHandler extends SimpleChannelInboundHandler<DataContentSerializer.DataContent> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DataContentSerializer.DataContent msg) throws Exception {
        System.out.println(msg.getAction());
//        ctx.channel().writeAndFlush("server: " + UUID.randomUUID().toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("出错");
        cause.printStackTrace();
        ctx.close();
    }
}