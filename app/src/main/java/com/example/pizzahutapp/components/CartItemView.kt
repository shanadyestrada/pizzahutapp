package com.example.pizzahutapp.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pizzahutapp.AppUtil
import com.example.pizzahutapp.model.ProductModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun CartItemView(modifier: Modifier = Modifier, productId: String, qty: Long) {

    val parts = productId.split("_")
    val id = parts.getOrNull(0) ?: ""
    val tamano = parts.getOrNull(1) ?: ""
    val corteza = parts.getOrNull(2) ?: ""

    var product by remember {
        mutableStateOf(ProductModel())
    }

    val context = LocalContext.current

    LaunchedEffect(key1 = id) {
        Firebase.firestore.collection("data")
            .document("stock")
            .collection("productos")
            .document(id)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val result = it.result.toObject(ProductModel::class.java)
                    if (result != null) {
                        product = result
                    }
                }
            }
    }

    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {

            // Imagen
            AsyncImage(
                model = product.image,
                contentDescription = product.nombre,
                modifier = Modifier
                    .height(100.dp)
                    .width(100.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            // Contenido
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = product.nombre.uppercase(),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Mostrar tamaño y corteza solo si el producto tiene variaciones
                if (product.variaciones != null) {
                    if (tamano.isNotEmpty()) {
                        Text(text = "Tamaño: ${tamano.replaceFirstChar { it.uppercase() }}", fontSize = 12.sp)
                    }
                    if (corteza.isNotEmpty()) {
                        Text(text = "Corteza: ${corteza.replaceFirstChar { it.uppercase() }}", fontSize = 12.sp)
                    }
                }

                Text(
                    text = "S/. ${product.precio}",
                    fontWeight = FontWeight.SemiBold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        AppUtil.removeFromCart(context, id, tamano, corteza)
                    }) {
                        Text(text = "-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }

                    Text(text = "$qty", fontSize = 16.sp)

                    IconButton(onClick = {
                        AppUtil.addToCart(context, id, tamano, corteza)
                    }) {
                        Text(text = "+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            IconButton(onClick = {
                AppUtil.removeFromCart(context, id, tamano, corteza, removeAll = true)
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar del carrito"
                )
            }
        }
    }
}
