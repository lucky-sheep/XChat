package cn.tim.xchat.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.util.Log;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import cn.tim.xchat.common.event.AppEvent;

public class NetWorkStateReceiver extends BroadcastReceiver {

    private static final String TAG = "NetWorkStateReceiver";
    private ConnectivityManager connMgr;

    @Override
    public void onReceive(Context context, Intent intent) {
        connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取所有网络连接的信息
        Network[] networks = connMgr.getAllNetworks();
        //用于存放网络连接信息
        StringBuilder sb = new StringBuilder();
        //通过循环将网络信息逐个取出来
        boolean retAvailable = false;
        for (Network network : networks) {
            //获取ConnectivityManager对象对应的NetworkInfo对象
            NetworkInfo networkInfo = connMgr.getNetworkInfo(network);

            sb.append(networkInfo.getTypeName())
                    .append(" connect is ")
                    .append(networkInfo.isConnected())
                    .append(", available is ")
                    .append(networkInfo.isAvailable())
                    .append("\n");
            if(networkInfo.isAvailable()){
                retAvailable = true;
            }
        }

        //Log.i(TAG, "onReceive: " + sb);
        EventBus.getDefault().post(new AppEvent(
                retAvailable ?
                        AppEvent.Type.NETWORK_AVAILABLE :
                        AppEvent.Type.NETWORK_UNAVAILABLE
        ));
//        getNetWorkByConnectivityManager();
    }

    // 效果不太行，直接使用广播吧
    private void getNetWorkByConnectivityManager(){
        connMgr.requestNetwork(new NetworkRequest.Builder().build(),
                new ConnectivityManager.NetworkCallback(){
                    @Override
                    public void onAvailable(@NonNull Network network) {
                        super.onAvailable(network);
                        Log.i(TAG, "onAvailable: ");
                        EventBus.getDefault().post(new AppEvent(AppEvent.Type.NETWORK_AVAILABLE));
                    }

                    @Override
                    public void onUnavailable() {
                        super.onUnavailable();
                        Log.i(TAG, "onUnavailable: ");
                        EventBus.getDefault().post(new AppEvent(AppEvent.Type.NETWORK_UNAVAILABLE));
                    }
                });
    }
}