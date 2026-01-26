// COMPLETE IMPLEMENTATIONS FOR 8 NEW PATHS
// Copy these methods into OpenAIService.java to replace the placeholder versions

/**
 * iOS Developer Path - Complete 30 nodes + 15 competencies
 * Competency IDs: 1001-1015
 */
private List<DetailedPathNode> generateiOSDeveloperPath() {
    List<DetailedPathNode> nodes = new ArrayList<>();

    // PHASE 1: Swift & iOS Basics (1-7)
    nodes.add(createNode(1, "Swift Fundamentals", "Swift is Apple's modern, type-safe language for iOS",
        "build", "Write Swift code using var/let, optionals, guard, and type inference", 4, 5, "foundational",
        List.of(2), List.of(1001))); // → 2, comp: Swift history

    nodes.add(createNode(2, "Swift Collections & Generics", "Collections are fundamental data structures",
        "build", "Use Array, Dictionary, Set with generic types and protocols", 4, 4, "foundational",
        List.of(3), List.of(1002))); // → 3, comp: Protocol-oriented programming

    nodes.add(createNode(3, "Xcode IDE Mastery", "Xcode is your iOS development environment",
        "probe", "Navigate Xcode, use storyboards, manage build configurations", 3, 3, "foundational",
        List.of(4), List.of()));

    nodes.add(createNode(4, "iOS App Lifecycle", "Understanding app states prevents crashes and data loss",
        "probe", "Explain app states: not running, inactive, active, background, suspended", 4, 3, "foundational",
        List.of(5), List.of(1003))); // → 5, comp: App delegate patterns

    nodes.add(createNode(5, "UIKit Views & View Controllers", "UIKit is the traditional iOS UI framework",
        "build", "Create UIView hierarchy, manage view controllers, implement navigation", 5, 6, "foundational",
        List.of(6), List.of()));

    nodes.add(createNode(6, "Auto Layout Fundamentals", "Auto Layout enables responsive iOS interfaces",
        "build", "Use constraints, stack views, and size classes for all devices", 5, 5, "foundational",
        List.of(7), List.of(1004))); // → 7, comp: Programmatic constraints

    nodes.add(createNode(7, "Storyboards vs Programmatic UI", "Choose the right approach for your team",
        "probe", "Compare storyboards, XIBs, and programmatic UI pros/cons", 4, 3, "core",
        List.of(8), List.of()));

    // PHASE 2: SwiftUI & Modern iOS (8-15)
    nodes.add(createNode(8, "SwiftUI Basics", "SwiftUI is Apple's declarative UI framework",
        "build", "Build views with Text, Image, Button, VStack, HStack using SwiftUI", 5, 6, "core",
        List.of(9), List.of(1005))); // → 9, comp: SwiftUI vs UIKit

    nodes.add(createNode(9, "SwiftUI State Management", "@State and @Binding connect UI to data",
        "build", "Use @State, @Binding, @ObservedObject for reactive UIs", 6, 5, "core",
        List.of(10), List.of(1006))); // → 10, comp: Combine framework

    nodes.add(createNode(10, "Navigation in SwiftUI", "NavigationView enables multi-screen apps",
        "build", "Implement NavigationView, NavigationLink, and sheet presentations", 5, 4, "core",
        List.of(11), List.of()));

    nodes.add(createNode(11, "Lists & Data Display", "Lists are the most common iOS UI pattern",
        "build", "Build scrollable lists with List, ForEach, and custom cells", 5, 5, "core",
        List.of(12), List.of(1007))); // → 12, comp: UITableView vs List

    nodes.add(createNode(12, "Network Requests with URLSession", "Most apps need to fetch data from APIs",
        "build", "Make GET/POST requests, parse JSON with Codable", 6, 6, "core",
        List.of(13), List.of(1008))); // → 13, comp: Alamofire library

    nodes.add(createNode(13, "Async/Await in Swift", "Modern Swift uses async/await for concurrency",
        "build", "Replace completion handlers with async/await syntax", 6, 5, "core",
        List.of(14), List.of()));

    nodes.add(createNode(14, "Core Data Basics", "Core Data persists app data locally",
        "build", "Set up Core Data stack, create entities, perform CRUD operations", 6, 7, "core",
        List.of(15), List.of(1009))); // → 15, comp: UserDefaults vs Core Data

    nodes.add(createNode(15, "Image Loading & Caching", "Efficient image handling improves performance",
        "build", "Load remote images asynchronously with caching", 5, 4, "core",
        List.of(16), List.of(1010))); // → 16, comp: SDWebImage

    // PHASE 3: Advanced iOS (16-23)
    nodes.add(createNode(16, "MVVM Architecture", "MVVM separates business logic from UI",
        "build", "Refactor app to use Model-View-ViewModel pattern", 6, 6, "advanced",
        List.of(17), List.of()));

    nodes.add(createNode(17, "Dependency Injection", "DI makes code testable and modular",
        "build", "Implement protocol-based dependency injection", 6, 5, "advanced",
        List.of(18), List.of(1011))); // → 18, comp: Swinject framework

    nodes.add(createNode(18, "Unit Testing in iOS", "Tests prevent regressions and document behavior",
        "build", "Write XCTest unit tests with mocks and expectations", 5, 6, "advanced",
        List.of(19), List.of()));

    nodes.add(createNode(19, "UI Testing with XCUITest", "Automated UI tests catch visual regressions",
        "build", "Write XCUITest scripts for critical user flows", 5, 5, "advanced",
        List.of(20), List.of()));

    nodes.add(createNode(20, "Push Notifications", "Push notifications re-engage users",
        "build", "Implement APNs, request permissions, handle notifications", 6, 6, "advanced",
        List.of(21), List.of(1012))); // → 21, comp: Firebase Cloud Messaging

    nodes.add(createNode(21, "Keychain for Secure Storage", "Never store credentials in UserDefaults",
        "build", "Use Keychain Services to store sensitive data securely", 5, 4, "advanced",
        List.of(22), List.of()));

    nodes.add(createNode(22, "Face ID & Touch ID Authentication", "Biometric auth improves UX and security",
        "build", "Implement Local Authentication framework for biometrics", 5, 5, "advanced",
        List.of(23), List.of()));

    nodes.add(createNode(23, "App Performance Profiling", "Instruments reveals performance bottlenecks",
        "build", "Use Instruments to profile CPU, memory, and network usage", 6, 5, "advanced",
        List.of(24), List.of(1013))); // → 24, comp: Memory leaks detection

    // PHASE 4: Production & App Store (24-30)
    nodes.add(createNode(24, "App Icon & Launch Screen", "First impressions matter in the App Store",
        "build", "Design app icon, launch screen, and App Store screenshots", 3, 3, "specialized",
        List.of(25), List.of()));

    nodes.add(createNode(25, "App Store Connect", "Publishing requires understanding Apple's process",
        "probe", "Navigate App Store Connect, create app record, manage metadata", 4, 4, "specialized",
        List.of(26), List.of()));

    nodes.add(createNode(26, "Provisioning Profiles & Certificates", "Code signing is required for distribution",
        "build", "Create development and distribution certificates and profiles", 5, 5, "specialized",
        List.of(27), List.of(1014))); // → 27, comp: Fastlane automation

    nodes.add(createNode(27, "TestFlight Beta Testing", "Beta testing catches bugs before public release",
        "build", "Upload build to TestFlight, manage beta testers", 4, 3, "specialized",
        List.of(28), List.of()));

    nodes.add(createNode(28, "App Review Guidelines Compliance", "Violating guidelines causes rejection",
        "probe", "Review Apple's guidelines on privacy, payments, content", 4, 4, "specialized",
        List.of(29), List.of()));

    nodes.add(createNode(29, "Crash Reporting with Crashlytics", "Production crashes must be monitored",
        "build", "Integrate Firebase Crashlytics for crash reporting", 4, 4, "specialized",
        List.of(30), List.of(1015))); // → 30, comp: Analytics integration

    nodes.add(createNode(30, "App Store Submission", "Final step: publishing to millions of users",
        "apply", "Submit app for review, respond to rejection feedback if needed", 5, 6, "specialized",
        List.of(), List.of())); // Terminal node

    // COMPETENCIES (1001-1015)
    nodes.add(createNode(1001, "History of Swift & Objective-C", "Understanding evolution aids migration",
        "probe", "Explain Swift's evolution from Objective-C, key language milestones", 3, 2, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1002, "Protocol-Oriented Programming", "Protocols enable flexible, composable code",
        "probe", "Explain protocols, extensions, and POP vs OOP in Swift", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1003, "AppDelegate vs SceneDelegate", "iOS 13+ uses SceneDelegate for multi-window",
        "probe", "Explain scene-based lifecycle in modern iOS apps", 4, 2, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1004, "Programmatic Auto Layout", "Code-based layouts offer more control",
        "build", "Create constraints in code using NSLayoutConstraint and anchors", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1005, "SwiftUI vs UIKit Trade-offs", "Choose the right framework for your project",
        "probe", "Compare SwiftUI and UIKit for different use cases and iOS versions", 4, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1006, "Combine Framework", "Reactive programming handles asynchronous events",
        "build", "Use Combine publishers, subscribers, and operators", 6, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1007, "UITableView Deep Dive", "UITableView remains powerful for complex UIs",
        "build", "Implement custom cells, sections, cell reuse optimization", 5, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1008, "Alamofire Networking", "Alamofire simplifies network requests",
        "build", "Use Alamofire for requests with interceptors and validation", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1009, "UserDefaults vs Keychain vs Core Data", "Choose appropriate storage mechanism",
        "probe", "Compare persistence options by use case and security requirements", 4, 2, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1010, "SDWebImage for Image Caching", "SDWebImage handles image loading efficiently",
        "build", "Implement SDWebImage for async image loading with cache", 4, 2, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1011, "Swinject Dependency Injection", "Swinject provides powerful DI container",
        "build", "Set up Swinject container for app-wide dependency management", 6, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1012, "Firebase Cloud Messaging", "FCM enables cross-platform push notifications",
        "build", "Integrate FCM for push notifications with custom data", 5, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1013, "Memory Leak Detection", "Leaks cause crashes and poor performance",
        "build", "Use Instruments and Memory Graph Debugger to find retain cycles", 6, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1014, "Fastlane CI/CD Automation", "Automate builds, tests, and deployments",
        "build", "Set up Fastlane lanes for testing, building, and deploying", 6, 5, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1015, "Firebase Analytics Integration", "Analytics inform product decisions",
        "build", "Track user events, screen views, and conversion funnels", 4, 3, "competency",
        List.of(), List.of()));

    System.out.println("[TEMPLATE] Generated iOS Developer path with " + nodes.size() + " nodes (30 main + 15 competencies)");
    return nodes;
}

/**
 * Android Developer Path - Complete 30 nodes + 15 competencies
 * Competency IDs: 1101-1115
 */
private List<DetailedPathNode> generateAndroidDeveloperPath() {
    List<DetailedPathNode> nodes = new ArrayList<>();

    // PHASE 1: Kotlin & Android Basics (1-7)
    nodes.add(createNode(1, "Kotlin Fundamentals", "Kotlin is Google's modern language for Android",
        "build", "Write Kotlin code using val/let, null safety, data classes, and extension functions", 4, 5, "foundational",
        List.of(2), List.of(1101))); // → 2, comp: Kotlin vs Java

    nodes.add(createNode(2, "Kotlin Coroutines Basics", "Coroutines enable async programming in Kotlin",
        "build", "Use launch, async, and suspend functions for asynchronous operations", 5, 4, "foundational",
        List.of(3), List.of(1102))); // → 3, comp: Flow vs LiveData

    nodes.add(createNode(3, "Android Studio IDE", "Android Studio is the official Android development environment",
        "probe", "Navigate Android Studio, use Layout Editor, manage Gradle builds", 3, 3, "foundational",
        List.of(4), List.of()));

    nodes.add(createNode(4, "Android Activity Lifecycle", "Understanding lifecycle prevents memory leaks and crashes",
        "probe", "Explain activity states: onCreate, onStart, onResume, onPause, onStop, onDestroy", 4, 3, "foundational",
        List.of(5), List.of(1103))); // → 5, comp: Fragment lifecycle

    nodes.add(createNode(5, "Views & ViewGroups", "Views are the building blocks of Android UI",
        "build", "Create layouts with LinearLayout, RelativeLayout, ConstraintLayout", 5, 6, "foundational",
        List.of(6), List.of()));

    nodes.add(createNode(6, "RecyclerView Fundamentals", "RecyclerView efficiently displays large lists",
        "build", "Build scrollable lists with RecyclerView, ViewHolder pattern, and adapters", 5, 5, "foundational",
        List.of(7), List.of(1104))); // → 7, comp: DiffUtil optimization

    nodes.add(createNode(7, "Intents & Navigation", "Intents enable navigation between activities",
        "build", "Use explicit and implicit intents, pass data between screens", 4, 4, "core",
        List.of(8), List.of()));

    // PHASE 2: Jetpack Compose & Modern Android (8-15)
    nodes.add(createNode(8, "Jetpack Compose Basics", "Compose is Android's modern declarative UI framework",
        "build", "Build UIs with Text, Image, Button, Column, Row using Compose", 5, 6, "core",
        List.of(9), List.of(1105))); // → 9, comp: Compose vs XML

    nodes.add(createNode(9, "Compose State Management", "State and remember drive reactive UIs",
        "build", "Use remember, mutableStateOf, derivedStateOf for UI state", 6, 5, "core",
        List.of(10), List.of(1106))); // → 10, comp: ViewModel integration

    nodes.add(createNode(10, "Navigation in Compose", "Navigation component enables multi-screen apps",
        "build", "Implement Navigation Compose with routes and arguments", 5, 4, "core",
        List.of(11), List.of()));

    nodes.add(createNode(11, "LazyColumn & LazyRow", "Lazy layouts efficiently display scrollable content",
        "build", "Build performant lists with LazyColumn and custom item layouts", 5, 5, "core",
        List.of(12), List.of()));

    nodes.add(createNode(12, "Retrofit for Networking", "Retrofit is the standard Android HTTP client",
        "build", "Make API calls with Retrofit, parse JSON with Gson/Moshi", 6, 6, "core",
        List.of(13), List.of(1107))); // → 13, comp: OkHttp interceptors

    nodes.add(createNode(13, "Room Database", "Room provides SQLite abstraction for local data",
        "build", "Define entities, DAOs, and database for local storage", 6, 7, "core",
        List.of(14), List.of(1108))); // → 14, comp: DataStore vs SharedPreferences

    nodes.add(createNode(14, "WorkManager for Background Tasks", "WorkManager handles deferrable background work",
        "build", "Schedule periodic and one-time work with constraints", 5, 5, "core",
        List.of(15), List.of()));

    nodes.add(createNode(15, "Coil Image Loading", "Coil is the modern Kotlin-first image library",
        "build", "Load remote images asynchronously with Coil in Compose", 4, 3, "core",
        List.of(16), List.of(1109))); // → 16, comp: Glide vs Picasso

    // PHASE 3: Advanced Android (16-23)
    nodes.add(createNode(16, "MVVM Architecture with ViewModel", "MVVM separates UI from business logic",
        "build", "Implement Model-View-ViewModel pattern with Android ViewModel", 6, 6, "advanced",
        List.of(17), List.of()));

    nodes.add(createNode(17, "Dependency Injection with Hilt", "Hilt provides compile-time DI for Android",
        "build", "Set up Hilt modules, inject dependencies across app components", 6, 6, "advanced",
        List.of(18), List.of(1110))); // → 18, comp: Dagger vs Koin

    nodes.add(createNode(18, "Unit Testing with JUnit & Mockk", "Tests ensure code quality and prevent regressions",
        "build", "Write JUnit tests with Mockk for mocking dependencies", 5, 6, "advanced",
        List.of(19), List.of()));

    nodes.add(createNode(19, "UI Testing with Espresso", "Espresso automates UI testing on Android",
        "build", "Write Espresso tests for critical user flows", 5, 5, "advanced",
        List.of(20), List.of()));

    nodes.add(createNode(20, "Firebase Cloud Messaging", "FCM enables push notifications on Android",
        "build", "Implement FCM, request permissions, handle notification payloads", 6, 6, "advanced",
        List.of(21), List.of(1111))); // → 21, comp: Notification channels

    nodes.add(createNode(21, "Biometric Authentication", "Biometric APIs provide secure user authentication",
        "build", "Implement fingerprint and face authentication using BiometricPrompt", 5, 4, "advanced",
        List.of(22), List.of()));

    nodes.add(createNode(22, "ProGuard & R8 Optimization", "Code shrinking reduces APK size and improves security",
        "build", "Configure R8 rules for code obfuscation and optimization", 5, 5, "advanced",
        List.of(23), List.of()));

    nodes.add(createNode(23, "Android Profiler & Performance", "Profiling reveals CPU, memory, and network issues",
        "build", "Use Android Profiler to diagnose performance bottlenecks", 6, 5, "advanced",
        List.of(24), List.of(1112))); // → 24, comp: LeakCanary

    // PHASE 4: Production & Play Store (24-30)
    nodes.add(createNode(24, "App Icon & Adaptive Icons", "Adaptive icons display consistently across devices",
        "build", "Design app icon with foreground, background layers for all densities", 3, 3, "specialized",
        List.of(25), List.of()));

    nodes.add(createNode(25, "Google Play Console", "Play Console manages app distribution and analytics",
        "probe", "Navigate Play Console, create app listing, manage releases", 4, 4, "specialized",
        List.of(26), List.of()));

    nodes.add(createNode(26, "App Signing & Play App Signing", "Signing ensures app authenticity and security",
        "build", "Generate upload key, configure Play App Signing", 5, 5, "specialized",
        List.of(27), List.of(1113))); // → 27, comp: APK vs AAB

    nodes.add(createNode(27, "Internal Testing & Closed Beta", "Testing tracks catch bugs before public release",
        "build", "Upload AAB to internal testing, manage beta testers", 4, 3, "specialized",
        List.of(28), List.of()));

    nodes.add(createNode(28, "Play Store Listing Optimization", "App listing affects discoverability and downloads",
        "build", "Write compelling description, add screenshots and feature graphic", 4, 4, "specialized",
        List.of(29), List.of()));

    nodes.add(createNode(29, "Firebase Crashlytics", "Crashlytics monitors production crashes in real-time",
        "build", "Integrate Crashlytics, analyze crash reports and trends", 4, 4, "specialized",
        List.of(30), List.of(1114))); // → 30, comp: Firebase Analytics

    nodes.add(createNode(30, "Play Store Submission", "Final step: publishing to billions of Android users",
        "apply", "Submit app for review, respond to policy violations if needed", 5, 6, "specialized",
        List.of(), List.of())); // Terminal node

    // COMPETENCIES (1101-1115)
    nodes.add(createNode(1101, "Kotlin vs Java for Android", "Understanding both languages aids legacy code maintenance",
        "probe", "Compare Kotlin and Java syntax, null safety, and Android idioms", 3, 2, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1102, "Flow vs LiveData", "Choose the right reactive stream for your architecture",
        "probe", "Compare Kotlin Flow and LiveData for UI state management", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1103, "Fragment Lifecycle Deep Dive", "Fragments have complex lifecycle interactions",
        "probe", "Explain fragment lifecycle states and interaction with activity lifecycle", 4, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1104, "DiffUtil & ListAdapter", "DiffUtil enables efficient RecyclerView updates",
        "build", "Implement DiffUtil.Callback for animated list updates", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1105, "Compose vs XML Layouts", "Choose the right UI approach for your project",
        "probe", "Compare Jetpack Compose and XML layouts for different use cases", 4, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1106, "ViewModel & StateFlow Integration", "StateFlow provides lifecycle-aware state in MVVM",
        "build", "Expose UI state from ViewModel using StateFlow and collectAsState", 6, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1107, "OkHttp Interceptors", "Interceptors add headers, logging, and auth to requests",
        "build", "Implement custom OkHttp interceptors for authentication and logging", 5, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1108, "DataStore vs SharedPreferences", "DataStore is the modern preference storage solution",
        "probe", "Compare Preferences DataStore, Proto DataStore, and SharedPreferences", 4, 2, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1109, "Glide vs Picasso vs Coil", "Choose the right image loading library",
        "probe", "Compare Glide, Picasso, and Coil for image loading use cases", 3, 2, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1110, "Dagger vs Hilt vs Koin", "Different DI frameworks suit different needs",
        "probe", "Compare compile-time DI (Dagger/Hilt) vs runtime DI (Koin)", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1111, "Notification Channels & Importance", "Android 8+ requires notification channels",
        "build", "Create notification channels with different importance levels", 4, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1112, "LeakCanary Memory Leak Detection", "LeakCanary automatically detects memory leaks",
        "build", "Integrate LeakCanary and interpret leak traces", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1113, "APK vs AAB (App Bundle)", "App Bundles enable smaller downloads and dynamic delivery",
        "probe", "Explain benefits of AAB over APK and dynamic feature modules", 4, 2, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1114, "Firebase Analytics & Events", "Analytics drive product decisions and growth",
        "build", "Track custom events, user properties, and conversion funnels", 4, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1115, "Material Design 3 (Material You)", "Material 3 provides modern Android design system",
        "build", "Implement Material 3 theming with dynamic colors in Compose", 5, 4, "competency",
        List.of(), List.of()));

    System.out.println("[TEMPLATE] Generated Android Developer path with " + nodes.size() + " nodes (30 main + 15 competencies)");
    return nodes;
}

/**
 * React Native Developer Path - Complete 30 nodes + 15 competencies
 * Competency IDs: 1201-1215
 */
private List<DetailedPathNode> generateReactNativeDeveloperPath() {
    List<DetailedPathNode> nodes = new ArrayList<>();

    // PHASE 1: React & JavaScript Foundations (1-7)
    nodes.add(createNode(1, "React Fundamentals for Mobile", "React's component model powers React Native",
        "build", "Build functional components with props, state, and hooks", 4, 5, "foundational",
        List.of(2), List.of(1201))); // → 2, comp: React vs React Native

    nodes.add(createNode(2, "JavaScript ES6+ Features", "Modern JavaScript enables cleaner React code",
        "build", "Use arrow functions, destructuring, spread operator, and async/await", 4, 4, "foundational",
        List.of(3), List.of(1202))); // → 3, comp: TypeScript for React Native

    nodes.add(createNode(3, "React Hooks Deep Dive", "Hooks manage state and lifecycle in functional components",
        "build", "Master useState, useEffect, useContext, useReducer, useMemo, useCallback", 5, 5, "foundational",
        List.of(4), List.of()));

    nodes.add(createNode(4, "React Native Environment Setup", "Proper environment prevents development issues",
        "probe", "Set up React Native CLI, Xcode, Android Studio, and run on simulators", 3, 4, "foundational",
        List.of(5), List.of()));

    nodes.add(createNode(5, "Core Components & Styling", "React Native provides cross-platform UI components",
        "build", "Use View, Text, Image, ScrollView, FlatList with StyleSheet", 5, 6, "foundational",
        List.of(6), List.of(1203))); // → 6, comp: Flexbox layout

    nodes.add(createNode(6, "Touchables & User Input", "Handling touch events enables interactive UIs",
        "build", "Implement TouchableOpacity, TextInput, Switch, and gesture handling", 4, 4, "foundational",
        List.of(7), List.of()));

    nodes.add(createNode(7, "React Navigation", "Navigation library enables multi-screen apps",
        "build", "Implement stack, tab, and drawer navigation with React Navigation", 5, 6, "core",
        List.of(8), List.of(1204))); // → 8, comp: Navigation patterns

    // PHASE 2: State Management & Data (8-15)
    nodes.add(createNode(8, "Context API for Global State", "Context avoids prop drilling in large apps",
        "build", "Create context providers for theme, auth, and app-wide state", 5, 4, "core",
        List.of(9), List.of()));

    nodes.add(createNode(9, "Redux Toolkit for React Native", "Redux manages complex application state",
        "build", "Set up Redux Toolkit with slices, async thunks, and middleware", 6, 6, "core",
        List.of(10), List.of(1205))); // → 10, comp: Redux vs MobX

    nodes.add(createNode(10, "React Query for Data Fetching", "React Query handles server state elegantly",
        "build", "Fetch, cache, and sync server data with React Query", 6, 5, "core",
        List.of(11), List.of()));

    nodes.add(createNode(11, "Axios for API Calls", "Axios simplifies HTTP requests in React Native",
        "build", "Make REST API calls with Axios interceptors and error handling", 5, 4, "core",
        List.of(12), List.of(1206))); // → 12, comp: Fetch vs Axios

    nodes.add(createNode(12, "AsyncStorage for Persistence", "AsyncStorage provides key-value storage on device",
        "build", "Store user preferences and cached data with AsyncStorage", 4, 3, "core",
        List.of(13), List.of()));

    nodes.add(createNode(13, "Forms & Validation", "Robust forms improve user experience and data quality",
        "build", "Build forms with React Hook Form and Yup validation", 5, 5, "core",
        List.of(14), List.of(1207))); // → 14, comp: Formik alternative

    nodes.add(createNode(14, "Image Handling & FastImage", "Efficient image loading improves performance",
        "build", "Load and cache images with react-native-fast-image", 4, 3, "core",
        List.of(15), List.of()));

    nodes.add(createNode(15, "Push Notifications Setup", "Push notifications re-engage users across platforms",
        "build", "Integrate Firebase Cloud Messaging for iOS and Android push notifications", 6, 6, "core",
        List.of(16), List.of(1208))); // → 16, comp: Notification libraries

    // PHASE 3: Native Modules & Advanced (16-23)
    nodes.add(createNode(16, "Linking Native Libraries", "React Native bridges to native iOS and Android code",
        "build", "Link and configure native libraries with autolinking and manual setup", 5, 5, "advanced",
        List.of(17), List.of()));

    nodes.add(createNode(17, "Writing Custom Native Modules", "Native modules access platform-specific APIs",
        "build", "Create native modules in Swift/Objective-C and Kotlin/Java", 7, 8, "advanced",
        List.of(18), List.of(1209))); // → 18, comp: Turbo Modules

    nodes.add(createNode(18, "Performance Optimization", "Profiling and optimization ensure smooth 60fps UIs",
        "build", "Use React DevTools Profiler, Flipper, and optimize FlatList rendering", 6, 6, "advanced",
        List.of(19), List.of(1210))); // → 19, comp: Hermes engine

    nodes.add(createNode(19, "Animations with Reanimated", "Reanimated 2 provides smooth native-thread animations",
        "build", "Create complex animations with react-native-reanimated and gestures", 6, 7, "advanced",
        List.of(20), List.of()));

    nodes.add(createNode(20, "Testing with Jest & Testing Library", "Tests prevent regressions and document behavior",
        "build", "Write unit and integration tests with Jest and React Native Testing Library", 5, 6, "advanced",
        List.of(21), List.of(1211))); // → 21, comp: Detox E2E testing

    nodes.add(createNode(21, "Deep Linking & Universal Links", "Deep links enable navigation from web and other apps",
        "build", "Configure deep linking for iOS and Android with URL schemes", 5, 5, "advanced",
        List.of(22), List.of()));

    nodes.add(createNode(22, "Secure Storage & Keychain", "Sensitive data requires encrypted storage",
        "build", "Store tokens and credentials with react-native-keychain", 5, 4, "advanced",
        List.of(23), List.of(1212))); // → 23, comp: Biometric auth

    nodes.add(createNode(23, "Error Boundaries & Crash Reporting", "Production apps need robust error handling",
        "build", "Implement error boundaries and integrate Sentry for crash reporting", 5, 5, "advanced",
        List.of(24), List.of()));

    // PHASE 4: Production & Deployment (24-30)
    nodes.add(createNode(24, "App Icons & Splash Screens", "Branding assets create professional first impressions",
        "build", "Generate app icons and splash screens for iOS and Android", 3, 3, "specialized",
        List.of(25), List.of()));

    nodes.add(createNode(25, "Environment Configuration", "Different environments require different API keys and configs",
        "build", "Set up dev, staging, and production environments with react-native-config", 4, 4, "specialized",
        List.of(26), List.of(1213))); // → 26, comp: Build variants

    nodes.add(createNode(26, "iOS Build & Fastlane", "Automating iOS builds saves time and reduces errors",
        "build", "Configure Fastlane for iOS code signing, building, and TestFlight upload", 6, 6, "specialized",
        List.of(27), List.of()));

    nodes.add(createNode(27, "Android Build & Play Store", "Android builds require signing and AAB generation",
        "build", "Generate signed AAB, configure Play App Signing, upload to Play Console", 6, 6, "specialized",
        List.of(28), List.of()));

    nodes.add(createNode(28, "Over-The-Air Updates", "OTA updates deploy fixes without app store review",
        "build", "Implement CodePush or Expo Updates for instant updates", 5, 5, "specialized",
        List.of(29), List.of(1214))); // → 29, comp: CodePush vs EAS Update

    nodes.add(createNode(29, "Analytics & Performance Monitoring", "Production monitoring informs product decisions",
        "build", "Integrate Firebase Analytics and Performance Monitoring", 4, 4, "specialized",
        List.of(30), List.of()));

    nodes.add(createNode(30, "App Store & Play Store Submission", "Final step: publishing to millions on both platforms",
        "apply", "Submit to App Store and Play Store, respond to review feedback", 5, 6, "specialized",
        List.of(), List.of())); // Terminal node

    // COMPETENCIES (1201-1215)
    nodes.add(createNode(1201, "React vs React Native Differences", "Understanding differences prevents common mistakes",
        "probe", "Explain DOM vs native components, styling differences, and platform APIs", 3, 2, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1202, "TypeScript for React Native", "TypeScript adds type safety to JavaScript",
        "build", "Set up TypeScript, define component prop types and state interfaces", 5, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1203, "Flexbox Layout Deep Dive", "Flexbox is the primary layout system in React Native",
        "build", "Master flexDirection, justifyContent, alignItems for responsive layouts", 4, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1204, "Navigation Patterns & Best Practices", "Proper navigation architecture scales well",
        "probe", "Compare stack, tab, drawer navigation patterns and nesting strategies", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1205, "Redux vs MobX vs Zustand", "Different state management libraries suit different needs",
        "probe", "Compare Redux Toolkit, MobX, and Zustand for React Native apps", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1206, "Fetch API vs Axios", "Choose the right HTTP client for your needs",
        "probe", "Compare native Fetch API and Axios for API requests", 3, 2, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1207, "Formik Alternative Comparison", "Multiple form libraries exist for React Native",
        "probe", "Compare React Hook Form, Formik, and uncontrolled forms", 4, 2, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1208, "Push Notification Libraries", "Different libraries offer different features",
        "probe", "Compare react-native-firebase, Notifee, and OneSignal", 4, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1209, "Turbo Modules & New Architecture", "React Native's new architecture improves performance",
        "probe", "Explain Turbo Modules, Fabric renderer, and migration path", 6, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1210, "Hermes JavaScript Engine", "Hermes reduces app size and improves startup time",
        "probe", "Explain Hermes benefits, bytecode compilation, and when to use it", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1211, "Detox End-to-End Testing", "E2E tests validate entire user flows on real devices",
        "build", "Write Detox tests for critical user journeys on iOS and Android", 6, 5, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1212, "Biometric Authentication", "Fingerprint and face unlock improve UX and security",
        "build", "Implement biometric authentication with react-native-biometrics", 5, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1213, "Build Variants & Schemes", "Different builds for development, staging, production",
        "build", "Configure Xcode schemes and Android build variants", 5, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1214, "CodePush vs EAS Update", "OTA update solutions have different trade-offs",
        "probe", "Compare Microsoft CodePush and Expo EAS Update features and pricing", 4, 2, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1215, "Expo vs React Native CLI", "Managed vs bare workflow trade-offs",
        "probe", "Compare Expo managed workflow, Expo bare workflow, and React Native CLI", 4, 3, "competency",
        List.of(), List.of()));

    System.out.println("[TEMPLATE] Generated React Native Developer path with " + nodes.size() + " nodes (30 main + 15 competencies)");
    return nodes;
}

/**
 * Flutter Developer Path - Complete 30 nodes + 15 competencies
 * Competency IDs: 1301-1315
 */
private List<DetailedPathNode> generateFlutterDeveloperPath() {
    List<DetailedPathNode> nodes = new ArrayList<>();

    // PHASE 1: Dart & Flutter Basics (1-7)
    nodes.add(createNode(1, "Dart Programming Language", "Dart is the foundation of Flutter development",
        "build", "Write Dart code with strong typing, async/await, collections, and null safety", 4, 5, "foundational",
        List.of(2), List.of(1301))); // → 2, comp: Dart vs JavaScript

    nodes.add(createNode(2, "Object-Oriented Dart", "OOP principles structure Flutter applications",
        "build", "Use classes, inheritance, mixins, abstract classes, and interfaces", 5, 4, "foundational",
        List.of(3), List.of(1302))); // → 3, comp: Mixins vs inheritance

    nodes.add(createNode(3, "Flutter Environment Setup", "Proper setup ensures smooth cross-platform development",
        "probe", "Install Flutter SDK, configure VS Code/Android Studio, run on all platforms", 3, 3, "foundational",
        List.of(4), List.of()));

    nodes.add(createNode(4, "Widget Tree Fundamentals", "Everything in Flutter is a widget",
        "probe", "Understand widget tree, element tree, and render tree architecture", 4, 3, "foundational",
        List.of(5), List.of(1303))); // → 5, comp: StatelessWidget vs StatefulWidget

    nodes.add(createNode(5, "Layout Widgets", "Layout widgets organize UI structure",
        "build", "Use Container, Row, Column, Stack, Expanded, Flexible for layouts", 5, 6, "foundational",
        List.of(6), List.of()));

    nodes.add(createNode(6, "Material & Cupertino Widgets", "Flutter provides both Android and iOS design systems",
        "build", "Build UIs with Material Design and iOS-style Cupertino widgets", 5, 5, "foundational",
        List.of(7), List.of(1304))); // → 7, comp: Adaptive widgets

    nodes.add(createNode(7, "Navigation & Routing", "Navigator enables multi-screen Flutter apps",
        "build", "Implement navigation with Navigator, named routes, and route arguments", 5, 5, "core",
        List.of(8), List.of()));

    // PHASE 2: State Management & Data (8-15)
    nodes.add(createNode(8, "Stateful Widgets & setState", "setState is the simplest state management approach",
        "build", "Manage local widget state with StatefulWidget and setState", 4, 4, "core",
        List.of(9), List.of()));

    nodes.add(createNode(9, "InheritedWidget & Provider", "Provider simplifies state propagation down the widget tree",
        "build", "Use Provider for dependency injection and state management", 6, 5, "core",
        List.of(10), List.of(1305))); // → 10, comp: Provider vs Riverpod

    nodes.add(createNode(10, "Riverpod State Management", "Riverpod is compile-safe Provider with better testing",
        "build", "Implement app state with Riverpod providers and consumers", 6, 6, "core",
        List.of(11), List.of()));

    nodes.add(createNode(11, "HTTP Requests with Dio", "Dio is the powerful HTTP client for Flutter",
        "build", "Make REST API calls with Dio interceptors, error handling, and retries", 5, 5, "core",
        List.of(12), List.of(1306))); // → 12, comp: http package vs Dio

    nodes.add(createNode(12, "JSON Serialization", "Convert JSON to Dart objects for type safety",
        "build", "Use json_serializable for automatic JSON parsing and code generation", 5, 4, "core",
        List.of(13), List.of()));

    nodes.add(createNode(13, "Local Storage with SharedPreferences", "SharedPreferences stores simple key-value data",
        "build", "Persist user preferences and settings with shared_preferences", 4, 3, "core",
        List.of(14), List.of()));

    nodes.add(createNode(14, "SQLite with sqflite", "sqflite provides local database storage",
        "build", "Create database, define schemas, perform CRUD operations with sqflite", 6, 6, "core",
        List.of(15), List.of(1307))); // → 15, comp: Hive vs sqflite

    nodes.add(createNode(15, "Forms & Validation", "Flutter forms collect and validate user input",
        "build", "Build forms with TextFormField, Form widget, and custom validators", 5, 5, "core",
        List.of(16), List.of()));

    // PHASE 3: Advanced Flutter (16-23)
    nodes.add(createNode(16, "Streams & Reactive Programming", "Streams handle asynchronous data sequences",
        "build", "Use StreamBuilder, StreamController, and rxdart for reactive UIs", 6, 6, "advanced",
        List.of(17), List.of(1308))); // → 17, comp: BLoC pattern

    nodes.add(createNode(17, "BLoC Architecture Pattern", "BLoC separates business logic from UI",
        "build", "Implement BLoC pattern with flutter_bloc for scalable architecture", 7, 7, "advanced",
        List.of(18), List.of()));

    nodes.add(createNode(18, "Custom Painters & Canvas", "CustomPaint enables custom graphics and animations",
        "build", "Draw custom shapes and graphics using Canvas API", 6, 6, "advanced",
        List.of(19), List.of(1309))); // → 19, comp: Animation types

    nodes.add(createNode(19, "Implicit & Explicit Animations", "Animations bring UIs to life",
        "build", "Create animations with AnimatedContainer, AnimationController, Tween", 6, 6, "advanced",
        List.of(20), List.of()));

    nodes.add(createNode(20, "Platform Channels", "Platform channels bridge Flutter to native code",
        "build", "Call native iOS/Android APIs using MethodChannel and EventChannel", 7, 7, "advanced",
        List.of(21), List.of(1310))); // → 21, comp: FFI for C libraries

    nodes.add(createNode(21, "Testing in Flutter", "Tests ensure code quality across platforms",
        "build", "Write unit tests, widget tests, and integration tests", 6, 7, "advanced",
        List.of(22), List.of(1311))); // → 22, comp: Golden tests

    nodes.add(createNode(22, "Firebase Integration", "Firebase provides backend services for Flutter",
        "build", "Integrate Firebase Auth, Firestore, Cloud Messaging, and Analytics", 6, 7, "advanced",
        List.of(23), List.of()));

    nodes.add(createNode(23, "Performance Optimization", "Profiling reveals performance bottlenecks",
        "build", "Use Flutter DevTools to profile rendering, memory, and network performance", 6, 6, "advanced",
        List.of(24), List.of(1312))); // → 24, comp: const constructors

    // PHASE 4: Production & Multi-platform (24-30)
    nodes.add(createNode(24, "App Icons & Splash Screens", "flutter_launcher_icons automates icon generation",
        "build", "Generate adaptive icons and splash screens for all platforms", 3, 3, "specialized",
        List.of(25), List.of()));

    nodes.add(createNode(25, "Flavors & Environment Config", "Flavors enable multiple app variants",
        "build", "Configure dev, staging, prod flavors for iOS and Android", 5, 5, "specialized",
        List.of(26), List.of(1313))); // → 26, comp: Build modes

    nodes.add(createNode(26, "iOS Build & App Store", "Building for iOS requires Xcode and certificates",
        "build", "Configure Xcode project, code signing, build IPA, upload to TestFlight", 6, 6, "specialized",
        List.of(27), List.of()));

    nodes.add(createNode(27, "Android Build & Play Store", "Android builds generate AAB for Play Store",
        "build", "Configure signing, build AAB, upload to Play Console with app bundle", 6, 6, "specialized",
        List.of(28), List.of()));

    nodes.add(createNode(28, "Web & Desktop Deployment", "Flutter supports web, Windows, macOS, and Linux",
        "build", "Build and deploy Flutter apps to web and desktop platforms", 5, 5, "specialized",
        List.of(29), List.of(1314))); // → 29, comp: Platform-specific code

    nodes.add(createNode(29, "Crash Reporting & Analytics", "Production monitoring informs product decisions",
        "build", "Integrate Firebase Crashlytics and Analytics for production monitoring", 4, 4, "specialized",
        List.of(30), List.of()));

    nodes.add(createNode(30, "Multi-platform App Submission", "Final step: publishing to all app stores",
        "apply", "Submit Flutter app to App Store, Play Store, and web hosting", 5, 6, "specialized",
        List.of(), List.of())); // Terminal node

    // COMPETENCIES (1301-1315)
    nodes.add(createNode(1301, "Dart vs JavaScript/TypeScript", "Understanding language differences aids migration",
        "probe", "Compare Dart and JavaScript/TypeScript syntax, typing, and async models", 3, 2, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1302, "Mixins vs Inheritance", "Mixins enable code reuse without deep hierarchies",
        "probe", "Explain when to use mixins vs inheritance in Dart", 4, 2, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1303, "StatelessWidget vs StatefulWidget", "Choose the right widget type for your UI",
        "probe", "Compare stateless and stateful widgets, lifecycle methods, and use cases", 4, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1304, "Adaptive & Platform-Aware Widgets", "Build apps that feel native on each platform",
        "build", "Create adaptive UIs that adapt to iOS, Android, and platform conventions", 5, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1305, "Provider vs Riverpod vs GetX", "Different state management solutions for different needs",
        "probe", "Compare Provider, Riverpod, GetX, and BLoC for state management", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1306, "http Package vs Dio", "Choose the right HTTP client for your needs",
        "probe", "Compare built-in http package and Dio for API requests", 3, 2, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1307, "Hive vs sqflite vs Drift", "Different local storage solutions for different use cases",
        "probe", "Compare Hive (NoSQL), sqflite (SQLite), and Drift (type-safe SQL)", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1308, "BLoC Pattern Deep Dive", "BLoC provides predictable state management",
        "build", "Implement full BLoC pattern with events, states, and repositories", 7, 5, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1309, "Animation Types & Controllers", "Different animations for different effects",
        "probe", "Compare implicit, explicit, hero, and physics-based animations", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1310, "FFI for C/C++ Libraries", "FFI enables calling native C/C++ code",
        "build", "Use dart:ffi to call C libraries directly from Dart", 7, 5, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1311, "Golden Image Tests", "Golden tests catch visual regressions",
        "build", "Write golden tests to validate widget rendering across changes", 5, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1312, "const Constructors & Performance", "const widgets reduce rebuilds and improve performance",
        "probe", "Explain const constructors, const widgets, and when to use them", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1313, "Debug vs Profile vs Release Modes", "Build modes affect performance and debugging",
        "probe", "Explain differences between debug, profile, and release build modes", 4, 2, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1314, "Platform-Specific Code Organization", "Conditional imports keep code clean",
        "build", "Organize platform-specific code with conditional imports and abstract classes", 5, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1315, "Flutter for Web Considerations", "Web builds have different constraints than mobile",
        "probe", "Explain web-specific considerations: SEO, routing, canvas vs HTML renderer", 5, 4, "competency",
        List.of(), List.of()));

    System.out.println("[TEMPLATE] Generated Flutter Developer path with " + nodes.size() + " nodes (30 main + 15 competencies)");
    return nodes;
}

/**
 * Site Reliability Engineer (SRE) Path - Complete 30 nodes + 15 competencies
 * Competency IDs: 1401-1415
 */
private List<DetailedPathNode> generateSREPath() {
    List<DetailedPathNode> nodes = new ArrayList<>();

    // PHASE 1: Foundations & Linux (1-7)
    nodes.add(createNode(1, "SRE Principles & Philosophy", "SRE bridges software engineering and operations",
        "probe", "Explain error budgets, toil reduction, SLIs/SLOs/SLAs, and blameless postmortems", 4, 4, "foundational",
        List.of(2), List.of(1401))); // → 2, comp: SRE vs DevOps

    nodes.add(createNode(2, "Linux System Administration", "Linux powers most production infrastructure",
        "build", "Manage processes, filesystems, permissions, users, and system services", 5, 6, "foundational",
        List.of(3), List.of(1402))); // → 3, comp: systemd deep dive

    nodes.add(createNode(3, "Bash Scripting for Automation", "Shell scripts automate repetitive operations tasks",
        "build", "Write bash scripts with loops, conditionals, functions, and error handling", 5, 5, "foundational",
        List.of(4), List.of()));

    nodes.add(createNode(4, "Networking Fundamentals", "Understanding networks enables troubleshooting production issues",
        "probe", "Explain TCP/IP, DNS, HTTP/HTTPS, load balancing, and CDNs", 5, 5, "foundational",
        List.of(5), List.of(1403))); // → 5, comp: OSI model

    nodes.add(createNode(5, "Git for Infrastructure", "Version control tracks infrastructure changes",
        "build", "Use Git for version controlling configs, scripts, and infrastructure code", 4, 3, "foundational",
        List.of(6), List.of()));

    nodes.add(createNode(6, "Python for SRE", "Python is the lingua franca of SRE automation",
        "build", "Write Python scripts for system automation, API calls, and data processing", 5, 6, "foundational",
        List.of(7), List.of(1404))); // → 7, comp: Go for infrastructure

    nodes.add(createNode(7, "SQL & Database Basics", "Databases store application state and require monitoring",
        "build", "Query databases, understand indexes, transactions, and basic tuning", 5, 4, "core",
        List.of(8), List.of()));

    // PHASE 2: Containers & Orchestration (8-15)
    nodes.add(createNode(8, "Docker Fundamentals", "Containers provide consistent environments across all stages",
        "build", "Build Docker images, manage containers, understand layers and registries", 6, 6, "core",
        List.of(9), List.of(1405))); // → 9, comp: Docker vs containerd

    nodes.add(createNode(9, "Kubernetes Architecture", "K8s orchestrates containers at scale",
        "probe", "Explain pods, services, deployments, namespaces, and control plane components", 6, 6, "core",
        List.of(10), List.of(1406))); // → 10, comp: K8s alternatives

    nodes.add(createNode(10, "Kubernetes Deployments & Services", "Deploy and expose applications in K8s",
        "build", "Create deployments, services, ingress, and manage rolling updates", 6, 7, "core",
        List.of(11), List.of()));

    nodes.add(createNode(11, "ConfigMaps & Secrets", "Manage configuration and sensitive data in K8s",
        "build", "Use ConfigMaps for config, Secrets for credentials, and volume mounts", 5, 4, "core",
        List.of(12), List.of(1407))); // → 12, comp: External Secrets Operator

    nodes.add(createNode(12, "Helm Package Manager", "Helm simplifies K8s application deployment",
        "build", "Create Helm charts, manage releases, and use templating", 5, 5, "core",
        List.of(13), List.of()));

    nodes.add(createNode(13, "Kubernetes Monitoring & Logging", "Observability is critical for K8s reliability",
        "build", "Set up logging with Fluentd, monitoring with Prometheus, and dashboards", 6, 6, "core",
        List.of(14), List.of(1408))); // → 14, comp: ELK vs Loki

    nodes.add(createNode(14, "Pod Autoscaling & Resource Management", "Autoscaling handles variable load",
        "build", "Configure HPA, VPA, resource requests/limits, and cluster autoscaling", 6, 6, "core",
        List.of(15), List.of()));

    nodes.add(createNode(15, "Service Mesh with Istio", "Service meshes provide observability and traffic control",
        "build", "Deploy Istio for traffic management, security, and distributed tracing", 7, 7, "core",
        List.of(16), List.of(1409))); // → 16, comp: Linkerd vs Istio

    // PHASE 3: Cloud & Infrastructure as Code (16-23)
    nodes.add(createNode(16, "AWS Fundamentals", "AWS is the dominant cloud platform",
        "build", "Use EC2, S3, RDS, VPC, IAM, and understand the well-architected framework", 6, 7, "advanced",
        List.of(17), List.of(1410))); // → 17, comp: Multi-cloud comparison

    nodes.add(createNode(17, "Terraform Infrastructure as Code", "Terraform declaratively provisions cloud resources",
        "build", "Write Terraform configs, manage state, use modules and workspaces", 6, 7, "advanced",
        List.of(18), List.of(1411))); // → 18, comp: Terraform vs Pulumi

    nodes.add(createNode(18, "CI/CD Pipelines", "Automated pipelines enable rapid, reliable deployments",
        "build", "Build CI/CD with GitHub Actions, GitLab CI, or Jenkins for automated testing and deployment", 6, 7, "advanced",
        List.of(19), List.of()));

    nodes.add(createNode(19, "GitOps with ArgoCD", "GitOps uses Git as the source of truth for infrastructure",
        "build", "Implement GitOps workflows with ArgoCD for K8s deployments", 6, 6, "advanced",
        List.of(20), List.of(1412))); // → 20, comp: FluxCD alternative

    nodes.add(createNode(20, "Observability & APM", "Full observability requires metrics, logs, and traces",
        "build", "Implement distributed tracing with Jaeger/Tempo and APM with Datadog/New Relic", 6, 7, "advanced",
        List.of(21), List.of()));

    nodes.add(createNode(21, "Incident Management", "Incidents are inevitable; response quality matters",
        "build", "Set up on-call rotation, runbooks, incident response, and postmortems", 5, 5, "advanced",
        List.of(22), List.of(1413))); // → 22, comp: PagerDuty vs Opsgenie

    nodes.add(createNode(22, "Chaos Engineering", "Chaos testing builds confidence in system resilience",
        "build", "Implement chaos experiments with Chaos Mesh or Gremlin to test failure scenarios", 6, 6, "advanced",
        List.of(23), List.of()));

    nodes.add(createNode(23, "Cost Optimization", "Cloud costs can spiral; optimization is essential",
        "build", "Analyze cloud spend, right-size resources, use spot/reserved instances, implement FinOps", 5, 5, "advanced",
        List.of(24), List.of(1414))); // → 24, comp: FinOps practices

    // PHASE 4: Advanced SRE & Leadership (24-30)
    nodes.add(createNode(24, "Disaster Recovery & Backup", "DR plans protect against catastrophic failures",
        "build", "Design backup strategies, test recovery procedures, and document RPO/RTO", 6, 6, "specialized",
        List.of(25), List.of()));

    nodes.add(createNode(25, "Security & Compliance", "SREs are responsible for security posture",
        "build", "Implement security scanning, vulnerability management, and compliance automation", 6, 6, "specialized",
        List.of(26), List.of()));

    nodes.add(createNode(26, "Performance Engineering", "Performance optimization requires methodical analysis",
        "build", "Profile applications, optimize databases, tune caching, reduce latency", 7, 7, "specialized",
        List.of(27), List.of(1415))); // → 27, comp: Load testing tools

    nodes.add(createNode(27, "Capacity Planning", "Predict and provision for future growth",
        "build", "Analyze trends, forecast capacity needs, and plan infrastructure scaling", 6, 6, "specialized",
        List.of(28), List.of()));

    nodes.add(createNode(28, "SLI/SLO Engineering", "SLOs quantify reliability and guide error budgets",
        "build", "Define meaningful SLIs, set realistic SLOs, and implement error budget policies", 6, 6, "specialized",
        List.of(29), List.of()));

    nodes.add(createNode(29, "Toil Automation & Elimination", "Reducing toil is core to SRE effectiveness",
        "build", "Identify toil, automate repetitive tasks, and measure toil reduction impact", 5, 5, "specialized",
        List.of(30), List.of()));

    nodes.add(createNode(30, "SRE Team Building & Culture", "Building SRE culture transforms organizations",
        "apply", "Establish SRE practices, mentor teams, drive blameless culture, and scale reliability", 6, 7, "specialized",
        List.of(), List.of())); // Terminal node

    // COMPETENCIES (1401-1415)
    nodes.add(createNode(1401, "SRE vs DevOps vs Platform Engineering", "Understanding differences clarifies role expectations",
        "probe", "Compare SRE, DevOps, and Platform Engineering philosophies and practices", 4, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1402, "systemd Service Management", "systemd is the standard init system for modern Linux",
        "build", "Create systemd units, manage services, understand timers and targets", 5, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1403, "OSI Model & Network Troubleshooting", "Layer-by-layer approach debugs network issues",
        "probe", "Explain OSI layers and use tools like tcpdump, netstat, traceroute for debugging", 5, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1404, "Go for Infrastructure Tools", "Go is popular for building infrastructure tooling",
        "build", "Write Go programs for CLI tools, API servers, and system utilities", 6, 5, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1405, "Docker vs containerd vs CRI-O", "Different container runtimes for different needs",
        "probe", "Compare Docker, containerd, and CRI-O for production use", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1406, "Kubernetes Alternatives: ECS, Nomad, Swarm", "K8s isn't always the right choice",
        "probe", "Compare Kubernetes, AWS ECS, HashiCorp Nomad, and Docker Swarm", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1407, "External Secrets Operator", "Manage secrets from external vaults in K8s",
        "build", "Integrate External Secrets Operator with AWS Secrets Manager or Vault", 6, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1408, "ELK vs Loki vs Splunk", "Different logging solutions for different scales",
        "probe", "Compare Elasticsearch/Logstash/Kibana, Loki, and Splunk for log aggregation", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1409, "Linkerd vs Istio Service Meshes", "Choose the right service mesh for your needs",
        "probe", "Compare Linkerd and Istio complexity, features, and performance", 6, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1410, "Multi-cloud: AWS vs GCP vs Azure", "Multi-cloud strategies reduce vendor lock-in",
        "probe", "Compare AWS, GCP, and Azure services, pricing, and use cases", 6, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1411, "Terraform vs Pulumi vs CloudFormation", "Different IaC tools for different needs",
        "probe", "Compare Terraform HCL, Pulumi (real languages), and CloudFormation", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1412, "FluxCD vs ArgoCD", "Both implement GitOps but with different approaches",
        "probe", "Compare FluxCD and ArgoCD for GitOps workflows", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1413, "PagerDuty vs Opsgenie vs VictorOps", "Incident management platforms reduce MTTR",
        "probe", "Compare incident management tools and on-call best practices", 4, 2, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1414, "FinOps Practices", "FinOps brings financial accountability to cloud",
        "probe", "Explain FinOps principles, cost allocation, and optimization strategies", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1415, "Load Testing: k6 vs Locust vs JMeter", "Load testing validates performance under stress",
        "build", "Perform load tests with k6, Locust, or JMeter and analyze results", 6, 4, "competency",
        List.of(), List.of()));

    System.out.println("[TEMPLATE] Generated SRE path with " + nodes.size() + " nodes (30 main + 15 competencies)");
    return nodes;
}

/**
 * Platform Engineer Path - Complete 30 nodes + 15 competencies
 * Competency IDs: 1501-1515
 */
private List<DetailedPathNode> generatePlatformEngineerPath() {
    List<DetailedPathNode> nodes = new ArrayList<>();

    // PHASE 1: Foundations & Developer Experience (1-7)
    nodes.add(createNode(1, "Platform Engineering Philosophy", "Platform engineering builds golden paths for developers",
        "probe", "Explain platform thinking, self-service, developer experience, and platform as a product", 4, 4, "foundational",
        List.of(2), List.of(1501))); // → 2, comp: Platform vs DevOps

    nodes.add(createNode(2, "Internal Developer Platforms (IDPs)", "IDPs abstract infrastructure complexity",
        "probe", "Understand IDP components: service catalog, CI/CD, observability, security, governance", 5, 5, "foundational",
        List.of(3), List.of(1502))); // → 3, comp: Buy vs build platforms

    nodes.add(createNode(3, "Developer Portal with Backstage", "Backstage unifies tooling and documentation",
        "build", "Deploy Backstage, create software templates, integrate plugins for unified developer portal", 6, 6, "foundational",
        List.of(4), List.of()));

    nodes.add(createNode(4, "Infrastructure as Code Foundations", "IaC enables reproducible infrastructure",
        "build", "Master Terraform for cloud resources, understand state management and modules", 6, 6, "foundational",
        List.of(5), List.of(1503))); // → 5, comp: Terraform vs Crossplane

    nodes.add(createNode(5, "Kubernetes for Platforms", "K8s is the foundation of modern platforms",
        "build", "Deploy production K8s clusters, understand operators, CRDs, and admission controllers", 7, 7, "foundational",
        List.of(6), List.of()));

    nodes.add(createNode(6, "API Design for Platforms", "Platform APIs enable self-service",
        "build", "Design REST and GraphQL APIs, implement versioning, authentication, and rate limiting", 5, 6, "foundational",
        List.of(7), List.of(1504))); // → 7, comp: API gateways

    nodes.add(createNode(7, "Platform Documentation", "Great docs are critical for platform adoption",
        "build", "Write technical documentation, runbooks, and getting-started guides with docs-as-code", 4, 4, "core",
        List.of(8), List.of()));

    // PHASE 2: Self-Service & Automation (8-15)
    nodes.add(createNode(8, "GitOps for Platform Management", "GitOps provides declarative platform configuration",
        "build", "Implement GitOps with ArgoCD or FluxCD for platform component management", 6, 6, "core",
        List.of(9), List.of(1505))); // → 9, comp: Push vs pull deployments

    nodes.add(createNode(9, "Service Templates & Scaffolding", "Templates accelerate app development with best practices",
        "build", "Create Cookiecutter or Backstage templates for microservices, frontend, and data pipelines", 6, 6, "core",
        List.of(10), List.of()));

    nodes.add(createNode(10, "Secrets Management", "Centralized secrets management improves security",
        "build", "Implement HashiCorp Vault or AWS Secrets Manager with dynamic secrets and rotation", 6, 6, "core",
        List.of(11), List.of(1506))); // → 11, comp: Sealed Secrets

    nodes.add(createNode(11, "Container Registry & Artifact Management", "Centralized artifact storage enables reuse",
        "build", "Set up Harbor or Artifactory for container images, Helm charts, and build artifacts", 5, 5, "core",
        List.of(12), List.of()));

    nodes.add(createNode(12, "CI/CD Platform Design", "Standardized pipelines reduce toil",
        "build", "Build reusable pipeline templates with GitHub Actions, GitLab CI, or Tekton", 6, 7, "core",
        List.of(13), List.of(1507))); // → 13, comp: Jenkins vs cloud-native CI

    nodes.add(createNode(13, "Policy as Code", "Automate compliance and governance",
        "build", "Implement OPA (Open Policy Agent) for Kubernetes admission control and Terraform validation", 6, 6, "core",
        List.of(14), List.of()));

    nodes.add(createNode(14, "Multi-tenancy Architecture", "Isolate teams while sharing infrastructure",
        "build", "Design namespace-based or cluster-based multi-tenancy with resource quotas and network policies", 7, 7, "core",
        List.of(15), List.of(1508))); // → 15, comp: Virtual clusters

    nodes.add(createNode(15, "Service Mesh Integration", "Service meshes provide traffic management and security",
        "build", "Deploy Istio or Linkerd for mTLS, traffic splitting, and observability", 7, 7, "core",
        List.of(16), List.of()));

    // PHASE 3: Observability & Developer Tools (16-23)
    nodes.add(createNode(16, "Platform Observability Stack", "Platform teams need visibility into platform health",
        "build", "Deploy Prometheus, Grafana, Loki for metrics, dashboards, and logs aggregation", 6, 7, "advanced",
        List.of(17), List.of(1509))); // → 17, comp: OpenTelemetry

    nodes.add(createNode(17, "Distributed Tracing", "Traces debug microservice performance issues",
        "build", "Implement Jaeger or Tempo with OpenTelemetry for distributed tracing", 6, 6, "advanced",
        List.of(18), List.of()));

    nodes.add(createNode(18, "Cost Visibility & Showback", "Cost transparency drives efficient resource usage",
        "build", "Implement Kubecost or OpenCost for per-team cost allocation and showback", 5, 5, "advanced",
        List.of(19), List.of(1510))); // → 19, comp: FinOps for platforms

    nodes.add(createNode(19, "Developer Productivity Metrics", "DORA metrics quantify platform effectiveness",
        "build", "Track deployment frequency, lead time, MTTR, and change failure rate", 5, 5, "advanced",
        List.of(20), List.of()));

    nodes.add(createNode(20, "Internal CLI Tools", "CLIs enable scriptable platform interactions",
        "build", "Build internal CLI tools with Go or Python using cobra, click, or typer", 6, 6, "advanced",
        List.of(21), List.of(1511))); // → 21, comp: CLI design patterns

    nodes.add(createNode(21, "Platform APIs & SDKs", "APIs and SDKs enable programmatic platform access",
        "build", "Build platform APIs with FastAPI or Go, generate SDKs for multiple languages", 6, 7, "advanced",
        List.of(22), List.of()));

    nodes.add(createNode(22, "Database as a Service (DBaaS)", "Managed databases reduce operational burden",
        "build", "Build DBaaS for PostgreSQL, MySQL, Redis using Kubernetes operators", 7, 8, "advanced",
        List.of(23), List.of(1512))); // → 23, comp: CloudNativePG operator

    nodes.add(createNode(23, "Platform Security & Compliance", "Security must be built into the platform",
        "build", "Implement RBAC, pod security policies, vulnerability scanning, and SBOM generation", 6, 7, "advanced",
        List.of(24), List.of()));

    // PHASE 4: Scaling & Platform Maturity (24-30)
    nodes.add(createNode(24, "Disaster Recovery for Platforms", "Platform DR protects entire organizations",
        "build", "Design multi-region platform architecture with backup, restore, and failover procedures", 7, 8, "specialized",
        List.of(25), List.of(1513))); // → 25, comp: Velero backups

    nodes.add(createNode(25, "Platform Performance & Capacity", "Platform bottlenecks affect all teams",
        "build", "Profile platform components, optimize control planes, plan capacity for growth", 7, 7, "specialized",
        List.of(26), List.of()));

    nodes.add(createNode(26, "Platform Versioning & Upgrades", "Safe upgrades prevent platform-wide outages",
        "build", "Design upgrade strategies, test compatibility, implement blue-green platform updates", 6, 7, "specialized",
        List.of(27), List.of()));

    nodes.add(createNode(27, "Developer Feedback Loops", "Platform improves through user feedback",
        "build", "Establish feedback channels, conduct user interviews, track platform satisfaction scores", 5, 5, "specialized",
        List.of(28), List.of(1514))); // → 28, comp: Platform metrics

    nodes.add(createNode(28, "Platform as a Product", "Treating platforms as products drives adoption",
        "probe", "Apply product thinking: roadmaps, user research, feature prioritization, and marketing", 5, 5, "specialized",
        List.of(29), List.of()));

    nodes.add(createNode(29, "Cross-functional Platform Teams", "Platform teams combine diverse expertise",
        "build", "Build teams with SRE, backend, frontend, and product skills for platform development", 5, 5, "specialized",
        List.of(30), List.of()));

    nodes.add(createNode(30, "Platform Engineering Culture", "Sustainable platforms require organizational change",
        "apply", "Drive platform adoption, measure success, scale platform team, and establish platform community", 6, 7, "specialized",
        List.of(), List.of())); // Terminal node

    // COMPETENCIES (1501-1515)
    nodes.add(createNode(1501, "Platform Engineering vs DevOps vs SRE", "Distinct roles with overlapping skills",
        "probe", "Compare platform engineering, DevOps, and SRE responsibilities and focus areas", 4, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1502, "Buy vs Build Platform Decisions", "Build vs buy affects platform strategy",
        "probe", "Analyze build vs buy trade-offs for platform components and managed services", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1503, "Terraform vs Crossplane vs Pulumi", "Different IaC tools for platform needs",
        "probe", "Compare Terraform, Crossplane (K8s-native), and Pulumi for platform IaC", 6, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1504, "API Gateway Patterns", "Gateways centralize API management",
        "build", "Implement Kong, Ambassador, or AWS API Gateway for platform APIs", 5, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1505, "Push vs Pull Deployment Models", "GitOps pulls; traditional CI pushes",
        "probe", "Compare push-based (Jenkins) vs pull-based (ArgoCD) deployment models", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1506, "Sealed Secrets vs External Secrets", "Different K8s secret management approaches",
        "probe", "Compare Sealed Secrets, External Secrets Operator, and direct Vault integration", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1507, "Jenkins vs Cloud-Native CI (Tekton, Argo)", "Traditional vs cloud-native CI/CD",
        "probe", "Compare Jenkins with cloud-native alternatives like Tekton and Argo Workflows", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1508, "Virtual Clusters (vClusters)", "Virtual clusters provide hard multi-tenancy",
        "build", "Deploy vCluster for isolated virtual K8s clusters within host cluster", 6, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1509, "OpenTelemetry for Platform Observability", "OTel standardizes observability data",
        "build", "Implement OpenTelemetry collector for metrics, logs, and traces", 6, 5, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1510, "FinOps for Platform Teams", "Cost optimization at platform scale",
        "probe", "Apply FinOps principles to platform infrastructure and enable team accountability", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1511, "CLI Design Patterns & Best Practices", "Great CLIs improve developer experience",
        "probe", "Explain CLI design: subcommands, flags, config files, output formats, and help text", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1512, "CloudNativePG Operator", "Operator pattern for PostgreSQL on K8s",
        "build", "Deploy and manage PostgreSQL clusters with CloudNativePG operator", 6, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1513, "Velero for Kubernetes Backups", "Velero backs up K8s resources and volumes",
        "build", "Configure Velero for cluster backup, restore, and disaster recovery", 6, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1514, "Platform Metrics: NPS, CSAT, Adoption", "Metrics prove platform value",
        "probe", "Measure platform success with NPS, CSAT, adoption rate, and ticket volume reduction", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1515, "Platform Team Topologies", "Team structure affects platform success",
        "probe", "Apply Team Topologies patterns: platform team, enabling team, and stream-aligned teams", 5, 4, "competency",
        List.of(), List.of()));

    System.out.println("[TEMPLATE] Generated Platform Engineer path with " + nodes.size() + " nodes (30 main + 15 competencies)");
    return nodes;
}

/**
 * Cloud Architect Path - Complete 30 nodes + 15 competencies
 * Competency IDs: 1601-1615
 */
private List<DetailedPathNode> generateCloudArchitectPath() {
    List<DetailedPathNode> nodes = new ArrayList<>();

    // PHASE 1: Cloud Foundations (1-7)
    nodes.add(createNode(1, "Cloud Computing Fundamentals", "Cloud transforms IT from CapEx to OpEx",
        "probe", "Explain IaaS, PaaS, SaaS, public/private/hybrid cloud, and cloud economics", 4, 4, "foundational",
        List.of(2), List.of(1601))); // → 2, comp: Cloud service models

    nodes.add(createNode(2, "AWS Core Services", "AWS dominates the cloud market",
        "build", "Master EC2, S3, VPC, IAM, CloudWatch, and understand the shared responsibility model", 6, 7, "foundational",
        List.of(3), List.of(1602))); // → 3, comp: AWS vs Azure vs GCP

    nodes.add(createNode(3, "Cloud Networking Architecture", "Network design affects performance, security, and cost",
        "build", "Design VPCs, subnets, route tables, NAT gateways, VPN, and Direct Connect", 6, 6, "foundational",
        List.of(4), List.of()));

    nodes.add(createNode(4, "Identity & Access Management", "IAM is the foundation of cloud security",
        "build", "Design IAM policies, roles, users, MFA, federation, and least privilege access", 6, 6, "foundational",
        List.of(5), List.of(1603))); // → 5, comp: RBAC vs ABAC

    nodes.add(createNode(5, "Cloud Storage Architectures", "Choose the right storage for each use case",
        "build", "Design solutions with S3, EBS, EFS, FSx, and understand performance/cost trade-offs", 5, 5, "foundational",
        List.of(6), List.of()));

    nodes.add(createNode(6, "Compute Services & Optimization", "Right-size compute to balance cost and performance",
        "build", "Use EC2, Lambda, ECS, EKS, and choose instance types, pricing models, and autoscaling", 6, 6, "foundational",
        List.of(7), List.of(1604))); // → 7, comp: Spot vs Reserved vs Savings Plans

    nodes.add(createNode(7, "Database Services Selection", "Choose managed vs self-managed, SQL vs NoSQL",
        "build", "Architect solutions with RDS, Aurora, DynamoDB, ElastiCache, and Redshift", 6, 6, "core",
        List.of(8), List.of()));

    // PHASE 2: Well-Architected Framework (8-15)
    nodes.add(createNode(8, "Operational Excellence Pillar", "Automate operations and respond to events",
        "probe", "Apply operational excellence: IaC, runbooks, deployment automation, and observability", 5, 5, "core",
        List.of(9), List.of(1605))); // → 9, comp: Infrastructure as Code best practices

    nodes.add(createNode(9, "Security Pillar", "Implement defense in depth across all layers",
        "build", "Apply security best practices: encryption, network isolation, security groups, WAF, GuardDuty", 6, 6, "core",
        List.of(10), List.of()));

    nodes.add(createNode(10, "Reliability Pillar", "Design for failure and automatic recovery",
        "build", "Implement fault tolerance with multi-AZ, auto-scaling, health checks, and circuit breakers", 6, 7, "core",
        List.of(11), List.of(1606))); // → 11, comp: RTO vs RPO

    nodes.add(createNode(11, "Performance Efficiency Pillar", "Use the right resources for the right workload",
        "build", "Optimize with CloudFront CDN, ElastiCache, database read replicas, and auto-scaling policies", 6, 6, "core",
        List.of(12), List.of()));

    nodes.add(createNode(12, "Cost Optimization Pillar", "Architect for cost efficiency without sacrificing quality",
        "build", "Implement cost optimization: right-sizing, Reserved Instances, S3 lifecycle, cost allocation tags", 6, 6, "core",
        List.of(13), List.of(1607))); // → 13, comp: AWS Cost Explorer

    nodes.add(createNode(13, "Sustainability Pillar", "Minimize environmental impact of cloud workloads",
        "probe", "Apply sustainability: efficient architectures, serverless, spot instances, carbon footprint tracking", 5, 4, "core",
        List.of(14), List.of()));

    nodes.add(createNode(14, "High Availability Architectures", "Design for 99.99% or higher uptime",
        "build", "Architect multi-AZ, multi-region solutions with Route 53, Global Accelerator, and failover", 7, 7, "core",
        List.of(15), List.of(1608))); // → 15, comp: Active-active vs active-passive

    nodes.add(createNode(15, "Disaster Recovery Strategies", "Plan for worst-case scenarios",
        "build", "Implement backup/restore, pilot light, warm standby, or multi-site DR strategies", 6, 7, "core",
        List.of(16), List.of()));

    // PHASE 3: Advanced Cloud Architecture (16-23)
    nodes.add(createNode(16, "Microservices on Cloud", "Microservices leverage cloud scalability and resilience",
        "build", "Design microservices with API Gateway, Lambda, ECS/EKS, service mesh, and event-driven patterns", 7, 8, "advanced",
        List.of(17), List.of(1609))); // → 17, comp: Serverless vs containers

    nodes.add(createNode(17, "Event-Driven Architectures", "Decouple services with asynchronous messaging",
        "build", "Design with SNS, SQS, EventBridge, Kinesis for event-driven and streaming architectures", 7, 7, "advanced",
        List.of(18), List.of()));

    nodes.add(createNode(18, "Data Lake & Analytics", "Build scalable data platforms on cloud",
        "build", "Design data lakes with S3, Glue, Athena, EMR, and implement data governance", 7, 8, "advanced",
        List.of(19), List.of(1610))); // → 19, comp: Data lake vs data warehouse

    nodes.add(createNode(19, "Machine Learning on Cloud", "Cloud provides ML infrastructure and managed services",
        "build", "Architect ML pipelines with SageMaker, training jobs, model deployment, and MLOps", 7, 7, "advanced",
        List.of(20), List.of()));

    nodes.add(createNode(20, "Hybrid & Multi-Cloud Architecture", "Connect on-premises to cloud and across clouds",
        "build", "Design hybrid architectures with VPN, Direct Connect, Transit Gateway, and Outposts", 7, 8, "advanced",
        List.of(21), List.of(1611))); // → 21, comp: Multi-cloud strategies

    nodes.add(createNode(21, "Container Orchestration at Scale", "Kubernetes powers modern cloud-native apps",
        "build", "Design production EKS clusters with Fargate, managed node groups, service mesh, and GitOps", 7, 8, "advanced",
        List.of(22), List.of()));

    nodes.add(createNode(22, "Serverless Architectures", "Serverless eliminates server management",
        "build", "Design serverless apps with Lambda, API Gateway, DynamoDB, Step Functions, and SAM/CDK", 7, 7, "advanced",
        List.of(23), List.of(1612))); // → 23, comp: Cold start optimization

    nodes.add(createNode(23, "Cloud Migration Strategies", "Move workloads to cloud safely and efficiently",
        "build", "Apply 7 R's of migration: rehost, replatform, refactor, retire, retain, relocate, repurchase", 6, 7, "advanced",
        List.of(24), List.of()));

    // PHASE 4: Enterprise Architecture (24-30)
    nodes.add(createNode(24, "Landing Zone & Account Strategy", "Multi-account strategy enables governance at scale",
        "build", "Design AWS Organizations, Control Tower, SCPs, account structure, and centralized logging", 7, 8, "specialized",
        List.of(25), List.of(1613))); // → 25, comp: AWS Control Tower

    nodes.add(createNode(25, "Compliance & Governance", "Meet regulatory requirements in the cloud",
        "build", "Implement compliance with Config, Security Hub, Audit Manager, and artifact collection", 6, 7, "specialized",
        List.of(26), List.of()));

    nodes.add(createNode(26, "FinOps & Cloud Economics", "Manage cloud costs across the organization",
        "build", "Implement FinOps practices: budgets, forecasting, showback/chargeback, and optimization", 6, 6, "specialized",
        List.of(27), List.of(1614))); // → 27, comp: Reserved capacity planning

    nodes.add(createNode(27, "Cloud Center of Excellence", "CoE drives cloud adoption and best practices",
        "probe", "Establish cloud governance, training programs, architecture reviews, and innovation culture", 5, 6, "specialized",
        List.of(28), List.of()));

    nodes.add(createNode(28, "Enterprise Integration Patterns", "Connect cloud services to legacy systems",
        "build", "Design integration with API Gateway, AppFlow, EventBridge, and hybrid connectivity", 6, 7, "specialized",
        List.of(29), List.of()));

    nodes.add(createNode(29, "Cloud-Native Architecture Patterns", "Apply proven patterns for cloud success",
        "probe", "Master patterns: strangler fig, CQRS, event sourcing, saga, BFF, and anti-corruption layer", 7, 7, "specialized",
        List.of(30), List.of(1615))); // → 30, comp: CAP theorem

    nodes.add(createNode(30, "Architecting for the Future", "Lead cloud transformation at enterprise scale",
        "apply", "Drive cloud strategy, mentor architects, establish standards, and innovate with emerging tech", 7, 8, "specialized",
        List.of(), List.of())); // Terminal node

    // COMPETENCIES (1601-1615)
    nodes.add(createNode(1601, "IaaS vs PaaS vs SaaS Trade-offs", "Choose the right abstraction level",
        "probe", "Compare IaaS (EC2), PaaS (Elastic Beanstalk), and SaaS for different use cases", 4, 2, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1602, "AWS vs Azure vs GCP Comparison", "Multi-cloud knowledge enables best-of-breed solutions",
        "probe", "Compare compute, storage, networking, and ML services across major cloud providers", 6, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1603, "RBAC vs ABAC Access Control", "Choose the right access control model",
        "probe", "Compare role-based and attribute-based access control for enterprise IAM", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1604, "EC2 Pricing: Spot vs Reserved vs Savings Plans", "Optimize compute costs",
        "probe", "Compare on-demand, spot, reserved instances, and compute/EC2 savings plans", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1605, "Terraform vs CloudFormation vs CDK", "Choose the right IaC tool",
        "probe", "Compare Terraform HCL, CloudFormation YAML, and AWS CDK (TypeScript/Python)", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1606, "RTO vs RPO in Disaster Recovery", "Define recovery objectives",
        "probe", "Explain Recovery Time Objective and Recovery Point Objective in DR planning", 5, 2, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1607, "AWS Cost Explorer & Optimization", "Visualize and optimize cloud spend",
        "build", "Use Cost Explorer, Cost Anomaly Detection, and Compute Optimizer for cost management", 5, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1608, "Active-Active vs Active-Passive HA", "Choose the right HA pattern",
        "probe", "Compare active-active and active-passive high availability architectures", 6, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1609, "Serverless vs Containers vs VMs", "Choose the right compute abstraction",
        "probe", "Compare Lambda, Fargate/ECS/EKS, and EC2 for different workload types", 6, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1610, "Data Lake vs Data Warehouse", "Choose the right data storage paradigm",
        "probe", "Compare data lakes (S3/Glue) and data warehouses (Redshift) for analytics", 6, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1611, "Multi-Cloud vs Cloud-Agnostic", "Balance portability and cloud-native features",
        "probe", "Analyze trade-offs between multi-cloud, cloud-agnostic, and cloud-native approaches", 6, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1612, "Lambda Cold Start Optimization", "Reduce serverless latency",
        "build", "Optimize Lambda with provisioned concurrency, SnapStart, and runtime selection", 6, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1613, "AWS Control Tower & Landing Zones", "Automate multi-account governance",
        "build", "Deploy Control Tower for automated account provisioning and guardrails", 7, 5, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1614, "Reserved Capacity Planning", "Commit to reserved capacity strategically",
        "probe", "Analyze usage patterns to optimize Reserved Instances and Savings Plans commitments", 6, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1615, "CAP Theorem in Distributed Systems", "Understand consistency/availability trade-offs",
        "probe", "Explain CAP theorem and apply to DynamoDB, Aurora, and distributed system design", 6, 3, "competency",
        List.of(), List.of()));

    System.out.println("[TEMPLATE] Generated Cloud Architect path with " + nodes.size() + " nodes (30 main + 15 competencies)");
    return nodes;
}

/**
 * Mobile Architect Path - Complete 30 nodes + 15 competencies
 * Competency IDs: 1701-1715
 */
private List<DetailedPathNode> generateMobileArchitectPath() {
    List<DetailedPathNode> nodes = new ArrayList<>();

    // PHASE 1: Mobile Foundations (1-7)
    nodes.add(createNode(1, "Mobile Architecture Principles", "Mobile has unique constraints: battery, network, screen size",
        "probe", "Explain mobile-specific concerns: offline-first, battery efficiency, responsive UI, app lifecycle", 5, 5, "foundational",
        List.of(2), List.of(1701))); // → 2, comp: Native vs cross-platform

    nodes.add(createNode(2, "iOS & Android Platform Expertise", "Architects must understand both major platforms",
        "probe", "Compare iOS (Swift/UIKit/SwiftUI) and Android (Kotlin/Views/Compose) ecosystems", 6, 6, "foundational",
        List.of(3), List.of(1702))); // → 3, comp: Platform-specific patterns

    nodes.add(createNode(3, "Mobile App Architecture Patterns", "Choose the right architecture for your team and app",
        "probe", "Compare MVC, MVP, MVVM, VIPER, Clean Architecture, and MVI for mobile apps", 6, 6, "foundational",
        List.of(4), List.of()));

    nodes.add(createNode(4, "Dependency Injection for Mobile", "DI improves testability and modularity",
        "build", "Implement DI with Dagger/Hilt (Android) and Swinject/Factory (iOS)", 6, 5, "foundational",
        List.of(5), List.of(1703))); // → 5, comp: Service locator vs DI

    nodes.add(createNode(5, "State Management Architectures", "State management complexity grows with app size",
        "build", "Design state management with Redux, MobX, Bloc, Riverpod, or reactive patterns", 6, 6, "foundational",
        List.of(6), List.of()));

    nodes.add(createNode(6, "Navigation Architecture", "Complex apps need scalable navigation patterns",
        "build", "Design deep linking, tab navigation, modal flows, and coordinator patterns", 5, 5, "foundational",
        List.of(7), List.of(1704))); // → 7, comp: Universal links

    nodes.add(createNode(7, "Mobile API Design", "Mobile APIs need to optimize for latency and battery",
        "build", "Design mobile-optimized APIs: pagination, caching headers, GraphQL, and BFF pattern", 6, 6, "core",
        List.of(8), List.of()));

    // PHASE 2: Cross-Platform & Performance (8-15)
    nodes.add(createNode(8, "React Native Architecture", "RN bridges JavaScript to native platforms",
        "build", "Architect RN apps with navigation, state, native modules, and performance patterns", 6, 7, "core",
        List.of(9), List.of(1705))); // → 9, comp: RN new architecture

    nodes.add(createNode(9, "Flutter Architecture", "Flutter compiles to native ARM code",
        "build", "Design Flutter apps with widget tree optimization, BLoC, and platform channels", 6, 7, "core",
        List.of(10), List.of(1706))); // → 10, comp: Flutter vs React Native

    nodes.add(createNode(10, "Offline-First Architecture", "Mobile apps must work without connectivity",
        "build", "Implement offline sync with local DB, conflict resolution, and background sync", 7, 8, "core",
        List.of(11), List.of()));

    nodes.add(createNode(11, "Mobile Database Architecture", "Choose the right local storage solution",
        "build", "Design with SQLite, Realm, Core Data, Room, or key-value stores", 6, 6, "core",
        List.of(12), List.of(1707))); // → 12, comp: SQLite vs NoSQL mobile

    nodes.add(createNode(12, "Mobile Performance Optimization", "60fps rendering requires careful optimization",
        "build", "Profile and optimize: list virtualization, image caching, bundle size, startup time", 7, 8, "core",
        List.of(13), List.of()));

    nodes.add(createNode(13, "Mobile Memory Management", "Memory leaks crash apps and drain battery",
        "build", "Implement memory management: weak references, lifecycle-aware observers, profiling", 6, 6, "core",
        List.of(14), List.of(1708))); // → 14, comp: ARC vs manual memory

    nodes.add(createNode(14, "Battery & Energy Optimization", "Battery life is a top user concern",
        "build", "Optimize battery: background task scheduling, location accuracy, network batching", 6, 6, "core",
        List.of(15), List.of()));

    nodes.add(createNode(15, "Mobile Security Architecture", "Mobile devices are high-value attack targets",
        "build", "Implement: certificate pinning, jailbreak detection, secure storage, code obfuscation", 7, 7, "core",
        List.of(16), List.of(1709))); // → 16, comp: OWASP Mobile Top 10

    // PHASE 3: Scaling & Infrastructure (16-23)
    nodes.add(createNode(16, "Modular App Architecture", "Modularization enables team scaling",
        "build", "Design multi-module apps: feature modules, shared libraries, dependency graphs", 7, 8, "advanced",
        List.of(17), List.of(1710))); // → 17, comp: Dynamic feature modules

    nodes.add(createNode(17, "Mobile CI/CD Pipelines", "Automated pipelines accelerate mobile releases",
        "build", "Build CI/CD with Fastlane, Bitrise, or GitHub Actions for testing and deployment", 6, 7, "advanced",
        List.of(18), List.of()));

    nodes.add(createNode(18, "A/B Testing & Feature Flags", "Experiment and gradually roll out features",
        "build", "Implement feature flags with Firebase Remote Config, LaunchDarkly, or Split", 6, 6, "advanced",
        List.of(19), List.of(1711))); // → 19, comp: Remote config strategies

    nodes.add(createNode(19, "Mobile Analytics Architecture", "Data-driven decisions require comprehensive analytics",
        "build", "Design analytics with Firebase, Mixpanel, Amplitude: events, funnels, cohorts", 6, 6, "advanced",
        List.of(20), List.of()));

    nodes.add(createNode(20, "Crash Reporting & Monitoring", "Production issues need real-time detection",
        "build", "Implement Crashlytics, Sentry, or Bugsnag with symbolication and alerting", 5, 5, "advanced",
        List.of(21), List.of()));

    nodes.add(createNode(21, "Mobile App Distribution", "Manage beta testing and phased rollouts",
        "build", "Set up TestFlight, Play Internal Testing, Firebase App Distribution for beta programs", 5, 5, "advanced",
        List.of(22), List.of(1712))); // → 22, comp: OTA updates

    nodes.add(createNode(22, "Push Notification Architecture", "Notifications re-engage users across platforms",
        "build", "Design push infrastructure with APNs, FCM, topics, user segments, and rich notifications", 6, 6, "advanced",
        List.of(23), List.of()));

    nodes.add(createNode(23, "Mobile Backend Architecture", "Mobile backends require special considerations",
        "build", "Design BFF, API Gateway, real-time sync, file upload/download, and webhooks", 7, 7, "advanced",
        List.of(24), List.of(1713))); // → 24, comp: Firebase vs custom backend

    // PHASE 4: Enterprise & Leadership (24-30)
    nodes.add(createNode(24, "Multi-Platform Strategy", "Balance code reuse with platform optimization",
        "probe", "Define strategy: native, cross-platform, hybrid, or progressive web apps", 6, 6, "specialized",
        List.of(25), List.of()));

    nodes.add(createNode(25, "Mobile Design Systems", "Design systems ensure consistency across platforms",
        "build", "Build mobile design system: tokens, components, platform adaptations, and documentation", 6, 7, "specialized",
        List.of(26), List.of(1714))); // → 26, comp: Material vs Cupertino

    nodes.add(createNode(26, "App Store Optimization (ASO)", "ASO drives organic discovery and downloads",
        "probe", "Optimize: keywords, screenshots, videos, ratings/reviews, and localization", 5, 5, "specialized",
        List.of(27), List.of()));

    nodes.add(createNode(27, "Mobile DevOps & SRE", "Production mobile apps need SRE practices",
        "build", "Implement: SLIs/SLOs for mobile, incident response, on-call, and blameless postmortems", 6, 6, "specialized",
        List.of(28), List.of()));

    nodes.add(createNode(28, "Mobile Team Scaling", "Scale mobile teams without losing velocity",
        "probe", "Design team structure: platform teams, feature teams, and shared infrastructure", 6, 6, "specialized",
        List.of(29), List.of(1715))); // → 29, comp: Conway's Law

    nodes.add(createNode(29, "Mobile Architecture Governance", "Standards prevent technical debt at scale",
        "build", "Establish: architecture reviews, RFCs, tech radar, and migration strategies", 6, 6, "specialized",
        List.of(30), List.of()));

    nodes.add(createNode(30, "Mobile Innovation & Strategy", "Lead mobile technology decisions and culture",
        "apply", "Drive mobile strategy, evaluate emerging tech, mentor architects, and build mobile excellence", 7, 8, "specialized",
        List.of(), List.of())); // Terminal node

    // COMPETENCIES (1701-1715)
    nodes.add(createNode(1701, "Native vs Cross-Platform Trade-offs", "Choose the right approach for your context",
        "probe", "Compare native (Swift/Kotlin), cross-platform (RN/Flutter), and hybrid (Ionic/Cordova)", 6, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1702, "iOS vs Android Platform Patterns", "Platform conventions affect UX and development",
        "probe", "Compare navigation, lifecycle, permissions, and design patterns across platforms", 6, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1703, "Service Locator vs Dependency Injection", "Different approaches to manage dependencies",
        "probe", "Compare service locator pattern with constructor injection and property injection", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1704, "Universal Links & App Links", "Deep linking connects web and mobile",
        "build", "Implement iOS Universal Links and Android App Links with domain verification", 5, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1705, "React Native New Architecture", "Fabric and TurboModules improve RN performance",
        "probe", "Explain new architecture: Fabric renderer, TurboModules, and CodeGen", 6, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1706, "Flutter vs React Native Comparison", "Choose the right cross-platform framework",
        "probe", "Compare Flutter and React Native: performance, ecosystem, developer experience", 6, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1707, "Mobile SQLite vs NoSQL Databases", "Choose the right local database",
        "probe", "Compare SQLite, Realm, and other mobile databases for different use cases", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1708, "ARC vs Manual Memory Management", "Understand memory management across platforms",
        "probe", "Explain Automatic Reference Counting (iOS) vs garbage collection vs manual", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1709, "OWASP Mobile Top 10", "Know the most critical mobile security risks",
        "probe", "Explain OWASP Mobile Top 10: insecure data storage, auth, crypto, code quality", 6, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1710, "Android Dynamic Feature Modules", "On-demand modules reduce initial download size",
        "build", "Implement dynamic feature modules for large Android apps with Play Feature Delivery", 6, 5, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1711, "Remote Config vs Feature Flags", "Different approaches to runtime configuration",
        "probe", "Compare Firebase Remote Config, LaunchDarkly, and in-app feature flags", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1712, "OTA Updates: CodePush & EAS", "Update apps without app store review",
        "build", "Implement over-the-air updates with CodePush (RN) or EAS Update (Expo)", 6, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1713, "Firebase vs Custom Backend", "BaaS vs custom backend trade-offs",
        "probe", "Compare Firebase BaaS with custom backend for auth, database, storage, and functions", 6, 4, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1714, "Material Design vs iOS Human Interface", "Platform design languages differ",
        "probe", "Compare Material Design 3 and iOS Human Interface Guidelines for adaptive design", 5, 3, "competency",
        List.of(), List.of()));

    nodes.add(createNode(1715, "Conway's Law in Mobile Teams", "Team structure determines architecture",
        "probe", "Apply Conway's Law: how team organization affects mobile app architecture", 5, 3, "competency",
        List.of(), List.of()));

    System.out.println("[TEMPLATE] Generated Mobile Architect path with " + nodes.size() + " nodes (30 main + 15 competencies)");
    return nodes;
}
