package cn.tim.xchat.chat;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.tim.xchat.common.event.ChatEvent;
import cn.tim.xchat.common.module.chat.ChatMsg;

public class ChatViewModel extends ViewModel {
    private static final String TAG = "ChatViewModel";
    /**
     * k: 对话者ID
     * v: 消息列表
     */
    public Map<String, List<ChatMsg>> msgList = new ConcurrentHashMap<>();

    public MutableLiveData<String> currentUserId = new MutableLiveData<>(); // 对话者

    public MutableLiveData<Boolean> needRefreshMsg = new MutableLiveData<>(false);

    public ChatViewModel(){
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onMsg(ChatEvent chatEvent){
        if(ChatEvent.Type.RECEIVE_MSG == chatEvent.getType()){
            Map<String, Object> data = chatEvent.getData();
            String msgItemId = (String) data.get(ChatEvent.MSG_ITEM_ID);
            String senderId = (String) data.get(ChatEvent.SENDER_ID);
            if(TextUtils.isEmpty(senderId) || TextUtils.isEmpty(msgItemId)) {
//                Log.e(TAG, "onMsg: event data is null!!!");
                throw new RuntimeException("onMsg: event data is null!!!");
            }

            if(senderId.equals(currentUserId.getValue())){
                // TODO update list
                List<ChatMsg> chatMsgs = msgList.get(senderId);
                ChatMsg dstMsg = LitePal.where("itemId=?", msgItemId).findFirst(ChatMsg.class);
                Log.i(TAG, "onMsg: dstMsg = " + dstMsg);
                chatMsgs.add(dstMsg);
                needRefreshMsg.setValue(true);
            }else {
                // TODO 更新Main列表
                Log.i(TAG, "onMsg: 更新Main列表");
            }

        }else if(ChatEvent.Type.SEND_MSG == chatEvent.getType()){
            //
        }
    }
}
