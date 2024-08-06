package com.example.androidtask.navigation

enum class TaskAppScreens {
    HomeScreen,
    QRCodeScannerScreen;

    companion object {
        fun fromRoute(route: String?): TaskAppScreens
                = when(route?.substringBefore("/")) {
            HomeScreen.name -> HomeScreen
            QRCodeScannerScreen.name -> QRCodeScannerScreen
            null -> HomeScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}