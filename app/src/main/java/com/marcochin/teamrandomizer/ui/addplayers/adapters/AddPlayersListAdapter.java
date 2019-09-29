package com.marcochin.teamrandomizer.ui.addplayers.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.marcochin.teamrandomizer.R;
import com.marcochin.teamrandomizer.model.Player;

public class AddPlayersListAdapter extends ListAdapter<Player, AddPlayersListAdapter.PlayerHolder> {
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, Player player);

        void onDeleteClick(int position, Player player);
    }

    private static final DiffUtil.ItemCallback<Player> DIFF_CALLBACK = new DiffUtil.ItemCallback<Player>() {
        @Override
        public boolean areItemsTheSame(@NonNull Player oldItem, @NonNull Player newItem) {
            // These Player items aren't coming from the db, so they don't have an id associated
            // w them.
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Player oldItem, @NonNull Player newItem) {
            return oldItem.getName().equals(newItem.getName())
                    && oldItem.isIncluded() == newItem.isIncluded()
                    && oldItem.isCheckboxVisible() == newItem.isCheckboxVisible();
        }
    };

    public AddPlayersListAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public PlayerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
        return new PlayerHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerHolder holder, int position) {
        Player player = getItem(position);

        if (player.isCheckboxVisible()) {
            holder.checkbox.setVisibility(View.VISIBLE);

            if (player.isIncluded()) {
                holder.checkbox.setChecked(true);
            } else {
                holder.checkbox.setChecked(false);
            }
        } else {
            holder.checkbox.setVisibility(View.GONE);
        }

        holder.playerNameText.setText(player.getName());
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    class PlayerHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;
        TextView playerNameText;
        ViewGroup itemContainer;
        ViewGroup deleteContainer;

        PlayerHolder(@NonNull View itemView) {
            super(itemView);

            itemContainer = itemView.findViewById(R.id.ip_item_container);
            deleteContainer = itemView.findViewById(R.id.ip_del_player_container);
            checkbox = itemView.findViewById(R.id.ip_checkbox);
            playerNameText = itemView.findViewById(R.id.ip_player_name_text);

            checkbox.setOnClickListener(checkBoxOnClickListener);
            itemContainer.setOnClickListener(checkBoxOnClickListener);

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

        private View.OnClickListener checkBoxOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                if (mOnItemClickListener != null && position != RecyclerView.NO_POSITION) {
                    mOnItemClickListener.onItemClick(position, getItem(position));
                }
            }
        };
    }
}
