package com.jcjiron.androidsample.kmp

import androidx.compose.ui.window.ComposeUIViewController
import com.jcjiron.androidsample.core.AppConfig
import com.jcjiron.androidsample.kmp.di.startAppKoin
import kotlin.experimental.ExperimentalNativeApi
import platform.UIKit.UIViewController

/** Swift lo llama como `MainViewControllerKt.MainViewController()`. */
fun MainViewController(): UIViewController = ComposeUIViewController { App() }

/** Swift lo llama como `MainViewControllerKt.startKoinIos()` al arrancar la app. */
@OptIn(ExperimentalNativeApi::class)
fun startKoinIos() {
    startAppKoin(
        AppConfig(
            baseUrl = "https://rickandmortyapi.com/api/",
            isDebug = Platform.isDebugBinary,
        ),
    )
}
