package com.example.androidtask.navigation

enum class TaskAppScreens {
    HomeScreen,
    QRCodeScannerScreen,
    SearchScreen;

    companion object {
        fun fromRoute(route: String?): TaskAppScreens
                = when(route?.substringBefore("/")) {
            HomeScreen.name -> HomeScreen
            QRCodeScannerScreen.name -> QRCodeScannerScreen
            SearchScreen.name -> SearchScreen
            null -> HomeScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}