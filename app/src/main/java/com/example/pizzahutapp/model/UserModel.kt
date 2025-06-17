package com.example.pizzahutapp.model

data class UserModel(
    val nombre : String = "",
    val email : String = "",
    val userId : String = "",
    val cartItems : Map<String, Long> = emptyMap()
)
