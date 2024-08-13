package com.example.ppgandroidexample.data.remote

import com.example.ppgandroidexample.data.remote.dto.TransactionalNotificationDTO
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path


interface PPGTransactionalAPI {

    @POST("/core/projects/{project}/pushes/transaction")
    suspend fun sendTransactionalPushBySubscriberId(
        @Path("project") projectId: String,
        @Body requestBody: TransactionalNotificationDTO
    )
}