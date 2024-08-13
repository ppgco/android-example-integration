# android-example-integration
Example of integration PPG android sdk with Jetpack Compose/Material3 android APP

## Description

Repository contains complete APP integrated with PPG android native sdk.
**To run APP you need to provide credentials of your test project (PPG / Firebase). Check Requirements & installation section for guidance**

Application is built using Jetpack Compose / Material3 / Retrofit2 / DaggerHilt / MVVM architecture / **Kotlin DSL configuration** (if you would like to see how integration is performed on Groovy DSL - check example contained in our native android sdk repository https://github.com/ppgco/android-sdk)

## Requirements & installation

**Requirements**
1. Test project in PPG application
2. google-service.json
3. FCM v1 credentials as .json

**Installation**
1. Collect Project ID and API key from PPG application (to generate API key visit https://app.pushpushgo.com/user/access-manager/keys). Provide those credentials in AndroidManifest.xml meta-data tags.
2. Create test project using Firebase console. Connect this app with that project and download google-services.json, then place it in the app root folder.
3. Steps to generate FCM v1 credentials and upload it in PPG APP:
   * Go to your Firebase console and navigate to project settings
   * Open Cloud Messaging tab
   * Click Manage Service Accounts
   * Click on your service account email
   * Navigate to KEYS tab
   * Click ADD KEY
   * Click CREATE NEW KEY
   * Pick JSON type and click create
   * Download file and upload it in PushPushGo Application (/project/providers) in FCM v1 credentials section

## Functionalities
App implements some of PPG android-sdk methods, as well as a transactional API for sending push notification
* **Register** - register subscriber
* **Unregister** - unregister subscriber
* **Subscriber ID** - returns subscriber ID if registered
* **Is Subscribed** - returns subscriber status
* **Send beacon** - add tag with label to your subscriber by sending beacon
* **Get Subscriber Labels** - returns list of subscriber's tags with labels
* **Send Push Notification** - send a test transactional push notification to your subscriber