package cn.tim.xchat.contacts;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import org.litepal.LitePal;

import java.util.Arrays;
import java.util.List;

import cn.tim.xchat.common.XChatBaseActivity;
import cn.tim.xchat.common.enums.business.RequestFriendEnum;
import cn.tim.xchat.common.module.FriendInfo;
import cn.tim.xchat.common.module.FriendRequest;
import cn.tim.xchat.common.widget.titlebar.BaseTitleBar;
import cn.tim.xchat.common.widget.titlebar.TitleBarType;
import cn.tim.xchat.contacts.adapter.BaseFriendAdapter;
import cn.tim.xchat.contacts.adapter.FriendApplyAdapter;
import cn.tim.xchat.contacts.adapter.FriendInfoAdapter;

@Route(path = "/contacts/apply")
public class FriendApplyActivity extends XChatBaseActivity {
    private static final String TAG = "FriendApplyActivity";
    private RecyclerView applyRv;
    private RecyclerView acceptRv;

    /*
     *  FriendInfo  info = LitePal.where("itemId = ?", friendInfo.getItemId())
     *          .findFirst(FriendInfo.class);
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_contacts_friend_apply);
        ARouter.getInstance().inject(this);
        BaseTitleBar baseTitleBar = findViewById(R.id.contact_friend_apply_title_bar);
        baseTitleBar.autoChangeByType(TitleBarType.CONTACTS_APPLY_LIST);

        applyRv = findViewById(R.id.contacts_apply_list_rv);
        acceptRv = findViewById(R.id.contacts_accept_list_rv);

//        List<FriendRequest> friendRequests = LitePal.findAll(FriendRequest.class);
        List<FriendRequest> friendRequests = LitePal.where("argeeState < ?", String.valueOf(RequestFriendEnum.PARTITION_VALIDATOR.getCode()))
                .find(FriendRequest.class);
        for (FriendRequest request: friendRequests){
            Log.i(TAG, "onCreate: " + request);
        }

        FriendApplyAdapter applyAdapter = new FriendApplyAdapter(this, applyRv);
        applyAdapter.setDataSource(friendRequests);
        applyAdapter.setListener(position -> {
            FriendRequest friendRequest = friendRequests.get(position);
            ARouter.getInstance()
                    .build("/contacts/detail")
                    .withObject("friendRequest", friendRequest)
                    .navigation();
        });

        applyRv.setLayoutManager(new LinearLayoutManager(this));
        applyRv.setAdapter(applyAdapter);

        // ======================================================

//
        List<FriendRequest> friendAccept = LitePal.where("argeeState > ?", String.valueOf(RequestFriendEnum.PARTITION_VALIDATOR.getCode()))
                .find(FriendRequest.class);
        baseTitleBar.backBtn.setOnClickListener(v -> finish());
        FriendApplyAdapter acceptAdapter = new FriendApplyAdapter(this, acceptRv);
        acceptAdapter.setDataSource(friendAccept);
        acceptAdapter.setListener(position -> {
            FriendRequest friendRequest = friendAccept.get(position);
            ARouter.getInstance()
                    .build("/contacts/detail")
                    .withObject("friendRequest", friendRequest)
                    .navigation();
        });
        acceptRv.setLayoutManager(new LinearLayoutManager(this));
        acceptRv.setAdapter(acceptAdapter);
    }

    @Override
    protected ViewGroup getContentView() {
        return findViewById(android.R.id.content);
    }
}