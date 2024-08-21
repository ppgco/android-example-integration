package com.example.ppgandroidexample.presentation.screens.transactional

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ppgandroidexample.common.Resource
import com.example.ppgandroidexample.domain.use_case.transactional.AssignExternalIdToSubUC
import com.example.ppgandroidexample.domain.use_case.transactional.ForgetExternalIdFromAllSubsUC
import com.example.ppgandroidexample.domain.use_case.transactional.GetSubsWithExternalIdUC
import com.example.ppgandroidexample.domain.use_case.transactional.TransactionalPushByExternalIdUC
import com.example.ppgandroidexample.domain.use_case.transactional.TransactionalPushByIdUC
import com.example.ppgandroidexample.domain.use_case.transactional.UnassignSubFromExternalIdUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class TransactionalScreenViewModel @Inject constructor(
    private val sendTransactionalPushByIdUC: TransactionalPushByIdUC,
    private val sendTransactionalPushByExternalIdUC: TransactionalPushByExternalIdUC,
    private val getSubsWithExternalIdUC: GetSubsWithExternalIdUC,
    private val assignExternalIdUC: AssignExternalIdToSubUC,
    private val forgetExternalIdFromAllSubsUC: ForgetExternalIdFromAllSubsUC,
    private val unassignSubFromExternalIdUC: UnassignSubFromExternalIdUC
) : ViewModel() {

    private val _state = mutableStateOf(TransactionalScreenState())
    val state: State<TransactionalScreenState> = _state

    fun sendTransactionalPush(sentBy: PushSentByLiterals, externalId: String?) {
        val pushType = if (sentBy === PushSentByLiterals.Id) {
            sendTransactionalPushByIdUC()
        } else {
            sendTransactionalPushByExternalIdUC(externalId = externalId ?: "")
        }
        pushType.onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val messageId = result.data?.messageId
                    _state.value = _state.value.copy(
                        message = "Success - notification should arrive shortly. Message ID: $messageId",
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

    fun getSubsWithExternalId(externalId: String) {
        getSubsWithExternalIdUC(externalId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val subscribersList = result.data?.subscriberIds
                    _state.value = _state.value.copy(
                        message = "List of Subscribers with externalId: $externalId \n $subscribersList",
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

    fun assignSubToExternalId(externalId: String) {
        assignExternalIdUC(externalId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val subscribersList = result.data?.subscriberIds
                    _state.value = _state.value.copy(
                        message = "Successfully added your Subscriber \n $subscribersList \n to externalId: $externalId",
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

    fun forgetExternalIdFromAllSubs(externalId: String) {
        forgetExternalIdFromAllSubsUC(externalId).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        message = "Successfully removed externalId: $externalId from all Subscribers",
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

    fun unassignSubFromExternalId() {
        unassignSubFromExternalIdUC().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        message = "Successfully removed current externalId: ${state.value.externalId} from your Subscriber",
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

sealed class PushSentByLiterals(val value: String) {
    data object Id : PushSentByLiterals("id")
    data object ExternalId : PushSentByLiterals("externalId")
}