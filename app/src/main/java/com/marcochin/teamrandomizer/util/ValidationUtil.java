package com.marcochin.teamrandomizer.util;

import com.marcochin.teamrandomizer.database.GroupDatabase;

public class ValidationUtil {
    public static boolean validatePlayerName(String playerName) {
        return playerName != null && !playerName.trim().isEmpty() && !playerName.contains(ListUtil.NAME_SEPARATOR);
    }

    public static boolean validateGroupName(String groupName) {
        return groupName != null && !groupName.trim().isEmpty() && !groupName.trim().equals(GroupDatabase.NEW_GROUP_NAME);
    }
}
