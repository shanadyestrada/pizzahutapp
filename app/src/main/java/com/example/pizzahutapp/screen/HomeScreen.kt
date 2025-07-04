package com.example.pizzahutapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pizzahutapp.pages.CartPage
import com.example.pizzahutapp.pages.FavoritePage
import com.example.pizzahutapp.pages.HomePage
import com.example.pizzahutapp.pages.LocalPage
import com.example.pizzahutapp.pages.ProfilePage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label

@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {
    val navItemList = listOf(
        NavItem("Home", Icons.Default.Home),
        NavItem("Products", Icons.Default.Favorite),
        NavItem("Locals", Icons.Default.LocationOn),
        NavItem("Cart", Icons.Default.ShoppingCart),
        NavItem("Profile", Icons.Default.AccountCircle)
    )

    var selectedIndex by rememberSaveable {
        mutableStateOf(0)
    }




    Scaffold (bottomBar = {
        NavigationBar {
            navItemList.forEachIndexed { index, navItem ->
                NavigationBarItem(
                    selected = index==selectedIndex,
                    onClick = {
                        selectedIndex = index
                    },
                    icon = {
                        Icon(imageVector = navItem.icon, contentDescription = navItem.label)
                    },
                    label = {
                        Text(text = navItem.label)
                    }
                )
            }
        }
    }) {
        ContentScreen(modifier = Modifier.padding(it), selectedIndex)
    }
}

@Composable
fun ContentScreen(modifier: Modifier = Modifier, selectedIndex : Int) {
    when (selectedIndex) {
        0 -> HomePage(modifier)
        1 -> FavoritePage(modifier)
        2 -> LocalPage(modifier)
        3 -> CartPage(modifier)
        4 -> ProfilePage(modifier)
    }
}

data class NavItem (
    val label: String,
    val icon: ImageVector
)