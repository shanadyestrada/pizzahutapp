package com.example.pizzahutapp.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pizzahutapp.model.CategoryModel
import com.example.pizzahutapp.model.ProductModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun CategoryProductsPage(modifier: Modifier = Modifier, categoryId : String) {
    Column (modifier = Modifier
        .fillMaxSize()
        .padding(32.dp)
        .height(18.dp)
    ) {
        val productList = remember {
            mutableStateOf<List<ProductModel>>(emptyList())
        }

        LaunchedEffect(key1 = Unit) {
            Firebase.firestore.collection("data").document("stock")
                .collection("productos")
                .whereEqualTo("categoria", categoryId )
                .get().addOnCompleteListener() { task ->
                    if(task.isSuccessful) {
                        val resultList = task.result.documents.mapNotNull { doc ->
                            doc.toObject(ProductModel :: class.java)
                        }
                        productList.value = resultList
                    }
                }
        }

        LazyColumn (
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            items(productList.value) { item ->
                Text(text = item.nombre)
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
    
}