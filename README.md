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
   * Download file and upload it in PushPushGo Application (https://next.pushpushgo.com/projects/yourProjectID/settings/integration/fcm)

## SDK Functionalities
App implements some of PPG android-sdk methods, as well as a transactional API for sending push notification
* **Register** - register subscriber
* **Unregister** - unregister subscriber
* **Subscriber ID** - returns subscriber ID if registered
* **Is Subscribed** - returns subscriber status
* **Send beacon** - add tag with label to your subscriber by sending beacon
* **Send Push Notification** - send a test transactional push notification to your subscriber

## Transactional API
In transactional API section you can find buttons which implements some of transactional endpoints (https://docs.pushpushgo.company/developers-guide/rest-api/transactional-push).

**Example implementation details:**
In case of this application we used MVVM architecture + Retrofit2 along with Use Cases approach.

1. Define interface for transactional api (/data/remote/PPGTransactionalAPI.kt)
2. Create transactional repository interface (/domain/repository/TransactionalScreenRepository.kt)
3. Implement that repository (/data/repository/TransactionalScreenRepositoryImplementation.kt)
4. **optionally** Create Use Cases (/domain/use-case/transactional/...)
5. Use implemented functions (or Use Cases) in view model (/presentation/screens/transactional/TransactionalScreenViewModel.kt)
6. Inject view model to your screen (/presentation/screens/transactional/TransactionalScreen)

In transactional section you can test functionalities like:
* **Send transactional push to subscriber's ID or external ID**
* **Get list of Subscribers with given external ID**
* **Assign external ID to your subscriber**
* **Unassign your subscriber from current external ID**
* **Remove given external ID from all Subscribers**
