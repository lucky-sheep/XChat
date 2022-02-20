package cn.tim.xchat.chat.ws;

import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.BinaryFrame;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import cn.tim.xchat.chat.msg.MsgActionEnum;

import cn.tim.xchat.common.constans.StorageKey;
import cn.tim.xchat.common.event.TokenEvent;
import cn.tim.xchat.common.event.WSEvent;
import cn.tim.xchat.core.model.DataContentSerializer;

public class XWebSocketClient extends WebSocketClient {
    private static final String TAG = "XWebSocketClient";
    private final AtomicBoolean retry = new AtomicBoolean(false);
    private Timer timer;

    public XWebSocketClient(URI serverUri, String token) {
        super(serverUri);
        addHeader("token", token);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        Log.e(TAG, "onOpen()");
        sendKeepAliveMsg();
        EventBus.getDefault().postSticky(new WSEvent(WSEvent.Type.CONNECTED));
//        MMKV.defaultMMKV().putBoolean(StorageKey.WEB_SOCKET_STATUS, true);
    }

    @Override
    public void onMessage(String message) {
        Log.e(TAG, "String onMessage() ->");
    }

    @Override
    public void onMessage(ByteBuffer byteBuffer) {
        byteBuffer.flip();
        try {
            DataContentSerializer.DataContent dataContent =
                    DataContentSerializer.DataContent.parseFrom(byteBuffer.array());
            Log.d(TAG, "onMessage: dataContent = " + dataContent);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        byteBuffer.clear();
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.e(TAG, "onClose(), remote = " + remote);
        if(timer != null) timer.cancel();
        if(remote) {
            EventBus.getDefault().post(new TokenEvent(TokenEvent.TokenType.TOKEN_OVERDUE));
        }else {
            retry.set(true);
            reconnect();
        }
        EventBus.getDefault().postSticky(new WSEvent(WSEvent.Type.DISCONNECTED));
    }

    @Override
    public void onError(Exception ex) {
        EventBus.getDefault().postSticky(new WSEvent(WSEvent.Type.DISCONNECTED));
        //Log.e(TAG, "onError()");
        // 如果存在心跳连接，则取消心跳任务
        if(timer != null) timer.cancel();
        if(retry.get()) {
            Log.e(TAG, "onError: 已经尝试重连仍然失败");
        }
    }

    public void sendKeepAliveMsg(){
        timer = new Timer();
        AtomicInteger i = new AtomicInteger(0);
        TimerTask keepAliveTask = new TimerTask() {
            @Override
            public void run() {
                //Log.d(TAG, "send keepAlive msg, " + (i.getAndIncrement()) + ", isOpen() = " + isOpen() + ", keepAlive.toByteArray().length = " + keepAlive.toByteArray().length);
                if(isOpen()) {
                    BinaryFrame binaryFrame = new BinaryFrame();
                    binaryFrame.setPayload(ByteBuffer.wrap(keepAlive.toByteArray()));
                    sendFrame(binaryFrame);
                }
            }
        };
        timer.schedule(keepAliveTask, 2_000, 10_000);
    }

    static DataContentSerializer.DataContent keepAlive =
            DataContentSerializer
                    .DataContent
                    .newBuilder()
                    .setAction(MsgActionEnum.KEEPALIVE.type).build();
}