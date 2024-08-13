package com.example.ppgandroidexample.common

import android.content.Context
import android.content.pm.PackageManager

object PPGMetaData {
    private var apiKey: String? = null
    private var projectId: String? = null

    fun initialize(context: Context) {
        apiKey = getMetaData(context, "com.pushpushgo.apikey")
        projectId = getMetaData(context, "com.pushpushgo.projectId")
    }

    fun getApiKey(): String {
        return apiKey ?: throw IllegalStateException("API key not initialized.")
    }

    fun getProjectId(): String {
        return projectId ?: throw IllegalStateException("Project ID not initialized.")
    }

    private fun getMetaData(context: Context, key: String): String? {
        return try {
            val appInfo = context.packageManager
                .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            val bundle = appInfo.metaData
            bundle.getString(key)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }
}
