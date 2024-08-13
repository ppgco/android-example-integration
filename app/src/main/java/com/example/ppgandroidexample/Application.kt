package com.example.ppgandroidexample

import android.app.Application
import com.example.ppgandroidexample.common.PPGMetaData
import com.pushpushgo.sdk.PushPushGo
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        // Getting data for PPG transactional part of App
        PPGMetaData.initialize(this)

        // this is for testing environment only
        // in production u should only use PushPushGo.getInstance(this)
        // as api key and projectId is provided by AndroidManifest.xml
        PushPushGo.getInstance(this)
    }
}