package cn.tim.xchat.netty;

import cn.tim.xchat.constant.RedisConstant;
import cn.tim.xchat.utils.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Slf4j
@Component
public class AuthHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    public static AttributeKey<String> TOKEN = AttributeKey.valueOf("token");
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
        String token = msg.headers().get("token");
        log.info("token = " + token);
        StringRedisTemplate redisTemplate = SpringUtil.getBean(StringRedisTemplate.class);
        String userId = redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX, token));
        if (!ObjectUtils.isEmpty(userId)) {
            ctx.pipeline().remove(this);
            // 对事件进行传播，知道完成WebSocket连接。
            ctx.channel().attr(TOKEN).set(token);
            ctx.fireChannelRead(msg.retain());
        }else {
            ctx.channel().writeAndFlush(new TextWebSocketFrame("NoAuth"));
            ctx.close();
        }
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            // 移除性能更加
            ctx.pipeline().remove(AuthHandler.class);
        }
    }
}
