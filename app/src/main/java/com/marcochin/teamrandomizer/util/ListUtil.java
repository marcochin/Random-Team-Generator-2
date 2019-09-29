package com.marcochin.teamrandomizer.util;

import android.content.Context;
import android.util.DisplayMetrics;

import com.marcochin.teamrandomizer.model.Player;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {
    public static final String NAME_SEPARATOR = ",";

    public static ArrayList<Player> csvToPlayerList(String csvNames){
        ArrayList<Player> playerList = new ArrayList<>();

        if(csvNames != null && !csvNames.isEmpty()){
            String[] names = csvNames.split(NAME_SEPARATOR);
            for (String name: names){
                playerList.add(new Player(name));
            }
        }

        return playerList;
    }

    public static String playerListToCsv(List<Player> playerList){
        if(playerList == null || playerList.isEmpty()){
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Player player : playerList){
            sb.append(player.getName());
            sb.append(NAME_SEPARATOR);
        }

        return sb.toString();
    }

    /**
     * Use to calculate the number of columns for the GridLayoutManager to fill the width of the screen
     * https://stackoverflow.com/a/38472370/5673746
     * @param context Context
     * @param columnWidthPx - Width of the grid item in px
     * @return Numbers of columns to fill width of screen
     */
    public static int calculateGridColumns(Context context, float columnWidthPx) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int)(displayMetrics.widthPixels / columnWidthPx);
    }
}
