package com.example.pizzahutapp.components

import androidx.compose.foundation.background
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pizzahutapp.AppUtil
import com.example.pizzahutapp.GlobalNavigation
import com.example.pizzahutapp.model.ProductModel // Asegúrate de que esta importación sea correcta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductItemView(modifier: Modifier = Modifier, product: ProductModel) {

    val context = LocalContext.current
    var selectedVariationType by remember {
        mutableStateOf(product.variaciones.keys.firstOrNull() ?: "")
    }

    var selectedVariationName by remember {
        mutableStateOf(
            product.variaciones[selectedVariationType]?.keys?.firstOrNull() ?: ""
        )
    }

    val currentPrice = remember(selectedVariationType, selectedVariationName){
        if(selectedVariationType.isNotBlank() && selectedVariationName.isNotBlank()){
            product.variaciones[selectedVariationType]?.get(selectedVariationName) ?: product.precio
        }else{
            product.precio
        }
    }

    Card(
        modifier = modifier
            .padding(8.dp)
            .clickable {
                GlobalNavigation.navController.navigate("product-details/" + product.id)
            }
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth() // La columna dentro de la Card ocupa todo el ancho
        ) {
            // Imagen del producto
            AsyncImage(
                model = product.image,
                contentDescription = product.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            )

            // Contenido de texto
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = product.nombre.uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (product.descripcion.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.descripcion,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if(product.variaciones.isNotEmpty()){
                    var expandedType by remember { mutableStateOf(false)}

                    ExposedDropdownMenuBox(
                        expanded = expandedType,
                        onExpandedChange = { expandedType = !expandedType },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = selectedVariationType.takeIf{it.isNotBlank()} ?: "Select Type",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tamaño")},
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedType,
                            onDismissRequest = { expandedType = false }
                        ){
                            product.variaciones.keys.forEach { type ->
                                DropdownMenuItem(
                                    text = {Text(type.replaceFirstChar {if (it.isLowerCase()) it.titlecase() else it.toString()})},
                                    onClick = {
                                        selectedVariationType = type
                                        selectedVariationName = product.variaciones[type]?.keys?.firstOrNull() ?: ""
                                        expandedType = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (product.variaciones[selectedVariationType]?.isNotEmpty() == true){
                        var expandedName by remember {mutableStateOf(false)}

                        ExposedDropdownMenuBox(
                            expanded = expandedName,
                            onExpandedChange = {expandedName = !expandedName},
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextField(
                                value = selectedVariationName.takeIf{it.isNotBlank()} ?: "Select Variation",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Variación")},
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedName)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedName,
                                onDismissRequest = { expandedName = false}
                            ){
                                product.variaciones[selectedVariationType]?.forEach { (name, _)->
                                    DropdownMenuItem(
                                        text = {Text(name.replaceFirstChar {if (it.isLowerCase()) it.titlecase() else it.toString() }) },
                                        onClick = {
                                            selectedVariationName = name
                                            expandedName = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    } else if (selectedVariationType.isNotBlank()){
                        Text(
                            text = "No hay variaciones para este tamaño",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = "S/. $currentPrice", // Usar el precio directamente como String
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary // Color principal para el precio
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        AppUtil.addToCart(
                            context, product.id, selectedVariationType, selectedVariationName, currentPrice
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error) // <-- ¡CAMBIO AQUÍ!
                ) {
                    Text(text = "Agregar al Carrito", fontSize = 16.sp)
                }

            }
        }
    }
}