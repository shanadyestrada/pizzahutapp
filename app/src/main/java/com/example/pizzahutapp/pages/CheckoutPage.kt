package com.example.pizzahutapp.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahutapp.model.ProductModel
import com.example.pizzahutapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

@Composable
fun CheckoutPage(modifier: Modifier = Modifier) {

    val userModel = remember { mutableStateOf(UserModel()) }
    val productMap = remember { mutableStateMapOf<String, ProductModel>() }
    val total = remember { mutableStateOf(0f) }

    val context = LocalContext.current

    fun calculateTotal() {
        total.value = 0f
        userModel.value.cartItems.forEach { (fullId, qty) ->
            val product = productMap[fullId]
            if (product != null && product.precio.isNotEmpty()) {
                total.value += product.precio.toFloat() * qty
            }
        }
    }

    LaunchedEffect(Unit) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect

        val userDoc = com.google.firebase.Firebase.firestore.collection("usuarios")
            .document(currentUserUid)

        userDoc.get().addOnSuccessListener { doc ->
            val user = doc.toObject<UserModel>()
            if (user != null) {
                userModel.value = user

                val ids = user.cartItems.keys.map { it.split("_")[0] }.distinct()
                if (ids.isNotEmpty()) {
                    com.google.firebase.Firebase.firestore.collection("data")
                        .document("stock").collection("productos")
                        .whereIn("id", ids)
                        .get()
                        .addOnSuccessListener { snapshot ->
                            val products = snapshot.toObjects(ProductModel::class.java)

                            productMap.clear()
                            user.cartItems.forEach { (fullId, _) ->
                                val id = fullId.split("_")[0]
                                val product = products.find { it.id == id }
                                if (product != null) {
                                    productMap[fullId] = product
                                }
                            }

                            calculateTotal()
                        }
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Checkout", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        userModel.value.cartItems.forEach { (fullId, qty) ->
            val parts = fullId.split("_")
            val tamano = parts.getOrNull(1) ?: ""
            val corteza = parts.getOrNull(2) ?: ""
            val product = productMap[fullId] ?: return@forEach
            val subtotal = product.precio.toFloat() * qty

            val nameBuilder = StringBuilder(product.nombre)
            if (tamano.isNotEmpty()) nameBuilder.append(" - ${tamano.replaceFirstChar { it.uppercase() }}")
            if (corteza.isNotEmpty()) nameBuilder.append(" (${corteza.replaceFirstChar { it.uppercase() }})")

            RowCheckoutItems(
                title = "${nameBuilder} x$qty",
                value = "S/. ${String.format("%.2f", subtotal)}"
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Total",
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "S/. ${String.format("%.2f", total.value)}",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RowCheckoutItems(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Text(text = value, fontSize = 18.sp)
    }
}
