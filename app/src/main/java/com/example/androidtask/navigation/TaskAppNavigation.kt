package com.example.androidtask.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidtask.screen.HomeScreen
import com.example.androidtask.screen.MainViewModel
import com.example.androidtask.screen.QRCodeScannerScreen

@Composable
fun TaskAppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = TaskAppScreens.HomeScreen.name
    ) {
        composable(TaskAppScreens.HomeScreen.name) {
            val homeViewModel = hiltViewModel<MainViewModel>()
            HomeScreen(navController = navController, homeViewModel)
        }

        composable(TaskAppScreens.QRCodeScannerScreen.name) {
            QRCodeScannerScreen(navController = navController) {}
        }

    }
}