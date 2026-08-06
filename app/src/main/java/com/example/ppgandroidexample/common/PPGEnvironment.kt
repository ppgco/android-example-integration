package com.example.ppgandroidexample.common

object PPGEnvironment {

    /** Set to `false` to run against the `master1` test environment. */
    const val IS_PRODUCTION = true

    /**
     * Base URL without a trailing slash - matches what the push SDK expects for
     * `customBaseUrl`. Retrofit callers that need the slash append it themselves.
     */
    val apiBaseUrl: String
        get() = if (IS_PRODUCTION) PRODUCTION_URL else MASTER1_URL

    private const val PRODUCTION_URL = "https://api.pushpushgo.com"
    private const val MASTER1_URL = "https://api.master1.qappg.co"
}
