# 🎲 Decidr: The Mini Decision Maker

**Decidr** is a minimalist, offline-first Android application designed to cure decision fatigue. Simply input your options, and let Decidr make the choice for you with a fun "slot machine" animation. It keeps a beautiful, searchable local history of all your past decisions.

![Android](https://img.shields.io/badge/Android-7.0+-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-11+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Material 3](https://img.shields.io/badge/Material-3-6200EE?style=for-the-badge)
![Offline](https://img.shields.io/badge/Mode-100%25%20Offline-4CAF50?style=for-the-badge)

## 📸 Screenshots

| Home & Input | Slot Machine Animation | Search & History |
| :---: | :---: | :---: |
| ![Input Screen](https://github.com/Create-cloud/decidr/blob/main/screenshot_1.png?raw=true) | ![Animation](https://github.com/Create-cloud/decidr/blob/main/screenshot_2.png?raw=true) | ![History](https://github.com/Create-cloud/decidr/blob/main/screenshot_3.png?raw=true) |
| *Enter your dilemma and options* | *Watch the options shuffle before deciding* | *Search, export, or clear your history* |

## ✨ Features

- **Zero Friction Input:** Comma-separated entry for lightning-fast option generation.
- **Slot Machine Animation:** A fun, engaging visual effect that shuffles through your options before landing on the final choice.
- **Offline First:** 100% local storage using Room Database. No accounts, no internet required.
- **Real-Time Search:** Instantly filter your decision history by prompt or result using SQL LIKE queries.
- **Data Export:** Export your entire decision history to a cleanly formatted `.txt` file using the modern Storage Access Framework.
- **Share Intent:** Share your latest decision directly to WhatsApp, SMS, or any other app.
- **Reactive UI:** Built with `LiveData` and `ViewModel` for seamless, lifecycle-aware updates.
- **Material Design 3:** Clean, modern, and accessible UI components with semantic colors (supports Light/Dark mode automatically).
- **Smart Empty States:** Polished UX that guides the user when the history list or search results are empty.

## 🏗️ Architecture & Tech Stack

Decidr strictly follows the **MVVM (Model-View-ViewModel)** architectural pattern to ensure a clean separation of concerns.

```
[ UI Layer (Activity / XML / ViewBinding) ] 
          ↕ (Observes LiveData / Calls Methods)
[ ViewModel Layer (MainViewModel) ] 
          ↕ (Calls Repository)
[ Data Layer (DecisionRepository) ] 
          ↕ (Abstracts Data Source / Background Threading)
[ Local Database (Room DAO / Entities) ]
```

### Core Technologies
*   **Language:** Java
*   **UI:** XML Layouts, ViewBinding, Material Components 3
*   **Architecture:** MVVM (Model-View-ViewModel)
*   **Local Storage:** Room Persistence Library (SQLite)
*   **Async/Threading:** ExecutorService (for Room background threads & File I/O)
*   **Reactivity:** AndroidX Lifecycle (LiveData, ViewModel)
*   **System Integration:** Android Intents (Share), Storage Access Framework (Export)

## 🚀 Setup & Installation

### Prerequisites
*   Android Studio (Hedgehog, Iguana, or newer recommended)
*   JDK 11 or higher
*   Android SDK (Min SDK 24 / Android 7.0)

### Steps to Run
1.  **Clone the repository:**
    ```
    git clone https://github.com/Create-cloud/decidr.git
    cd decidr
    ```
2.  **Open in Android Studio:**
    *   Open Android Studio → `File` → `Open` → Select the `decidr` folder.
3.  **Sync Gradle:**
    *   Allow Android Studio to download dependencies and sync the project.
4.  **Run the App:**
    *   Connect an Android device (with USB debugging enabled) or start an Android Emulator.
    *   Click the green **Run ▶️** button in the toolbar.

## 📂 Project Structure

```
app/src/main/java/com/decidr/app/
├── data/               # Data Layer
│   ├── AppDatabase.java
│   ├── DecisionDao.java
│   ├── DecisionEntry.java
│   └── DecisionRepository.java
├── MainActivity.java   # UI Layer (Activity)
├── MainViewModel.java  # Presentation Layer (ViewModel)
└── HistoryAdapter.java # RecyclerView Adapter
```

## 📝 License

This project is open-source and available under the [MIT License](LICENSE).
