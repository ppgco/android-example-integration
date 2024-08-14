package com.example.ppgandroidexample.data.repository

import com.example.ppgandroidexample.common.PPGMetaData
import com.example.ppgandroidexample.data.remote.PPGTransactionalAPI
import com.example.ppgandroidexample.data.remote.dto.TransactionalNotificationDTO
import com.example.ppgandroidexample.domain.repository.HomeScreenRepository
import com.pushpushgo.sdk.PushPushGo
import javax.inject.Inject

class HomeScreenRepositoryImplementation @Inject constructor(
    private val transactionalApi: PPGTransactionalAPI
) : HomeScreenRepository {

    private val ppg = PushPushGo.getInstance()

    override suspend fun registerSubscriber() {
        ppg.registerSubscriber()
    }

    override suspend fun unregisterSubscriber() {
        ppg.unregisterSubscriber()
    }

    override suspend fun getSubscriberId(): String {
        return ppg.getSubscriberId()
    }

    override suspend fun isSubscribed(): Boolean {
        return ppg.isSubscribed()
    }

    override suspend fun sendBeacon(tag: String, label: String, ttl: Int) {
        ppg.createBeacon().appendTag(tag, label, "append", ttl).send()
    }

    override suspend fun sendTestPushNotification() {
        val currentSubId = ppg.getSubscriberId()
        val projectId = PPGMetaData.getProjectId()
        val notification = TransactionalNotificationDTO(
            omitCapping = true, message = TransactionalNotificationDTO.Message(
                actions = listOf(
                    TransactionalNotificationDTO.Message.Action(
                        clickUrl = "https://docs.pushpushgo.company/", title = "PushPushGo"
                    )
                ),
                title = "This is transactional push notification",
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
        transactionalApi.sendTransactionalPushBySubscriberId(projectId, notification)
    }
}