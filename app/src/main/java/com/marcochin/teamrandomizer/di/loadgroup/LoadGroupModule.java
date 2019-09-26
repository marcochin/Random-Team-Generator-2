package com.marcochin.teamrandomizer.di.loadgroup;

import androidx.lifecycle.ViewModel;

import com.marcochin.teamrandomizer.di.viewmodelfactory.ViewModelKey;
import com.marcochin.teamrandomizer.ui.loadgroup.LoadGroupViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class LoadGroupModule {
    @Binds
    @IntoMap
    @ViewModelKey(LoadGroupViewModel.class)
    public abstract ViewModel bindLoadGroupViewModel(LoadGroupViewModel viewModel);
}
