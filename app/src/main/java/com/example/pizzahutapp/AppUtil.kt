package com.example.pizzahutapp

import android.content.Context
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

object AppUtil {

    fun showToast(context : Context, mesagge : String) {
        Toast.makeText(context,mesagge,Toast.LENGTH_LONG).show()
    }

    fun addToCart (context: Context, productId: String) {
        val userDoc = Firebase.firestore.collection("usuarios")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)

        userDoc.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val currentCart = it.result.get("cartItems") as? Map<String, Long> ?: emptyMap()
                val currentQuantity = currentCart[productId]?:0
                val updatedQuantity = currentQuantity + 1;

                val updatedCart = mapOf("cartItems.$productId" to updatedQuantity)

                userDoc.update(updatedCart)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            showToast(context, "Producto añadido al carrito")
                        } else {
                            showToast(context, "No se pudo agregar al carrito")
                        }
                    }

            }
        }
    }
}