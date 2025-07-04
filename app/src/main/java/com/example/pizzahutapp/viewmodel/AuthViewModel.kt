package com.example.pizzahutapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pizzahutapp.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class AuthViewModel  : ViewModel() {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    fun login (email: String, password: String, onResult : (Boolean,String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false,it.exception?.localizedMessage)
                }
            }
    }

    fun signup(
        email: String,
        nombre: String,
        apellidos: String,
        password: String,
        fechaNacimiento: String,
        telefono: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    var userId = it.result?.user?.uid
                    val userModel = UserModel(nombre,apellidos, fechaNacimiento, telefono, email, userId!!)
                    firestore.collection("usuarios").document(userId)
                        .set(userModel)
                        .addOnCompleteListener { dbTask->
                            if (dbTask.isSuccessful) {
                                onResult(true, null)
                            } else {
                                onResult(false,"Algo salió mal..")
                            }
                        }
                } else {
                    onResult(false,it.exception?.localizedMessage)
                }
            }
    }
}