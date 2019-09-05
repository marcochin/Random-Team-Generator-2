package com.marcochin.teamrandomizer.di;

import android.app.Application;

import com.marcochin.teamrandomizer.BaseApplication;
import com.marcochin.teamrandomizer.di.viewmodelfactory.ViewModelFactoryModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        AppModule.class,
        ActivityBuildersModule.class,
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
