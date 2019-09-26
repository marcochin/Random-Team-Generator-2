package com.marcochin.teamrandomizer.ui.loadgroup.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.marcochin.teamrandomizer.R;
import com.marcochin.teamrandomizer.model.Group;

public class GroupListAdapter extends ListAdapter<Group, GroupListAdapter.GroupHolder> {
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, Group group);

        void onDeleteClick(int position, Group group);
    }

    private static final DiffUtil.ItemCallback<Group> DIFF_CALLBACK = new DiffUtil.ItemCallback<Group>() {
        @Override
        public boolean areItemsTheSame(@NonNull Group oldItem, @NonNull Group newItem) {
            // These Player items aren't coming from the db, so they don't have an id associated
            // w them.
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Group oldItem, @NonNull Group newItem) {
            return oldItem.getName().equals(newItem.getName())
                    && oldItem.getPlayers().equals(newItem.getPlayers())
                    && oldItem.getUpdatedAt() == newItem.getUpdatedAt();
        }
    };

    public GroupListAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupHolder holder, int position) {

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    class GroupHolder extends RecyclerView.ViewHolder {
        ViewGroup itemContainer;
        ViewGroup deleteContainer;

        GroupHolder(@NonNull View itemView) {
            super(itemView);

            itemContainer = itemView.findViewById(R.id.ip_item_container);
            deleteContainer = itemView.findViewById(R.id.ip_del_player_container);

            itemContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (mOnItemClickListener != null && position != RecyclerView.NO_POSITION) {
                        mOnItemClickListener.onItemClick(position, getItem(position));
                    }
                }
            });

            deleteContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (mOnItemClickListener != null && position != RecyclerView.NO_POSITION) {
                        mOnItemClickListener.onDeleteClick(position, getItem(position));
                    }
                }
            });
        }
    }
}
