# Zuyp – Drink & Social App

Device Development project (Semester 4, Toegepaste Informatica).
Zuyp is an Android app for quickly planning hangouts, staying connected with
friends, and keeping nights out transparent and safe.

> A separate document explains the features in detail. This README is used to
> **track progress** during the project week.

## Table of contents
- [Before this week](#what-i-did-before-the-start-of-the-project-week)
- [Day 1 (01/06/2026)](#01062026)
- [Day 2 (02/06/2026)](#02062026)
- [Day 3 (03/06/2026)](#03062026)
- [Day 4 (04/06/2026)](#04062026)
- [Day 5 (05/06/2026)](#05062026)
- [Day 6 (06/06/2026)](#06062026)
- [Day 7 (07/06/2026)](#07062026)
- [Day 8 (08/06/2026)](#08062026)
- [TO-DO](#what-i-still-need-to-do)

---

## What I did before the start of the project week

Foundations and most of the core app were built in the weeks leading up to the
project week (Apr 10 – May 31).

**Project setup & architecture**
- Android project with Hilt dependency injection and a clean
  domain / data / ui layering (models, repositories, use cases).
- Bottom navigation bar across the main screens (Home, Discover, Friends,
  Profile) with Material 3 theming, custom fonts (Noto Serif / Inter) and colors.

**Data layer**
- Room database for local storage (users, hangouts, friendships) with foreign
  keys, unique constraints and stable seeded UUIDs.
- Reactive data via Kotlin `Flow`, fake seed data for development.

**Hangouts (Home + details)**
- Home list of upcoming hangouts, sorted by date, with hangout cards showing
  location, time, attendee count and friend avatars.
- Search overlay and pull-to-refresh.
- Hangout detail overlay with attendance status (going / not interested) and a
  "friends attending" filter.
- Create-hangout flow.

**Friends**
- Friendship management (add/remove friends, see friends attending an event).

**Weather API (external API call)**
- Integration with a weather service to show the forecast at the event location
  and suggest what to wear (dresscode tip).

**Messaging & notifications (message broker)**
- Messaging foundation over CloudAMQP (RabbitMQ) with SSL/vhost.
- Heads-up and full-screen notifications with hangout info and notification
  permissions.
- SOS "Zuyp Alert" full-screen activity, flashlight on urgent alert, and a
  receiver to join an event straight from the notification.
- Separate notification channels (general hangout updates vs. urgent SOS
  alerts), each with its own importance and sound.

**Discover**
- Mapbox map integration and location permissions for the Discover screen.

## 01/06/2026

- Notification tap intent + subtle alert sound.
- Create-hangout: date/time pickers with all-day switch, address search, invite notification on create.
- Owner can delete their own hangout.
- In-app user invites; off-platform share link via GitHub Pages → `zuyp://` deep link.
- Privacy filter: home list shows only public hangouts and private ones you're part of.
- UX fixes (padding, keyboard, ordering); replaced repo calls with use cases; split large screens into components; centralized `CurrentUser`.

## 02/06/2026

- Profile screen: avatar/name/stats header, owned and upcoming activity sections, settings dialog, pull-to-refresh.
- Edit profile dialog (name, email, birthdate) backed by Room via `EditProfileUseCase`.
- DataStore settings + startup screen selector persisted in DataStore.
- Groups end-to-end: Room entities, create/edit/rename/leave, group cards, member avatar clusters.
- Friend management: add-friend dialog, friend rows, tap-to-view profile popups.
- Moved use cases into per-feature folders; shared form components; color/button polish.

## 03/06/2026

- Expenses end-to-end: domain model, Room entities/DAO, reactive balance calculation, use cases (add/delete/settle), equal split (cent-accurate), custom split with validation, `AddExpenseDialog` (paid-by, split modes, photo), `ExpensesSection`, `ExpenseDetailDialog`.
- Map: marker tap animates + centres camera, hangout popup slides up and links to full detail screen.
- `MembersSelector`: invite-all button + group invite (adds all members at once).
- `PermissionViewModel` and `PermissionManager` extracted from `MainActivity`.
- Profile: past attended events added; UI and card styling improved.
- Room entities reorganised into per-domain subdirectories; hangout UI events unified into one sealed interface; components split and cleaned up.

## 04/06/2026

- Shake sensor auto-joins viewed hangout (`ShakeRepository` → `DetectShakeUseCase` → `HangoutViewModel`).
- `MessagingService` foreground service replaces `NotificationWorker`; keeps RabbitMQ alive after app kill. Notification grouping via `setGroup`/`setGroupSummary`.
- Expense balance direction bug fixed; custom split gets cascade auto-fill (Tricount-style locking).
- Full friendship graph seeded; `UserProfileDialog` reused in attendee list.
- Create-hangout form state moved from composables into `HomeViewModel`.
- Geofencing: `MarkPresentWorker` auto-marks `PRESENT` on entry; `HydrationReminderScheduler` pings every 30 min; `BackgroundLocationRationaleDialog` added.
- Permission architecture unified — single `PermissionViewModel` with `SharedFlow<PermissionResult>`.
- Map: all hangouts shown, time filter fixed (OR → AND), relative seed dates, marker and FAB polish; shake-to-go on map popup too; location fields tappable (`geo:` intent).

## 05/06/2026

- AMQP credentials stored in Android `EncryptedSharedPreferences` via `SecureStorage` / `CredentialsRepository` (wired with Hilt).
- Adding an expense now executes in a single Room transaction.
- Creating a hangout automatically adds yourself as `GOING`.
- Shake listener updated to use explicit start/stop lifecycle methods.
- Location field tappable in hangout card, detail header, and map popup; `openMapsForHangout()` extracted to shared utility.
- Map marker style improved (filled hole); settle dialog restyled; FAB padding aligned.

## 06/06/2026

- Unit tests: `AddExpenseUseCaseTest`, `UpdateAttendanceUseCaseTest`, `PublishMessageTest`.
- Instrumented tests: `ExpenseDaoTest`, `UserDaoTest`, `HomeScreenTest`, `FriendsScreenTest`, `NavigationTest`, `HomeViewModelTest`.
- Geofence time-range filter fixed (events must be within active window).

## 07/06/2026

- Geofence registration bug fixed.
- Present members are shown back in the attendees list.

## 08/06/2026

- Shake listener reference counting added; user location updates disabled when not actively needed.
- Geofence logic cleaned up; permission guard added for when location is not granted.
- Full-screen intent permission checked before displaying alert.
- Database index changed and version bumped.
- Present marking bug fixed.
- Additional tests added.

## Requirement checklist — status overview

Legend: ✅ done · ⚠️ partly done · ❌ not started

### Must have (12/20)

| Requirement | Status | What I did |
|---|---|---|
| Building native UI (Jetpack Compose) | ✅ | Whole UI is Compose — screens, overlays, dialogs, custom components. |
| Multi-screen app (min. 4 screens) | ✅ | Home, Discover, Friends, Profile + Hangout detail. |
| Menu-based navigation | ✅ | Bottom navigation bar (`ZuypBottomBar`) across the main screens. |
| Material design (custom theme/icon) | ✅ | Material 3 theme, custom colors, Noto Serif / Inter fonts, app icon. |
| Android app architecture | ✅ | ViewModel + UiState, repositories, use cases, Hilt DI, clean domain/data/ui layering. |
| Room database | ✅ | Users, hangouts, friendships, groups, expenses with FKs, unique constraints, seeded data. |
| Retrofit (≥ 1 API) | ✅ | Weather API via Retrofit; Mapbox geocoding for addresses. |
| WorkManager (background task) | ✅ | `MarkPresentWorker` + `HydrationReminderScheduler` (periodic notification every 30 min at an event). |
| 2 intents | ✅ | Share intent (off-platform invite link) + maps `geo:` chooser intent + camera capture intent. |
| MessageBroker | ✅ | CloudAMQP (RabbitMQ) consume over SSL/vhost via `LavinMQMessageConsumer`. |
| GPS data (Mapbox map + current location) | ✅ | Discover screen Mapbox map with location puck and hangout markers. |
| 2 sensors | ✅ | Accelerometer (shake → auto-join) + flashlight driven on urgent SOS alert. |
| Notifications | ✅ | Heads-up + full-screen notifications, tap intent to open hangout. |
| Unit tests | ✅ | `AddExpenseUseCaseTest`, `UpdateAttendanceUseCaseTest`, `PublishMessageTest`. |

### Intermediate (14/20)

| Requirement | Status | What I did |
|---|---|---|
| Multiple notifications via notification channel | ✅ | Separate channels (general hangout updates vs. urgent SOS), grouped summary notifications. |
| More data types in MessageBroker (also publish) | ⚠️ | Publishing implemented (`LavinMQMessagePublisher`); 3 message types (`HangoutInvite`, `ZuypAlert`, `HangoutJoined`). More types (e.g. `arrived`, `cant_come`) still to add. |
| Geofencing | ✅ | Mapbox geofences for nearby upcoming hangouts; entry fires `MarkPresentWorker`. |
| Automatic actions from sensor data | ✅ | Shake auto-joins the viewed hangout; geofence entry auto-marks `PRESENT`. |
| Camera | ✅ | Attach a receipt photo to an expense (camera or gallery). |
| Unit & instrumented tests | ✅ | `ExpenseDaoTest`, `UserDaoTest`, `HomeScreenTest`, `FriendsScreenTest`, `NavigationTest`, `HomeViewModelTest`. |

### Experienced (16/20)

| Requirement | Status | What I did |
|---|---|---|
| Key vault | ⚠️ | AMQP credentials stored in `EncryptedSharedPreferences` via `SecureStorage`; credentials still ship in APK raw. |
| Filtering MessageBroker data | ✅ | Filter data die binnenkomt via de MessageBroker. |
| GPS navigation between locations | ✅ | Tapping the location field (card, map popup, detail) opens a maps chooser to the exact coordinates. |

### Going for the extra mile (18+/20)

| Requirement | Status | What I did |
|---|---|---|
| CI/CD → Firebase App Distribution | ❌ | No `.github/` workflow yet. |

---

## What I still need to do

**Must have**
- [x] Unit tests — `AddExpenseUseCaseTest`, `UpdateAttendanceUseCaseTest`, `PublishMessageTest`.

**Intermediate**
- [x] Instrumented tests — `ExpenseDaoTest`, `UserDaoTest`, `HomeScreenTest`, `FriendsScreenTest`, `NavigationTest`, `HomeViewModelTest`.
- [ ] Publish more message-broker data types (e.g. `arrived`, `cant_come`).

**Experienced**
- [x] Key vault — AMQP credentials stored in `EncryptedSharedPreferences` via `SecureStorage` (credentials still ship in APK raw).
- [x] Filtering — filter data die binnenkomt via de MessageBroker.

**Extra mile**
- [ ] CI/CD — GitHub Actions workflow to build and deploy to Firebase App Distribution.

**Feature work still open**
- [ ] Profile: edit profile photo and IBAN.
- [ ] Fix crash when joining an event from a push notification.
- [ ] Post-event reminder to settle expenses (WorkManager periodic work).