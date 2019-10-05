package com.marcochin.teamrandomizer2.util;

import com.marcochin.teamrandomizer2.model.Group;

public class ValidationUtil {
    public static boolean validatePlayerName(String playerName) {
        return playerName != null && !playerName.trim().isEmpty() && !playerName.contains(ListUtil.NAME_SEPARATOR);
    }

    public static boolean validateGroupName(String groupName) {
        return groupName != null && !groupName.trim().isEmpty() && !groupName.trim().equals(Group.NEW_GROUP_NAME);
    }
}
