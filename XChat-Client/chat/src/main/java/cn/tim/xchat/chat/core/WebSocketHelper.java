package cn.tim.xchat.chat.core;

import android.util.Log;

import java.net.URI;

import cn.tim.xchat.chat.BuildConfig;

public class WebSocketHelper {
    static String webSocketUrl = BuildConfig.WEBSOCKET_URL;
    private static CoreWSClient coreWSClient;

    public static void launchWebSocket(){
        Log.i(WebSocketService.TAG, "launchWebSocket: ");
        coreWSClient = new CoreWSClient(URI.create(webSocketUrl));
        try {
            coreWSClient.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void cancelWebSocket(){
        if(coreWSClient != null) {
            coreWSClient.close();
        }
    }
}
