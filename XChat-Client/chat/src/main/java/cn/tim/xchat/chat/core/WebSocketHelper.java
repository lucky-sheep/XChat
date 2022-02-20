package cn.tim.xchat.chat.core;

import android.text.TextUtils;
import android.util.Log;

import com.tencent.mmkv.MMKV;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.URI;
import cn.tim.xchat.chat.BuildConfig;
import cn.tim.xchat.chat.ws.XWebSocketClient;
import cn.tim.xchat.common.constans.StorageKey;
import cn.tim.xchat.common.event.NetworkStateEvent;
import cn.tim.xchat.common.event.TokenEvent;
import cn.tim.xchat.common.event.WSEvent;

public class WebSocketHelper {
    public static WebSocketHelper instance;

    private static final String TAG = "WebSocketHelper";
    private static final MMKV mmkv = MMKV.defaultMMKV();
    private XWebSocketClient socketClient;

    private WebSocketHelper(){
        EventBus.getDefault().register(this);
    }

    public static WebSocketHelper getInstance(){
        if(instance == null) {
            synchronized (WebSocketHelper.class){
                if(instance == null) {
                    instance = new WebSocketHelper();
                }
            }
        }
        return instance;
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

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onWSEvent(WSEvent event){
        if(WSEvent.Type.DISCONNECTED_BY_USER.equals(event.getType())) {
            if(socketClient != null) socketClient.close();
            Log.e(TAG, "onWSEvent: 用户主动登出-> WebSocket关闭");
            stopWebSocket();
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onNetStateEvent(NetworkStateEvent event){
        if(NetworkStateEvent.Type.AVAILABLE.equals(event.getType())) {
            Log.e(TAG, "onNetStateEvent: 检测到网络恢复");
            // 网络变为可用的时候，重新启动 WebSocket
            if(socketClient != null && socketClient.isClosed()){
                launchHandle();
            }
        }
    }

    public void cancelWebSocket(){
        EventBus.getDefault().unregister(this);
    }
}
