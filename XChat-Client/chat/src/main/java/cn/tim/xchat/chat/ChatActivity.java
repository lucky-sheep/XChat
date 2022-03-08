package cn.tim.xchat.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.button.MaterialButton;

import org.litepal.FluentQuery;
import org.litepal.LitePal;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchPanelLinearLayout;
import cn.tim.xchat.chat.adapter.ChatListAdapter;
import cn.tim.xchat.chat.send.CallBackAsync;
import cn.tim.xchat.chat.send.SendMsgAction;
import cn.tim.xchat.chat.send.impl.SendMsgActionImpl;
import cn.tim.xchat.chat.vo.ChatMsgVO;
import cn.tim.xchat.common.enums.business.MsgSignEnum;
import cn.tim.xchat.common.module.FriendInfo;
import cn.tim.xchat.common.module.UserInfo;
import cn.tim.xchat.common.module.chat.ChatMsg;
import cn.tim.xchat.common.msg.MsgTypeEnum;
import cn.tim.xchat.common.utils.BeanCopyUtil;
import cn.tim.xchat.common.utils.DateUtils;
import cn.tim.xchat.common.utils.EnumUtil;
import cn.tim.xchat.common.utils.KeyUtil;
import cn.tim.xchat.common.utils.StatusBarUtil;
import cn.tim.xchat.common.utils.ext.UserUtil;
import cn.tim.xchat.common.widget.titlebar.BaseTitleBar;
import cn.tim.xchat.common.widget.titlebar.TitleBarType;

@Route(path = "/chat/main")
public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    @Autowired(required = true)
    FriendInfo friendInfo; // 表示正在和谁对话
    private ChatViewModel chatViewModel;
    private ChatListAdapter chatListAdapter;

    SendMsgAction sendMsgAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ARouter.getInstance().inject(this);
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


        // 填充用户数据
        baseTitleBar.descTv.setText(UserUtil.getUserShowName(friendInfo));

        // 填充聊天信息
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);


        chatListAdapter = new ChatListAdapter(this, msgListRv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        msgListRv.setLayoutManager(linearLayoutManager);
        chatListAdapter.setDataSource(convertMsgDataToVO());
        msgListRv.setAdapter(chatListAdapter);


        sendMsgAction = new SendMsgActionImpl();
//
//        List<ChatMsgVO> chatMsgVOS = convertMsgDataToVO();
//        List<ChatMsg> chatMsgList = chatViewModel.msgList.get(friendInfo.getUserId());
//        if(chatMsgList == null){
////            ArrayList<ChatMsg> tmpMsgList = new ArrayList<>();
////            tmpMsgList.add()
//            chatViewModel.msgList.put(friendInfo.getUserId(),
//                    LitePal.where("sendUserId = ? and acceptUserId = ?",
//                    friendInfo.getUserId(), UserUtil.get().getId())
//                    .find(ChatMsg.class));
//    }

        chatViewModel.needRefreshMsg.observe(this, needRefresh -> {
            // 增量更新
            updateListByMsgSign(chatListAdapter);
        });


        initClickListener();


        testOnData();


        msgListRv.setLayoutManager(linearLayoutManager);
    }

    private void testOnData() {
        ChatMsg chatMsg = new ChatMsg();
        chatMsg.setItemId(KeyUtil.genUniqueKey());
        chatMsg.setType(MsgTypeEnum.TEXT.getCode());
        chatMsg.setSendUserId(UserUtil.get().getId());
        chatMsg.setAcceptUserId("213950848");
        chatMsg.setSignFlag(MsgSignEnum.READ.getCode());
        chatMsg.setCreateTime(Instant.now().toEpochMilli());
        chatMsg.setMsg("Instant.now().toEpochMilli()");
//        chatMsg("Instant.now().toEpochMilli()");

        chatListAdapter.addData(0, _convertMsgData(chatMsg));
    }

    private void initClickListener() {
        sendBtn.setOnClickListener(v -> {
            sendBaseMsg();
        });


        baseTitleBar.backBtn.setOnClickListener(v -> finish());
    }


    // update by webSocket
    private void updateListByMsgSign(ChatListAdapter chatListAdapter) {
        List<ChatMsg> chatMsgList = LitePal.where("sendUserId = ? and acceptUserId = ? and signFlag = ?",
                friendInfo.getUserId(), UserUtil.get().getId(), String.valueOf(MsgSignEnum.UN_READ.getCode()))
                .find(ChatMsg.class);
        for(ChatMsg chatMsg: chatMsgList){
            // 状态置为已读
            ChatMsg findRet = LitePal.where("itemId=?", chatMsg.getItemId()).findFirst(ChatMsg.class);
            if(findRet != null){
                findRet.setSignFlag(MsgSignEnum.READ.getCode());
                findRet.save();
                // FIXME msg order
                chatListAdapter.addData(0, _convertMsgData(chatMsg));
            }else {
                throw new RuntimeException("db error!!");
            }
        }


//        chatListAdapter.addData(_convertMsgData(chatMsgList));
    }

    // init onCreate
    private List<ChatMsgVO> convertMsgDataToVO() {
        List<ChatMsgVO> voRet = new ArrayList<>();
        List<ChatMsg> chatMsgList = LitePal.where("sendUserId = ? and acceptUserId = ?",
                friendInfo.getUserId(), UserUtil.get().getId())
                .find(ChatMsg.class);
        if(chatMsgList.isEmpty()) return voRet;
        ChatMsgVO chatMsgVO;
        for(ChatMsg chatMsg: chatMsgList){
            chatMsgVO = _convertMsgData(chatMsg);
            Log.i(TAG, "convertMsgDataToVO: chatMsgVO = " + chatMsgVO);
            voRet.add(chatMsgVO);

            // 状态置为已读
            ChatMsg findRet = LitePal.where("itemId=?", chatMsg.getItemId()).findFirst(ChatMsg.class);
            if(findRet != null){
                findRet.setSignFlag(MsgSignEnum.READ.getCode());
                findRet.save();
            }else {
                throw new RuntimeException("db error!!");
            }
        }

        return voRet;
    }


    // text msg type
    private void sendBaseMsg() {
        String msgText = sendEdit.getText().toString();
        Log.i(TAG, "sendBaseMsg: msgText = " + msgText);
        boolean sendRet = sendMsgAction.sendTextMsg(friendInfo.getUserId(), msgText, null);

        ChatMsg chatMsg = new ChatMsg(); // FIXME ID从服务器确认
        chatMsg.setItemId(KeyUtil.genUniqueKey());
        chatMsg.setType(MsgTypeEnum.TEXT.getCode());
        chatMsg.setSendUserId( friendInfo.getUserId());
        chatMsg.setAcceptUserId(UserUtil.get().getId());
        chatMsg.setSignFlag(MsgSignEnum.UN_READ.getCode());
        chatMsg.setCreateTime(Instant.now().toEpochMilli());
        chatMsg.setMsg(msgText);
        chatMsg.save();

        chatListAdapter.addData(chatListAdapter.getItemCount(), _convertMsgData(chatMsg));

//        ChatMsg newChatMsg = new ChatMsg();
//        try {
//            BeanCopyUtil.copyProperties(chatMsg, newChatMsg);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        chatMsg.setItemId(KeyUtil.genUniqueKey());
//        chatMsg.setSendUserId(friendInfo.getUserId());
//        chatMsg.setAcceptUserId(UserUtil.get().getId());
//        chatMsg.setCreateTime(Instant.now().toEpochMilli() + 6000);
//        chatMsg.save();
//
//        chatListAdapter.addData(chatListAdapter.getItemCount(), _convertMsgData(chatMsg));
    }

    /*
     * 自我头像需要处理
     */
    private ChatMsgVO _convertMsgData(ChatMsg chatMsg) {
        if(chatMsg == null) return null;
        ChatMsgVO chatMsgVO = new ChatMsgVO();
        chatMsgVO.setMsgId(chatMsg.getItemId());
        chatMsgVO.setInstant(chatMsg.getCreateTime());
        chatMsgVO.setSign(EnumUtil.getByCode(chatMsg.getSignFlag(), MsgSignEnum.class));

        chatMsgVO.setMsgTypeEnum(EnumUtil.getByCode(chatMsg.getType(), MsgTypeEnum.class));
        chatMsgVO.setSenderUserId(chatMsg.getSendUserId());
        UserInfo userInfo = UserUtil.get();
        chatMsgVO.setMe(chatMsg.getSendUserId().equals(userInfo.getId()));

        chatMsgVO.setText(chatMsg.getMsg());
        if(chatMsgVO.isMe()){
            chatMsgVO.setSenderAvatar(userInfo.getUsername());
        }else {
            // 多次查询相同数据
//            chatMsgVO.setSenderAvatar(UserUtil.getUserShowName(LitePal.where("sendUserId =?",
//                    chatMsg.getSendUserId()).findFirst(FriendInfo.class)));
            chatMsgVO.setSenderAvatar(UserUtil.getUserShowName(friendInfo));
        }

        chatMsgVO.setTime(DateUtils.formatDate(Instant.ofEpochMilli(chatMsg.getCreateTime())));
        Log.i(TAG, "_convertMsgData: chatMsg = " + chatMsg);
        Log.i(TAG, "_convertMsgData: chatMsgVO = " + chatMsgVO);
        return chatMsgVO;
    }


    private KPSwitchPanelLinearLayout panelRoot;
    private EditText sendEdit;
    private ImageView addBtn;
    private RecyclerView msgListRv;
    private BaseTitleBar baseTitleBar;
    private MaterialButton sendBtn;

    private void findView() {
        msgListRv = findViewById(R.id.chat_msg_rv);
        panelRoot = findViewById(R.id.chat_panel_root);
        sendEdit = findViewById(R.id.send_img_bar_content_et);
        addBtn = findViewById(R.id.send_img_bar_add_iv);
        baseTitleBar = findViewById(R.id.chat_msg_titlebar);
        baseTitleBar.autoChangeByType(TitleBarType.MESSAGE_CHAT_PAGER);
        sendBtn = findViewById(R.id.send_img_bar_send_btn);
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