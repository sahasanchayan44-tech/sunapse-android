# Synapse AI Agents Guide

This file serves as a guide for AI agents working on the Synapse project.

## Build & Testing
- **Always run** `./gradlew :app:compileDebugKotlin` after making changes to verify compilation
- Fix any compilation errors before considering a task complete
- Test on both light and dark themes when modifying UI components

## Database Consistency
All data models and Firestore structures must remain consistent with the documentation.

**IMPORTANT**: If you modify any data models in the Kotlin code or introduce new Firestore collections/fields, you **must** update the `database_structure.md` file.

### Reference Files:
- `database_structure.md`: Defines the current Firestore schema
- `app/src/main/java/com/example/synapse/models/FirestoreModels.kt`: Kotlin data classes mapped to Firestore

## UI/Design Guidelines

### Typography
- App uses `FontFamily.Monospace` (terminal-style font) throughout
- All text styles defined in `app/src/main/java/com/example/synapse/ui/theme/Theme.kt`
- Use MaterialTheme typography tokens (`titleLarge`, `bodyMedium`, etc.)

### Color Parsing
When parsing hex color strings from Firestore, always use safe parsing:
```kotlin
private fun parseColor(colorString: String, fallback: Color): Color {
    return try {
        if (colorString.isNotBlank() && colorString.startsWith("#")) {
            Color(android.graphics.Color.parseColor(colorString))
        } else fallback
    } catch (e: Exception) { fallback }
}
```
**Never** use `Color(string.toColorInt())` directly without try-catch.

### Neumorphic Design Language
- App uses neumorphic UI style with soft shadows
- Neumorphic components in `ui/components/NeumorphicComponents.kt`
- Dark mode colors in `ui/theme/Color.kt` (`DarkNeuBackground`, `DarkNeuShadowLight`, etc.)
- Light mode colors use `NeuBackground`, `NeuShadowLight`, etc.

### Firestore Repository Pattern
All database operations return `FirestoreRepository.Result<T>`:
```kotlin
when (val result = repository.getSubjects()) {
    is FirestoreRepository.Result.Success -> { /* use result.data */ }
    is FirestoreRepository.Result.Error -> { /* handle error */ }
}
```
**Never** use bare `try-catch` with empty catch blocks.

### State Management
- Use `mutableStateOf` for UI state in ViewModels
- Clear state on logout (e.g., `userStats = UserProfileStats()`)
- Use `LaunchedEffect` for async operations in Composables

### Animations
- Use `animateFloatAsState` with `Spring` specs for smooth animations
- Match neumorphic design when creating custom animated components
- Support both light and dark themes in animation colors

## Rank System
- Levels 1-100 map to 10 rank tiers (levels 1-10 = Novice, 11-20 = Seeker, etc.)
- Rank index formula: `rankIdx = ((level - 1) / 10).coerceIn(0, 9)`
- Rank names: Novice, Seeker, Scholar, Adept, Sage, Expert, Master, Grandmaster, Legend, Transcendent

## Common Issues & Fixes

### Color Crashes
If `subject.startColor` or `subject.endColor` is empty/malformed, app will crash.

### Index Out of Bounds in Rank Calculation
Always use `.coerceIn(0, 9)` when calculating rank index from level.

### NPE in Google Sign-In
Always check `account.idToken != null` before using it.

### Year-Independent Date Comparison
When checking if a date is "today", always compare year, month, AND day.

## Project Structure
```
app/src/main/java/com/example/synapse/
├── auth/
│   └── AuthViewModel.kt          # Authentication + user stats
├── data/
│   └── FirestoreRepository.kt    # Database operations
├── models/
│   ├── FirestoreModels.kt        # Firestore data classes
│   └── Models.kt                 # UI models
├── ui/
│   ├── components/               # Reusable UI components
│   ├── screens/                  # Screen composables
│   └── theme/                    # Theme, colors, typography
└── MainActivity.kt
```

## Dependency Injection
- ViewModels created in `SynapseApp.kt`
- Pass `AuthViewModel` and `ThemeViewModel` to screens
- Repository instantiated directly in ViewModels (no DI framework)
