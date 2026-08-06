package com.example.ppgandroidexample.domain.use_case.liveactivities

import com.example.ppgandroidexample.common.Resource
import com.example.ppgandroidexample.domain.repository.LiveActivitiesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class UnsubscribeFromLiveActivityUC @Inject constructor(
    private val repository: LiveActivitiesRepository
) {
    operator fun invoke(liveNotificationId: String): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading())
            emit(Resource.Success(repository.unsubscribe(liveNotificationId)))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (_: IOException) {
            emit(Resource.Error("Couldn't reach server - Check your Internet connection."))
        } catch (e: Exception) {
            // Also covers "not subscribed to this live notification"
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
}
