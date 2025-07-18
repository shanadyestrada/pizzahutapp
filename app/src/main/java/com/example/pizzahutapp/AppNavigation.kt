package com.example.pizzahutapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold // Import Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pizzahutapp.pages.CategoryProductsPage
import com.example.pizzahutapp.pages.CheckoutPage
import com.example.pizzahutapp.pages.MyAccountPage // Make sure this is MyAccountPage as you used it
import com.example.pizzahutapp.pages.ProfilePage
import com.example.pizzahutapp.pages.ProductDetailsPage
import com.example.pizzahutapp.screen.AuthScreen
import com.example.pizzahutapp.screen.HomeScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.example.pizzahutapp.screen.LoginScreen
import com.example.pizzahutapp.screen.SignUpScreen


object Routes {
    const val AUTH = "auth"
    const val LOGIN = "login"
    const val SIGN_UP = "signup"
    const val HOME = "home"
    const val MENU = "menu"
    const val LOCATIONS = "locations"
    const val CART = "cart"
    const val ACCOUNT = "account" // This is the key route for your MyAccountPage
    const val CATEGORY_PRODUCTS = "category-products/{categoryId}"
    const val PRODUCT_DETAILS = "product-details/{productId}"
    const val CHECKOUT = "checkout"
    const val PROFILE_DETAILS = "profile_details"
    const val MY_ADDRESSES = "my_addresses"
    const val MY_ORDERS = "my_orders"
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    GlobalNavigation.navController = navController
    val isLoggedIn = Firebase.auth.currentUser!=null
    val firstPage = if(isLoggedIn)"home" else "auth"

    NavHost(navController = navController, startDestination = firstPage) {
        composable(Routes.AUTH) { // Use Routes constant
            AuthScreen(modifier,navController)
        }

        composable(Routes.LOGIN) { // Use Routes constant
            LoginScreen(modifier,navController)
        }

        composable(Routes.SIGN_UP) { // Use Routes constant
            SignUpScreen(modifier,navController)
        }

        composable(Routes.HOME) { // Use Routes constant
            HomeScreen(modifier,navController)
        }
        composable(Routes.CATEGORY_PRODUCTS) { backStackEntry -> // Use Routes constant
            val categoryId = backStackEntry.arguments?.getString("categoryId")
            CategoryProductsPage(modifier, categoryId?:"")
        }

        composable(Routes.PRODUCT_DETAILS) { backStackEntry -> // Use Routes constant
            val productId = backStackEntry.arguments?.getString("productId")
            ProductDetailsPage(modifier, productId?:"")
        }

        composable(Routes.CHECKOUT) { // Use Routes constant
            CheckoutPage(modifier)
        }

        composable(Routes.ACCOUNT) { // This route is what the bottom nav's "Cuenta" item points to
            MyAccountPage(modifier,navController)
        }

        composable(Routes.PROFILE_DETAILS) {
            // This route is the destination when "Mi información" is clicked from MyAccountPage.
            ProfilePage(modifier = modifier)
        }

        composable(Routes.MY_ADDRESSES) {
            // This is a placeholder for your "Mis direcciones" screen.
            // You'll replace `Text("My Addresses Screen")` with your actual Composable.
            Text(text = "Mis Direcciones", modifier = modifier)
        }

        composable(Routes.MY_ORDERS) {
            // This is a placeholder for your "Ver mis pedidos" screen.
            // You'll replace `Text("My Orders Screen")` with your actual Composable.
            Text(text = "Mis Pedidos", modifier = modifier)
        }
        composable(Routes.MENU) {
            // Placeholder/direct link for 'Menu' tab.
            // You might want to pass a default category ID or navigate to a MenuCategoriesScreen
            CategoryProductsPage(modifier, "default_category_id_for_menu_tab")
        }

        composable(Routes.LOCATIONS) {
            // Placeholder for 'Locales' tab.
            Text(text = "Locales", modifier = modifier)
        }

        composable(Routes.CART) {
            // Placeholder for 'Carrito' tab.
            Text(text = "Carrito", modifier = modifier)
        }

    }
}

object GlobalNavigation {
    lateinit var navController : NavController
}