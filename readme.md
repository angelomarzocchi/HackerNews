# 📰 HackerNews Client

A modern and sleek Android client for browsing Hacker News, built with the latest Android development technologies.

![Screenshot_2025-11-07-19-13-53-66_12873b5ca0377f18cb474135db30cc11](https://github.com/user-attachments/assets/6a3deeec-bfe3-40f4-9f18-25744fbf3f2b)

## ✨ Project Features

This application is designed to offer a smooth and responsive user experience, leveraging the power of **Jetpack Compose** for the UI and the **Paging 3** library for efficient data management.

* **Infinite & Efficient Navigation:** Powered by the **Paging 3** library, the app loads stories incrementally, ensuring high performance while minimizing data and memory usage.
* **Modern Design (Material 3):** The interface is built entirely with Material Design 3, offering a clean, consistent look with support for dynamic theming and dark mode.
* **Multiple Categories:** Easily browse the most popular stories (**Top**), the newest ones (**New**), and the all-time greats (**Best**) via a convenient selector.
* **Smooth UX:** Includes native features like "Pull-to-refresh" to update content and smooth loading animations.
* **External Links:** Direct integration to open full articles in the device's default browser.

## 🛠 Tech Stack

The project is written entirely in **Kotlin** and follows the **MVVM (Model-View-ViewModel)** architecture to ensure maintainability and testability.

* **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
* **Architecture:** MVVM & Repository Pattern
* **Networking:** [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/)
* **Data Handling:** [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3) for paginated data loading.
* **Concurrency:** Kotlin Coroutines & Flow
* **Serialization:** Kotlinx Serialization
* **Dependency Injection:** Manual Dependency Injection (AppContainer)

## 🚀 Getting Started

To try the application on your device or emulator:

1.  Clone the repository:
    ```bash
    git clone [https://github.com/angelomarzocchi/HackerNews.git](https://github.com/angelomarzocchi/HackerNews.git)
    ```
2.  Open the project in **Android Studio**.
3.  Let Gradle sync the dependencies.
4.  Run the app (`Run 'app'`).

## 📸 Preview

The interface displays a clean list of news items with essential details like author, score, and comment count, all wrapped in modern and readable cards.

---
*Made with ❤️ and Kotlin.*
