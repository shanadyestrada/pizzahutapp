package com.example.pizzahutapp.pages

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pizzahutapp.AppUtil
import com.example.pizzahutapp.model.ProductModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Composable
fun ProductDetailsPage(modifier: Modifier = Modifier, productId : String) {

    var product by remember {
        mutableStateOf(ProductModel())
    }

    var selectedTamano by remember { mutableStateOf("") }
    var selectedCorteza by remember { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        Firebase.firestore.collection("data").document("stock")
            .collection("productos")
            .document(productId).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val result = it.result.toObject(ProductModel::class.java)
                    if (result != null) {
                        product = result
                    }
                }
            }
    }

    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = product.image,
            contentDescription = product.nombre,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
        )
        Spacer(modifier = Modifier.padding(8.dp))

        Text(
            text = product.nombre,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(8.dp)
        )

        Text(
            text = product.descripcion,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.padding(8.dp))

        Text(
            text = "S/. ${product.precio}",
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.padding(8.dp))

        // SOLO si el producto tiene variaciones (es decir, si es pizza)
        if (product.variaciones != null) {
            // SECCIÓN: Selección de tamaño
            Text("Selecciona Tamaño:", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            val tamanos = listOf("familiar", "grande", "mediana")
            tamanos.forEach { tamano ->
                val isSelected = selectedTamano == tamano
                Button(
                    onClick = { selectedTamano = tamano },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = tamano.replaceFirstChar { it.uppercase() },
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // SECCIÓN: Selección de corteza
            Text("Selecciona Corteza:", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            val cortezas = listOf("artesanal", "delgada", "panPizza")
            cortezas.forEach { corteza ->
                val isSelected = selectedCorteza == corteza
                Button(
                    onClick = { selectedCorteza = corteza },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = corteza.replaceFirstChar { it.uppercase() },
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // BOTÓN AGREGAR AL CARRITO
        Button(
            onClick = {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button

                if (product.variaciones == null) {
                    // Producto sin variaciones (bebidas, entradas)
                    AppUtil.addToCart(
                        context,
                        productId = product.id,
                        tamano = "",
                        corteza = ""
                    )
                } else {
                    if (selectedTamano.isNotEmpty() && selectedCorteza.isNotEmpty()) {
                        AppUtil.addToCart(
                            context,
                            productId = product.id,
                            tamano = selectedTamano,
                            corteza = selectedCorteza
                        )
                    } else {
                        Toast.makeText(context, "Selecciona tamaño y corteza", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Agregar al Carrito", fontSize = 18.sp)
        }
    }
}

