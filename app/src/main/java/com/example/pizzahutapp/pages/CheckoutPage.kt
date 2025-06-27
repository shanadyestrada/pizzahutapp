package com.example.pizzahutapp.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahutapp.model.ProductModel
import com.example.pizzahutapp.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

@Composable
fun CheckoutPage (modifier: Modifier = Modifier) {

    val userModel = remember {
        mutableStateOf(UserModel())
    }

    val productList = remember {
        mutableStateListOf<ProductModel>()
    }

    val total = remember { // Cambiado de 'subtotal' a 'total' para el valor final
        mutableStateOf(0f)
    }

    fun calculateTotal () { // Renombrada la función para reflejar que ahora es el total final
        total.value = 0f // Reiniciar total antes de cada cálculo
        productList.forEach { product ->
            if (product.precio.isNotEmpty()) {
                val qty = userModel.value.cartItems[product.id] ?: 0
                total.value +=  product.precio.toFloat() * qty // Acumular al total
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid == null) {
            return@LaunchedEffect
        }

        Firebase.firestore.collection("usuarios")
            .document(currentUserUid)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val result = it.result.toObject(UserModel::class.java)
                    if (result!= null) {
                        userModel.value = result

                        val productIdsInCart = userModel.value.cartItems.keys.toList()
                        if (productIdsInCart.isNotEmpty()) {
                            Firebase.firestore.collection("data")
                                .document("stock").collection("productos")
                                .whereIn("id", productIdsInCart)
                                .get().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        productList.clear()
                                        val resultProducts = task.result.toObjects(ProductModel::class.java)
                                        productList.addAll(resultProducts)
                                        calculateTotal()
                                    }
                                }
                        } else {
                            total.value = 0f // Si no hay items, el total es 0
                        }
                    }
                }
            }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(text = "Checkout",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar el subtotal por cada producto
        productList.forEach { product ->
            val qty = userModel.value.cartItems[product.id] ?: 0
            if (qty > 0 && product.precio.isNotEmpty()) {
                val productSubtotal = product.precio.toFloat() * qty
                RowCheckoutItems(
                    title = "${product.nombre} (x$qty)",
                    value = "S/. ${String.format("%.2f", productSubtotal)}"
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
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
fun RowCheckoutItems(title : String, value : String) {
    Row (
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text (text = title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Text(text = value, fontSize = 18.sp)
    }
}