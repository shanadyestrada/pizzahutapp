package com.example.pizzahutapp.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pizzahutapp.GlobalNavigation
import com.example.pizzahutapp.components.BannerView
import com.example.pizzahutapp.components.HeaderView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun HomePage(modifier: Modifier = Modifier) {
    Column (modifier = Modifier.fillMaxSize().padding(16.dp)
    ){
        HeaderView(modifier)
        Spacer(modifier = Modifier.height(10.dp))
        BannerView(modifier = Modifier.height(200.dp))

        Button(onClick = {
            Firebase.auth.signOut()
            GlobalNavigation.navController.navigate("auth") {
                popUpTo("home") { inclusive = true}
            }
        }
        ) {
            Text(text = "Cerrar Sesión")
        }

    }

}