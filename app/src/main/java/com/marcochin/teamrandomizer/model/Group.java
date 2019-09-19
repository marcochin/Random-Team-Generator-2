package com.marcochin.teamrandomizer.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "group_table")
public class Group {

    @PrimaryKey(autoGenerate = true)
    private int id;

    /**
     * The name of the group of players
     */
    private String name;

    /**
     * The csv of player names
     */
    private String players;

    /**
     * Time list was updated, so we can sort by updated_at desc
     */
    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    public Group(String name, String players, long updatedAt) {
        this.name = name;
        this.players = players;
        this.updatedAt = updatedAt;
    }

    // Id should not go in constructor because it is auto-generated. If it was in constructor
    // we would have no way to create the object correctly ourselves. We have a setter for
    // id that is only used by Room to create the obj.
    public void setId(int id) {
        this.id = id;
    }

    // Have getter methods so Room has a way of retrieving the values and storing them in db.
    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPlayers(String players) {
        this.players = players;
    }

    public String getPlayers() {
        return players;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
