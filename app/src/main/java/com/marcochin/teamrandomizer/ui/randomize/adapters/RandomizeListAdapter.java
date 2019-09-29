package com.marcochin.teamrandomizer.ui.randomize.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marcochin.teamrandomizer.R;
import com.marcochin.teamrandomizer.model.Team;

import java.util.List;

public class RandomizeListAdapter extends RecyclerView.Adapter<RandomizeListAdapter.RandomTeamHolder> {
    private Context mContext;
    private List<Team> mTeamList;

    public RandomizeListAdapter(Context context, List<Team> teamList){
        mTeamList = teamList;
        mContext = context;
    }

    @NonNull
    @Override
    public RandomTeamHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team, parent, false);
        return new RandomTeamHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RandomTeamHolder holder, int position) {
        Team team = mTeamList.get(position);

        holder.teamNumberText.setText(mContext.getString(R.string.ph_team_number, team.getTeamNumber()));

        StringBuilder sb = new StringBuilder();
        List<String> playerNamesList = team.getPlayerNames();
        for (int i = 0; i < playerNamesList.size(); i++){
            String name = playerNamesList.get(i);
            sb.append(mContext.getString(R.string.ph_player_number, i + 1 , name));
        }

        holder.playersText.setText(sb.toString());
    }

    @Override
    public int getItemCount() {
        return mTeamList == null ? 0 : mTeamList.size();
    }

    public void setList(List<Team> teamList){
        mTeamList = teamList;
    }

    class RandomTeamHolder extends RecyclerView.ViewHolder {
        TextView teamNumberText;
        TextView playersText;

        RandomTeamHolder(@NonNull View itemView) {
            super(itemView);

            teamNumberText = itemView.findViewById(R.id.it_team_number_text);
            playersText = itemView.findViewById(R.id.it_players_text);

            // Underlines the teamNumberText
            teamNumberText.setPaintFlags(teamNumberText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
    }
}
