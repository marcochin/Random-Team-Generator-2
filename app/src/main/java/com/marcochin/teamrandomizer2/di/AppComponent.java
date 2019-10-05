package com.marcochin.teamrandomizer2.di;

import android.app.Application;

import com.marcochin.teamrandomizer2.BaseApplication;
import com.marcochin.teamrandomizer2.di.viewmodelfactory.ViewModelFactoryModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        SubComponentModule.class,
        ViewModelFactoryModule.class
})
public interface AppComponent extends AndroidInjector<BaseApplication> {
    /* AndroidInjector<BaseApplication> creates an inject method for the BaseApplication and injects
       dependencies to it automatically when the AppComponent is used. */

    @Component.Factory
    interface Factory {
        AppComponent create(@BindsInstance Application application);
    }
}
