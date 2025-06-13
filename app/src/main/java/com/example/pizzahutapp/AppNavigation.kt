package com.example.pizzahutapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pizzahutapp.screen.AuthScreen
import com.example.pizzahutapp.screen.LoginScreen
import com.example.pizzahutapp.screen.SignUpScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "auth") {
        composable("auth") {
            AuthScreen(modifier,navController)
        }

        composable("login") {
            LoginScreen(modifier)
        }

        composable("signup") {
            SignUpScreen(modifier)
        }
    }
}