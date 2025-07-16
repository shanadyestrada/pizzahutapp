package com.example.pizzahutapp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.pizzahutapp.GlobalNavigation
import com.example.pizzahutapp.model.ProductModel
import com.example.pizzahutapp.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun CartItemView(modifier: Modifier = Modifier, cartItemId: String, itemDetails: Map<String, Any>) {

    val productId = itemDetails["productId"] as? String ?: ""
    val qty = itemDetails["quantity"] as? Long ?: 0L
    val price = itemDetails["price"]?.toString() ?: "0.00"
    val variationType = itemDetails["variationType"] as? String ?: ""
    val variationName = itemDetails["varaitionName"] as? String ?: ""

    var productBaseInfo by remember { mutableStateOf(ProductModel()) }

    LaunchedEffect(key1 = productId) {
        if(productId.isNotBlank()){
            Firebase.firestore.collection("data")
                .document("stock").collection("productos")
                .document(productId).get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val result = it.result.toObject(ProductModel::class.java)
                        if (result!=null) {
                            productBaseInfo = result
                        }
                    } else{

                    }
                }
        }
    }

    var context = LocalContext.current

    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del producto
            AsyncImage(
                model = productBaseInfo.image,
                contentDescription = productBaseInfo.nombre,
                modifier = Modifier
                    .height(100.dp)
                    .width(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Contenido de texto
            Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                Text(
                    text = if(variationType.isNotBlank() && variationName.isNotBlank()){
                        "${productBaseInfo.nombre.uppercase()} (${variationType.replaceFirstChar{it.titlecase() }} - ${variationName.replaceFirstChar { it.titlecase() }})"
                    } else {
                        productBaseInfo.nombre.uppercase()
                    },
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "S/. ${price}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    IconButton(onClick = { AppUtil.removeFromCart(context, productId, variationType, variationName)}) {
                        Text(text = "-", fontSize = 20.sp,  fontWeight = FontWeight.Bold)
                    }

                    Text(text= "$qty", fontSize = 16.sp)

                    IconButton(onClick = {AppUtil.addToCart(context, productId, variationType, variationName, price)}) {
                        Text(text = "+", fontSize = 20.sp,  fontWeight = FontWeight.Bold)
                    }
                }
            }



            IconButton(
                onClick = { AppUtil.removeFromCart(context, productId, variationType, variationName,removeAll = true) },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar del carrito",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

}