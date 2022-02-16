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

import cn.tim.xchat.chat.BuildConfig;
import cn.tim.xchat.chat.msg.MsgActionEnum;
import cn.tim.xchat.chat.ws.WebSocketManager;
import cn.tim.xchat.common.constans.StorageKey;
import cn.tim.xchat.common.event.TokenEvent;
import cn.tim.xchat.core.model.DataContentSerializer;

public class WebSocketHelper {
    private static final String TAG = "WebSocketHelper";
    private static MMKV mmkv;
    private WebSocketManager manager;


    public WebSocketHelper() {
        EventBus.getDefault().register(this);
    }

    public void launchWebSocket(){
        new Thread(this::launchHandle).start();
    }

    private void launchHandle(){
        String webSocketUrl = BuildConfig.WEBSOCKET_URL;
        mmkv = MMKV.defaultMMKV();
        String token = mmkv.getString(StorageKey.TOKEN_KEY, null);
        if(TextUtils.isEmpty(token)) {
            EventBus.getDefault().post(new TokenEvent(TokenEvent.TokenType.TOKEN_OVERDUE));
        }else {
            manager = new WebSocketManager(token, 2000);
            manager.setWebSocketListener(new WebSocketManager.WebSocketListener() {
                @Override
                public void onConnected(Map<String, List<String>> headers) {
                    Log.i(TAG, "onConnected: ");
                    sendKeepAliveMsg();
                }

                @Override
                public void onTextMessage(String text) {
                    Log.i(TAG, "onTextMessage: text = " + text);
                    if("NoAuth".equals(text)){
                        // 说明验证未通过
                        EventBus.getDefault().post(new TokenEvent(TokenEvent.TokenType.TOKEN_OVERDUE));
                    }
                }

                @Override
                public void onByteMessage(byte[] retByte) {

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
//        Timer timer = new Timer();
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                manager.sendMessage(keepAlive.toByteArray());
//            }
//        };
//        timer.schedule(task, 2_000, 10_000);
    }

    static DataContentSerializer.DataContent keepAlive = DataContentSerializer
            .DataContent
            .newBuilder()
            .setAction(MsgActionEnum.KEEPALIVE.type).build();
}
