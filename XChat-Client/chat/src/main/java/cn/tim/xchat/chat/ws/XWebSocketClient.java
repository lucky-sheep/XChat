package cn.tim.xchat.chat.ws;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.BinaryFrame;
import org.java_websocket.framing.DataFrame;
import org.java_websocket.framing.FramedataImpl1;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import cn.tim.xchat.chat.msg.MsgActionEnum;
import cn.tim.xchat.chat.ws.event.WSEvent;
import cn.tim.xchat.common.event.TokenEvent;
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
        EventBus.getDefault().post(new WSEvent(WSEvent.Type.CONN_SUCCESS));
    }

    @Override
    public void onMessage(String message) {
        Log.e(TAG, "String onMessage() ->");
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        Log.e(TAG, "ByteBuffer onMessage() -> ");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.e(TAG, "onClose(), remote = " + remote);
        if(timer != null) timer.cancel();
        if(remote) {
            EventBus.getDefault().post(new TokenEvent(TokenEvent.TokenType.TOKEN_OVERDUE));
        }else {
            reconnect();
            retry.set(true);
        }
    }

    @Override
    public void onError(Exception ex) {
        Log.e(TAG, "onError()");
        // 如果存在心跳连接，则取消心跳任务
        if(timer != null) timer.cancel();
        if(retry.get()) { // 已经重试过了
            EventBus.getDefault().post(new WSEvent(WSEvent.Type.DIS_CONN));
        }
    }

    public void sendKeepAliveMsg(){
        timer = new Timer();
        AtomicInteger i = new AtomicInteger(0);
        TimerTask keepAliveTask = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "send keepAlive msg, " + (i.getAndIncrement()) + ", isOpen() = " + isOpen() + ", keepAlive.toByteArray().length = " + keepAlive.toByteArray().length);
//                Log.d(TAG, keepAlive.toByteArray()[0]);
//                Log.d(TAG, keepAlive.toByteArray()[1]);
                //if(isOpen()) send(keepAlive.toByteArray());
                if(isOpen()) {
//                    BinaryFrame binaryFrame = new BinaryFrame();
//                    binaryFrame.setPayload(ByteBuffer.wrap(keepAlive.toByteArray()));
//                    sendFrame(binaryFrame);
                    //send(ByteBuffer.wrap(keepAlive.toByteArray()));

//                    //sendFrame();
                    byte[] bytes = keepAlive.toByteArray();
                    byte[] msg = new byte[ 2 + bytes.length];
                    System.arraycopy(bytes, 0, msg, 2, bytes.length);

                    short length = (short) bytes.length;
                    System.arraycopy(shortToByte(length), 0, msg, 0, 2);
                    msg[0] = 0;
                    msg[1] = (byte) 2;
//                    Log.i(TAG, "" + msg[0]);
//                    Log.i(TAG, "" + msg[1]);
                    //System.ArrayCopy(System.BitConverter.GetBytes(data.Length), 0, msg, 0, 2);
                    send(msg);
                }

//                if(isOpen()) send(("keepAlive->" + System.currentTimeMillis()));
            }
        };
        timer.schedule(keepAliveTask, 2_000, 2);

    }

    static DataContentSerializer.DataContent keepAlive = DataContentSerializer
            .DataContent
            .newBuilder()
            .setAction(MsgActionEnum.KEEPALIVE.type).build();

    public static byte[] shortToByte(short number) {
        int temp = number;
        byte[] b = new byte[2];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Integer(temp & 0xff).byteValue();
            temp = temp >> 8; // 向右移8位
        }
        return b;
    }
}