package com.marcochin.teamrandomizer.ui.loadgroup;

import androidx.lifecycle.ViewModel;

import com.marcochin.teamrandomizer.repository.GroupRepository;

import javax.inject.Inject;

public class LoadGroupViewModel extends ViewModel {
    @Inject
    public LoadGroupViewModel(GroupRepository repository) {
    }
}
