package com.example.ppgandroidexample.presentation.screens.liveactivities

import androidx.compose.ui.graphics.Color
import com.example.ppgandroidexample.data.liveactivity.LiveActivityDemoPayloads

data class LiveActivitiesScreenState(
    val error: String? = null,
    val message: String? = null,
    val messageColor: Color = Color.Transparent,
    val isLoading: Boolean = false,
    /** API 36+ - below that the SDK ignores Live Activity pushes. */
    val isSupported: Boolean = false,
    val liveNotificationId: String = LiveActivityDemoPayloads.DEMO_LIVE_NOTIFICATION_ID,
    /** LA subscriber id persisted by the SDK, empty when not subscribed. */
    val laSubscriberId: String = "",
    /** Dump of `getActiveLiveActivities()`. */
    val activeActivities: String = "",
    // Local mirror of the simulated match, so the buttons can evolve its state
    val homeScore: Int = 0,
    val awayScore: Int = 0,
    val phaseIndex: Int = 0,
    /**
     * When the current phase started. The SDK counts the match clock from this
     * value, so it must only be refreshed on a phase change - goals and hot
     * messages keep it, otherwise every update would restart the clock.
     */
    val phaseStartedAtMs: Long = System.currentTimeMillis()
) {
    val currentPhase: String
        get() = LiveActivityDemoPayloads.PHASES[phaseIndex.coerceIn(LiveActivityDemoPayloads.PHASES.indices)]
}
