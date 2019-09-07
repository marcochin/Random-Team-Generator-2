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
    private OnItemClickListenerListener mOnItemClickListener;

    public interface OnItemClickListenerListener {
        void onCheckboxClick();
        void onDeleteClick();
    }

    private static final DiffUtil.ItemCallback<Player> DIFF_CALLBACK = new DiffUtil.ItemCallback<Player>(){
        @Override
        public boolean areItemsTheSame(@NonNull Player oldItem, @NonNull Player newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Player oldItem, @NonNull Player newItem) {
            return oldItem.getName().equals(newItem.getName())
                    && oldItem.isIncluded() == newItem.isIncluded();
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

        if(player.isCheckboxVisible()){
            holder.checkbox.setVisibility(View.VISIBLE);

            if(player.isIncluded()){
                holder.checkbox.setSelected(true);
            }else{
                holder.checkbox.setSelected(false);
            }
        }else{
            holder.checkbox.setVisibility(View.INVISIBLE);
            // If checkbox is not visible player is automatically included in the random pool
            player.setIncluded(true);
        }

        holder.playerNameText.setText(player.getName());
    }

    public void setOnItemClickListener(OnItemClickListenerListener onItemClickListener) {
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

            checkboxNameContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mOnItemClickListener != null){
                        mOnItemClickListener.onCheckboxClick();
                    }
                }
            });

            deleteContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onDeleteClick();
                }
            });
        }
    }
}
