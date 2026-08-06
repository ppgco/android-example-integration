package com.example.ppgandroidexample.domain.repository

import com.pushpushgo.sdk.push.liveactivity.data.LiveActivity

interface LiveActivitiesRepository {

    fun isSupported(): Boolean
    suspend fun subscribe(liveNotificationId: String): String
    suspend fun unsubscribe(liveNotificationId: String)
    fun getLiveActivitySubscriberId(liveNotificationId: String): String
    fun getActiveLiveActivities(): List<LiveActivity>
    fun isLiveActivityActive(liveNotificationId: String): Boolean
    fun simulateMatchPush(
        liveNotificationId: String,
        event: String,
        phase: String,
        statusChangedAt: Long,
        homeScore: Int,
        awayScore: Int,
        includeConfiguration: Boolean,
        hotMessage: String?
    )
}
