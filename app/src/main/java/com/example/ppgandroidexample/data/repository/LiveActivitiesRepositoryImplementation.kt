package com.example.ppgandroidexample.data.repository

import com.example.ppgandroidexample.data.liveactivity.LiveActivityDemoPayloads
import com.example.ppgandroidexample.domain.repository.LiveActivitiesRepository
import com.pushpushgo.sdk.PushPushGo
import com.pushpushgo.sdk.push.liveactivity.data.LiveActivity
import kotlinx.coroutines.guava.await
import javax.inject.Inject

/**
 * Wraps the SDK's Live Activities API.
 *
 * All of these methods are safe to call on any API level - below Android 16
 * (API 36) the SDK degrades gracefully and simply renders nothing.
 */
class LiveActivitiesRepositoryImplementation @Inject constructor() : LiveActivitiesRepository {

    private val ppg = PushPushGo.getInstance()

    /** Live Activities need the Android 16 ProgressStyle template (API 36+). */
    override fun isSupported(): Boolean = ppg.isLiveActivitiesSupported()

    /**
     * Registers this device on the backend live notification so it starts
     * receiving its pushes. The device must already be a push subscriber.
     *
     * Returns the LA subscriber id - the SDK persists it, so unsubscribing
     * later only needs the live notification id.
     *
     * If the campaign is already running, the SDK also fetches its current
     * state and renders the notification right away (late-join catch-up).
     */
    override suspend fun subscribe(liveNotificationId: String): String =
        ppg.subscribeToLiveActivity(liveNotificationId).await()

    override suspend fun unsubscribe(liveNotificationId: String) {
        ppg.unsubscribeFromLiveActivity(liveNotificationId).await()
    }

    /** Empty when this device is not subscribed to the given live notification. */
    override fun getLiveActivitySubscriberId(liveNotificationId: String): String =
        ppg.getLiveActivitySubscriberId(liveNotificationId)

    /** Activities currently tracked and rendered by the SDK. */
    override fun getActiveLiveActivities(): List<LiveActivity> = ppg.getActiveLiveActivities()

    override fun isLiveActivityActive(liveNotificationId: String): Boolean =
        ppg.isLiveActivityActive(liveNotificationId)

    /**
     * Feeds a hand-built push envelope straight into the SDK's rendering
     * pipeline - the testing shortcut that lets the demo drive the match
     * without a backend campaign.
     */
    override fun simulateMatchPush(
        liveNotificationId: String,
        event: String,
        phase: String,
        statusChangedAt: Long,
        homeScore: Int,
        awayScore: Int,
        includeConfiguration: Boolean,
        hotMessage: String?
    ) {
        val envelope = LiveActivityDemoPayloads.buildEnvelope(
            liveNotificationId = liveNotificationId,
            event = event,
            phase = phase,
            statusChangedAt = statusChangedAt,
            homeScore = homeScore,
            awayScore = awayScore,
            projectId = ppg.getProjectId(),
            // Throws when there is no subscriber yet - the envelope tolerates an empty value
            subscriberId = runCatching { ppg.getSubscriberId() }.getOrNull().orEmpty(),
            includeConfiguration = includeConfiguration,
            hotMessage = hotMessage
        )
        ppg.simulateLiveActivityPush(envelope)
    }
}
