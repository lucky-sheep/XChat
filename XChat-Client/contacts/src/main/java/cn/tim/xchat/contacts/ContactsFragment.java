package cn.tim.xchat.contacts;

import android.content.Intent;
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
import java.util.Objects;

import cn.tim.xchat.common.module.FriendInfo;
import cn.tim.xchat.common.task.ThreadManager;
import cn.tim.xchat.common.widget.titlebar.BaseTitleBar;
import cn.tim.xchat.common.widget.toast.XChatToast;
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
    private ContactRVAdapter contactRVAdapter;

    private BaseTitleBar baseTitleBar;

    public ContactsFragment(BaseTitleBar baseTitleBar) {
        this.baseTitleBar = baseTitleBar;
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

//        LitePal.deleteAll(FriendInfo.class);
        List<FriendInfo> friendsInfo = LitePal.findAll(FriendInfo.class);
        contactRVAdapter = new ContactRVAdapter(getContext(), contactRV);
        contactRVAdapter.setDataSource(friendsInfo);
        contactRV.setLayoutManager(new LinearLayoutManager(getContext()));
        contactRV.setAdapter(contactRVAdapter);

        // 后台请求数据后更新数据库
        ThreadManager.getInstance().runTask(this::requestData);
        contactRVAdapter.setListener(position -> {
            FriendInfo friendInfo = contactRVAdapter.getDataSource().get(position);
            Log.i(TAG, "inflateData: friendInfo = " + friendInfo.getUsername());
//            ARouter.getInstance()
//                    .build("/contacts/friend")
////                    .withObject("friendInfo", friendInfo) // 傻逼ARouter，果断unstar
//                    .withString("username", friendInfo.getUsername())
//                    .navigation();
            Intent intent = new Intent(getContext(), FriendActivity.class);
            intent.putExtra("username", friendInfo.getUsername());
            startActivity(intent);
        });


        baseTitleBar.addBtn.setOnClickListener(v -> {
            AddFriendDialog addFriendDialog = new AddFriendDialog(requireContext(), R.style.DiyDialog);
            addFriendDialog.setTitle("添加新朋友");
            addFriendDialog.show();
        });
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
                                    FriendInfo info = LitePal.where("username = ?", friendInfo.getUsername())
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