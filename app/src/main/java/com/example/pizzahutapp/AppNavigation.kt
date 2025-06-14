package com.example.pizzahutapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pizzahutapp.pages.CategoryProductsPage
import com.example.pizzahutapp.screen.AuthScreen
import com.example.pizzahutapp.screen.HomeScreen
import com.example.pizzahutapp.screen.LoginScreen
import com.example.pizzahutapp.screen.SignUpScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    GlobalNavigation.navController = navController
    val isLoggedIn = Firebase.auth.currentUser!=null
    val firstPage = if(isLoggedIn)"home" else "auth"

    NavHost(navController = navController, startDestination = firstPage) {
        composable("auth") {
            AuthScreen(modifier,navController)
        }

        composable("login") {
            LoginScreen(modifier,navController)
        }

        composable("signup") {
            SignUpScreen(modifier,navController)
        }

        composable("home") {
            HomeScreen(modifier,navController)
        }
        composable("category-products/{categoryId}") {
            var categoryId = it.arguments?.getString("categoryId")
            CategoryProductsPage(modifier, categoryId?:"")
        }

    }
}

object GlobalNavigation {
    lateinit var navController : NavController
}