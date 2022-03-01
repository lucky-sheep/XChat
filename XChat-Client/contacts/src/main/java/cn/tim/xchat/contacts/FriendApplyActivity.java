package cn.tim.xchat.contacts;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.core.internal.deps.guava.collect.Maps;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.HashMap;
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
import cn.tim.xchat.common.mvvm.MainViewModel;
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
    private FriendApplyAdapter applyAdapter;
    private FriendApplyAdapter acceptAdapter;

    @Autowired
    MainViewModel mainViewModel;
    private List<FriendRequest> friendsAccept;
    private List<FriendRequest> friendsRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_contacts_friend_apply);
        ARouter.getInstance().inject(this);
        EventBus.getDefault().register(this);
        BaseTitleBar baseTitleBar = findViewById(R.id.contact_friend_apply_title_bar);
        baseTitleBar.autoChangeByType(TitleBarType.CONTACTS_APPLY_LIST);

        applyRv = findViewById(R.id.contacts_apply_list_rv);
        acceptRv = findViewById(R.id.contacts_accept_list_rv);

        getFriendReqAccFromDB();

        // =============== my accept ==================
        applyAdapter = new FriendApplyAdapter(this, applyRv);
        applyAdapter.setDataSource(friendsRequest);

        // 跳转详情页
        applyAdapter.setListener(position -> {
            FriendRequest friendRequest = friendsRequest.get(position);
            navigationToDetail(friendRequest);
        });

        applyRv.setLayoutManager(new LinearLayoutManager(this));
        applyRv.setAdapter(applyAdapter);

        // 设置Item的点击事件
        applyAdapter.setApplyClickListener(new FriendApplyAdapter.OnApplyClickListener() {
            @Override
            public void onItemClickPass(int position) {
                Log.i(TAG, "onItemClickPass: ");
                FriendRequest friendRequest = friendsRequest.get(position);
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
                FriendRequest friendRequest = friendsRequest.get(position);
                friendRequest.setArgeeState(RequestFriendEnum.REFUSE.getCode());
                boolean sendRet = sendPassOrRefuseMsg(friendRequest, false);
                if(sendRet) {
                    friendRequest.save();
                    applyAdapter.updateOneData(position);
                }
            }
        });

        // =============== my send ==================
        baseTitleBar.backBtn.setOnClickListener(v -> finish());
        acceptAdapter = new FriendApplyAdapter(this, acceptRv);
        acceptAdapter.setDataSource(friendsAccept);

        acceptAdapter.setListener(position -> {
            FriendRequest friendRequest = friendsAccept.get(position);
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
        // 已经同意的跳转
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
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFriendReqResultEvent(AppEvent appEvent){
        if(appEvent.getType() == AppEvent.Type.NEW_FRIENDS_REQUEST){
            getFriendReqAccFromDB();
            acceptAdapter.setDataSource(friendsAccept);
            applyAdapter.setDataSource(friendsRequest);
        }
    }

    // 从数据库得到好友请求信息
    private void getFriendReqAccFromDB() {
        friendsAccept = LitePal.where("isMyRequest = ?", "0")
                .find(FriendRequest.class);
        friendsRequest = LitePal.where("isMyRequest = ?", "1")
                .find(FriendRequest.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        // 外部通知清零
        HashMap<String, Object> data = Maps.newHashMap();
        data.put(AppEvent.Type.NEW_FRIENDS_REQUEST.name(), 0);
        EventBus.getDefault().post(new AppEvent(AppEvent.Type.NEW_FRIENDS_REQUEST, data));
    }

    @Override
    protected ViewGroup getContentView() {
        return findViewById(android.R.id.content);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}