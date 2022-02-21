package cn.tim.xchat.contacts.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import cn.tim.xchat.common.widget.toast.XChatToast;
import cn.tim.xchat.contacts.R;
import cn.tim.xchat.network.OkHttpUtils;
import cn.tim.xchat.network.config.NetworkConfig;
import cn.tim.xchat.network.model.ResponseModule;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AddFriendDialog extends Dialog {
    private static final String TAG = "AddFriendDialog";
    private String nameOrEmail;
    private MaterialButton submitBtn;
    private Timer timer = new Timer();

    private TextView timeoutTips;

    public AddFriendDialog(@NonNull Context context, int themeResId) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_new_friends_dialog);
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText usernameOrEmailEt = findViewById(R.id.contact_add_new_friend_et);
        submitBtn = findViewById(R.id.contact_add_new_friend_submit_btn);
        timeoutTips = findViewById(R.id.contact_add_new_friend_timeout_close);

        submitBtn.setOnClickListener(v -> {
            nameOrEmail = usernameOrEmailEt.getText().toString().trim();
            if(nameOrEmail.length() < 2 || nameOrEmail.length() > 32) {
                XChatToast.INSTANCE.showToast(getContext(), "用户名/邮箱长度为2-32");
                return;
            }
            submitBtn.setEnabled(false);
            submitBtn.setText("正在发送...");
            setCanceledOnTouchOutside(false);
            sendData();
        });
    }

    private void sendData() {
        OkHttpClient client = OkHttpUtils.getInstance();
        Map<String, Object> map = new HashMap<>();
        map.put("username", nameOrEmail);
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                JSONObject.toJSONString(map));
        Request request = new Request.Builder()
                .post(requestBody)
                .url(NetworkConfig.baseUrl + NetworkConfig.REQUEST_FRIENDS_URL)
                .build();
        client.newCall(request)
                .enqueue(new Callback() {
                    @RequiresApi(api = Build.VERSION_CODES.P)
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        handleError(-1, null);
                    }

                    @RequiresApi(api = Build.VERSION_CODES.P)
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        ResponseBody responseBody = response.body();
                        assert responseBody != null;
                        String responseBodyStr = responseBody.string();
                        Log.e(TAG, "onResponse: responseBodyStr = " + responseBodyStr);
                        ResponseModule responseModule = JSON.parseObject(
                                responseBodyStr, ResponseModule.class);
                        if(responseModule != null){
                            if(responseModule.getSuccess()) {
                                handleSuccess();
                            }else {
                                handleError(responseModule.getCode(), responseModule.getMessage());
                            }
                        }
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void handleSuccess() {
        Executor mainExecutor = getContext().getMainExecutor();
        mainExecutor.execute(() -> {
            timeoutTips.setVisibility(View.VISIBLE);
            submitBtn.setIcon(getContext().getResources().getDrawable(
                    R.drawable.icon_contacts_send_reqfriend_ok, null));
            submitBtn.setText("发送成功");
            submitBtn.setBackgroundColor(Color.parseColor("#66BB6A"));
            setCanceledOnTouchOutside(true);
        });

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mainExecutor.execute(AddFriendDialog.this::dismiss);
                timer.cancel();
            }
        }, 10_000); // 10s 后自动关闭

        AtomicInteger count = new AtomicInteger(10);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mainExecutor.execute(()-> {
                    if(count.getAndDecrement() > 0){
                        // 自动关闭Dialog
                        timeoutTips.setText(count + "s后自动关闭");
                    }
                });
            }
        }, 1_000, 1_000);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint("UseCompatLoadingForDrawables")
    private void handleError(int errorCode, String msg){
        Executor mainExecutor = getContext().getMainExecutor();
        mainExecutor.execute(() -> {
            submitBtn.setIcon(getContext().getResources().getDrawable(
                    R.drawable.icon_contacts_send_reqfriend_failed, null));
            if(errorCode == -1) {
                submitBtn.setText("网络错误");
            }else {
                submitBtn.setText(msg);
            }
        });

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mainExecutor.execute(() -> {
                    submitBtn.setText("申请添加好友");
                    submitBtn.setEnabled(true);
                    submitBtn.setIcon(getContext().getResources().getDrawable(
                            R.drawable.icon_contacts_send_reqfriend, null));
                    setCanceledOnTouchOutside(true);
                });
            }
        }, 2500);
    }
}
