package cn.tim.xchat.chat.netty;

import com.google.protobuf.Message;

import cn.tim.xchat.core.model.DataContentSerializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.util.CharsetUtil;

public class ClientHandler extends SimpleChannelInboundHandler<Object> {
    WebSocketClientHandshaker handShaker;
    ChannelPromise handshakeFuture;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        FullHttpResponse response;
        if(!handShaker.isHandshakeComplete()) {
            try {
                response = (FullHttpResponse)msg;
                //握手协议返回，设置结束握手
                this.handShaker.finishHandshake(ch, response);
                //设置成功
                this.handshakeFuture.setSuccess();
                System.out.println("WebSocket Client connected! response headers[sec-websocket" +
                        "-extensions]:{}"+response.headers());

            } catch (WebSocketHandshakeException var7) {
                FullHttpResponse res = (FullHttpResponse)msg;
                String errorMsg = String.format("WebSocket Client failed to connect, status:%s, reason:%s",
                        res.status(), res.content().toString(CharsetUtil.UTF_8));
                this.handshakeFuture.setFailure(new Exception(errorMsg));
            }
        }else {
            //System.out.println("ch.pipeline().remove(this);");
            System.out.println("channelActive: channel活跃, 发送Action");
            DataContentSerializer.DataContent dataContent = DataContentSerializer.DataContent.newBuilder()
                    .setAction(0)
                    .build();
            ch.writeAndFlush(dataContent);
        }
    }


    public void handlerAdded(ChannelHandlerContext ctx) {
        this.handshakeFuture = ctx.newPromise();
    }


    public WebSocketClientHandshaker getHandShaker() {
        return handShaker;
    }

    public void setHandShaker(WebSocketClientHandshaker handShaker) {
        this.handShaker = handShaker;
    }

    public ChannelPromise getHandshakeFuture() {
        return handshakeFuture;
    }

    public void setHandshakeFuture(ChannelPromise handshakeFuture) {
        this.handshakeFuture = handshakeFuture;
    }

    public ChannelFuture handshakeFuture() {
        return this.handshakeFuture;
    }
}
