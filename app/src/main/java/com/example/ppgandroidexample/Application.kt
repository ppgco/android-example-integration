package com.example.ppgandroidexample

import android.app.Application
import com.example.ppgandroidexample.common.PPGMetaData
import com.pushpushgo.sdk.PushPushGo
import com.pushpushgo.inappmessages.InAppMessagesSDK
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        // Getting data for PPG transactional part of App
        PPGMetaData.initialize(this)

        PushPushGo.getInstance(this)

        // Initialize In-App Messages SDK
        InAppMessagesSDK.initialize(application = this, projectId = "Your project id", apiKey = "Your API key")
    }
}