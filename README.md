# ✨ Yene Shop Manager ✨

Yene Shop is a feature-rich mobile application designed for managing and selling items, with robust functionality implemented in Java using modern Android development practices. It leverages Android's Room database for local data storage and a modular architecture to ensure scalability and maintainability.

---

## 📋 Project Overview

- **Language**: Java
- **Build Tool**: Gradle (Kotlin DSL)
- **Architecture**: Modular Android application with Room Database integration
- **Features**:
  - 🛍️ Item management: Add, update, and delete items.
  - 📊 Sales tracking and reporting.
  - 🖌️ User-friendly UI with custom animations and responsive layouts.
  - 🧭 Navigation using Fragments and Activity-based screens.

---

## 📂 Directory Structure

Below is the high-level directory structure:

```
abeelgetahun-yene-shop/
├── build.gradle.kts                # Project-level Gradle configuration
├── settings.gradle.kts             # Module inclusion settings
├── app/                            # Main application module
│   ├── build.gradle.kts            # App-level Gradle configuration
│   ├── src/                        # Source code and resources
│   │   ├── main/                   # Main source set
│   │   │   ├── java/               # Application source code
│   │   │   ├── res/                # Resources (layouts, drawables, animations, etc.)
│   │   │   └── AndroidManifest.xml # Application manifest
│   │   ├── androidTest/            # Instrumented tests
│   │   └── test/                   # Unit tests
└── gradle/                         # Gradle wrapper and library versions

For a detailed breakdown of the project directory structure, [**View Details**](https://github.com/abeelgetahun/Yene-shop/blob/master/DIRECTORY-README.md).

```

---

## 🌟 Key Features

1. **🏠 Home Activity**:
   - Acts as the main entry point to the app.
   - Provides navigation to different features like item lists, reports, and settings.

2. **💾 Database Layer**:
   - Built with Room Database to provide seamless local storage.
   - `MainDao` and `RoomDB` classes handle data operations efficiently.

3. **📱 Fragments**:
   - Modularized UI components for adding items, viewing sales, and generating reports.
   - Includes: `AddFragment`, `ReportFragment`, `SaleFragment`, and `StoreFragment`.

4. **🔄 Adapters**:
   - Custom adapters for dynamic data handling.
   - Examples include `CategoryListAdapter` and `ItemListAdapter`.

5. **🎨 User Interface**:
   - Includes custom animations for transitions.
   - Custom themes and drawable resources for enhanced visuals.

6. **🗂️ Activity Menus**:
   - Dedicated activities for About, Feedback, Help, and Settings.

---

## ⚙️ Prerequisites

- Android Studio Arctic Fox or higher.
- Minimum SDK: 21
- Target SDK: 33
- Java 11

---

## 📥 Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/abeelgetahun-yene-shop.git
   ```

2. Open the project in Android Studio.

3. Sync the Gradle files.

4. Run the project on an emulator or connected Android device.

---

## 🚀 How to Use

- **🔑 Sign Up/Login**: Register a new user or log in with existing credentials.
- **📦 Manage Items**: Navigate to the "Store" section to add, update, or delete items.
- **📊 Generate Reports**: Use the "Reports" section to view sales data and filter by category.
- **⚙️ Settings**: Customize the app's behavior and view information about the app.

---

## 🤝 Contribution

Contributions are welcome! Follow these steps to contribute:

1. Fork the repository.
2. Create a new branch:
   ```sh
   git checkout -b feature-name
   ```
3. Make your changes and commit:
   ```sh
   git commit -m "Add new feature"
   ```
4. Push to the branch:
   ```sh
   git push origin feature-name
   ```
5. Open a pull request.

---


## 📧 Contact

For any inquiries or support, please reach out to:

---

## 🙌 Acknowledgments

- Icons and images from [Material Design Icons](https://material.io/icons).
- Fonts from [Google Fonts](https://fonts.google.com).

