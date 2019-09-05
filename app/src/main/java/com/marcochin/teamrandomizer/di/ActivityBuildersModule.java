package com.marcochin.teamrandomizer.di;

import com.marcochin.teamrandomizer.ui.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ActivityBuildersModule {

    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity ();
}
