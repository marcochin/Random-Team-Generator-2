package com.marcochin.teamrandomizer2.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.marcochin.teamrandomizer2.model.Group;

@Database(entities = {Group.class}, version = 1)
public abstract class GroupDatabase extends RoomDatabase {
    public static String DATABASE_NAME = "group_db";

    public abstract GroupDao getPlayersGroupDao();
}
