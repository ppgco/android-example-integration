package com.example.ppgandroidexample.domain.use_case.home

import retrofit2.HttpException
import com.example.ppgandroidexample.common.Resource
import com.example.ppgandroidexample.domain.repository.HomeScreenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class RegisterSubscriberUC @Inject constructor(
    private val repository: HomeScreenRepository
) {
    operator fun invoke(): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            val registration = repository.registerSubscriber()
            emit(Resource.Success(registration))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server - Check your Internet connection."))
        }
    }
}