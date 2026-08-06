package com.example.ppgandroidexample

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.ppgandroidexample.common.PPGEnvironment
import com.example.ppgandroidexample.common.PPGMetaData
import com.example.ppgandroidexample.presentation.MainActivity
import com.pushpushgo.sdk.PushPushGo
import com.pushpushgo.inappmessages.InAppMessagesSDK
import dagger.hilt.android.HiltAndroidApp
import androidx.core.net.toUri

@HiltAndroidApp
class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        // Getting data for PPG transactional part of App
        PPGMetaData.initialize(this)

        // The single-argument getInstance(this) always targets production. Passing
        // the credentials explicitly lets us pick the environment: isProduction =
        // false routes the SDK to api.master1.qappg.co (see PPGEnvironment).
        PushPushGo.getInstance(
            application = this,
            apiKey = PPGMetaData.getApiKey(),
            projectId = PPGMetaData.getProjectId(),
            isProduction = PPGEnvironment.IS_PRODUCTION,
            // Network logging - handy while testing against master1
            isDebug = !PPGEnvironment.IS_PRODUCTION,
        ).apply {
            // Single routing point for links coming from regular push clicks AND
            // from Live Activity notifications (body tap or REDIRECT action button).
            notificationHandler = { _, url, overrideFlags ->
                routeLink(url, overrideFlags)
            }
        }

        // Initialize In-App Messages SDK (credentials read from AndroidManifest metadata via PPGMetaData).
        // This SDK defaults to production, so it takes the URL explicitly - Retrofit requires
        // the trailing slash here.
        InAppMessagesSDK.initialize(
            application = this,
            projectId = PPGMetaData.getProjectId(),
            apiKey = PPGMetaData.getApiKey(),
            debug = true,
            baseUrl = "${PPGEnvironment.apiBaseUrl}/",
        )
    }

    /**
     * Sends `app://www.example.com/...` links to [MainActivity], which forwards
     * the URI to the NavGraph deep links; everything else (https etc.) is left
     * to the system.
     *
     * Without overriding `notificationHandler` the SDK would resolve every link
     * with a plain ACTION_VIEW intent, which works too - as long as a matching
     * intent-filter exists in the manifest.
     */
    private fun routeLink(url: String, overrideFlags: Int) {
        Log.d("PPGExample", "routeLink: $url")
        val uri = runCatching { url.toUri() }.getOrNull() ?: return

        val intent = if (uri.scheme == "app" && uri.host == "www.example.com") {
            Intent(this, MainActivity::class.java).setData(uri)
        } else {
            Intent(Intent.ACTION_VIEW, uri)
        }
        intent.addFlags(overrideFlags or Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e("PPGExample", "No activity found for link: $url", e)
            Toast.makeText(this, "Cannot open: $url", Toast.LENGTH_SHORT).show()
        }
    }
}
