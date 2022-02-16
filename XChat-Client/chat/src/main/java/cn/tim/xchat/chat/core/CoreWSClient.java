package cn.tim.xchat.chat.core;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import cn.tim.xchat.chat.BuildConfig;

public class CoreWSClient extends WebSocketClient {
    int retryCount = 0;
    public CoreWSClient(URI serverUri) {
        super(serverUri);
    }

    public CoreWSClient(URI serverUri, Draft protocolDraft) {
        super(serverUri, protocolDraft);
    }

    // WebSocket连接开启
    @Override
    public void onOpen(ServerHandshake handshakeData) {

    }

    // 收到消息
    @Override
    public void onMessage(String message) {

    }

    // 断开连接
    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    // 连接出错
    @Override
    public void onError(Exception ex) {

    }
}
