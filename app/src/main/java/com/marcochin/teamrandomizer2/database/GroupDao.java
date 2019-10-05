package com.marcochin.teamrandomizer2.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.marcochin.teamrandomizer2.model.Group;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface GroupDao {

    @Insert
    Single<Long> insert(Group group); // Returns id

    @Delete
    Single<Integer> delete(Group group); // Returns rows affected

    @Update
    Single<Integer> update(Group group); // Returns rows affected

    @Query("SELECT * FROM group_table ORDER BY updated_at DESC LIMIT 1")
    LiveData<Group> getMostRecentGroup();

    @Query("SELECT * FROM group_table WHERE name = ''")
    LiveData<Group> getTheNewGroup();

    @Query("SELECT * FROM group_table WHERE name != '' ORDER BY updated_at DESC")
    LiveData<List<Group>> getAllGroups();
}
