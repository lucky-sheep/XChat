package cn.tim.xchat.netty;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
    private final WebSocketClientHandshaker handShaker;
    private ChannelPromise handshakeFuture;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        System.out.println("WebSocketClientHandler channelRead0");
        if(!handShaker.isHandshakeComplete()) {  // 未完成握手
            try {
                handShaker.finishHandshake(ch, (FullHttpResponse) msg);
                System.err.println("websocket Handshake 完成!");
                handshakeFuture.setSuccess();
                //ctx.pipeline().remove(this);
                ctx.fireChannelRead(((FullHttpResponse) msg).retain());
            } catch (WebSocketHandshakeException e) {
                System.err.println("websocket连接失败!");
                handshakeFuture.setFailure(e);
            }
            return;
        }

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException(
                    "Unexpected FullHttpResponse (getStatus=" + response.status() +
                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            System.out.println("接收到TXT消息: " + textFrame.text());
        } else if (frame instanceof PongWebSocketFrame) {
            System.out.println("接收到pong消息");
        } else if (frame instanceof CloseWebSocketFrame) {
            System.out.println("接收到closing消息");
            ch.close();
            return;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 异常处理
        System.err.println("出现异常" + cause);
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }

    public WebSocketClientHandler(WebSocketClientHandshaker handshaker) {
        this.handShaker = handshaker;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handShaker.handshake(ctx.channel());
        System.err.println("channelActive!");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
    }
}
