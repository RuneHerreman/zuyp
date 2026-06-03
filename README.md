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

## What I still need to do

Against the project requirement checklist:

**Must have**
- [ ] Background task: hourly hydration reminders while at an event, and a
      post-event reminder to fill in expenses / settle debts.
- [ ] Second sensor: shake (accelerometer) to join the event you're viewing.
- [ ] Unit tests (only example stubs exist for now).

**Intermediate**
- [ ] Publish more message-broker data types (e.g. "arrived" / "can't come").
- [ ] Geofencing — notify the group when someone enters an event's area.
- [ ] Trigger actions automatically from sensor data (auto-join on shake).
- [ ] Camera: attach a photo of the receipt to an expense (fallback: profile
      photo).
- [ ] Instrumented tests.

**Feature work still open**
- [ ] Expenses tracking (the expenses section is currently a placeholder).
- [x] Profile screen: edit name, email and birthdate. _(photo and IBAN still
      open; permission toggles dropped in favour of a launch-page setting)_
- [x] Groups management on the Friends screen (create, edit/rename, leave).
- [ ] Profile: edit profile photo and IBAN.
