package cn.tim.xchat.chat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchPanelLinearLayout;
import cn.tim.xchat.common.utils.StatusBarUtil;
import cn.tim.xchat.common.widget.titlebar.BaseTitleBar;

@Route(path = "/chat/list")
public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

//    private void adaptTheme(final boolean isFullScreenTheme) {
//        if (isFullScreenTheme) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        } else {
//            ;
//        }
//    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StatusBarUtil.setDarkStatusIcon(getWindow(), true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_chat);
        findView();

        KeyboardUtil.attach(this, panelRoot);
        KPSwitchConflictUtil.attach(panelRoot, addBtn, sendEdit,
                switchToPanel -> {
                    if (switchToPanel) {
                        sendEdit.clearFocus();
                    } else {
                        sendEdit.requestFocus();
                    }
                });

        msgListRv.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                KPSwitchConflictUtil.hidePanelAndKeyboard(panelRoot);
            }
            return false;
        });
    }

    private KPSwitchPanelLinearLayout panelRoot;
    private EditText sendEdit;
    private ImageView addBtn;
    private RecyclerView msgListRv;
    private BaseTitleBar baseTitleBar;


    private void findView() {
        msgListRv = findViewById(R.id.chat_msg_rv);
        panelRoot = findViewById(R.id.chat_panel_root);
        sendEdit = findViewById(R.id.send_img_bar_content_et);
        addBtn = findViewById(R.id.send_img_bar_add_iv);
        baseTitleBar = findViewById(R.id.chat_msg_titlebar);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && panelRoot.getVisibility() != View.GONE) {
            KPSwitchConflictUtil.hidePanelAndKeyboard(panelRoot);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}