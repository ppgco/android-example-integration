package com.example.ppgandroidexample.data.remote.dto

data class SubscribersWithGivenExternalIdDTO(
    val externalId: String,
    val subscriberIds: List<String>
)
