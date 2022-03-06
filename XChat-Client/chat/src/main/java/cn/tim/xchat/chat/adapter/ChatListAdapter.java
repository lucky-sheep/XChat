package cn.tim.xchat.chat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;

import org.litepal.LitePal;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import cn.tim.xchat.chat.vo.ChatMsgVO;
import cn.tim.xchat.chat.R;
import cn.tim.xchat.common.module.FriendInfo;
import cn.tim.xchat.common.msg.MsgTypeEnum;
import cn.tim.xchat.common.utils.DateUtils;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private final Context context;
    private final RecyclerView recyclerView;
    private List<ChatMsgVO> dataSource;
    private OnItemClickListener listener;

    private String[] avatarKey = {"http://", "file://", "https://"};

    public ChatListAdapter(Context context, RecyclerView recyclerView){
        this.context = context;
        this.recyclerView = recyclerView;
        this.dataSource = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataSource(List<ChatMsgVO> dataSource) {
        this.dataSource = dataSource;
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_msg_item,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMsgVO chatMsgVO = dataSource.get(position);
        if(chatMsgVO.isMe()) {
            holder.myLayout.setVisibility(View.VISIBLE);
            holder.friendLayout.setVisibility(View.GONE);
        }else {
            holder.myLayout.setVisibility(View.GONE);
            holder.friendLayout.setVisibility(View.VISIBLE);
        }

        // ========================================
        // 处理头像
        handleAvatar(holder.avatarIv, chatMsgVO);

        // msg
        handleMsgByType(holder.msgRegionRl, chatMsgVO);

        // time
        handleMsgTime(holder.dataTimeTv, chatMsgVO, position);

        // sign
        handleSign(holder.msgStateTv, chatMsgVO);


        // ========================================
        holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
    }

    private void handleSign(TextView msgStateTv, ChatMsgVO chatMsgVO) {
        msgStateTv.setText(chatMsgVO.getSign().getDesc());
    }

    private void handleMsgTime(TextView dataTimeTv, ChatMsgVO chatMsgVO, int position) {
        if(position > 0) {
            // 上条消息时间 // TODO time order
            ChatMsgVO chatMsgOld = dataSource.get(position - 1);
            if(chatMsgVO.getInstant() - chatMsgOld.getInstant() > 60 * 1000 * 5) { // 5min
                dataTimeTv.setVisibility(View.VISIBLE);
                dataTimeTv.setText(DateUtils.formatDate(Instant.ofEpochMilli(chatMsgVO.getInstant())));
            }else {
                dataTimeTv.setVisibility(View.GONE);
            }
        }else {
            dataTimeTv.setVisibility(View.VISIBLE);
            dataTimeTv.setText(DateUtils.formatDate(Instant.ofEpochMilli(chatMsgVO.getInstant())));
        }
    }

    private void handleMsgByType(RelativeLayout layoutPosition, ChatMsgVO chatMsgVO) {
        MsgTypeEnum msgTypeEnum = chatMsgVO.getMsgTypeEnum();
        if(msgTypeEnum.equals(MsgTypeEnum.TEXT)){
            TextView msgTv = new TextView(context);
            msgTv.setTextColor(Color.BLACK);
            msgTv.setText(chatMsgVO.getText());
//            ViewGroup.LayoutParams layoutParams = textView.getLayoutParams();
//            layoutParams.width =

            layoutPosition.addView(msgTv);
        }else if(msgTypeEnum.equals(MsgTypeEnum.ADDRESS)){
            // TODO
        }else {
            // TODO
        }
    }


    private void handleAvatar(AvatarImageView avatarIv, ChatMsgVO chatMsgVO) {
        String senderAvatar = chatMsgVO.getSenderAvatar();
        boolean useNameChar = true;
        for (String s : avatarKey) {
            if (senderAvatar.startsWith(s)) {
                useNameChar = false;
                break;
            }
        }

        if(useNameChar){
            avatarIv.setTextAndColor(senderAvatar.substring(0, 1), Color.BLACK);
        }else {
            Glide.with(context)
                    .load(senderAvatar)
                    .centerCrop()
                    .into(avatarIv);
        }

        avatarIv.setOnClickListener(v -> {
            if(chatMsgVO.isMe()){
                ARouter.getInstance()
                        .build("/personal/detail")
                        .navigation(context);
            }else {
                ARouter.getInstance()
                        .build("/contacts/detail")
                        .withObject("friendInfo",
                                LitePal.where("userId = ?", chatMsgVO.getSenderUserId())
                                        .findFirst(FriendInfo.class))
                        .navigation();
            }
        });

    }

    private int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    // 返回数据数量
    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    // 添加一条数据
    public void addData (int position, ChatMsgVO chatMsgVO) {
        dataSource.add(position, chatMsgVO);
        notifyItemInserted(position);
        // 刷新ItemView
        notifyItemRangeChanged(position, dataSource.size() - position);
    }

    public void addData(List<ChatMsgVO> chatMsgVOList){
        dataSource.addAll(chatMsgVOList);
        // FIXME msg order
        notifyItemRangeChanged(0, chatMsgVOList.size());
    }

    // 删除一条数据
    public void removeData (int position) {
        dataSource.remove(position);
        notifyItemRemoved(position);

        // 刷新ItemView
        notifyItemRangeChanged(position, dataSource.size() - position);
    }

    /*
     *  private String msgId;
     *  private String text;
     *  private String senderAvatar; // 不是 http://xxxxx, https://xxxx, file:// 那么就是用文字头像
     *  private String time;
     *  private MsgSignEnum sign; // 已读/未读 -> 签收状态
     *  private boolean isMe;
     *  private MsgTypeEnum msgTypeEnum;
     */
    static class ChatViewHolder extends RecyclerView.ViewHolder {
        AvatarImageView avatarIv;
        RelativeLayout msgRegionRl; // addView
        TextView dataTimeTv;
        TextView msgStateTv;
        RelativeLayout myLayout;
        RelativeLayout friendLayout;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.msg_item_avatar_iv);
            msgRegionRl = itemView.findViewById(R.id.msg_item_msg_region);
            dataTimeTv = itemView.findViewById(R.id.msg_item_datetime_tv);
            msgStateTv = itemView.findViewById(R.id.msg_item_state_tv);
            myLayout = itemView.findViewById(R.id.msg_item_my_layout);
            friendLayout = itemView.findViewById(R.id.msg_item_friend_layout);
        }
    }

    interface OnItemClickListener {
        void onItemClick(int position);
    }
}