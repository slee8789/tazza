package com.leesc.tazza.di.builder;

import com.leesc.tazza.service.WifiService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ServiceBuilder {

    @ContributesAndroidInjector
    abstract WifiService bindNetworkService();

}
