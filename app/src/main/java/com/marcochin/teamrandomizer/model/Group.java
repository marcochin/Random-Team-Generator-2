package com.marcochin.teamrandomizer.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "group_table")
public class Group implements Parcelable {
    @Ignore
    public static final int NO_ID = 0; // Primary key can't be negative

    @Ignore
    public static String NEW_GROUP_NAME = "";

    @PrimaryKey(autoGenerate = true)
    private int id = NO_ID;

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

    @Ignore
    public Group(int id, String name, String players, long updatedAt) {
        this.id = id;
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


    // ---------------------------------------------------------------------------
    // Parcelable implementation

    protected Group(Parcel in) {
        id = in.readInt();
        name = in.readString();
        players = in.readString();
        updatedAt = in.readLong();
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(players);
        parcel.writeLong(updatedAt);
    }
}
