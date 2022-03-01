package cn.tim.xchat.contacts.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import cn.tim.xchat.common.enums.business.RequestFriendEnum;
import cn.tim.xchat.common.module.FriendInfo;
import cn.tim.xchat.common.module.FriendObtain;
import cn.tim.xchat.common.module.FriendRequest;
import cn.tim.xchat.common.utils.EnumUtil;
import cn.tim.xchat.contacts.R;

public class BaseFriendAdapter<T extends FriendObtain>
        extends RecyclerView.Adapter<BaseFriendAdapter.BaseViewHolder> {

    protected final Context context;
    protected final RecyclerView recyclerView;
    protected List<T> dataSource;
    protected OnItemClickListener listener;

    public BaseFriendAdapter(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.dataSource = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataSource(List<T> dataSource) {
        this.dataSource = dataSource;
        notifyDataSetChanged();
    }

    public List<T> getDataSource() {
        return dataSource;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_contact_base_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        FriendRequest request;

        T itemData = dataSource.get(position);
        if(itemData instanceof FriendRequest) {
            request = (FriendRequest) itemData;
            int argeeState = request.getArgeeState();

            if(request.getIsMyRequest() == 1){
                // 我需要处理的
                if(argeeState == RequestFriendEnum.UNHAND.getCode()) {
                    holder.applyBtn.setVisibility(View.VISIBLE);
                    holder.refuseBtn.setVisibility(View.VISIBLE);
                    holder.optResultTv.setVisibility(View.GONE);
                }else {
                    holder.applyBtn.setVisibility(View.GONE);
                    holder.refuseBtn.setVisibility(View.GONE);
                    holder.optResultTv.setText(
                            Objects.requireNonNull(EnumUtil.getByCode(request.getArgeeState(),
                                    RequestFriendEnum.class)).getDesc());
                }
            }else if(request.getIsMyRequest() == 0){
                // 对方处理的
                holder.applyBtn.setVisibility(View.GONE);
                holder.refuseBtn.setVisibility(View.GONE);
                holder.optResultTv.setText(
                        Objects.requireNonNull(EnumUtil.getByCode(request.getArgeeState(),
                                RequestFriendEnum.class)).getDesc()
                );
            }
        }else if(itemData instanceof FriendInfo) {
            holder.applyBtn.setVisibility(View.GONE);
            holder.refuseBtn.setVisibility(View.GONE);
            holder.optResultTv.setVisibility(View.GONE);
        }

        String notesName = itemData.getNotes(); // 备注名
        String nickname = itemData.getNickname(); // 昵称
        String username = itemData.getUsername(); // 用户名

        String showName = TextUtils.isEmpty(notesName)?
                (TextUtils.isEmpty(nickname)? username : nickname): notesName;

        holder.usernameTv.setText(showName);
        holder.headerIv.setTextAndColor(itemData.getUsername().substring(0, 1), Color.BLACK);
        Glide.with(context)
                .load(itemData.getFaceImage())
                .centerCrop()
                .into(holder.headerIv);
        holder.itemView.setOnClickListener(v -> {
                    if (listener != null) listener.onItemClick(position);
                }
        );
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    // 添加一条数据
    public void addData(int position, T friendInfo) {
        dataSource.add(position, friendInfo);
        notifyItemInserted(position);
        // 刷新ItemView
        notifyItemRangeChanged(position, dataSource.size() - position);
    }

    // 删除一条数据
    public void removeData(int position) {
        dataSource.remove(position);
        notifyItemRemoved(position);

        // 刷新ItemView
        notifyItemRangeChanged(position, dataSource.size() - position);
    }

    // 刷新一条数据
    public void updateOneData(int position) {
        notifyItemRangeChanged(position, 1);
    }

    static class BaseViewHolder extends RecyclerView.ViewHolder {
        AvatarImageView headerIv;
        TextView usernameTv;
        TextView optResultTv;
        TextView applyBtn;
        TextView refuseBtn;
        public BaseViewHolder( View itemView) {
            super(itemView);
            headerIv = itemView.findViewById(R.id.contact_item_header_iv);
            usernameTv = itemView.findViewById(R.id.contact_item_nickname_tv);
            optResultTv = itemView.findViewById(R.id.contact_item_opt_result_tv);
            applyBtn = itemView.findViewById(R.id.contact_item_apply_btn);
            refuseBtn = itemView.findViewById(R.id.contact_item_refuse_btn);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
