package com.example.pizzahutapp.viewmodel

import android.content.Context
import android.credentials.GetCredentialException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzahutapp.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption

import kotlinx.coroutines.launch
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.firebase.auth.GoogleAuthProvider

class AuthViewModel  : ViewModel() {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    fun signInWithGoogleCredentialManager(context : Context, onResult: (Boolean, String?) -> Unit){
        viewModelScope.launch{
                try{
                    val credentialManager = CredentialManager.create(context)

                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(context.getString(com.example.pizzahutapp.R.string.default_web_client_id))
                        .build()

                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    val result = credentialManager.getCredential(context, request)
                    handleGoogleSignInCredentialManagerResult(result, onResult)
                }
                catch(e: GetCredentialException){

                    Log.e("AuthViewModel", "Credential Manager Option: ${e.message}", e)
                    onResult(false, "Error al iniciar sesión con Google: ${e.localizedMessage}")
                } catch (e: Exception){
                    Log.e("AuthViewModel", "Unexpected exception: ${e.message}",e)
                    onResult(false, "Ocurrió un error inesperado: ${e.localizedMessage}")
                }
        }
    }

    private fun handleGoogleSignInCredentialManagerResult(result: GetCredentialResponse, onResult: (Boolean,String?)-> Unit){

        val credential = result.credential
        if(credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
            try {
                val googleIdTokenCredential  = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful){
                            val firebaseUser = authTask.result?.user
                            firebaseUser?.let { user ->
                                firestore.collection("usuarios").document(user.uid)
                                    .get()
                                    .addOnSuccessListener { documentSnapshot ->
                                        if (!documentSnapshot.exists()){
                                            val userModel = UserModel(
                                                nombre = user.displayName?.split(" ")?.getOrNull(0) ?: "",
                                                apellidos = user.displayName?.split(" ")?.getOrNull(1)?: "",
                                                fechaNacimiento = "",
                                                telefono = user.phoneNumber ?: "",
                                                email = user.email ?: "",
                                                userId = user.uid,
                                                cartItems = emptyMap()
                                            )
                                            firestore.collection("usuarios").document(user.uid)
                                                .set(userModel)
                                                .addOnCompleteListener { dbTask ->
                                                    if(dbTask.isSuccessful){
                                                        onResult(true, null)
                                                    } else{
                                                        onResult(false, "Algo salio mal al guardar los datos del usuario en Firestore")
                                                    }
                                                }
                                                .addOnFailureListener { e ->
                                                    onResult(false, e.localizedMessage ?: "Error al guardar el usuario en Firestore")
                                                }
                                        } else {
                                            onResult(true, null)
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        onResult(false, e.localizedMessage ?: "Error al verificar el usuario en Firestore")
                                    }
                            } ?: run{
                                onResult(false, " Usuario de Firebase nulo despues de la autenticacion")
                            }
                        } else {
                            onResult(false, authTask.exception?.localizedMessage)
                        }
                    }
            } catch (e: GoogleIdTokenParsingException){
                Log.e("AuthViewModel", "Invalid Google ID Token: ${e.message}", e)
                onResult(false, "Token de Google inválido.")
            }
        } else {
            onResult(false, "Tipo de credencial no soportado para Google Sign-In.")
        }
    }

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