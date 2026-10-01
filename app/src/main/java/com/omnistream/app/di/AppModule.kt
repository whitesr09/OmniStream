package com.omnistream.app.di

import com.omnistream.app.core.plugin.MediaSourcePlugin
import com.omnistream.app.core.plugin.PublicDomainPlugin
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @IntoSet
    @Singleton
    abstract fun bindPublicDomainPlugin(plugin: PublicDomainPlugin): MediaSourcePlugin
}