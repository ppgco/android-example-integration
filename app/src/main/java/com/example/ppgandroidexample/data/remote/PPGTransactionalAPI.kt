package com.example.ppgandroidexample.data.remote

import com.example.ppgandroidexample.data.remote.dto.AssignExternalIdDTO
import com.example.ppgandroidexample.data.remote.dto.MessageIdDTO
import com.example.ppgandroidexample.data.remote.dto.SubscribersWithGivenExternalIdDTO
import com.example.ppgandroidexample.data.remote.dto.SuccessDTO
import com.example.ppgandroidexample.data.remote.dto.TransactionalNotificationByExternalIdDTO
import com.example.ppgandroidexample.data.remote.dto.TransactionalNotificationByIdDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface PPGTransactionalAPI {

    @POST("/core/projects/{project}/pushes/transaction")
    suspend fun sendTransactionalPushBySubscriberId(
        @Path("project") projectId: String,
        @Body requestBody: TransactionalNotificationByIdDTO
    ): MessageIdDTO

    @POST("/core/projects/{project}/pushes/transaction/external_id")
    suspend fun sendTransactionalPushByExternalId(
        @Path("project") projectId: String,
        @Body requestBody: TransactionalNotificationByExternalIdDTO
    ): MessageIdDTO

    @GET("/core/projects/{project}/external_ids/{externalId}")
    suspend fun getSubscriberIdsWithGivenExternalId(
        @Path("project") projectId: String,
        @Path("externalId") externalId: String,
    ): SubscribersWithGivenExternalIdDTO

    @POST("/core/projects/{project}/external_ids/assign")
    suspend fun assignExternalIdToSubscriber(
        @Path("project") projectId: String,
        @Body requestBody: AssignExternalIdDTO
    ): SubscribersWithGivenExternalIdDTO

    @DELETE("/core/projects/{project}/external_ids/forgot/{externalId}")
    suspend fun forgetSubscriberIdFromExternalId(
        @Path("project") projectId: String,
        @Path("externalId") externalId: String,
    ): SuccessDTO

    @DELETE("/core/projects/{project}/external_ids/unassign/{subscriberId}")
    suspend fun unassignSubscriberFromCurrentExternalId(
        @Path("project") projectId: String,
        @Path("subscriberId") subscriberId: String,
    ): SuccessDTO
}