package com.example.pizzahutapp

import android.content.Context
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

object AppUtil {

    fun showToast(context: Context, mesagge: String) {
        Toast.makeText(context, mesagge, Toast.LENGTH_LONG).show()
    }

    fun addToCart(
        context: Context,
        productId: String,
        selectedVariationType: String = "",
        selectedVariationName: String = "",
        selectedPrice: String = ""
    ) {
        val userDoc = Firebase.firestore.collection("usuarios")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)

        val cartItemId =
            if (selectedVariationType.isNotBlank() && selectedVariationName.isNotBlank()) {
                "${productId}_${selectedVariationType}_${selectedVariationName}"
            } else {
                productId
            }

        userDoc.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val currentCart =
                    it.result.get("cartItems") as? Map<String, Map<String, Any>> ?: emptyMap()
                val existingItem = currentCart[cartItemId]
                val currentQuantity = (existingItem?.get("quantity") as? Long) ?: 0L
                val updatedQuantity = currentQuantity + 1;

                val itemDetails = mutableMapOf<String, Any>(
                    "productId" to productId as Any,
                    "quantity" to updatedQuantity as Any,
                    "price" to (selectedPrice.toDoubleOrNull() ?: 0.0) as Any,
                    "nombre" to (if (selectedVariationType.isNotBlank() && selectedVariationName.isNotBlank()) {
                        "$productId (${selectedVariationType.replaceFirstChar { it.titlecase() }} - ${selectedVariationName.replaceFirstChar { it.titlecase() }})"
                    } else {
                        productId
                    }) as Any
                )
                if(selectedVariationType.isNotBlank()){
                    itemDetails["variationType"] = selectedVariationType
                }
                if (selectedVariationName.isNotBlank()){
                    itemDetails["variationName"] = selectedVariationName
                }

                val updatedCartMap = currentCart.toMutableMap()
                updatedCartMap[cartItemId] = itemDetails

                userDoc.update("cartItems",updatedCartMap)
                    .addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            showToast(context, "Producto añadido al carrito")
                        } else {
                            showToast(context, "No se pudo agregar al carrito: ${updateTask.exception?.message}")
                        }
                    }

            }else{
                showToast(context, "No se pudo obtener el carrito: ${it.exception?.message}")
            }
        }
    }

    fun removeFromCart(
        context: Context,
        productId: String,
        selectedVariationType: String,
        selectedVariationName: String,
        removeAll: Boolean = false
    ) {
        val userDoc = Firebase.firestore.collection("usuarios")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)

        val cartItemId = if (selectedVariationType.isNotBlank() && selectedVariationName.isNotBlank()) {
            "${productId}_${selectedVariationType}_${selectedVariationName}"
        } else{
            productId
        }

        userDoc.get().addOnCompleteListener { task ->
            if(task.isSuccessful){
                val currentCart = task.result.get("cartItems") as? Map<String, Map<String, Any>> ?: emptyMap()
                val existingItem = currentCart[cartItemId]

                val currentQuantity = (existingItem?.get("quantity") as? Long)?: 0L
                val updatedQuantity = currentQuantity - 1

                val updatedCartMap = currentCart.toMutableMap()

                if (updatedQuantity <= 0 || removeAll) {
                    updatedCartMap.remove(cartItemId) // Remove the whole item if quantity is zero or removeAll is true
                } else {
                    // Update only the quantity for the specific item
                    val updatedItemDetails = existingItem?.toMutableMap() ?: mutableMapOf()
                    updatedItemDetails["quantity"] = updatedQuantity
                    updatedCartMap[cartItemId] = updatedItemDetails
                }
                userDoc.update("cartItems", updatedCartMap)
                    .addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            showToast(context, "Producto removido del carrito")
                        } else {
                            showToast(context, "No se pudo remover del carrito: ${updateTask.exception?.message}")
                        }
                    }
            } else {
                showToast(context, "No se pudo obtener el carrito: ${task.exception?.message}")
            }
        }

        userDoc.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val currentCart = it.result.get("cartItems") as? Map<String, Long> ?: emptyMap()
                val currentQuantity = currentCart[productId] ?: 0
                val updatedQuantity = currentQuantity - 1;

                val updatedCart =
                    if (updatedQuantity <= 0 || removeAll) {
                        mapOf("cartItems.$productId" to FieldValue.delete())
                    } else {
                        mapOf("cartItems.$productId" to updatedQuantity)
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