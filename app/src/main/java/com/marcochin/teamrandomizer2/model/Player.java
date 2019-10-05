package com.marcochin.teamrandomizer2.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Player implements Parcelable {
    private boolean checkboxVisible;
    private boolean included;
    private String name;

    public Player(String name) {
        this.name = name;
        this.included = true; // Player is included by default
    }

    public boolean isCheckboxVisible() {
        return checkboxVisible;
    }

    public void setCheckboxVisible(boolean checkboxVisible) {
        this.checkboxVisible = checkboxVisible;
    }

    public boolean isIncluded() {
        return included;
    }

    public void setIncluded(boolean included) {
        this.included = included;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    // ---------------------------------------------------------------------------
    // Parcelable implementation

    protected Player(Parcel in) {
        checkboxVisible = in.readByte() != 0;
        included = in.readByte() != 0;
        name = in.readString();
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (checkboxVisible ? 1 : 0));
        parcel.writeByte((byte) (included ? 1 : 0));
        parcel.writeString(name);
    }
}
