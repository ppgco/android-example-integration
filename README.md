# android-example-integration
Example of integration PPG android sdk with Jetpack Compose/Material3 android APP

## Description

Repository contains "ready to go" Android app integrated with PPG android native sdk which runs on PPG testing environment.

Application is built using Jetpack Compose / Material3 / Retrofit2 / DaggerHilt / MVVM architecture / **Kotlin DSL configuration** (if you would like to see how integration is performed on Groovy DSL - check example contained in our native android sdk repository https://github.com/ppgco/android-sdk)

## Functionalities
App implements some of PPG android-sdk methods, as well as a transactional API for sending push notification
* **Register** - register subscriber
* **Unregister** - unregister subscriber
* **Subscriber ID** - returns subscriber ID if registered
* **Is Subscribed** - returns subscriber status
* **Send beacon** - add tag with label to your subscriber by sending beacon
* **Get Subscriber Labels** - returns list of subscriber's tags with labels
* **Send Push Notification** - send a test transactional push notification to your subscriber