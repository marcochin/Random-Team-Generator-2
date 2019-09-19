package com.marcochin.teamrandomizer.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.marcochin.teamrandomizer.model.Group;

@Database(entities = {Group.class}, version = 1)
public abstract class GroupDatabase extends RoomDatabase {
    public static String DATABASE_NAME = "group_db";
    public static String NEW_GROUP_NAME = "";

    public abstract GroupDao getPlayersGroupDao();
}
