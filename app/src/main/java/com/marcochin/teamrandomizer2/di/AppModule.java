package com.marcochin.teamrandomizer2.di;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.room.Room;

import com.marcochin.teamrandomizer2.database.GroupDao;
import com.marcochin.teamrandomizer2.database.GroupDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
abstract class AppModule {
    @Singleton
    @Provides
    static GroupDatabase providePlayersGroupDatabase(Application application) {
        return Room.databaseBuilder(
                application,
                GroupDatabase.class,
                GroupDatabase.DATABASE_NAME)
                .build();
    }

    @Singleton
    @Provides
    // NO ABSTRACT. Even though this is an interface. We don't use @Binds because we need the PlayersGroupDao
    // that generated from the database obj. This is because we want the db obj to auto gen and fill
    // in the abstract methods.
    static GroupDao providePlayersGroupDao(GroupDatabase playersDatabase){
        return playersDatabase.getPlayersGroupDao();
    }

    // DefaultSharedPrefs is already a singleton;
    @Provides
    static SharedPreferences provideSharedPreferences(Application application){
        return PreferenceManager.getDefaultSharedPreferences(application);
    }
}
