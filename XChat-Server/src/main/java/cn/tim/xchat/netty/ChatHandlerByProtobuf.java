package cn.tim.xchat.netty;

import cn.tim.xchat.core.enums.MsgActionEnum;
import cn.tim.xchat.core.model.DataContentSerializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

@Slf4j
public class ChatHandlerByProtobuf extends SimpleChannelInboundHandler<DataContentSerializer.DataContent> {
    // 用于记录和管理所有客户端的Channel
    public static ChannelGroup users =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private AttributeKey<String> userIdAttributeKey = AttributeKey.valueOf("userId");;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DataContentSerializer.DataContent dataContent) throws Exception {
        Channel currentChannel = ctx.channel();
        int action = dataContent.getAction();

        if(action != MsgActionEnum.KEEPALIVE.type) {
            log.info("ChatHandlerByProtobuf receive msg, size = " + dataContent.toByteArray().length);
        }

        // 判断消息类型, 根据不同的类型来处理不同的业务
        if (action == MsgActionEnum.CONNECT.type) {
            String senderId = dataContent.getSenderId();
            if(ObjectUtils.isEmpty(senderId)) {
                log.error(" ============== 严重错误: 初始化指令未包含用户ID ==============");
                currentChannel.close();
            }
            // 获取用户ID，塞到Channel的属性字段里面
            currentChannel.attr(userIdAttributeKey).set(senderId);
            UserChannelHelper.online(senderId, currentChannel);
            log.info("用户:" + senderId + "上线, channelId:" + currentChannel.id().asLongText() + ", 开始发送积压消息------->");
        } else if (action == MsgActionEnum.CHAT.type) {
        } else if (action == MsgActionEnum.SIGNED.type) {
        } else if (action == MsgActionEnum.KEEPALIVE.type) {
            log.debug("KEEPALIVE MSG, size = " + dataContent.toByteArray().length);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    /**
     * 当客户端连接服务端之后（打开连接）
     * 获取客户端的channel，并且放到ChannelGroup中去进行管理
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        users.add(ctx.channel());
        log.info("ChatHandlerByProtobuf ChannelGroup:users -> add channel, id = " + ctx.channel().id());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asShortText();
        log.info("remove channel id = " + channelId);
        // 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
        users.remove(ctx.channel());
        String userId = ctx.channel().attr(userIdAttributeKey).get();
        if(ObjectUtils.isEmpty(userId)) {
            log.error(" ============== (R)严重错误: 该Channel未包含用户ID ==============, channelId = "
                    + ctx.channel().id().asLongText());
        }else {
            UserChannelHelper.offOnline(userId);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 发生异常之后关闭channel，随后从ChannelGroup中移除
        String userId = ctx.channel().attr(userIdAttributeKey).get();
        if(ObjectUtils.isEmpty(userId)) {
            log.error(" ============== (E)严重错误: 该Channel未包含用户ID ==============, channelId = "
                    + ctx.channel().id().asLongText());
        }else {
            UserChannelHelper.offOnline(userId);
        }
        ctx.channel().close();
        users.remove(ctx.channel());
    }
}
