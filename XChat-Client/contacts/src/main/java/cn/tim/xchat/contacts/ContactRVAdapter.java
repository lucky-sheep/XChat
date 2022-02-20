package cn.tim.xchat.contacts;

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

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import cn.tim.xchat.common.module.FriendInfo;

public class ContactRVAdapter extends RecyclerView.Adapter<ContactRVAdapter.MyViewHolder> {
    private final Context context;
    private final RecyclerView recyclerView;
    private List<FriendInfo> dataSource;
    private OnItemClickListener listener;

    public ContactRVAdapter(Context context, RecyclerView recyclerView){
        this.context = context;
        this.recyclerView = recyclerView;
        this.dataSource = new ArrayList<>();
    }

    public void setDataSource(List<FriendInfo> dataSource) {
        this.dataSource = dataSource;
        notifyDataSetChanged();
    }

    public List<FriendInfo> getDataSource() {
        return dataSource;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_contact_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        holder.imageView
        FriendInfo friendInfo = dataSource.get(position);
        String notes = friendInfo.getNotes(); // 备注名
        String nickname = friendInfo.getNickname(); // 昵称
        String username = friendInfo.getUsername(); // username
        String showName = TextUtils.isEmpty(notes) ?
                (TextUtils.isEmpty(nickname)? username : nickname) : notes;
        holder.textView.setText(showName);

        holder.imageView.setTextAndColor(showName.substring(0, 1), Color.BLACK);
        Glide.with(context)
                .load(friendInfo.getFaceImage())
                .centerCrop()
                .into(holder.imageView);
        holder.itemView.setOnClickListener(v -> {
                if(listener != null) listener.onItemClick(position);
            }
        );
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    // 添加一条数据
    public void addData (int position, FriendInfo friendInfo) {
        dataSource.add(position, friendInfo);
        notifyItemInserted(position);
        // 刷新ItemView
        notifyItemRangeChanged(position, dataSource.size() - position);
    }

    // 删除一条数据
    public void removeData (int position) {
        dataSource.remove(position);
        notifyItemRemoved(position);

        // 刷新ItemView
        notifyItemRangeChanged(position, dataSource.size() - position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        AvatarImageView imageView;
        TextView textView;
        public MyViewHolder( View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.contact_item_header_iv);
            textView = itemView.findViewById(R.id.contact_item_nickname_tv);
        }
    }

    interface OnItemClickListener {
        void onItemClick(int position);
    }
}
