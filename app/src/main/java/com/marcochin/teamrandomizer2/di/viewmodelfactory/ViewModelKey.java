package com.marcochin.teamrandomizer2.di.viewmodelfactory;

import androidx.lifecycle.ViewModel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dagger.MapKey;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@MapKey /* @MapKey designates this as a key to map Class -> ViewModel */
public @interface ViewModelKey {
    // Any class that extends ViewModel is the key type
    Class<? extends ViewModel> value();
}
