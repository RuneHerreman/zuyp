# Zuyp

> A native Android app for planning hangouts with friends, splitting the bill afterwards, and getting everyone there safely. Built around live maps, notifications, and a few sensor tricks.

<p align="left">
  <img alt="Platform" src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white">
  <img alt="Language" src="https://img.shields.io/badge/Kotlin-2.3-7F52FF?logo=kotlin&logoColor=white">
  <img alt="UI" src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white">
  <img alt="Min SDK" src="https://img.shields.io/badge/minSdk-34-555">
  <img alt="Architecture" src="https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM-orange">
</p>

Zuyp is an Android app I built on my own for the *Device Development* course (Applied Computer Science, Howest). It started as a coursework brief and turned into a full social planning app that touches most of the Android platform: local storage, REST APIs, a message broker, GPS and maps, geofencing, hardware sensors, the camera, foreground services and background work.

---

## 💡 The idea

We spend more time than ever on our phones, but what we actually want from a night out is to see people. Getting that night organised is harder than it should be:

- **Everyone's on a different app.** Messages get scattered and half of them go unread.
- **Schedules never line up.** Coordinating five friends turns into a planning project.
- **Plans quietly die.** One unanswered message and a good evening just doesn't happen.

Zuyp is built around the idea of drinking with a purpose. It's part event app, part safety net, part shared wallet. The goal is to let a group of friends:

- Throw together a hangout in under a minute, or fire off an SOS when things go sideways.
- Keep shared costs honest, so nobody's chasing anyone for money later.
- Check the weather before heading out, with a quick tip on what to wear.

---

## 📸 Screenshots

> _Drop your images into `docs/screenshots/` using the filenames below and they will show up here._

| Home (upcoming hangouts) | Discover (live map) | Hangout detail |
|:---:|:---:|:---:|
| ![Home screen](docs/screenshots/home.png) | ![Discover map](docs/screenshots/discover.png) | ![Hangout detail](docs/screenshots/hangout-detail.png) |

| Expenses & balances | Friends & groups | SOS "Zuyp Alert" |
|:---:|:---:|:---:|
| ![Expenses](docs/screenshots/expenses.png) | ![Friends](docs/screenshots/friends.png) | ![SOS alert](docs/screenshots/sos-alert.png) |

> _Optional: add a short screen capture as `docs/screenshots/demo.gif` and link it here._

---

## ✨ Features

### Plan & attend hangouts
- Browse upcoming hangouts sorted by date. Each card shows the location, time, attendee count and friend avatars.
- Create a hangout with date and time pickers, an all-day switch and address search. Owners can edit or delete their own events.
- Open a hangout for the full detail overlay, set your attendance (going or not interested), and filter to just the friends who are coming.
- The home feed only shows public hangouts plus the private ones you're actually part of.
- Invite friends inside the app, or share a link (GitHub Pages to a `zuyp://` deep link) that opens the event straight away.

### Split the bill
- Add shared expenses to a hangout, pick who paid, and attach a photo of the receipt from the camera or gallery. Balances recalculate live and stay accurate to the cent.
- Split equally, or do a custom split with Tricount-style cascade auto-fill and validation. There's a settle-up flow to clear what's owed.

### Discover (maps & location)
- A Mapbox map with your live location puck and markers for nearby hangouts.
- Tap a marker and the camera animates over to it, then a popup slides up that links through to the full hangout.
- Tap any location field (on a card, the detail header, or the map popup) to open a maps chooser routed to the exact coordinates.

### Stay connected & safe
- Add and remove friends, view their profiles, and create groups you can rename or leave, with member avatars clustered on each one.
- Heads-up and full-screen notifications come through a RabbitMQ message broker, split across separate channels for general updates and urgent alerts.
- The SOS "Zuyp Alert" is a full-screen alarm that also pulses the device flashlight, with a one-tap action to join the event right from the notification.

### Sensors & background work
- **Shake to join.** Shake your phone on a hangout you're viewing and you're in, picked up by the accelerometer.
- **Geofencing.** Walk into an event's area and you're auto-marked as present, with a hydration reminder every 30 minutes while you're there.
- **Foreground service.** Keeps the RabbitMQ connection alive after the app is killed so notifications still land.
- **Weather.** Pulls the forecast for the event location from a weather API and suggests what to wear.

---

## 🎨 Design & branding

The look mixes a bit of nostalgic, tactile warmth with clean modern lines, built on a grid that leaves room for some intentional asymmetry.

**Typography**
- **Noto Serif** for headings and titles. The serif gives it character and a more premium feel.
- **Inter** for body text and everything else, kept simple and readable.

**Palette**

| | Hex | Role |
|---|---|---|
| 🟦 | `#002366` | Primary |
| ⬜ | `#F5EFE0` | Secondary |
| ⬜ | `#FAF9F9` | Neutral |
| 🟦 | `#142A52` | Primary (faded) |
| 🟦 | `#435270` | Text |
| 🟦 | `#99BBFF` | Accent (40%) |
| ⬛ | `#4D4D4D` | Dark grey |
| ⬜ | `#E0E0E0` | Light grey |

---

## 🏗️ Architecture

The app uses Clean Architecture with an MVVM presentation layer, split into three layers under `be.runeherreman.zuyp`:

```
ui/        Jetpack Compose screens, ViewModels and UiState
            (home, discover, friends, profile, hangout, alert)
domain/    Business logic: models, repository interfaces, use cases
            (hangouts, expenses, friendship, groups, geofencing, ...)
data/      Implementations: Room, DataStore, Mapbox, RabbitMQ, Retrofit,
            sensors, workers, foreground services, secure storage
di/        Hilt modules wiring everything together
```

- ViewModels expose immutable `UiState`, and the data layer is reactive end to end with Kotlin `Flow`.
- Use cases keep each piece of business logic separate from both the UI and the repositories.
- Hilt handles dependency injection across ViewModels, repositories, workers and services.

---

## 🧰 Tech stack

| Area | Technologies |
|---|---|
| Language & UI | Kotlin, Jetpack Compose, Material 3, custom theme (Noto Serif / Inter) |
| Architecture | Clean Architecture, MVVM, Hilt (Dagger) DI |
| Persistence | Room (users, hangouts, friendships, groups, expenses), DataStore (settings) |
| Networking | Retrofit + Moshi + OkHttp (weather API), Coil (image loading) |
| Maps & location | Mapbox Maps / Search / Turf, geofencing, GPS location puck |
| Messaging | RabbitMQ (CloudAMQP) over SSL/vhost, both consume and publish |
| Background | WorkManager, foreground service, periodic workers |
| Sensors & hardware | Accelerometer (shake), flashlight, camera |
| Security | `EncryptedSharedPreferences` for broker credentials |
| Testing | JUnit, MockK, coroutines-test, Compose UI test, Espresso, Hilt testing |

---

## 🧪 Testing

- Unit tests for business logic: `AddExpenseUseCaseTest`, `UpdateAttendanceUseCaseTest`, `PublishMessageTest`.
- Instrumented tests for persistence and UI: `ExpenseDaoTest`, `UserDaoTest`, `HomeScreenTest`, `FriendsScreenTest`, `NavigationTest`, `HomeViewModelTest`.

```bash
./gradlew test                 # unit tests
./gradlew connectedAndroidTest # instrumented tests (device/emulator)
```

---

## 🚀 Getting started

**Requirements:** Android Studio (latest), JDK 11+, and an Android 14 (API 34) device or emulator.

1. Clone the repo and open it in Android Studio.
2. Copy the example properties file and fill in your own keys:
   ```bash
   cp gradle.properties.example gradle.properties
   ```
   You'll need a Mapbox download token and access token, a weather API base URL, and CloudAMQP (RabbitMQ) credentials. Put the Mapbox access token in `app/src/main/res/values/mapbox_access_token.xml`, replacing the `YOUR_MAPBOX_TOKEN` placeholder.
3. Run the app from Android Studio, or:
   ```bash
   ./gradlew installDebug
   ```

> The project ships with seeded fake data, so you can explore the whole app without running any backend.

---

## 📐 Coursework context

I built this on my own for the *Device Development* module to cover a wide slice of native Android: Compose UI, Room, Retrofit, a message broker, GPS and maps, geofencing, two sensors, the camera, notification channels, WorkManager, encrypted storage and an automated test suite. The day-by-day development log is kept in the git history.

---

## 👤 Author

**Rune Herreman**, Applied Computer Science, Howest.
