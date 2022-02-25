package cn.tim.xchat.chat;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.button.MaterialButton;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchFSPanelLinearLayout;
import cn.dreamtobe.kpswitch.widget.KPSwitchPanelLinearLayout;
import cn.tim.xchat.common.XChatBaseActivity;
import cn.tim.xchat.common.widget.titlebar.BaseTitleBar;
import io.github.rockerhieu.emojicon.util.Utils;

@Route(path = "/chat/list")
public class ChatActivity extends XChatBaseActivity {
    private KPSwitchFSPanelLinearLayout panelRoot;
    private ImageView micBtn;
    private ImageView emojiBtn;
    private ImageView addBtn;
    private EditText contentEt;
    private MaterialButton sendBtn;
    private RecyclerView msgListRv;
    private BaseTitleBar baseTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        findView();

        KeyboardUtil.attach(this, panelRoot);
        KPSwitchConflictUtil.attach(panelRoot, addBtn, contentEt,
                new KPSwitchConflictUtil.SwitchClickListener() {
                    @Override
                    public void onClickSwitch(View v, boolean switchToPanel) {
                        if (switchToPanel) {
                            contentEt.clearFocus();
                        } else {
                            contentEt.requestFocus();
                        }
                    }
                });

        msgListRv.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                KPSwitchConflictUtil.hidePanelAndKeyboard(panelRoot);
            }
            return false;
        });
//
//        contentEt.setOnClickListener(v -> {
//            KPSwitchConflictUtil.attach(panelRoot, baseTitleBar, contentEt,
//                    new KPSwitchConflictUtil.SwitchClickListener() {
//                        @Override
//                        public void onClickSwitch(View v, boolean switchToPanel) {
//                            if (switchToPanel) {
//                                contentEt.clearFocus();
//                            } else {
//                                contentEt.requestFocus();
//                            }
//                        }
//                    });
//        });
//        contentEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                KPSwitchConflictUtil.attach(panelRoot, baseTitleBar, contentEt,
//                        new KPSwitchConflictUtil.SwitchClickListener() {
//                            @Override
//                            public void onClickSwitch(View v, boolean switchToPanel) {
//                                if (switchToPanel) {
//                                    contentEt.clearFocus();
//                                } else {
//                                    contentEt.requestFocus();
//                                }
//                            }
//                        });
//            }
//        });
    }

    @Override
    protected ViewGroup getContentView() {
        return findViewById(android.R.id.content);
    }

    private void findView(){
        panelRoot = findViewById(R.id.panel_root);


        micBtn = findViewById(R.id.send_img_bar_mic_iv);
        emojiBtn = findViewById(R.id.send_img_bar_emoji_iv);
        addBtn = findViewById(R.id.send_img_bar_add_iv);
        contentEt = findViewById(R.id.send_img_bar_content_et);
        sendBtn = findViewById(R.id.send_img_bar_send_btn);


        msgListRv = findViewById(R.id.chat_msg_rv);
        baseTitleBar = findViewById(R.id.chat_msg_titlebar);
    }


    @Override
    protected void onPause() {
        super.onPause();
        panelRoot.recordKeyboardStatus(getWindow());
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

    // 当屏幕分屏/多窗口变化时回调
    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
        KPSwitchConflictUtil.onMultiWindowModeChanged(isInMultiWindowMode);
    }
}