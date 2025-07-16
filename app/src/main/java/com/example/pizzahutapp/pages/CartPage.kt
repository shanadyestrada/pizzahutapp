package com.example.pizzahutapp.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahutapp.AppUtil
import com.example.pizzahutapp.GlobalNavigation
import com.example.pizzahutapp.components.CartItemView
import com.example.pizzahutapp.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

@Composable
fun CartPage(modifier: Modifier = Modifier) {

    val userModel = remember {
        mutableStateOf(UserModel())
    }

    val context = LocalContext.current

    DisposableEffect(key1 = Unit) {
        val listenerRegistration = Firebase.firestore.collection("usuarios")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Use the captured 'context' here
                    AppUtil.showToast(context, "Error al cargar carrito: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val result = snapshot.toObject(UserModel::class.java)
                    if (result != null) {
                        userModel.value = result
                    }
                } else {
                    // Handle the case where the snapshot is null or doesn't exist (e.g., user doc deleted)
                    userModel.value = UserModel() // Reset cart if document is gone
                }
            }
        onDispose {
            listenerRegistration.remove()
        }
    }

    Column (modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
    ){
        Text(text = "Tu carrito", style = TextStyle(
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        ))

        if (userModel.value.cartItems.isNotEmpty()){
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(userModel.value.cartItems.entries.toList(), key = {it.key}){(cartItemId, itemDetails) ->
                    CartItemView(cartItemId = cartItemId, itemDetails = itemDetails)
                }
            }
        } else{
            Column(
                modifier = Modifier.fillMaxSize().weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tu carrito está vacío",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        val totalPrice = userModel.value.cartItems.values.sumOf{ itemDetails ->
            val price = (itemDetails["price"] as? Double) ?: 0.0
            val quantity = (itemDetails["quantity"] as? Long)?.toDouble() ?: 0.0
            price * quantity
        }

        if (userModel.value.cartItems.isNotEmpty()){
            Text(
                text = "Total: S/. ${"%.2f".format(totalPrice)}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End
                )

            )
        }

        Button(
            onClick = {
                if (userModel.value.cartItems.isNotEmpty()) {
                    GlobalNavigation.navController.navigate("checkout")
                } else {
                    AppUtil.showToast(context, "Tu carrito está vacío para proceder al pago.")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(top = 8.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Proceder al pago", fontSize = 16.sp)
        }
    }
}