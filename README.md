# android-example-integration
Example of integration PPG android SDK and In-App Messages SDK with Jetpack Compose/Material3 Android APP

## Description

Repository contains a complete APP integrated with PPG android native SDK and the In-App Messages SDK.
**To run the APP you need to provide credentials of your test project (PPG / Firebase). Check Requirements & installation section for guidance.**

Application is built using Jetpack Compose / Material3 / Retrofit2 / DaggerHilt / MVVM architecture / **Kotlin DSL configuration** (if you would like to see how integration is performed on Groovy DSL - check example contained in our native android sdk repository https://github.com/ppgco/android-sdk)

## Requirements & installation

**Requirements**
1. Test project in PPG application
2. google-services.json
3. FCM v1 credentials as .json
4. `local.properties` with PPG credentials (see below)

**Installation**
1. Copy `app/google-services.json.example` to `app/google-services.json` and fill in your Firebase project values.
2. Add your PPG credentials to `local.properties` in the project root:
   ```
   PPG_PROJECT_ID=your-project-id
   PPG_API_KEY=your-api-key
   ```
   These are injected as AndroidManifest meta-data at build time. To generate an API key visit your organization account in PPG.
3. Steps to generate FCM v1 credentials and upload them in PPG APP:
   * Go to your Firebase console and navigate to project settings
   * Open Cloud Messaging tab
   * Click Manage Service Accounts
   * Click on your service account email
   * Navigate to KEYS tab → ADD KEY → CREATE NEW KEY (JSON type)
   * Download the file and upload it in PushPushGo Application (https://next.pushpushgo.com/projects/yourProjectID/settings/integration/fcm)

## SDK Functionalities
App implements some of PPG android-sdk methods, as well as a transactional API for sending push notifications:
* **Register** - register subscriber
* **Unregister** - unregister subscriber
* **Subscriber ID** - returns subscriber ID if registered
* **Is Subscribed** - returns subscriber status
* **Send beacon** - add tag with label to your subscriber by sending beacon
* **Send Push Notification** - send a test transactional push notification to your subscriber

## In-App Messages

The app integrates the [PushPushGo In-App Messages SDK](https://github.com/ppgco/android-sdk) (`com.github.ppgco.android-sdk:inappmessages:3.1.0`).

### Initialization

Both SDKs are initialized in the `Application` class (`Application.kt`):

```kotlin
// Push SDK
PushPushGo.getInstance(this)

// In-App Messages SDK
InAppMessagesSDK.initialize(
    application = this,
    projectId = PPGMetaData.getProjectId(),
    apiKey = PPGMetaData.getApiKey(),
    debug = true,
)
```

### Automatic Route Tracking (NavGraph approach)

The recommended integration uses `InAppMessageHelper.setupWithNavController()` in `NavGraph.kt`. This automatically calls `showActiveMessages(route)` on every navigation destination change — no changes needed on individual screens:

```kotlin
DisposableEffect(navController) {
    val listener = InAppMessageHelper.setupWithNavController(navController)
    onDispose {
        navController.removeOnDestinationChangedListener(listener)
    }
}
```

### Alternative: Manual Triggering per Screen

If you prefer to control triggering individually, you can use `LaunchedEffect` on each screen instead of the NavGraph approach:

```kotlin
LaunchedEffect(Screens.Home.route) {
    InAppMessagesSDK.getInstance().showActiveMessages(Screens.Home.route)
}
```

This alternative is shown as commented-out code in `HomeScreen.kt`, `TransactionalScreen.kt`, and `InAppMessagesScreen.kt`.

### In-App Messages Screen

The dedicated **In-App Messages** screen (`InAppMessagesScreen.kt`) lets you test all IAM features:

* **Show Active Messages (Route)** — manually triggers `showActiveMessages()` for the current route
* **Fire Custom Trigger** — calls `showMessagesOnTrigger(key, value)` with a key/value you provide
* **Set JS Action Handler** — registers a handler that receives JS action payloads from message buttons

### Trigger Types
| Trigger | How to use |
|---------|-----------|
| `ENTER` | Shown on app open / any route navigation (handled automatically by NavGraph setup) |
| `CUSTOM_TRIGGER` | Call `showMessagesOnTrigger(key, value)` with a matching key/value |

## Transactional API
In transactional API section you can find buttons which implement some of transactional endpoints (https://docs.pushpushgo.company/developers-guide/rest-api/transactional-push).

**Example implementation details:**
In this application we used MVVM architecture + Retrofit2 along with Use Cases approach.

1. Define interface for transactional api (`/data/remote/PPGTransactionalAPI.kt`)
2. Create transactional repository interface (`/domain/repository/TransactionalScreenRepository.kt`)
3. Implement that repository (`/data/repository/TransactionalScreenRepositoryImplementation.kt`)
4. **optionally** Create Use Cases (`/domain/use-case/transactional/...`)
5. Use implemented functions (or Use Cases) in view model (`/presentation/screens/transactional/TransactionalScreenViewModel.kt`)
6. Inject view model to your screen (`/presentation/screens/transactional/TransactionalScreen`)

In transactional section you can test functionalities like:
* **Send transactional push to subscriber's ID or external ID**
* **Get list of Subscribers with given external ID**
* **Assign external ID to your subscriber**
* **Unassign your subscriber from current external ID**
* **Remove given external ID from all Subscribers**
