package com.marcochin.teamrandomizer.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.marcochin.teamrandomizer.model.Group;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface GroupDao {

    @Insert
    Single<Long> insert(Group group);

    @Delete
    Single<Integer> delete(Group group);

    @Update
    Single<Integer> update(Group group);

    @Query("SELECT * FROM group_table")
    LiveData<List<Group>> getAllGroups();
}
