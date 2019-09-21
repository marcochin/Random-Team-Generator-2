package com.marcochin.teamrandomizer.di.addplayers;

import androidx.lifecycle.ViewModel;

import com.marcochin.teamrandomizer.di.viewmodelfactory.ViewModelKey;
import com.marcochin.teamrandomizer.ui.addplayers.AddPlayersViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class AddPlayersModule {
    @Binds
    @IntoMap
    @ViewModelKey(AddPlayersViewModel.class)
    public abstract ViewModel bindAddPlayersViewModel(AddPlayersViewModel viewModel);
}
