package com.example.pizzahutapp

import android.content.Context
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

object AppUtil {

    fun showToast(context : Context, mesagge : String) {
        Toast.makeText(context,mesagge,Toast.LENGTH_LONG).show()
    }

    fun addToCart(
        context: Context,
        productId: String,
        tamano: String,
        corteza: String
    ) {
        val fullProductId = "${productId}_${tamano}_${corteza}"

        val userDoc = Firebase.firestore.collection("usuarios")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)

        userDoc.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val currentCart = it.result.get("cartItems") as? Map<String, Long> ?: emptyMap()
                val currentQuantity = currentCart[fullProductId] ?: 0
                val updatedQuantity = currentQuantity + 1

                val updatedCart = mapOf("cartItems.$fullProductId" to updatedQuantity)

                userDoc.update(updatedCart)
                    .addOnCompleteListener { result ->
                        if (result.isSuccessful) {
                            showToast(context, "Producto añadido al carrito")
                        } else {
                            showToast(context, "No se pudo agregar al carrito")
                        }
                    }
            }
        }
    }

    fun removeFromCart(
        context: Context,
        productId: String,
        tamano: String,
        corteza: String,
        removeAll: Boolean = false
    ) {
        val fullProductId = "${productId}_${tamano}_${corteza}"

        val userDoc = Firebase.firestore.collection("usuarios")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)

        userDoc.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val currentCart = it.result.get("cartItems") as? Map<String, Long> ?: emptyMap()
                val currentQuantity = currentCart[fullProductId] ?: 0
                val updatedQuantity = currentQuantity - 1

                val updatedCart = if (updatedQuantity <= 0 || removeAll) {
                    mapOf("cartItems.$fullProductId" to FieldValue.delete())
                } else {
                    mapOf("cartItems.$fullProductId" to updatedQuantity)
                }

                userDoc.update(updatedCart)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            showToast(context, "Producto removido del carrito")
                        } else {
                            showToast(context, "No se pudo remover del carrito")
                        }
                    }
            }
        }
    }

}
