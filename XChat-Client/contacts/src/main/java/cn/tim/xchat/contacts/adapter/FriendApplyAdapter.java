package cn.tim.xchat.contacts.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.tim.xchat.common.module.FriendRequest;

public class FriendApplyAdapter extends BaseFriendAdapter<FriendRequest> {
    public FriendApplyAdapter(Context context, RecyclerView recyclerView) {
        super(context, recyclerView);
    }


    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.applyBtn.setOnClickListener(v -> {
            if(this.applyClickListener != null) applyClickListener.onItemClickPass(position);
        });

        holder.refuseBtn.setOnClickListener(v -> {
            if(this.applyClickListener != null) applyClickListener.onItemClickRefuse(position);
        });
    }

    private OnApplyClickListener applyClickListener;
    public interface OnApplyClickListener {
        void onItemClickPass(int position);
        void onItemClickRefuse(int position);
    }

    public void setApplyClickListener(OnApplyClickListener applyClickListener) {
        this.applyClickListener = applyClickListener;
    }
}