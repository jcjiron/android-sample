import SwiftUI
import ComposeApp

@main
struct iOSApp: App {

    init() {
        // Arranca Koin (definido en Kotlin, app-kmp/src/iosMain).
        MainViewControllerKt.startKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
