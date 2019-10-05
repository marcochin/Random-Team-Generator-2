package com.marcochin.teamrandomizer2.di.loadgroup;

import androidx.lifecycle.ViewModel;

import com.marcochin.teamrandomizer2.di.viewmodelfactory.ViewModelKey;
import com.marcochin.teamrandomizer2.ui.loadgroup.LoadGroupViewModel;

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
