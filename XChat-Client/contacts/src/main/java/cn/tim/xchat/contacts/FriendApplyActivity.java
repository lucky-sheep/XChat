package cn.tim.xchat.contacts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.List;
import java.util.Locale;

import cn.tim.xchat.common.XChatBaseActivity;
import cn.tim.xchat.common.core.WebSocketHelper;
import cn.tim.xchat.common.enums.business.RequestFriendEnum;
import cn.tim.xchat.common.event.AppEvent;
import cn.tim.xchat.common.module.FriendInfo;
import cn.tim.xchat.common.module.FriendRequest;
import cn.tim.xchat.common.msg.MsgActionEnum;
import cn.tim.xchat.common.msg.MsgTypeEnum;
import cn.tim.xchat.common.utils.BeanCopyUtil;
import cn.tim.xchat.common.widget.titlebar.BaseTitleBar;
import cn.tim.xchat.common.widget.titlebar.TitleBarType;
import cn.tim.xchat.common.widget.toast.XChatToast;
import cn.tim.xchat.contacts.adapter.FriendApplyAdapter;
import cn.tim.xchat.core.model.DataContentSerializer;

@Route(path = "/contacts/apply")
public class FriendApplyActivity extends XChatBaseActivity {
    private static final String TAG = "FriendApplyActivity";
    private RecyclerView applyRv;
    private RecyclerView acceptRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_contacts_friend_apply);
        ARouter.getInstance().inject(this);
        BaseTitleBar baseTitleBar = findViewById(R.id.contact_friend_apply_title_bar);
        baseTitleBar.autoChangeByType(TitleBarType.CONTACTS_APPLY_LIST);

        applyRv = findViewById(R.id.contacts_apply_list_rv);
        acceptRv = findViewById(R.id.contacts_accept_list_rv);

        // 寻找
        List<FriendRequest> friendRequests = LitePal.where("isMyRequest = ?", "1")
                .find(FriendRequest.class);
        for (FriendRequest request: friendRequests){
            Log.i(TAG, "onCreate: " + request);
        }

        FriendApplyAdapter applyAdapter = new FriendApplyAdapter(this, applyRv);
        applyAdapter.setDataSource(friendRequests);

        // 跳转详情页
        applyAdapter.setListener(position -> {
            FriendRequest friendRequest = friendRequests.get(position);
            navigationToDetail(friendRequest);
        });

        applyRv.setLayoutManager(new LinearLayoutManager(this));
        applyRv.setAdapter(applyAdapter);

        // 设置Item的点击事件
        applyAdapter.setApplyClickListener(new FriendApplyAdapter.OnApplyClickListener() {
            @Override
            public void onItemClickPass(int position) {
                Log.i(TAG, "onItemClickPass: ");
                FriendRequest friendRequest = friendRequests.get(position);
                boolean sendRet = sendPassOrRefuseMsg(friendRequest, true);
                if(sendRet) {
                    friendRequest.setArgeeState(RequestFriendEnum.AGREE.getCode());
                    applyAdapter.updateOneData(position);
                    // 本地更新数据库 朋友库 + 申请库
                    friendRequest.save();
                    saveFriendRequestToFriend(friendRequest);

                    // 刷新FriendRV
                    EventBus.getDefault().post(new AppEvent(AppEvent.Type.REFRESH_LOCAL_FRIENDS_LIST));
                }
            }

            @Override
            public void onItemClickRefuse(int position) {
                FriendRequest friendRequest = friendRequests.get(position);
                friendRequest.setArgeeState(RequestFriendEnum.REFUSE.getCode());
                boolean sendRet = sendPassOrRefuseMsg(friendRequest, false);
                if(sendRet) {
                    friendRequest.save();
                    applyAdapter.updateOneData(position);
                }
            }
        });

        // ======================================================

        List<FriendRequest> friendAccept = LitePal.where("isMyRequest = ?", "0")
                .find(FriendRequest.class);
        baseTitleBar.backBtn.setOnClickListener(v -> finish());
        FriendApplyAdapter acceptAdapter = new FriendApplyAdapter(this, acceptRv);
        acceptAdapter.setDataSource(friendAccept);

        acceptAdapter.setListener(position -> {
            FriendRequest friendRequest = friendAccept.get(position);
            navigationToDetail(friendRequest);
        });

        acceptRv.setLayoutManager(new LinearLayoutManager(this));
        acceptRv.setAdapter(acceptAdapter);
    }

    /**
     * 跳转到详情页面
     */
    private void navigationToDetail(FriendRequest friendRequest) {
        String userId = friendRequest.getUserId();

        // 先查数据库
        FriendInfo friendInfo = LitePal.where("userId = ?", userId).findFirst(FriendInfo.class);
        if(friendInfo != null) {

//        }
        // 已经同意的跳转
//        if(friendRequest.getArgeeState() == RequestFriendEnum.AGREE_ME.getCode()){
            ARouter.getInstance()
                    .build("/contacts/detail")
                    .withObject("friendInfo", LitePal.where("userId = ?",
                            friendRequest.getUserId()).findFirst(FriendInfo.class))
                    .navigation();
        }else {  // 未同意的跳转
            ARouter.getInstance()
                    .build("/contacts/detail")
                    .withObject("friendRequest", friendRequest)
                    .navigation();
        }
    }

    /**
     * 发送同意好友请求的消息
     * @return 发送是否成功
     */
    private boolean sendPassOrRefuseMsg(FriendRequest friendRequest, boolean result) {
        String itemId = friendRequest.getItemId();
        int state = result? RequestFriendEnum.AGREE.getCode():
                RequestFriendEnum.REFUSE.getCode();

        String data = String.format(Locale.CHINA,"{\"itemId\": \"%s\", \"state\": %d}", itemId, state);

        DataContentSerializer.DataContent.ChatMessage message =
                DataContentSerializer.DataContent.ChatMessage
                        .newBuilder()
                        .setText(data)
                        .setType(MsgTypeEnum.FRIEND_REQUEST_NEW.getCode())
                        .build();

        DataContentSerializer.DataContent dataContent = DataContentSerializer.DataContent
                .newBuilder()
                .setAction(MsgActionEnum.BUSINESS.type)
                .setChatMessage(message)
                .build();
        if(!WebSocketHelper.getInstance().sendMsg(dataContent)){
            XChatToast.INSTANCE.showToast(this, "网络错误，请检查网络");
            return false;
        }
        return true;
    }

    /**
     * 保存新朋友信息到数据库
     */
    private void saveFriendRequestToFriend(FriendRequest friendRequest) {
        FriendInfo friendInfo = new FriendInfo();
        try {
            BeanCopyUtil.copyProperties(friendRequest, friendInfo);
            friendInfo.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "saveFriendRequestToFriend: friendInfo = " + friendInfo);
        // TODO 通知Friend列表更新
    }

    @Override
    protected ViewGroup getContentView() {
        return findViewById(android.R.id.content);
    }
}