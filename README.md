# Valentine's Garage – Android App

A truck check-in and repair tracking system built for Android using Kotlin,
Firebase, and the MVVM architecture pattern.

---

## Project Structure

```
app/src/main/java/com/valentinesgarage/
│
├── GarageApplication.kt          ← App entry point, applies theme on start
├── MainActivity.kt               ← Single activity, hosts all fragments
│
├── data/
│   ├── model/
│   │   ├── User.kt               ← User data class (uid, name, email, role)
│   │   ├── Truck.kt              ← Truck data class (plate, km, condition, status)
│   │   └── ServiceTask.kt        ← Task data class (title, notes, isDone, mechanic)
│   └── repository/
│       └── GarageRepository.kt   ← All Firestore + Auth operations
│
├── ui/
│   ├── auth/
│   │   ├── LoginFragment.kt      ← Login screen (all roles)
│   │   └── LoginViewModel.kt
│   ├── checkin/
│   │   ├── CheckInFragment.kt    ← Receptionist: check in a truck
│   │   └── CheckInViewModel.kt
│   ├── mechanic/
│   │   ├── MechanicDashboardFragment.kt ← List of active trucks
│   │   ├── MechanicViewModel.kt
│   │   ├── TruckAdapter.kt       ← RecyclerView adapter for trucks
│   │   ├── TaskListFragment.kt   ← Checklist of tasks per truck
│   │   └── TaskAdapter.kt        ← RecyclerView adapter for tasks
│   └── admin/
│       ├── AdminDashboardFragment.kt    ← Valentine's home screen
│       ├── AdminViewModel.kt
│       ├── VehicleReportFragment.kt     ← All vehicles + conditions
│       ├── VehicleReportAdapter.kt
│       ├── EmployeeReportFragment.kt    ← Per-mechanic task report
│       └── EmployeeReportAdapter.kt
│
└── utils/
    └── ThemeUtils.kt             ← Light/dark mode toggle + persistence
```

---

## Architecture

This app follows **MVVM (Model – View – ViewModel)**:

| Layer | Responsibility |
|-------|---------------|
| **Model** | `data/model/` and `data/repository/` – data classes and Firebase calls |
| **ViewModel** | `ui/*/...ViewModel.kt` – business logic, LiveData |
| **View** | Fragments + XML layouts – display data, react to user input |

---

## User Roles

| Role | What they can do |
|------|-----------------|
| **Receptionist** | Check trucks in (plate, km, condition) |
| **Mechanic** | View active trucks, tick off tasks, add notes |
| **Admin** (Valentine) | View vehicle reports and per-employee reports |

---

## Firebase Setup (Required before running)

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create a project named **ValentinesGarage**
3. Add an Android app with package name: `com.valentinesgarage`
4. Download `google-services.json` → place it in `/app/`
5. Enable **Authentication → Email/Password**
6. Enable **Firestore Database** (start in test mode, then apply `firestore.rules`)

### Creating Users

Users must be created manually in Firebase (admin creates them):

**Step 1** – Create email/password in Firebase Auth Console  
**Step 2** – In Firestore, create a document:

```
Collection: users
Document ID: <the UID from Firebase Auth>
Fields:
  uid:   "abc123"
  name:  "John Mechanic"
  email: "john@garage.com"
  role:  "mechanic"          ← "admin" | "mechanic" | "receptionist"
```

---

## Firestore Data Structure

```
users/
  {uid}/
    uid, name, email, role

trucks/
  {truckId}/
    id, plateNumber, ownerName, kmReading, condition,
    checkedInBy, checkedInAt, status

    tasks/
      {taskId}/
        id, truckId, title, notes, isDone,
        assignedTo, assignedToName, updatedAt
```

---

## Running Unit Tests

```bash
./gradlew test
```

Tests are in:
`app/src/test/java/com/valentinesgarage/GarageUnitTests.kt`

They cover: model defaults, task toggling, input validation, km parsing, timestamps.

---

## Theme Switching

The app supports both **light** (white/blue) and **dark** (black/blue) themes.
A toggle switch in the top toolbar switches themes instantly and saves the
preference to SharedPreferences so it persists across app restarts.

---

## Team Members & Task Split (Suggested)

| Member | Screens / Components |
|--------|---------------------|
| Member 1 | Login, Auth ViewModel, Firebase setup |
| Member 2 | Check-In screen, CheckInViewModel, Truck model |
| Member 3 | Mechanic screens, TaskAdapter, TruckAdapter |
| Member 4 | Admin reports, EmployeeReportAdapter, Unit tests |

---

## Dependencies

| Library | Purpose |
|---------|---------|
| Firebase Auth | User login |
| Firebase Firestore | Real-time cloud database |
| Navigation Component | Fragment navigation by role |
| ViewModel + LiveData | MVVM state management |
| Material Components | Professional UI |
| Coroutines | Async Firebase calls |
| JUnit + Mockito | Unit testing |
