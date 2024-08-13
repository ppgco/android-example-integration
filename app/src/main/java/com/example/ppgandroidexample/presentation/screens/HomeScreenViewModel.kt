package com.example.ppgandroidexample.presentation.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ppgandroidexample.common.Resource
import com.example.ppgandroidexample.domain.use_case.GetSubscriberIdUC
import com.example.ppgandroidexample.domain.use_case.IsSubscribedUC
import com.example.ppgandroidexample.domain.use_case.RegisterSubscriberUC
import com.example.ppgandroidexample.domain.use_case.SendBeaconUC
import com.example.ppgandroidexample.domain.use_case.TransactionalPushUC
import com.example.ppgandroidexample.domain.use_case.UnregisterSubscriberUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val registerUC: RegisterSubscriberUC,
    private val unregisterUC: UnregisterSubscriberUC,
    private val getSubIdUC: GetSubscriberIdUC,
    private val isSubscribedUC: IsSubscribedUC,
    private val sendBeaconUC: SendBeaconUC,
    private val sendTransactionalPushUC: TransactionalPushUC
) : ViewModel() {
    private val _state = mutableStateOf(HomeScreenState())
    val state: State<HomeScreenState> = _state

    fun registerSubscriber(context: Context) {
        registerUC().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val subscriptionMsg: String
                    val msgColor: Color
                    if (ContextCompat.checkSelfPermission(
                            context, Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        subscriptionMsg = "Subscriber created successfully"
                        msgColor = Color.Green
                    } else {
                        subscriptionMsg = "Notification permission denied"
                        msgColor = Color.Red
                    }
                    _state.value = _state.value.copy(
                        message = subscriptionMsg,
                        isLoading = false,
                        error = null,
                        messageColor = msgColor
                    )
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = result.message ?: "An unexpected error occurred",
                        isLoading = false,
                        message = null
                    )

                }

                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun unregisterSubscriber() {
        unregisterUC().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val unregisterMsg = "Subscriber unregistered"
                    _state.value = _state.value.copy(
                        message = unregisterMsg,
                        isLoading = false,
                        error = null,
                        messageColor = Color.Green
                    )
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = result.message ?: "An unexpected error occurred",
                        isLoading = false,
                        message = null,
                        messageColor = Color.Red
                    )

                }

                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getSubscriberId() {
        getSubIdUC().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val subscriberId: String
                    val msgColor: Color
                    if (result.data.isNullOrEmpty()) {
                        subscriberId = "user is not subscriber"
                        msgColor = Color.Red
                    } else {
                        subscriberId = result.data
                        msgColor = Color.Green
                    }
                    _state.value = _state.value.copy(
                        subId = subscriberId,
                        message = "Subscriber ID: $subscriberId",
                        isLoading = false,
                        error = null,
                        messageColor = msgColor
                    )
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = result.message ?: "An unexpected error occurred",
                        isLoading = false,
                        subId = null,
                        message = null,
                        messageColor = Color.Red
                    )

                }

                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun isSubscriberActive() {
        isSubscribedUC().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val isActive = result.data
                    val msgColor = if (isActive == true) {
                        Color.Green
                    } else {
                        Color.Red
                    }

                    _state.value = _state.value.copy(
                        isSubscriber = isActive,
                        message = "Subscriber is active: $isActive",
                        isLoading = false,
                        error = null,
                        messageColor = msgColor
                    )
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = result.message ?: "An unexpected error occurred",
                        isLoading = false,
                        isSubscriber = false,
                        message = null,
                        messageColor = Color.Red
                    )

                }

                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun sendBeacon(tag: String, label: String, ttl: Int) {
        sendBeaconUC(tag, label, ttl).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        message = "Beacon sent successfully. Click 'Get Subscriber Labels' for results",
                        isLoading = false,
                        error = null,
                        messageColor = Color.Green
                    )
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

    fun sendTransactionalPush() {
        sendTransactionalPushUC().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        message = "Success - notification should arrive shortly",
                        isLoading = false,
                        error = null,
                        messageColor = Color.Green
                    )
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

}