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

- **Notifications & alerts:** added a subtle alert sound and a tap intent on
  notifications that opens the relevant hangout.
- **Create hangout:** added start/end date & time pickers with an "all day"
  switch, address search + validation, and an invite notification sent for a
  newly created event.
- **Hangout owner actions:** owners can now delete their own hangouts.
- **Invites & sharing:**
  - In-app invites — invite other users to a hangout (with search, clear
    selection and a sending spinner).
  - Off-platform sharing — share a clickable link (GitHub Pages redirect →
    `zuyp://` deep link) via WhatsApp / Messages / etc. (share intent).
  - Deep links now reliably open the hangout on both cold and warm start.
- **Privacy filter:** the home list and search only show public hangouts, plus
  private ones you created or are attending.
- **UX polish:** keyboard auto-opens when searching for friends, fixed overlay
  padding, reordered some UI.
- **Refactors:** replaced direct repository calls with use cases, split large
  screens/popups into smaller composable components, and centralized the
  current user into a single `CurrentUser` object.

## 02/06/2026

- **Profile screen:** built out the profile page — header with avatar/name/email
  and friends/groups/events stats, owned and upcoming activity sections, and a
  settings dialog. Added pull-to-refresh to reload the page.
- **Edit profile:** added an edit-profile dialog (name, email, birthdate date
  picker) backed by a new `EditProfileUseCase`, persisting changes through the
  Room user repository.
- **Settings & preferences:**
  - Added a Jetpack DataStore for settings with preference use cases and Hilt
    wiring.
  - Replaced the old permission toggles with a "launch page" selector — pick
    which screen opens on startup (persisted in DataStore, applied on next run).
- **Groups:** introduced groups end-to-end — Room entities/DAO (group +
  group-member mapping), repository and domain model. Create, edit/rename and
  leave groups from the Friends screen, with group cards and member avatar
  clusters.
- **Friends:** added friend management (add-friend dialog with search, friend
  rows + requests) and tap-to-view popups for both friends (user profile dialog)
  and group members.
- **Refactors & polish:** moved use cases into per-feature folders, shared form
  components between the create/edit group dialogs, rounded action buttons less
  aggressively, increased color contrast, and fixed the Zuyp alert overlay.

## 03/06/2026

- **Expenses — full feature (end-to-end):**
  - Domain model: `Expense`, `ExpenseShare`, `PersonBalance`.
  - Room entities and DAO: `ExpenseEntity`, `ExpenseShareEntity`,
    `SettlementEntity` (hangoutId, fromUserId, toUserId, amount, settledAt).
  - Repository: `ExpenseRepositoryRoomImpl` — reactive balance calculation that
    accumulates per-person share debt and subtracts recorded settlements.
  - Use cases: add expense, delete expense (owner only), get expenses for
    hangout, get event balances, settle debt.
  - Equal split: cent-accurate integer arithmetic to avoid floating-point drift;
    remainder assigned to the payer.
  - Custom split: per-person amount entry with live running total and validation
    that the entries sum to the expense amount.
  - `AddExpenseDialog`: paid-by picker, equal/custom split modes, participant
    chip selector, and bill receipt photo (camera or gallery).
  - `ExpensesSection`: balance summary card (who owes whom, settle button with
    confirmation dialog), expense list with per-item detail.
  - `ExpenseDetailDialog`: full breakdown of shares per person.
  - Hilt wiring: `ExpenseDao` and `ExpenseRepository` injected across the graph.

- **Discover / Map improvements:**
  - Tapping a map marker animates its size and smoothly centres the camera on
    the selected pin.
  - A hangout detail popup slides up on marker tap, showing title, location and
    date — tapping it opens the full hangout detail screen.
  - `DiscoverViewModel` updated with selected-marker state; `DiscoverUiState`
    extended accordingly.

- **Groups in hangout creation:**
  - "Invite all" button in `MembersSelector` — one tap selects all friends for
    the hangout.
  - Selecting a group in the member picker adds all group members at once.

- **Permissions architecture:**
  - Extracted `PermissionViewModel` and `PermissionManager` out of `MainActivity` so
    runtime permission requests are driven by Compose state rather than
    imperative Activity code.

- **Profile — previous events:**
  - Profile activity section now includes past events the user attended, not
    just upcoming ones.
  - Improved profile screen UI and activity card styling.

- **Architecture refactors:**
  - Reorganised all Room entities into per-domain subdirectories (hangouts,
    users, expenses) and updated all DAO / repository / UI imports.
  - All hangout UI events grouped into a single sealed interface; every user
    action now flows through a single `onEvent` dispatcher in
    `HangoutViewModel`.
  - Expense components split into focused files: `AddExpenseDialog`,
    `ExpensesSection`, `ExpenseDetailDialog`, `ExpenseImage`.
  - Tidied UI composables: split shared form helpers into their own file,
    corrected naming inconsistencies.
  - Cleaned up redundant documentation and leftover comments across the data
    and domain layers.
  - Refactored open/close methods and overlay state into a consistent single
    interface pattern.

## 04/06/2026

- **Shake sensor — auto-join a hangout (sensor + automatic action):**
  - `ShakeRepository` / `ShakeRepositoryImpl` in the data layer — wraps
    `SensorManager` in a `callbackFlow`, subtracts gravity, applies a threshold
    (12 m/s²), and only emits after continuous shaking for 1 second (400 ms
    grace period between peaks so brief dips don't reset the timer).
  - `DetectShakeUseCase` exposes the flow to the domain layer.
  - `SensorModule` provides `SensorManager` via Hilt.
  - `HangoutViewModel` subscribes when a hangout is selected and unsubscribes
    on dismiss; fires `UpdateAttendance(GOING)` automatically on shake if the
    user hasn't already joined.

- **Notifications — foreground service & survival after app kill:**
  - Replaced the old `NotificationWorker`-as-consumer pattern with a proper
    `MessagingService` (`@AndroidEntryPoint`, `START_STICKY`, foreground
    notification) that keeps the RabbitMQ connection alive even after the app
    is removed from recents.
  - `NotificationHelper` object extracted — all channel creation and
    notification display logic in one place, callable from anywhere.
  - `ZuypApplication` starts `MessagingService` on launch; no more
    `HiltWorkerFactory` / `Configuration.Provider` needed.
  - Notification grouping: hangout invite and "joined" notifications are
    grouped under a live summary ("X hangout updates") using Android's
    `setGroup` / `setGroupSummary` API.
  - Replaced deprecated window flags in `ZuypAlertActivity`
    (`FLAG_SHOW_WHEN_LOCKED`, `FLAG_TURN_SCREEN_ON`, `FLAG_DISMISS_KEYGUARD`)
    with the modern `setShowWhenLocked(true)` / `setTurnScreenOn(true)` calls.

- **Expense bug-fix & UX:**
  - Fixed a critical balance calculation bug: the net formula was inverted
    (`owe(forUser, other) − owe(other, forUser)`) making every balance display
    the wrong direction. Corrected to `owe(other, forUser) − owe(forUser, other)`.
  - Custom split now shows per-row sublabels ("you absorb", "owes you",
    "auto · owes you") so the direction of money is always explicit.
  - Tricount-style cascade auto-fill: when a participant's amount is typed it
    becomes "locked"; all unlocked participants automatically share the
    remaining sum. Adding or removing a participant re-distributes instantly.
    Switching split mode resets all locks for a clean start.

- **Social — mutual friends & user profile reuse:**
  - `DatabaseSeeder` now seeds all `FakeUsers.allUsers` (not just hangout
    attendees) and inserts the full friendship graph from
    `FakeFriendshipsDataSource` into Room so `UserRepositoryRoomImpl` can
    actually read them.
  - Koen's friend group expanded to 8 friends with proper mutual-friend
    triangles (Jan↔Lotte, Jan↔Bram, Bram↔Elise, Bram↔Tibo, Milan↔Elise,
    Sanne↔Lotte).
  - `UserProfileDialog` reused from the Friends screen in the hangout attendee
    list — tapping any attendee (other than yourself) opens the full profile
    popup showing mutual friends, stats, and an add/remove friend button.

- **Architecture refactors:**
  - All create-hangout form state (`title`, `startDateTime`, `endDateTime`,
    `isAllDay`, `memberSearch`, `selectedMembers`, `isPrivate`) moved from
    `by remember` in composables into `CreateHangoutForm` in `HomeUiState`,
    managed entirely by `HomeViewModel`.
  - `addressQueryFlow` eliminated — debounce pipeline now maps directly from
    `_uiState.map { it.addressQuery }`, removing a duplicate source of truth.
  - `currentUserAttendanceStatus` promoted from a computed function on
    `HangoutUiState` to a stored `val` maintained by the ViewModel; toggle
    logic (`nextAttendanceStatus`) moved into the `onEvent` dispatcher.
  - `HomeScreen` simplified — passes the full `uiState` object to
    `CreateHangoutPopup` and `ZuypHangoutOverlay` instead of individual fields.

- **Geofencing — auto-mark present (background task + sensor-driven action):**
  - Domain: `GeofenceRepository` interface + `MarkPresentUseCase`,
    `GetHangoutsInRadiusUseCase`.
  - Data: `GeofenceRepositoryMapboxImpl` using the Mapbox Turf library for
    circle geometry; registers/unregisters Mapbox geofences for all upcoming
    hangouts within 50 km.
  - `GeofenceCoordinator` — scoped to the foreground `MessagingService`,
    subscribes to the hangout list and keeps geofences in sync.
  - `MarkPresentWorker` (WorkManager `CoroutineWorker`) — triggered on geofence
    entry; marks the current user `PRESENT` and starts the
    `HydrationReminderScheduler`.
  - `HydrationReminderScheduler` — schedules a periodic WorkManager task that
    fires a notification every 30 minutes while the user is at an event.
  - Added `PRESENT` attendance status separate from `GOING`; shake and manual
    RSVP are blocked once `PRESENT`.
  - `BackgroundLocationRationaleDialog` shown once on Discover if foreground
    location is granted but background location is not; "Continue" triggers the
    `ACCESS_BACKGROUND_LOCATION` runtime request.

- **Permission architecture refactor:**
  - Removed all direct `ContextCompat.checkSelfPermission` / launcher calls
    from `ZuypApp` and scattered composables.
  - Single `PermissionViewModel` with a `requestPermission()` entry point and a
    `SharedFlow<PermissionResult>` for one-shot grant events — all permission
    requests now flow through the nav graph's `PermissionManager`.
  - `AppPermission.isGranted(context)` extension checks all underlying Android
    permissions at once.
  - Camera launcher moved into `HangoutOverlay` (where it belongs); result
    collected via the permission results flow so no state polling is needed.

- **Discover / Map polish:**
  - All upcoming hangouts now shown on the map (was only showing a subset).
  - Fixed time-range filter: `isAfter(now − 12h) AND isBefore(now + 30d)` (was
    OR, so it matched everything).
  - Seed hangout dates converted to relative (`inDays(n)`) helpers so they
    never expire between app launches.
  - Map markers: `allowOverlap`, `allowOverlapWithPuck`, `ignoreCameraPadding`
    and `annotationAnchor(BOTTOM)` added — pins now visible at all zoom levels
    and not hidden by the location puck or camera padding.
  - Marker icon: dark blue `LocationOn` with a matching filled circle behind it
    to plug the transparent hole; click ripple removed for a cleaner tap.
  - Shake-to-go extended to the map popup — shaking while a hangout popup is
    open marks you as going, same as in the full detail screen.

- **Navigation & UX consistency:**
  - Location field in the home screen card, the map popup, and the hangout
    detail header are all now tappable and fire a maps chooser intent
    (`geo:lat,lng`).
  - `openMapsForHangout()` extracted to a shared utility (`ui/utils/`) so the
    three call sites share one implementation.
  - `InfoRow` composable extracted to `ui/components/` — previously duplicated
    across `HangoutCard`, `HangoutPopup` and `HangoutHeader`.
  - "Mark as paid" confirmation dialog restyled to match project standard:
    `Dialog` + `Surface(RoundedCornerShape(28.dp))` with `Button` / `OutlinedButton`
    footer instead of a plain `AlertDialog` with text buttons.
  - FAB padding aligned to `16.dp` on both Home and Discover screens.
  - Seeded user names updated to reflect real project group members.

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
| Unit tests | ❌ | Only `ExampleUnitTest` stub exists. |

### Intermediate (14/20)

| Requirement | Status | What I did |
|---|---|---|
| Multiple notifications via notification channel | ✅ | Separate channels (general hangout updates vs. urgent SOS), grouped summary notifications. |
| More data types in MessageBroker (also publish) | ⚠️ | Publishing implemented (`LavinMQMessagePublisher`); 3 message types (`HangoutInvite`, `ZuypAlert`, `HangoutJoined`). More types (e.g. `arrived`, `cant_come`) still to add. |
| Geofencing | ✅ | Mapbox geofences for nearby upcoming hangouts; entry fires `MarkPresentWorker`. |
| Automatic actions from sensor data | ✅ | Shake auto-joins the viewed hangout; geofence entry auto-marks `PRESENT`. |
| Camera | ✅ | Attach a receipt photo to an expense (camera or gallery). |
| Unit & instrumented tests | ❌ | Only `ExampleInstrumentedTest` stub exists. |

### Experienced (16/20)

| Requirement | Status | What I did |
|---|---|---|
| Key vault | ❌ | Secrets currently only injected via `BuildConfig`. |
| Filtering MessageBroker data | ❌ | Incoming messages not yet filtered by user preference. |
| GPS navigation between locations | ✅ | Tapping the location field (card, map popup, detail) opens a maps chooser to the exact coordinates. |

### Going for the extra mile (18+/20)

| Requirement | Status | What I did |
|---|---|---|
| CI/CD → Firebase App Distribution | ❌ | No `.github/` workflow yet. |

---

## What I still need to do

**Must have**
- [ ] Unit tests — currently only the example stub.

**Intermediate**
- [ ] Instrumented tests — currently only the example stub.
- [ ] Publish more message-broker data types (e.g. `arrived`, `cant_come`).

**Experienced**
- [ ] Key vault — store sensitive data (RabbitMQ / Mapbox credentials) in a key vault instead of `BuildConfig`.
- [ ] Filtering — filter incoming MessageBroker messages (e.g. mute certain notification types per user preference).

**Extra mile**
- [ ] CI/CD — GitHub Actions workflow to build and deploy to Firebase App Distribution.

**Feature work still open**
- [ ] Profile: edit profile photo and IBAN.
- [ ] Fix crash when joining an event from a push notification.
- [ ] Post-event reminder to settle expenses (WorkManager periodic work).