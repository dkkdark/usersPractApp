package com.kseniabl.userstasks.di

import com.kseniabl.userstasks.network.Repository
import com.kseniabl.userstasks.network.RetrofitApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun provideBaseUrl(): String = "http:///10.0.2.2"
        //"http://192.168.1.64"

    @Provides
    @Singleton
    fun provideRetrofit(BASE_URL : String) : RetrofitApi = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()
        .create(RetrofitApi::class.java)

    @Provides
    @Singleton
    fun provideMainRemoteData(mainService : RetrofitApi) : Repository = Repository(mainService)

}