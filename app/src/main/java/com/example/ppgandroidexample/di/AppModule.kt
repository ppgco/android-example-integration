package com.example.ppgandroidexample.di

import android.util.Log
import com.example.ppgandroidexample.common.PPGEnvironment
import com.example.ppgandroidexample.common.PPGMetaData
import com.example.ppgandroidexample.data.remote.PPGTransactionalAPI
import com.example.ppgandroidexample.data.repository.HomeScreenRepositoryImplementation
import com.example.ppgandroidexample.data.repository.LiveActivitiesRepositoryImplementation
import com.example.ppgandroidexample.data.repository.TransactionalScreenRepositoryImplementation
import com.example.ppgandroidexample.domain.repository.HomeScreenRepository
import com.example.ppgandroidexample.domain.repository.LiveActivitiesRepository
import com.example.ppgandroidexample.domain.repository.TransactionalScreenRepository
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

/**
 * Logs the response body of failed transactional API calls. Retrofit turns them
 * into an `HttpException` whose message is only "HTTP 400 Bad Request", which
 * hides the validation details the API actually returns.
 */
class ErrorLoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (!response.isSuccessful) {
            // peekBody leaves the body readable for Retrofit downstream
            val body = runCatching { response.peekBody(PEEK_LIMIT_BYTES).string() }.getOrNull()
            Log.w(
                "PPGTransactional",
                "${response.code} ${chain.request().method} ${chain.request().url}: $body"
            )
        }
        return response
    }

    private companion object {
        const val PEEK_LIMIT_BYTES = 8192L
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
    fun provideTransactionalScreenRepository(api: PPGTransactionalAPI): TransactionalScreenRepository {
        return TransactionalScreenRepositoryImplementation(api)
    }

    @Provides
    @Singleton
    fun provideLiveActivitiesRepository(): LiveActivitiesRepository {
        return LiveActivitiesRepositoryImplementation()
    }

    @Provides
    @Singleton
    fun providePPGTransactionalAPI(): PPGTransactionalAPI {
        val apiKey = PPGMetaData.getApiKey()
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(apiKey))
            .apply {
                if (!PPGEnvironment.IS_PRODUCTION) addInterceptor(ErrorLoggingInterceptor())
            }
            .build()
        return Retrofit.Builder()
            // Same environment as both SDKs - the API key is environment-specific
            .baseUrl(PPGEnvironment.apiBaseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PPGTransactionalAPI::class.java)
    }
}