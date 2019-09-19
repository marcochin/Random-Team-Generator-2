package com.marcochin.teamrandomizer.util;

import com.marcochin.teamrandomizer.model.Player;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {
    public static final String NAME_SEPARATOR = ",";

    public static List<Player> csvToPlayerList(String csvNames){
        List<Player> playerList = new ArrayList<>();

        if(csvNames != null){
            String[] names = csvNames.split(NAME_SEPARATOR);
            for (String name: names){
                playerList.add(new Player(name));
            }
        }

        return playerList;
    }

    public static String playerListToCsv(List<Player> playerList){
        if(playerList == null || playerList.isEmpty()){
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (Player player : playerList){
            sb.append(player.getName());
            sb.append(NAME_SEPARATOR);
        }

        return sb.toString();
    }
}
