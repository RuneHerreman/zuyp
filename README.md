# Zuyp — Plan hangouts, split the bill, get there safe

> A native Android app for planning casual hangouts with friends, keeping nights
> out transparent, and splitting shared expenses — with live maps, smart
> notifications, and sensor-driven shortcuts.

<p align="left">
  <img alt="Platform" src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white">
  <img alt="Language" src="https://img.shields.io/badge/Kotlin-2.3-7F52FF?logo=kotlin&logoColor=white">
  <img alt="UI" src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white">
  <img alt="Min SDK" src="https://img.shields.io/badge/minSdk-34-555">
  <img alt="Architecture" src="https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM-orange">
</p>

Zuyp is a solo-built Android app from my *Device Development* course (Applied
Computer Science, Howest). It started as a coursework brief and grew into a
fully-featured social planning app exploring the breadth of the Android
platform — local persistence, REST APIs, a message broker, GPS & maps,
geofencing, hardware sensors, the camera, foreground services and background
work.

---

## 📸 Screenshots

> _Drop your images into `docs/screenshots/` using the filenames below and they
> will render here automatically._

| Home — upcoming hangouts | Discover — live map | Hangout detail |
|:---:|:---:|:---:|
| ![Home screen](docs/screenshots/home.png) | ![Discover map](docs/screenshots/discover.png) | ![Hangout detail](docs/screenshots/hangout-detail.png) |

| Expenses & balances | Friends & groups | SOS "Zuyp Alert" |
|:---:|:---:|:---:|
| ![Expenses](docs/screenshots/expenses.png) | ![Friends](docs/screenshots/friends.png) | ![SOS alert](docs/screenshots/sos-alert.png) |

> _Optional: add a short screen-capture as `docs/screenshots/demo.gif` and link it here._

---

## ✨ Features

### Plan & attend hangouts
- Browse upcoming hangouts sorted by date, with location, time, attendee count
  and friend avatars on each card.
- Create hangouts with date/time pickers, an all-day switch and address search;
  the owner can edit or delete their own events.
- Hangout detail overlay with attendance status (going / not interested) and a
  "friends attending" filter.
- **Privacy model:** the home feed shows public hangouts plus the private ones
  you're actually part of.
- Invite friends in-app, or share an off-platform link (GitHub Pages →
  `zuyp://` deep link) that opens the event straight in the app.

### Split the bill
- Add shared expenses to a hangout with a paid-by selector, an attached receipt
  photo (camera or gallery) and reactive, cent-accurate balance calculation.
- **Equal split** or **custom split** with Tricount-style cascade auto-fill and
  validation, plus a settle-up flow.

### Discover (maps & location)
- Mapbox map showing your live location puck and markers for nearby hangouts.
- Tapping a marker animates and centres the camera, then slides up a popup that
  links through to the full hangout detail.
- Tap any location field (card, detail header, map popup) to open a maps
  chooser routed to the exact coordinates (`geo:` intent).

### Stay connected & safe
- Friendship graph (add/remove friends, view profiles) and groups
  (create / rename / leave, member avatar clusters).
- **Heads-up & full-screen notifications** over a RabbitMQ message broker, with
  separate channels for general hangout updates vs. urgent alerts.
- **SOS "Zuyp Alert":** a full-screen alarm activity that also pulses the
  device flashlight, with a one-tap action to join the event from the
  notification.

### Smart automation (sensors & background)
- **Shake to join** — the accelerometer auto-joins the hangout you're viewing.
- **Geofencing** — entering an event's area auto-marks you as `PRESENT` and a
  periodic hydration reminder pings every 30 minutes while you're there.
- **Foreground service** keeps the RabbitMQ connection alive after the app is
  killed so notifications still arrive.
- **Weather:** forecast at the event location with a "what to wear" dresscode
  tip, fetched from an external weather API.

---

## 🏗️ Architecture

The app follows **Clean Architecture** with an **MVVM** presentation layer,
organised into three layers under `be.runeherreman.zuyp`:

```
ui/        Jetpack Compose screens, ViewModels and UiState
            (home · discover · friends · profile · hangout · alert)
domain/    Business logic — models, repository interfaces, use cases
            (hangouts · expenses · friendship · groups · geofencing · …)
data/      Implementations — Room, DataStore, Mapbox, RabbitMQ, Retrofit,
            sensors, workers, foreground services, secure storage
di/        Hilt modules wiring everything together
```

- **Unidirectional data flow:** ViewModels expose immutable `UiState`; the data
  layer is reactive end-to-end via Kotlin `Flow`.
- **Use cases** isolate each piece of business logic from both the UI and the
  repositories.
- **Dependency injection** with Hilt across ViewModels, repositories, workers
  and services.

---

## 🧰 Tech stack

| Area | Technologies |
|---|---|
| Language & UI | Kotlin, Jetpack Compose, Material 3, custom theme (Noto Serif / Inter) |
| Architecture | Clean Architecture, MVVM, Hilt (Dagger) DI |
| Persistence | Room (users, hangouts, friendships, groups, expenses), DataStore (settings) |
| Networking | Retrofit + Moshi + OkHttp (weather API), Coil (image loading) |
| Maps & location | Mapbox Maps / Search / Turf, geofencing, GPS location puck |
| Messaging | RabbitMQ (CloudAMQP) over SSL/vhost — consume **and** publish |
| Background | WorkManager, foreground service, periodic workers |
| Sensors & hardware | Accelerometer (shake), flashlight, camera |
| Security | `EncryptedSharedPreferences` for broker credentials |
| Testing | JUnit, MockK, coroutines-test, Compose UI test, Espresso, Hilt testing |

---

## 🧪 Testing

- **Unit tests** for business logic: `AddExpenseUseCaseTest`,
  `UpdateAttendanceUseCaseTest`, `PublishMessageTest`.
- **Instrumented tests** for persistence and UI: `ExpenseDaoTest`,
  `UserDaoTest`, `HomeScreenTest`, `FriendsScreenTest`, `NavigationTest`,
  `HomeViewModelTest`.

```bash
./gradlew test                 # unit tests
./gradlew connectedAndroidTest # instrumented tests (device/emulator)
```

---

## 🚀 Getting started

**Requirements:** Android Studio (latest), JDK 11+, an Android 14+ (API 34)
device or emulator.

1. Clone the repo and open it in Android Studio.
2. Copy the example properties file and fill in your own keys:
   ```bash
   cp gradle.properties.example gradle.properties
   ```
   You'll need a **Mapbox** download token + access token, a **weather API**
   base URL, and **CloudAMQP (RabbitMQ)** credentials. Put the Mapbox access
   token in `app/src/main/res/values/mapbox_access_token.xml` (replace the
   `YOUR_MAPBOX_TOKEN` placeholder).
3. Run the app from Android Studio, or:
   ```bash
   ./gradlew installDebug
   ```

> The project ships with seeded fake data, so the app is fully explorable
> without any backend running.

---

## 📐 Coursework context

Built solo for the *Device Development* module to demonstrate native Android
breadth — Compose UI, Room, Retrofit, a message broker, GPS/maps, geofencing,
two sensors, the camera, notification channels, WorkManager, encrypted storage
and an automated test suite. A detailed day-by-day development log is preserved
in the project's git history.

---

## 👤 Author

**Rune Herreman** — Applied Computer Science, Howest.
