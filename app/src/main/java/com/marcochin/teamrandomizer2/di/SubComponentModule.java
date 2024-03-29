package com.marcochin.teamrandomizer2.di;

import com.marcochin.teamrandomizer2.di.addplayers.AddPlayersModule;
import com.marcochin.teamrandomizer2.di.loadgroup.LoadGroupModule;
import com.marcochin.teamrandomizer2.ui.addplayers.AddPlayersFragment;
import com.marcochin.teamrandomizer2.ui.addplayers.dialogs.numberofteams.NumberOfTeamsDialog;
import com.marcochin.teamrandomizer2.ui.loadgroup.LoadGroupFragment;

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

    @ContributesAndroidInjector(modules = {LoadGroupModule.class})
    abstract LoadGroupFragment contributeLoadGroupFragment ();

    @ContributesAndroidInjector
    abstract NumberOfTeamsDialog contributeNumberOfTeamsDialog ();
}
