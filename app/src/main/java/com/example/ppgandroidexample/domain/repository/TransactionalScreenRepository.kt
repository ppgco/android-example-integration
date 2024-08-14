package com.example.ppgandroidexample.domain.repository

import com.example.ppgandroidexample.data.remote.dto.MessageIdDTO
import com.example.ppgandroidexample.data.remote.dto.SubscribersWithGivenExternalIdDTO
import com.example.ppgandroidexample.data.remote.dto.SuccessDTO

interface TransactionalScreenRepository {
    suspend fun sendNotificationBySubscriberId(): MessageIdDTO
    suspend fun sendNotificationByExternalId(): MessageIdDTO
    suspend fun getAllSubscribersWithGivenExternalId(externalId: String): SubscribersWithGivenExternalIdDTO
    suspend fun assignExternalIdToSubscriber(
        externalId: String
    ): SubscribersWithGivenExternalIdDTO

    suspend fun forgetExternalIdFromAllSubscribers(externalId: String): SuccessDTO
    suspend fun unassignSubscriberIdFromExternalId(externalId: String): SuccessDTO
}