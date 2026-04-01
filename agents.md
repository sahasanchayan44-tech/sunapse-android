# Synapse AI Agents Guide

This file serves as a guide for AI agents working on the Synapse project.

## Database Consistency
All data models and Firestore structures must remain consistent with the documentation. 

**IMPORTANT**: If you modify any data models in the Kotlin code or introduce new Firestore collections/fields, you **must** update the `database_structure.md` file located in the project root.

### Reference Files:
- `database_structure.md`: Defines the current Firestore schema.
- `app/src/main/java/com/example/synapse/models/FirestoreModels.kt`: Contains the Kotlin data classes mapped to Firestore.
