# Synapse Database Structure (Firestore)

This document outlines the Firestore collection and document structure based on the current state of the app.

## 1. `subjects` (Collection)
Contains the main subject categories displayed on the Dashboard.

| Field | Type | Description |
| :--- | :--- | :--- |
| `id` | String | (Document ID) e.g., "physics_subject" |
| `title` | String | Upper-case main title (e.g., "QUANTUM") |
| `subtitle` | String | The subject name (e.g., "PHYSICS") |
| `bottomText`| String | Label at the bottom (e.g., "CORE") |
| `category` | String | Grouping category (e.g., "Science", "Maths") |
| `iconName` | String | Icon identifier (e.g., "Bolt", "Science") |
| `startColor`| String | Hex color for gradient start |
| `endColor` | String | Hex color for gradient end |
| `order` | Number | Sorting order on dashboard |

## 2. `topics` (Collection)
Individual units within a subject.

| Field | Type | Description |
| :--- | :--- | :--- |
| `id` | String | (Document ID) |
| `name` | String | Display name of the topic |
| `subjectIds`| Array<String> | List of Subject IDs this topic belongs to |
| `order` | Number | Vertical sorting order in topic list |

## 3. `lessons` (Sub-collection of `topics/{topicId}/lessons`)
The sequential learning nodes shown in the Duolingo-style snake trail.

| Field | Type | Description |
| :--- | :--- | :--- |
| `id` | String | (Document ID) |
| `topicId` | String | Reference to parent topic |
| `name` | String | Lesson name displayed under the circle |
| `order` | Number | Position in the snake trail |
| `studyPoints`| Array<String> | The content strings for flashcard chapters |

## 4. `users` (Collection)
User profiles and gamification stats.

| Field | Type | Description |
| :--- | :--- | :--- |
| `userId` | String | (Document ID) Firebase Auth UID |
| `level` | Number | Current user level (1-100) |
| `totalPoints`| Number | Total accumulated XP |
| `syncCoins` | Number | In-app currency |
| `correctAnswers`| Number | Count of correct flashcard sessions |
| `wrongAnswers`| Number | Count of incorrect flashcard sessions |
| `currentStreak`| Number | Daily streak count |
| `totalDays` | Number | Total days active |
| `subjectsStudiedToday`| Array<String> | IDs of subjects interacted with today |
| `yearlyStats`| Array<Map> | Monthly breakdown: `{month: String, correct: Int, wrong: Int}` |

---
**Note for AI Agents**: Always ensure any new data features or models are reflected in this file to maintain a single source of truth for the database schema.
