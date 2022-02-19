package cn.tim.xchat.chat.netty;


import cn.tim.xchat.core.model.DataContentSerializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatMsgHandler extends SimpleChannelInboundHandler<DataContentSerializer.DataContent> {
    private static final String TAG = "Netty";
    private Channel channel;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DataContentSerializer.DataContent msg) throws Exception {
        //Log.i(TAG, "channelRead0: msg.getAction() = " + msg.getAction());
        ctx.fireChannelRead(msg);
        System.out.println(msg != null);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //Log.i(TAG, "channelActive: channel活跃, 发送Action");
        channel = ctx.channel();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
        ctx.channel().close();
    }
}
