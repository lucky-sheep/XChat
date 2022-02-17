package cn.tim.xchat.netty;

import cn.tim.xchat.constant.RedisConstant;
import cn.tim.xchat.utils.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * 事实证明：最简单的才是最强的！
 */
@Slf4j
@Service
public class AuthHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        if(msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String token = request.headers().get("token");
            if(token == null) {
                log.warn("token is null!!");
                ctx.channel().close();
            }else {
                StringRedisTemplate redisTemplate = SpringUtil.getBean(StringRedisTemplate.class);
                String userId = redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX, token));
                if(ObjectUtils.isEmpty(userId)) {
                    log.warn("Redis无对应token, 主动断开");
                    ctx.channel().close();
                } else {
                    // 连接建立，后续的内容无需校验
                    ctx.pipeline().remove(this);
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.channel().close();
    }
}
