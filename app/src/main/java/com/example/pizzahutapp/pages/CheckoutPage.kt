package com.example.pizzahutapp.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahutapp.AppUtil
import androidx.compose.runtime.DisposableEffect
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import com.example.pizzahutapp.model.ProductModel
import com.example.pizzahutapp.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

@Composable
fun CheckoutPage(modifier: Modifier = Modifier) {

    val userModel = remember {
        mutableStateOf(UserModel())
    }
    val context = LocalContext.current

    // No need for productList now, as cartItems directly contain the necessary info

    // Using a DisposableEffect to listen to real-time cart changes
    DisposableEffect(key1 = Unit) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid == null) {
            // Handle not logged in case if necessary (e.g., navigate to login)
            return@DisposableEffect onDispose {} // Dispose immediately if no user
        }

        val listenerRegistration = Firebase.firestore.collection("usuarios")
            .document(currentUserUid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Use the captured 'context' directly here
                    AppUtil.showToast(context, "Error al cargar carrito: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val result = snapshot.toObject(UserModel::class.java)
                    if (result != null) {
                        userModel.value = result
                    }
                } else {
                    // Cart document might not exist, or user signed out
                    userModel.value = UserModel()
                }
            }
        onDispose {
            listenerRegistration.remove()
        }
    }

    // Calculate total price based on the current userModel.value.cartItems
    val totalPrice = remember(userModel.value.cartItems) { // Recalculate when cartItems change
        userModel.value.cartItems.values.sumOf { itemDetails ->
            val price = (itemDetails["price"] as? Double) ?: 0.0
            val quantity = (itemDetails["quantity"] as? Long)?.toDouble() ?: 0.0
            price * quantity
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "Checkout",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Display individual cart items using LazyColumn
        if (userModel.value.cartItems.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                // This 'items' function should now resolve after the import
                items(userModel.value.cartItems.entries.toList(), key = { it.key }) { (cartItemId, itemDetails) ->
                    val productName = itemDetails["nombre"] as? String ?: "Producto Desconocido"
                    val itemPrice = (itemDetails["price"] as? Double) ?: 0.0
                    val itemQuantity = (itemDetails["quantity"] as? Long) ?: 0L

                    if (itemQuantity > 0) {
                        val productSubtotal = itemPrice * itemQuantity
                        RowCheckoutItems(
                            title = "$productName (x$itemQuantity)",
                            value = "S/. ${String.format("%.2f", productSubtotal)}"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Tu carrito está vacío.",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }


        // Total price display
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Total",
            textAlign = TextAlign.End, // Align to end
            fontSize = 20.sp, // Slightly larger
            fontWeight = FontWeight.Bold
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "S/. ${String.format("%.2f", totalPrice)}",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End // Align to end
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun RowCheckoutItems(title : String, value : String) {
    Row (
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text (text = title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Text(text = value, fontSize = 18.sp)
    }
}