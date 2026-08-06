package com.example.ppgandroidexample.data.liveactivity

import org.json.JSONArray
import org.json.JSONObject

/**
 * Builds the "live notification" push envelopes used by the local simulation on
 * the Live Activities screen.
 *
 * A real Live Activity arrives as an FCM data message - a flat
 * `Map<String, String>` whose nested objects (`configuration`, `liveData`,
 * `hotMessage`) are JSON strings. PushPushGo.simulateLiveActivityPush
 * accepts exactly the same map, so the SDK's parse -> manage -> render pipeline
 * can be exercised without a backend campaign.
 *
 * The `configuration` block is static and is only sent with the `start` event;
 * `liveData` carries the state that changes (score, phase) and is sent with
 * every event.
 */
object LiveActivityDemoPayloads {

    const val DEMO_LIVE_NOTIFICATION_ID = "demo-match-1"
    const val HOME_TEAM = "Arsenal"
    const val AWAY_TEAM = "Chelsea"

    /** Deep link opened when the notification body is tapped - routed in `Application.kt`. */
    const val DEMO_DEEP_LINK = "app://www.example.com/live-activities"

    const val EVENT_START = "start"
    const val EVENT_UPDATE = "update"
    const val EVENT_END = "end"

    /**
     * Subset of the phases the `FOOTBALL_MATCH_TRACKING` template understands,
     * walked through by the "Next phase" button. The SDK also supports added
     * time, extra time and penalties - see MatchPhase in the SDK.
     */
    val PHASES = listOf("FIRST_HALF", "HALF_TIME_BREAK", "SECOND_HALF", "FULL_TIME")

    /**
     * Assembles the push envelope.
     *
     * @param statusChangedAt epoch millis when [phase] was entered - the SDK
     *   counts the match clock from it, so it must only move when the phase
     *   itself changes. Sending "now" on every update would restart the clock.
     * @param includeConfiguration send the static configuration block - required on `start`.
     * @param hotMessage optional transient message (e.g. "GOAL!") that briefly
     *   takes over the score line and then expires on its own.
     */
    fun buildEnvelope(
        liveNotificationId: String,
        event: String,
        phase: String,
        statusChangedAt: Long,
        homeScore: Int,
        awayScore: Int,
        projectId: String,
        subscriberId: String,
        includeConfiguration: Boolean = false,
        hotMessage: String? = null
    ): Map<String, String> = buildMap {
        put("type", "live_notification")
        put("liveNotificationId", liveNotificationId)
        put("event", event)
        put("template", "FOOTBALL_MATCH_TRACKING")
        put("project", projectId)
        put("subscriber", subscriberId)
        put("liveData", liveDataJson(phase, statusChangedAt, homeScore, awayScore))
        if (includeConfiguration) put("configuration", configurationJson())
        if (hotMessage != null) put("hotMessage", hotMessageJson(hotMessage))
    }

    /**
     * Static match configuration: teams, crests, progress bar colors, phase
     * labels and up to 3 action buttons (Android's limit).
     */
    private fun configurationJson(): String = JSONObject().apply {
        put("type", "FOOTBALL_MATCH_TRACKING")
        put("content", JSONObject().apply {
            put("title", "Premier League")
            put("homeTeamName", HOME_TEAM)
            put("homeTeamImage", "https://crests.football-data.org/57.png")
            put("awayTeamName", AWAY_TEAM)
            put("awayTeamImage", "https://crests.football-data.org/61.png")
        })
        put("design", JSONObject().apply {
            put("android", JSONObject().apply {
                // Draws a football as the progress tracker instead of a plain dot
                put("hasTrackerIcon", true)
                put("progressBarColor", colorSet(light = "#4CAF50", dark = "#2E7D32"))
                put("breakTimeBarColor", colorSet(light = "#FFC107", dark = "#FFA000"))
            })
        })
        put("statusLabels", JSONObject().apply {
            put("PRE_MATCH", "Starting soon")
            put("FIRST_HALF", "1st half")
            put("HALF_TIME_BREAK", "Half time")
            put("SECOND_HALF", "2nd half")
            put("FULL_TIME", "Full time")
            put("OTHER", "Match")
        })
        // One button of each supported type: opens the app, opens a URL, dismisses
        put("actions", JSONArray().apply {
            put(action(type = "OPEN_APP", name = "Open app"))
            put(action(type = "REDIRECT", name = "Docs", url = "https://docs.pushpushgo.company/"))
            put(action(type = "CLOSE", name = "Dismiss"))
        })
        put("timeout", JSONObject().put("minutes", 150))
        // Deep link for a tap on the notification body itself
        put("url", DEMO_DEEP_LINK)
    }.toString()

    /** Dynamic state - `statusChangedAt` is what the live match clock counts from. */
    private fun liveDataJson(
        phase: String,
        statusChangedAt: Long,
        homeScore: Int,
        awayScore: Int
    ): String =
        JSONObject().apply {
            put("type", "FOOTBALL_MATCH_TRACKING")
            put("homeTeamScore", homeScore)
            put("awayTeamScore", awayScore)
            put("status", phase)
            put("statusChangedAt", statusChangedAt)
        }.toString()

    /** `timestamp` is the expiry - the message disappears from the notification afterward. */
    private fun hotMessageJson(text: String): String = JSONObject().apply {
        put("id", "hot-${System.currentTimeMillis()}")
        put("text", text)
        put("timestamp", System.currentTimeMillis() / 1000 + HOT_MESSAGE_TTL_SECONDS)
    }.toString()

    private fun colorSet(light: String, dark: String) = JSONObject().apply {
        put("lightMode", light)
        put("darkMode", dark)
    }

    private fun action(type: String, name: String, url: String? = null) = JSONObject().apply {
        put("type", type)
        put("name", name)
        if (url != null) put("url", url)
    }

    private const val HOT_MESSAGE_TTL_SECONDS = 10
}
