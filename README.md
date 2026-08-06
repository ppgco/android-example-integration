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
   ppg.projectId=your-project-id
   ppg.apiKey=your-api-key
   ```
   These are injected as AndroidManifest meta-data at build time. To generate an API key visit your organization account in PPG.
3. Steps to generate FCM v1 credentials and upload them in PPG APP:
   * Go to your Firebase console and navigate to project settings
   * Open Cloud Messaging tab
   * Click Manage Service Accounts
   * Click on your service account email
   * Navigate to KEYS tab → ADD KEY → CREATE NEW KEY (JSON type)
   * Download the file and upload it in PushPushGo Application (https://next.pushpushgo.com/projects/yourProjectID/settings/integration/fcm)

## Environment (production / master1)

The app talks to a single PushPushGo backend, selected in `common/PPGEnvironment.kt`:

```kotlin
const val IS_PRODUCTION = true    // false -> https://api.master1.qappg.co
```

That flag drives all three consumers at once — the push SDK (`isProduction` in `Application.kt`), the In-App Messages SDK (`baseUrl`) and the transactional API (`AppModule.kt`). **API keys are environment-specific**, so the credentials in `local.properties` must come from the same environment as the flag, otherwise requests fail with 401.

Note that `PushPushGo.getInstance(this)` — the single-argument form — always targets production. Selecting an environment requires the longer overload with the credentials passed explicitly:

```kotlin
PushPushGo.getInstance(
    application = this,
    apiKey = PPGMetaData.getApiKey(),
    projectId = PPGMetaData.getProjectId(),
    isProduction = PPGEnvironment.IS_PRODUCTION,
    isDebug = !PPGEnvironment.IS_PRODUCTION,
)
```

With `isProduction = false` and no `customBaseUrl` the SDK defaults to `api.master1.qappg.co`; pass `customBaseUrl` to point at any other environment.

## SDK Functionalities
App implements some of PPG android-sdk methods, as well as a transactional API for sending push notifications:
* **Register** - register subscriber
* **Unregister** - unregister subscriber
* **Subscriber ID** - returns subscriber ID if registered
* **Is Subscribed** - returns subscriber status
* **Send beacon** - add tag with label to your subscriber by sending beacon
* **Send Push Notification** - send a test transactional push notification to your subscriber
* **Live Activities** - subscribe to a live notification campaign and render live updates (see the Live Activities section)

## In-App Messages

The app integrates the [PushPushGo In-App Messages SDK](https://github.com/ppgco/android-sdk) (`com.github.ppgco.android-sdk:inappmessages:3.2.0`).

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

## Live Activities

Live Activities (Android 16 "Live Updates") are real-time notifications that keep updating themselves from backend pushes — the Android counterpart of iOS Live Activities. The SDK renders them with the Android 16 `ProgressStyle` template; the first available template is `FOOTBALL_MATCH_TRACKING` (team crests, live score, match phase, per-second game clock, progress bar with break indicators).

**Requirements**

| Requirement | Notes |
|---|---|
| Android 16 (API 36) device | On older devices Live Activity pushes are ignored — check `isLiveActivitiesSupported()` |
| PushPushGo SDK `3.2.0`+ | Live Activities are not available in earlier releases |
| Registered subscriber | Register on the **SDK section** screen before subscribing |
| `POST_NOTIFICATIONS` granted | Requested by `MainActivity` |

`POST_PROMOTED_NOTIFICATIONS` (required for promoted Live Updates) is declared by the SDK's own manifest — nothing to add in the app.

### Live Activities Screen

The dedicated **Live Activities** screen (`presentation/screens/liveactivities/`) has two sections:

**Subscription** — the real flow. `subscribeToLiveActivity(id)` registers the device on a live notification campaign created in PPG, and the backend takes over from there: it pushes `start` / `update` / `end` events and the SDK renders them. Subscribing to an already running campaign renders its current state immediately (late-join catch-up). The returned LA subscriber id is persisted by the SDK, so `unsubscribeFromLiveActivity(id)` only needs the live notification id.

**Local simulation** — testing without a backend. `simulateLiveActivityPush(map)` accepts exactly the envelope an FCM data message carries, so the whole parse → manage → render pipeline runs locally. The buttons start a match, score goals, advance phases (`FIRST_HALF` → `HALF_TIME_BREAK` → `SECOND_HALF` → `FULL_TIME`), push a transient "hot message" and end the match. Envelopes are built in `data/liveactivity/LiveActivityDemoPayloads.kt`.

### SDK methods used

| Method | Where |
|---|---|
| `isLiveActivitiesSupported()` | `LiveActivitiesRepositoryImplementation` |
| `subscribeToLiveActivity(id)` / `unsubscribeFromLiveActivity(id)` | `SubscribeToLiveActivityUC` / `UnsubscribeFromLiveActivityUC` |
| `getLiveActivitySubscriberId(id)` | subscription status readout |
| `getActiveLiveActivities()` / `isLiveActivityActive(id)` | "Tracked by the SDK" section |
| `simulateLiveActivityPush(data)` | local simulation buttons |
| `handleLiveActivityClick(intent)` | `SplashScreenActivity` + `MainActivity` |

`subscribeToLiveActivity` / `unsubscribeFromLiveActivity` return a Guava `ListenableFuture`; the repository bridges it to coroutines with `await()` (`kotlinx-coroutines-guava`).

### Handling clicks

A Live Activity click opens the **launcher activity** — `SplashScreenActivity` in this app — with the click details as intent extras, so `handleLiveActivityClick(intent)` is called there (and in `MainActivity`, per the SDK docs):

```kotlin
PushPushGo.getInstance().handleLiveActivityClick(intent)
```

It reports the click analytics (body tap vs. action button is detected automatically) and opens the deep link the notification carries. Pass `openDeepLink = false` to receive the link and route it yourself.

### Deep link routing

`Application.kt` overrides `notificationHandler` once — a single routing point for regular push links **and** Live Activity links:

```kotlin
notificationHandler = { _, url, overrideFlags -> routeLink(url, overrideFlags) }
```

`app://www.example.com/...` links are sent to `MainActivity`, which forwards the URI to the NavGraph deep links (`app://www.example.com/live-activities` opens the Live Activities screen); anything else (https etc.) is left to the system. Without overriding the handler the SDK resolves links with a plain `ACTION_VIEW` intent, which works as well as long as a matching intent-filter exists.

The demo campaign configuration also defines three action buttons — `OPEN_APP`, `REDIRECT` (opens the docs) and `CLOSE` (dismisses the activity) — which is the Android maximum.

### Analytics

Reported by the SDK automatically: `started` (rendered on the device), `clicked` (body tap), `clicked_1` / `clicked_2` (action buttons), `closed` (dismissed).

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
