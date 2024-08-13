package com.example.ppgandroidexample.domain.repository

interface HomeScreenRepository {

    suspend fun registerSubscriber()
    suspend fun unregisterSubscriber()
    suspend fun getSubscriberId(): String
    suspend fun isSubscribed(): Boolean
    suspend fun sendBeacon(tag: String, label: String, ttl: Int)
    suspend fun getSubscriberLabels(): MutableList<Pair<String, String>>
    suspend fun sendTestPushNotification()
}