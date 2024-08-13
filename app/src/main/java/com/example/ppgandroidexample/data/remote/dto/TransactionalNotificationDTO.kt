package com.example.ppgandroidexample.data.remote.dto

data class TransactionalNotificationDTO(
    val omitCapping: Boolean,
    val message: Message,
    val to: String
) {
    data class Message(
        val actions: List<Action>,
        val title: String,
        val content: String,
        val clickUrl: String,
        val requireInteraction: Boolean,
        val direction: String,
        val sound: String?,
        val icon: String,
        val image: String,
        val ttl: Int
    ) {
        data class Action(
            val clickUrl: String,
            val title: String
        )
    }
}

