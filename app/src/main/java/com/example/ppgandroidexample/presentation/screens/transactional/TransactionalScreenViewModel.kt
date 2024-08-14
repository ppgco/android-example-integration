package com.example.ppgandroidexample.presentation.screens.transactional

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionalScreenViewModel @Inject constructor(

): ViewModel() {
    private val _state = mutableStateOf(TransactionalScreenState())
    val state: State<TransactionalScreenState> = _state
}