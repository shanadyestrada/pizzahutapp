package com.example.pizzahutapp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size // Asegúrate de que esta importación exista
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale // ¡Importante! Añadir esta importación
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pizzahutapp.GlobalNavigation
import com.example.pizzahutapp.model.CategoryModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun CategoriesView(modifier: Modifier = Modifier) {
    val categoryList = remember {
        mutableStateOf<List<CategoryModel>>(emptyList())
    }

    LaunchedEffect(key1 = Unit) {
        Firebase.firestore.collection("data").document("stock")
            .collection("categorias")
            .get().addOnCompleteListener() { task ->
                if(task.isSuccessful) {
                    val resultList = task.result.documents.mapNotNull { doc ->
                        doc.toObject(CategoryModel :: class.java)
                    }
                    categoryList.value = resultList
                } else {
                    // Manejar error aquí
                }
            }
    }

    LazyColumn (
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp) // Añadido padding vertical también
    ) {
        items(categoryList.value) { item ->
            // Ajustar el tamaño de la tarjeta para que tenga un aspecto más de "rectángulo"
            // y permita que la imagen y el texto se distribuyan mejor.
            CategoItem(category = item, modifier = Modifier
                .fillMaxWidth()
                .height(150.dp) // Aumenta la altura de la tarjeta
            )
        }
    }
}

@Composable
fun CategoItem(category : CategoryModel, modifier: Modifier = Modifier) {
    Card (
        modifier = modifier.clickable {
            GlobalNavigation.navController.navigate("category-products/${category.id}") // Usando string templates
        },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // Asumo que quieres el fondo blanco/claro
    ){
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            // Centra verticalmente el contenido dentro de la columna, pero sin usar fillMaxSize aquí.
            // En su lugar, el Spacer se encargará de empujar el texto hacia abajo.
            modifier = Modifier.fillMaxSize() // Fill the card's available space
        ){
            // La imagen debe ocupar el espacio restante que le permita su contentScale,
            // sin tener un size fijo, para que se expanda
            AsyncImage(
                model = category.imageUrl,
                contentDescription = category.nombre,
                contentScale = ContentScale.Crop, // ¡CLAVE! Esto recorta la imagen para llenar los límites
                modifier = Modifier
                    .fillMaxWidth() // La imagen intentará llenar todo el ancho de la columna
                    .weight(1f) // La imagen tomará todo el espacio vertical disponible (menos el texto y el Spacer)
            )
            Spacer(modifier = Modifier.height(8.dp)) // Espacio entre imagen y texto
            Text(text = category.nombre,
                modifier = Modifier.padding(bottom = 8.dp) // Añadir padding inferior al texto
            )
        }
    }
}