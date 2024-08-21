package com.example.ppgandroidexample.data.repository

import com.example.ppgandroidexample.common.PPGMetaData
import com.example.ppgandroidexample.data.remote.PPGTransactionalAPI
import com.example.ppgandroidexample.data.remote.dto.AssignExternalIdDTO
import com.example.ppgandroidexample.data.remote.dto.MessageIdDTO
import com.example.ppgandroidexample.data.remote.dto.SubscribersWithGivenExternalIdDTO
import com.example.ppgandroidexample.data.remote.dto.SuccessDTO
import com.example.ppgandroidexample.data.remote.dto.TransactionalNotificationByExternalIdDTO
import com.example.ppgandroidexample.data.remote.dto.TransactionalNotificationByIdDTO
import com.example.ppgandroidexample.domain.repository.TransactionalScreenRepository
import com.pushpushgo.sdk.PushPushGo
import javax.inject.Inject

class TransactionalScreenRepositoryImplementation @Inject constructor(
    private val transactionalApi: PPGTransactionalAPI
): TransactionalScreenRepository {

    private val currentSubId = PushPushGo.getInstance().getSubscriberId()
    private val projectId = PPGMetaData.getProjectId()
    override suspend fun sendNotificationBySubscriberId(): MessageIdDTO {
        val notification = TransactionalNotificationByIdDTO(
            omitCapping = true, message = TransactionalNotificationByIdDTO.Message(
                actions = listOf(
                    TransactionalNotificationByIdDTO.Message.Action(
                        clickUrl = "https://docs.pushpushgo.company/", title = "PushPushGo"
                    )
                ),
                title = "This is transactional push notification from Transactional screen",
                content = "Sent to your Subscriber's ID",
                clickUrl = "https://docs.pushpushgo.company/developers-guide/rest-api/transactional-push",
                requireInteraction = true,
                direction = "ltr",
                sound = "",
                icon = "https://next.master1.qappg.co/dummy-icon.png",
                image = "https://next.master1.qappg.co/dummy-icon.png",
                ttl = 72
            ), to = currentSubId
        )
        return transactionalApi.sendTransactionalPushBySubscriberId(projectId, notification)
    }

    override suspend fun sendNotificationByExternalId(externalId: String): MessageIdDTO {
        val notification = TransactionalNotificationByExternalIdDTO(
            omitCapping = true, message = TransactionalNotificationByExternalIdDTO.Message(
                actions = listOf(
                    TransactionalNotificationByExternalIdDTO.Message.Action(
                        clickUrl = "https://docs.pushpushgo.company/", title = "PushPushGo"
                    )
                ),
                title = "This is transactional push notification from Transactional screen",
                content = "Sent to your Subscriber's ID",
                clickUrl = "https://docs.pushpushgo.company/developers-guide/rest-api/transactional-push",
                requireInteraction = true,
                direction = "ltr",
                sound = "",
                icon = "https://next.master1.qappg.co/dummy-icon.png",
                image = "https://next.master1.qappg.co/dummy-icon.png",
                ttl = 72
            ), externalId = externalId
        )
        return transactionalApi.sendTransactionalPushByExternalId(projectId, notification)
    }

    override suspend fun getAllSubscribersWithGivenExternalId(externalId: String): SubscribersWithGivenExternalIdDTO {
        return transactionalApi.getSubscriberIdsWithGivenExternalId(projectId, externalId)
    }

    override suspend fun assignExternalIdToSubscriber(
        externalId: String
    ): SubscribersWithGivenExternalIdDTO {
        val requestBody = AssignExternalIdDTO(externalId, currentSubId)
        return transactionalApi.assignExternalIdToSubscriber(projectId, requestBody)
    }

    override suspend fun forgetExternalIdFromAllSubscribers(externalId: String): SuccessDTO {
        return transactionalApi.forgetSubscriberIdFromExternalId(projectId, externalId)
    }

    override suspend fun unassignSubscriberIdFromExternalId(): SuccessDTO {
        return transactionalApi.unassignSubscriberFromCurrentExternalId(projectId, currentSubId)
    }
}