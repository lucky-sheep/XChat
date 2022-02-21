package cn.tim.xchat.mvvm;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.tim.xchat.common.event.WSEvent;

public class MainViewModel extends ViewModel {
    private static final String TAG = "MainViewModel";
    public MutableLiveData<WSEvent.Type> status = new MutableLiveData<>(WSEvent.Type.DISCONNECTED);

    public MainViewModel(){
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED, sticky = true)
    public void setStatus(WSEvent event){
        Log.i(TAG, "responseWSEvent: event = " + event.getType().name());
        status.setValue(event.getType());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        EventBus.getDefault().unregister(this);
    }
}
