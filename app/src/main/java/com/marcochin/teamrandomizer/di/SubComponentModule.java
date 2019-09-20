package com.marcochin.teamrandomizer.di;

import com.marcochin.teamrandomizer.di.addplayers.AddPlayersModule;
import com.marcochin.teamrandomizer.ui.addplayers.AddPlayersFragment;
import com.marcochin.teamrandomizer.ui.addplayers.dialogs.SaveGroupDialog;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class SubComponentModule {
    // @ContributesAndroidInjector automatically generates subcomponents for our classes
    // and auto injects them. Make sure you extend DaggerActivity or DaggerFragment for this to happen.

    // Activities
    // None yet

    // Fragments
    @ContributesAndroidInjector(modules = {AddPlayersModule.class})
    abstract AddPlayersFragment contributeAddPlayersFragment ();

    @ContributesAndroidInjector(modules = {AddPlayersModule.class})
    abstract SaveGroupDialog contributeSaveGroupDialog ();
}
