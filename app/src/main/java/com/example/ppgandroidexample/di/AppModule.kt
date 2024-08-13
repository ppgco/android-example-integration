package com.example.ppgandroidexample.di

import com.example.ppgandroidexample.common.PPGMetaData
import com.example.ppgandroidexample.data.remote.PPGTransactionalAPI
import com.example.ppgandroidexample.data.repository.HomeScreenRepositoryImplementation
import com.example.ppgandroidexample.domain.repository.HomeScreenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


class AuthInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("X-Token", apiKey)
            .addHeader("Content-Type", "application/json")
            .build()
        return chain.proceed(request)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHomeScreenRepository(api: PPGTransactionalAPI): HomeScreenRepository {
        return HomeScreenRepositoryImplementation(api)
    }

    @Provides
    @Singleton
    fun providePPGTransactionalAPI(): PPGTransactionalAPI {
        val apiKey = PPGMetaData.getApiKey()
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(apiKey))
            .build()
        return Retrofit.Builder()
            .baseUrl("https://api.master1.qappg.co")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PPGTransactionalAPI::class.java)
    }
}