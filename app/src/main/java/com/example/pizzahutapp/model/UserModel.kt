package com.example.pizzahutapp.model

import com.google.firebase.firestore.PropertyName

data class UserModel(
    @get:PropertyName("nombre") // Si en Firestore es 'nombre'
    @set:PropertyName("nombre")
    var nombre : String = "",

    @get:PropertyName("apellidos") // Si en Firestore es 'apellidos'
    @set:PropertyName("apellidos")
    var apellidos : String = "",

    // Asumiendo que 'fechaNacimiento' es idéntico, no necesita anotación
    val fechaNacimiento: String = "",

    @get:PropertyName("telefono")
    @set:PropertyName("telefono")
    var telefono : String = "",

    @get:PropertyName("email")
    @set:PropertyName("email")
    var email : String = "",

    @get:PropertyName("userId")
    @set:PropertyName("userId")
    var userId : String = "",

    @get:PropertyName("cartItems")
    @set:PropertyName("cartItems")
    var cartItems : Map<String, Map<String, Any>> = emptyMap()
)
