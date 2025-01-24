# ✨ Yene Shop Manager ✨

## 📂 Directory Structure

Here’s a detailed breakdown of the project directory structure:

```
abeelgetahun-yene-shop/
├── build.gradle.kts                # Project-level Gradle configuration
├── gradle.properties               # Gradle properties for configuration
├── gradlew                         # Gradle wrapper script for UNIX systems
├── gradlew.bat                     # Gradle wrapper script for Windows
├── settings.gradle.kts             # Settings file for Gradle module inclusion
├── app/                            # Main application module
│   ├── build.gradle.kts            # App-level Gradle configuration
│   ├── proguard-rules.pro          # ProGuard rules for code obfuscation
│   ├── src/                        # Source code and resources
│   │   ├── main/                   # Main source set
│   │   │   ├── AndroidManifest.xml # Application manifest
│   │   │   ├── java/               # Application source code
│   │   │   │   └── com/teferi/abel/yeneshop/
│   │   │   │       ├── HomeActivity.java         # Main screen of the app
│   │   │   │       ├── ItemList.java             # Displays the list of items
│   │   │   │       ├── ItemUpdate.java           # Handles item updates
│   │   │   │       ├── Signup.java               # User sign-up functionality
│   │   │   │       ├── Adapter/                  # Adapters for RecyclerView
│   │   │   │       │   ├── CategoryListAdapter.java
│   │   │   │       │   ├── FragmentAdapter.java
│   │   │   │       │   ├── ItemListAdapter.java
│   │   │   │       │   └── SearchAdapter.java
│   │   │   │       ├── ClickListener/            # Listener interfaces
│   │   │   │       │   ├── CategoryClickListener.java
│   │   │   │       │   └── ItemClickListener.java
│   │   │   │       ├── Database/                 # Database components
│   │   │   │       │   ├── MainDao.java          # Data access object
│   │   │   │       │   └── RoomDB.java           # Database instance
│   │   │   │       ├── Fragments/                # Fragment-based UI components
│   │   │   │       │   ├── AddFragment.java
│   │   │   │       │   ├── ReportFragment.java
│   │   │   │       │   ├── SaleFragment.java
│   │   │   │       │   └── StoreFragment.java
│   │   │   │       ├── Menus/                    # Activity menus
│   │   │   │       │   ├── AboutActivity.java
│   │   │   │       │   ├── AllItemsActivity.java
│   │   │   │       │   ├── FeedbackActivity.java
│   │   │   │       │   ├── HelpActivity.java
│   │   │   │       │   ├── ReportByCategory.java
│   │   │   │       │   └── SettingActivity.java
│   │   │   │       ├── Models/                   # Data models
│   │   │   │       │   ├── Items.java
│   │   │   │       │   └── Sales.java
│   │   │   └── res/                  # Resource files
│   │   │       ├── anim/             # Animations
│   │   │       │   ├── bottom_animation.xml
│   │   │       │   ├── fade_in.xml
│   │   │       │   ├── fade_out.xml
│   │   │       │   ├── slide_up.xml
│   │   │       │   └── top_animation.xml
│   │   │       ├── drawable/         # Custom drawable resources
│   │   │       ├── font/             # Fonts used in the application
│   │   │       ├── layout/           # XML layouts for activities and fragments
│   │   │       ├── menu/             # Menu XML files
│   │   │       ├── mipmap/           # App icons for various screen densities
│   │   │       ├── navigation/       # Navigation graph
│   │   │       ├── values/           # Common values (colors, strings, etc.)
│   │   │       └── xml/              # XML configuration files
│   │   ├── androidTest/              # Instrumented tests
│   │   └── test/                     # Unit tests
└── gradle/                         # Gradle wrapper and library versioning
    ├── libs.versions.toml           # Dependency version management
    └── wrapper/
        └── gradle-wrapper.properties # Gradle wrapper configuration
```

