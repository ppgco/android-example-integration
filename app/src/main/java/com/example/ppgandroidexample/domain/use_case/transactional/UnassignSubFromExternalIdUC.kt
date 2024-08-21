package com.example.ppgandroidexample.domain.use_case.transactional

import com.example.ppgandroidexample.common.Resource
import com.example.ppgandroidexample.data.remote.dto.SuccessDTO
import com.example.ppgandroidexample.domain.repository.TransactionalScreenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class UnassignSubFromExternalIdUC @Inject constructor(
    private val repository: TransactionalScreenRepository
) {
    operator fun invoke(): Flow<Resource<SuccessDTO>> = flow {
        try {
            emit(Resource.Loading())
            val unassignExternalId = repository.unassignSubscriberIdFromExternalId()
            emit(Resource.Success(unassignExternalId))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server - Check your Internet connection."))
        }
    }
}