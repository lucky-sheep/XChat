package cn.tim.xchat.mvvm;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.tim.xchat.common.event.AppEvent;
import cn.tim.xchat.common.event.WSEvent;

public class MainViewModel extends ViewModel {
    private static final String TAG = "MainViewModel";
    public MutableLiveData<WSEvent.Type> status = new MutableLiveData<>(WSEvent.Type.DISCONNECTED);

    public MutableLiveData<Integer> newFriendReqNum = new MutableLiveData<>(0);

    public MainViewModel(){
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED, sticky = true)
    public void setWebSocketStatus(WSEvent event){
        Log.i(TAG, "responseWSEvent: event = " + event.getType().name());
        status.setValue(event.getType());
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void setMainUpdate(AppEvent event){
        if(event.getType() == AppEvent.Type.NEW_FRIENDS_REQUEST){
            newFriendReqNum.setValue((Integer) event.getData()
                    .get(AppEvent.Type.NEW_FRIENDS_REQUEST.name()));
            Log.i(TAG, "setMainUpdate: event = " + event.getType().name());
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        EventBus.getDefault().unregister(this);
    }
}
