package cn.tim.xchat.contacts;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.tim.xchat.common.module.FriendInfo;
import cn.tim.xchat.common.mvvm.MainViewModel;
import cn.tim.xchat.common.task.ThreadManager;
import cn.tim.xchat.common.widget.titlebar.BaseTitleBar;
import cn.tim.xchat.common.widget.toast.XChatToast;
import cn.tim.xchat.contacts.adapter.FriendInfoAdapter;
import cn.tim.xchat.contacts.widget.AddFriendDialog;
import cn.tim.xchat.network.OkHttpUtils;
import cn.tim.xchat.network.config.NetworkConfig;
import cn.tim.xchat.network.model.ResponseModule;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Route(path = "/contacts/main")
public class ContactsFragment extends Fragment {
    private static final String TAG = "ContactsFragment";
    private View view;
    private TextView newFriendBtn;
    private TextView groupManagerBtn;
    private RecyclerView contactRV;
    private FriendInfoAdapter contactRVAdapter;

    private final BaseTitleBar baseTitleBar;
    private final Timer timer;

    private final MainViewModel mainViewModel;
    private TimerTask tipsFlicker;

    public ContactsFragment(BaseTitleBar baseTitleBar, MainViewModel mainViewModel) {
        this.baseTitleBar = baseTitleBar;
        timer = new Timer();
        this.mainViewModel = mainViewModel;
    }

    // 创建视图
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_contacts_main_fragment, container, false);
        inflateData();
        return view;
    }

    private void inflateData() {
        newFriendBtn = view.findViewById(R.id.contacts_new_friend_btn);
        groupManagerBtn = view.findViewById(R.id.contacts_manager_group_btn);
        contactRV = view.findViewById(R.id.contacts_list_rv);

        // 创建数据库
        SQLiteDatabase database = LitePal.getDatabase();

        List<FriendInfo> friendsInfo = LitePal.findAll(FriendInfo.class);
        contactRVAdapter = new FriendInfoAdapter(getContext(), contactRV);
        contactRVAdapter.setDataSource(friendsInfo);
        contactRV.setLayoutManager(new LinearLayoutManager(getContext()));
        contactRV.setAdapter(contactRVAdapter);

        // 后台请求数据后更新数据库
        ThreadManager.getInstance().runTask(this::requestData);

        contactRVAdapter.setListener(position -> {
            FriendInfo friendInfo = contactRVAdapter.getDataSource().get(position);
            ARouter.getInstance()
                    .build("/contacts/detail")
                    .withObject("friendInfo", friendInfo)
                    .navigation();
        });

        baseTitleBar.addBtn.setOnClickListener(v -> {
            AddFriendDialog addFriendDialog = new AddFriendDialog(requireContext(), R.style.DiyDialog);
            addFriendDialog.setTitle("添加新朋友");
            addFriendDialog.show();
        });

        mainViewModel.newFriendReqNum.observe(requireActivity(), count -> {
            if(count > 0) {
                // 后台请求数据后更新数据库
                ThreadManager.getInstance().runTask(this::requestData);

                AtomicBoolean show = new AtomicBoolean(true);
                tipsFlicker = new TimerTask() {
                    @Override
                    public void run() {
                        if (show.getAndSet(!show.get())) {
                            newFriendBtn.setText(String.format(Locale.CHINA,
                                    "好友申请   (%d条新消息)", count));
                        } else {
                            newFriendBtn.setText("");
                        }
                    }
                };
                timer.schedule(tipsFlicker, 0, 650);
            }else {
                newFriendBtn.setText("好友申请");
                if(tipsFlicker != null) {
                    tipsFlicker.cancel();
                }
            }
        });

        newFriendBtn.setOnClickListener(v -> {
            mainViewModel.newFriendReqNum.setValue(0);
            // 好友请求页面
            ARouter.getInstance()
                    .build("/contacts/apply")
                    .withObject("mainViewModel", mainViewModel)
                    .navigation(requireActivity(), 1001);
        });

        mainViewModel.localFriendRefresh.observe(requireActivity(), needRefresh -> {
            // 从数据库更新本地好友列表
            if(needRefresh) {
                contactRVAdapter.setDataSource(LitePal.findAll(FriendInfo.class));
                mainViewModel.localFriendRefresh.setValue(false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void requestData() {
        Log.i(TAG, "requestData: ");
        OkHttpClient client = OkHttpUtils.getInstance();
        Request request = new Request.Builder()
                .get()
                .url(NetworkConfig.baseUrl + NetworkConfig.GET_FRIENDS_URL)
                .build();
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e(TAG, "onFailure: ", e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response)
                            throws IOException {
                        ResponseBody responseBody = response.body();
                        assert responseBody != null;
                        String responseBodyStr = responseBody.string();
                        Log.e(TAG, "onResponse: responseBodyStr = " + responseBodyStr);
                        try {
                            ResponseModule responseModule = JSON.parseObject(
                                    responseBodyStr, ResponseModule.class);
                            if(responseModule.getSuccess()) {
                                JSONArray friends = responseModule.getData().getJSONArray("friends");
                                Log.i(TAG, "onResponse: success, friends.size = " + friends.size());
                                for (int i = 0; i < friends.size(); i++) {
                                    FriendInfo friendInfo = friends.getObject(i, FriendInfo.class);
                                    FriendInfo info = LitePal.where("userId = ?", friendInfo.getUserId())
                                            .findFirst(FriendInfo.class);
                                    if(info != null) info.delete();
                                    friendInfo.save();
                                }

                                List<FriendInfo> friendsInfo = LitePal.findAll(FriendInfo.class);
                                // 数据存储完成 -> 发送信号
                                requireActivity().runOnUiThread(()-> {
                                    contactRVAdapter.setDataSource(friendsInfo);
                                });
                            }else {
                                requireActivity().runOnUiThread(() ->
                                        XChatToast.INSTANCE.showToast(getActivity(), responseModule.getMessage()));
                            }
                        }catch (JSONException | NullPointerException e){
                            Log.e(TAG, "onResponse: ", e);
                        }
                    }
                });
    }
}