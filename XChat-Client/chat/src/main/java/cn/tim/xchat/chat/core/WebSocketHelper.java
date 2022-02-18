package cn.tim.xchat.chat.core;

import android.text.TextUtils;
import android.util.Log;

import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import cn.tim.xchat.chat.BuildConfig;
import cn.tim.xchat.chat.msg.MsgActionEnum;
import cn.tim.xchat.chat.ws.WebSocketManager;
import cn.tim.xchat.common.constans.StorageKey;
import cn.tim.xchat.common.event.TokenEvent;
import cn.tim.xchat.core.model.DataContentSerializer;

public class WebSocketHelper {
    private static final String TAG = Constants.TAG;
    private WebSocketManager manager;
    private TimerTask keepAliveTask;
    private static final MMKV mmkv = MMKV.defaultMMKV();

    public WebSocketHelper() {
        EventBus.getDefault().register(this);
    }

    public void launchWebSocket(){
        new Thread(this::launchHandle).start();
    }

    private void launchHandle(){
        String token = mmkv.getString(StorageKey.TOKEN_KEY, null);
        if(TextUtils.isEmpty(token)) {
            EventBus.getDefault().post(new TokenEvent(TokenEvent.TokenType.TOKEN_OVERDUE));
        }else {
            manager = new WebSocketManager(token, 2000);
            manager.setWebSocketListener(new WebSocketManager.WebSocketListener() {
                @Override
                public void onConnected(Map<String, List<String>> headers) {
                    Log.e(TAG, "onConnected: ");
                    sendKeepAliveMsg();
                }

                @Override
                public void onTextMessage(String text) {
                    Log.e(TAG, "onTextMessage: text = " + text);
                }

                @Override
                public void onByteMessage(byte[] retByte) {

                }

                @Override
                public void onDisconnect(boolean closedByServer) {
                    Log.e(TAG, "onDisconnect: closedByServer = " + closedByServer);
                    keepAliveTask.cancel();
                    if(closedByServer) {
                        EventBus.getDefault().post(new TokenEvent(TokenEvent.TokenType.TOKEN_OVERDUE));
                        manager.disconnect();
                    }else {
                        manager.reconnect();
                    }
                }

                @Override
                public void connectFailed() {
                    Log.e(TAG, "connectFailed");
                }
            });
            manager.connect();
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onTokenEvent(TokenEvent event){
        if(event.getType().equals(TokenEvent.TokenType.TOKEN_REFRESH)) {
            launchWebSocket();
        }
    }

    public void cancelWebSocket(){
        EventBus.getDefault().unregister(this);
    }

    public void sendKeepAliveMsg(){
        Timer timer = new Timer();
        AtomicInteger i = new AtomicInteger(0);
        keepAliveTask = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "sendKeepAliveMsg, " + (i.getAndIncrement()));
                manager.sendMessage(keepAlive.toByteArray());
            }
        };
        timer.schedule(keepAliveTask, 2_000, 10_000);
    }

    static DataContentSerializer.DataContent keepAlive = DataContentSerializer
            .DataContent
            .newBuilder()
            .setAction(MsgActionEnum.KEEPALIVE.type).build();
}
