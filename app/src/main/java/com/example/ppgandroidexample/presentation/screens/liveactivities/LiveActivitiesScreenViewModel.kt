package com.example.ppgandroidexample.presentation.screens.liveactivities

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ppgandroidexample.common.Resource
import com.example.ppgandroidexample.data.liveactivity.LiveActivityDemoPayloads
import com.example.ppgandroidexample.domain.repository.LiveActivitiesRepository
import com.example.ppgandroidexample.domain.use_case.liveactivities.SubscribeToLiveActivityUC
import com.example.ppgandroidexample.domain.use_case.liveactivities.UnsubscribeFromLiveActivityUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class LiveActivitiesScreenViewModel @Inject constructor(
    private val repository: LiveActivitiesRepository,
    private val subscribeUC: SubscribeToLiveActivityUC,
    private val unsubscribeUC: UnsubscribeFromLiveActivityUC
) : ViewModel() {

    private val _state = mutableStateOf(LiveActivitiesScreenState())
    val state: State<LiveActivitiesScreenState> = _state

    init {
        _state.value = _state.value.copy(isSupported = repository.isSupported())
        refreshStatus()
    }

    fun onLiveNotificationIdChange(id: String) {
        _state.value = _state.value.copy(liveNotificationId = id)
    }

    /**
     * Registers the device on the backend live notification. Requires an
     * existing push subscriber - register one on the SDK section screen first.
     */
    fun subscribe() {
        val id = liveNotificationId()
        subscribeUC(id).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        message = "Subscribed to $id (laSubscriberId: ${result.data})",
                        isLoading = false,
                        error = null,
                        messageColor = Color.Green
                    )
                    refreshStatus()
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = result.message ?: "An unexpected error occurred",
                        isLoading = false,
                        message = null,
                        messageColor = Color.Red
                    )
                }

                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
            }
        }.launchIn(viewModelScope)
    }

    fun unsubscribe() {
        val id = liveNotificationId()
        unsubscribeUC(id).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        message = "Unsubscribed from $id",
                        isLoading = false,
                        error = null,
                        messageColor = Color.Green
                    )
                    refreshStatus()
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = result.message ?: "An unexpected error occurred",
                        isLoading = false,
                        message = null,
                        messageColor = Color.Red
                    )
                }

                is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
            }
        }.launchIn(viewModelScope)
    }

    /** `start` event - resets the match and sends the static configuration. */
    fun startMatch() {
        _state.value = _state.value.copy(
            homeScore = 0,
            awayScore = 0,
            phaseIndex = 0,
            // Kick-off - the clock starts counting from here
            phaseStartedAtMs = System.currentTimeMillis()
        )
        simulate(
            event = LiveActivityDemoPayloads.EVENT_START,
            includeConfiguration = true,
            feedback = "Match started - check your notification drawer"
        )
    }

    fun homeGoal() {
        _state.value = _state.value.copy(homeScore = _state.value.homeScore + 1)
        simulate(
            event = LiveActivityDemoPayloads.EVENT_UPDATE,
            hotMessage = "GOAL! ${LiveActivityDemoPayloads.HOME_TEAM} scores",
            feedback = "${LiveActivityDemoPayloads.HOME_TEAM} scored"
        )
    }

    fun awayGoal() {
        _state.value = _state.value.copy(awayScore = _state.value.awayScore + 1)
        simulate(
            event = LiveActivityDemoPayloads.EVENT_UPDATE,
            hotMessage = "GOAL! ${LiveActivityDemoPayloads.AWAY_TEAM} scores",
            feedback = "${LiveActivityDemoPayloads.AWAY_TEAM} scored"
        )
    }

    /** Advances the match phase - the progress bar and clock follow it. */
    fun nextPhase() {
        val nextIndex = (_state.value.phaseIndex + 1)
            .coerceAtMost(LiveActivityDemoPayloads.PHASES.lastIndex)
        _state.value = _state.value.copy(
            phaseIndex = nextIndex,
            // A new phase begins, so the clock restarts from its base minute
            phaseStartedAtMs = System.currentTimeMillis()
        )
        simulate(
            event = LiveActivityDemoPayloads.EVENT_UPDATE,
            feedback = "Phase: ${_state.value.currentPhase}"
        )
    }

    /** Transient message that takes over the score line and then expires. */
    fun sendHotMessage() {
        simulate(
            event = LiveActivityDemoPayloads.EVENT_UPDATE,
            hotMessage = "Yellow card - 67'",
            feedback = "Hot message sent"
        )
    }

    /** `end` event - the final score shows briefly, then the SDK removes the notification. */
    fun endMatch() {
        _state.value = _state.value.copy(
            phaseIndex = LiveActivityDemoPayloads.PHASES.lastIndex,
            phaseStartedAtMs = System.currentTimeMillis()
        )
        simulate(
            event = LiveActivityDemoPayloads.EVENT_END,
            feedback = "Match ended - notification will disappear shortly"
        )
    }

    /** Reads back what the SDK currently tracks. */
    fun refreshStatus() {
        val id = liveNotificationId()
        val active = repository.getActiveLiveActivities()
        val summary = if (active.isEmpty()) {
            "No active live activities"
        } else {
            active.joinToString("\n") { activity ->
                val content = activity.configuration.content
                "${activity.id}\n" +
                    "  ${content.homeTeamName} ${activity.liveData.scoreText} ${content.awayTeamName}\n" +
                    "  phase: ${activity.liveData.status}  active: ${repository.isLiveActivityActive(activity.id)}"
            }
        }
        _state.value = _state.value.copy(
            activeActivities = summary,
            laSubscriberId = repository.getLiveActivitySubscriberId(id)
        )
    }

    private fun simulate(
        event: String,
        includeConfiguration: Boolean = false,
        hotMessage: String? = null,
        feedback: String
    ) {
        val currentState = _state.value
        repository.simulateMatchPush(
            liveNotificationId = liveNotificationId(),
            event = event,
            phase = currentState.currentPhase,
            // Carried through unchanged on goals / hot messages so the clock keeps running
            statusChangedAt = currentState.phaseStartedAtMs,
            homeScore = currentState.homeScore,
            awayScore = currentState.awayScore,
            includeConfiguration = includeConfiguration,
            hotMessage = hotMessage
        )

        val supported = currentState.isSupported
        _state.value = currentState.copy(
            message = if (supported) feedback else "Device below API 36 - push ignored by the SDK",
            error = null,
            messageColor = if (supported) Color.Green else Color.Red
        )

        // The SDK renders asynchronously, so read the state back a moment later
        viewModelScope.launch {
            delay(STATE_READBACK_DELAY_MS.milliseconds)
            refreshStatus()
        }
    }

    private fun liveNotificationId(): String = _state.value.liveNotificationId.trim()
        .ifEmpty { LiveActivityDemoPayloads.DEMO_LIVE_NOTIFICATION_ID }

    private companion object {
        const val STATE_READBACK_DELAY_MS = 300L
    }
}
