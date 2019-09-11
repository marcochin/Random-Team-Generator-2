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

public class PlayerListAdapter extends ListAdapter<Player, PlayerListAdapter.PlayerHolder> {
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onCheckboxClick(int pos, Player player);

        void onDeleteClick(int pos, Player player);
    }

    private static final DiffUtil.ItemCallback<Player> DIFF_CALLBACK = new DiffUtil.ItemCallback<Player>() {
        @Override
        public boolean areItemsTheSame(@NonNull Player oldItem, @NonNull Player newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Player oldItem, @NonNull Player newItem) {
            return oldItem.getName().equals(newItem.getName())
                    && oldItem.isIncluded() == newItem.isIncluded()
                    && oldItem.isCheckboxVisible() == newItem.isCheckboxVisible();
        }
    };

    public PlayerListAdapter() {
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
        ViewGroup checkboxNameContainer;
        ViewGroup deleteContainer;

        PlayerHolder(@NonNull View itemView) {
            super(itemView);

            checkbox = itemView.findViewById(R.id.checkbox);
            playerNameText = itemView.findViewById(R.id.player_name_text);
            checkboxNameContainer = itemView.findViewById(R.id.checkbox_name_container);
            deleteContainer = itemView.findViewById(R.id.del_name_container);

            checkbox.setOnClickListener(checkBoxOnClickListener);
            checkboxNameContainer.setOnClickListener(checkBoxOnClickListener);

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
                    mOnItemClickListener.onCheckboxClick(position, getItem(position));
                }
            }
        };
    }
}
