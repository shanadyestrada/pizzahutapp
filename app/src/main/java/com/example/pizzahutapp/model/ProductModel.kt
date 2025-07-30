package com.example.pizzahutapp.model

import androidx.compose.runtime.snapshots.Snapshot
import com.google.firebase.firestore.DocumentSnapshot

data class ProductModel(
    val id : String = "",
    val nombre : String = "",
    val descripcion : String = "",
    val image : String = "",
    val categoria : String = "",
    val precio : String = "",
    val variaciones : Map<String, Map<String,String>> = emptyMap()
){
    companion object{
        fun getFromSnapshot(snapshot: DocumentSnapshot): ProductModel{
            return ProductModel(
                id = snapshot.id,
                nombre = snapshot.getString("nombre") ?: "",
                descripcion = snapshot.getString("descripcion") ?: "",
                image = snapshot.getString("image") ?: "",
                categoria = snapshot.getString("categoria") ?: "",
                precio = snapshot.getString("precio") ?: "",
                variaciones = snapshot.get("variaciones") as? Map<String, Map<String, String>> ?: emptyMap()
            )
        }
    }
}
