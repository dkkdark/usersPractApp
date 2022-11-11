package com.kseniabl.userstasks.di

import com.kseniabl.userstasks.utils.UserTokenDataStore
import com.kseniabl.userstasks.utils.UserTokenDataStoreInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSaveModule {

    @Singleton
    @Binds
    abstract fun bindSaveUser(userSave: UserTokenDataStore): UserTokenDataStoreInterface
}