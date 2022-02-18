package cn.tim.xchat.chat.core;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import cn.tim.xchat.chat.ChatActivity;
import cn.tim.xchat.chat.R;

public class WebSocketService extends Service {
    public static final String TAG = "WebSocketService";

    private final static String CHANNEL_ID = "100500";
    private final static String CHANNEL_NAME = "XChat";
    private final static int GRAY_SERVICE_ID = 1001;
    public final static int NOTIFY_ID = 10080;
    private WebSocketClientBinder mBinder = new WebSocketClientBinder();
    private final WebSocketHelper webSocketHelper;
    private Notification notification;

    public WebSocketService() {
        webSocketHelper = new WebSocketHelper();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //灰色保活
    public static class GrayInnerService extends Service {
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GRAY_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

    // 用于Activity和service通讯
    public class WebSocketClientBinder extends Binder {
        public WebSocketService getService() {
            return WebSocketService.this;
        }

        public void cancel() {
            webSocketHelper.cancelWebSocket();
        }

        public void startForeground() {
            WebSocketService.this.startForeground(NOTIFY_ID, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        //初始化WebSocket
        webSocketHelper.launchWebSocket();
        //mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启心跳检测
        //设置service为前台服务，提高优先级
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);

        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, ChatActivity.class), 0);
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("This is content title.")
                .setContentText("This is content text.")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .build();

        manager.createNotificationChannel(notificationChannel);
        manager.notify(NOTIFY_ID, notification);
        return START_STICKY;
    }
}