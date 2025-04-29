# Notica Music Player

Notica is a modern Android music player application built to demonstrate the implementation of Foreground Services in Android. The app showcases how to create and manage a persistent music playback service with a custom notification interface.

## Features

- 🎵 Continuous music playback using Foreground Service
- 🔔 Custom media notification with playback controls
- 🎨 Modern UI built with Jetpack Compose
- ⚡ Background playback support
- 🎮 Media controls (play, pause, next, previous)
- 📱 Responsive design with a bottom player bar

## Screenshots

### Main Player Interface
![Screenshot_1745960181](https://github.com/user-attachments/assets/a0a45871-03a2-4da9-9579-b8da5fc35328)


*Main screen with playlist and playback controls*

### Custom Notification
![Screenshot_1745960184](https://github.com/user-attachments/assets/da80d239-065a-4b5e-a442-0e2c7f695b01)


*Persistent notification with media controls*

## Technical Implementation

The application demonstrates several key Android development concepts:

- **Foreground Service**: Implements `MusicPlayerService` for continuous playback
- **Custom Notifications**: Features a custom notification layout with media controls
- **Jetpack Compose UI**: Modern declarative UI with components like:
  - Dynamic playlist
  - Bottom player bar
  - Custom playback controls
- **State Management**: Handles playback state across service and UI
- **Media Player Integration**: Uses Android's MediaPlayer for audio playback

## Architecture

The app follows a clean architecture approach with these main components:

- `MainActivity`: Main UI container and service interaction
- `MusicPlayerService`: Handles background playback and notifications
- `Song`: Data model for music tracks
- Composable UI components:
  - `MusicPlayerApp`
  - `TopBar`
  - `SongItem`
  - `BottomPlayerBar`

## Requirements

- Android SDK 21+
- Kotlin 1.8+
- Android Studio Arctic Fox or newer

## Getting Started

1. Clone the repository
2. Open the project in Android Studio
3. Run the app on an emulator or physical device

## Permissions

The app requires the following permissions:

```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

## Links

[Medium](https://medium.com/@mert_aapa/building-a-music-player-with-foreground-service-in-android-d7a6f2b4b29e)

