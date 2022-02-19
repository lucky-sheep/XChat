package cn.tim.xchat.chat.core;

import android.text.TextUtils;
import android.util.Log;

import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import cn.tim.xchat.chat.BuildConfig;
import cn.tim.xchat.chat.msg.MsgActionEnum;
//import cn.tim.xchat.chat.ws.WebSocketManager;
import cn.tim.xchat.chat.ws.XWebSocketClient;
import cn.tim.xchat.common.constans.StorageKey;
import cn.tim.xchat.common.event.TokenEvent;
import cn.tim.xchat.common.event.WSEvent;
import cn.tim.xchat.core.model.DataContentSerializer;

public class WebSocketHelper {
    private static final String TAG = "WebSocketHelper";
    private TimerTask keepAliveTask;
    private static final MMKV mmkv = MMKV.defaultMMKV();
    private XWebSocketClient socketClient;

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
            socketClient = new XWebSocketClient(URI.create(BuildConfig.WEBSOCKET_URL), token);
            try {
                socketClient.connectBlocking();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopWebSocket() {
        if(socketClient != null) socketClient.close();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onTokenEvent(TokenEvent event){
        if(event.getType().equals(TokenEvent.TokenType.TOKEN_REFRESH)) {
            if(socketClient != null) socketClient.close();
            launchWebSocket();
        }
    }

    public void onWSEvent(WSEvent event){
        if(WSEvent.Type.ACTIVE_CLOSE.equals(event.getType())) {
            if(socketClient != null) socketClient.close();
            stopWebSocket();
        }
    }

    public void cancelWebSocket(){
        EventBus.getDefault().unregister(this);
    }
}
