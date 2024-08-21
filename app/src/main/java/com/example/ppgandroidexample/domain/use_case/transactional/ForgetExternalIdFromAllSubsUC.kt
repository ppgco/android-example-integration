package com.example.ppgandroidexample.domain.use_case.transactional

import retrofit2.HttpException
import com.example.ppgandroidexample.common.Resource
import com.example.ppgandroidexample.data.remote.dto.SuccessDTO
import com.example.ppgandroidexample.domain.repository.TransactionalScreenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class ForgetExternalIdFromAllSubsUC @Inject constructor(
    private val repository: TransactionalScreenRepository
) {
    operator fun invoke(externalId: String): Flow<Resource<SuccessDTO>> = flow {
        try {
            emit(Resource.Loading())
            val forgetExternalId = repository.forgetExternalIdFromAllSubscribers(externalId)
            emit(Resource.Success(forgetExternalId))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server - Check your Internet connection."))
        }
    }
}