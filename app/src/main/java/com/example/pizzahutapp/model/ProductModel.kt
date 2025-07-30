package com.example.pizzahutapp.model

data class ProductModel(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val image: String = "",
    val categoria: String = "",
    val precio: String = "",
    val variaciones: VariacionesModel? = null
)

data class VariacionesModel(
    val familiar: TamanoModel? = null,
    val grande: TamanoModel? = null,
    val mediana: TamanoModel? = null
)

data class TamanoModel(
    val artesanal: String = "",
    val delgada: String = "",
    val panPizza: String = ""
)
