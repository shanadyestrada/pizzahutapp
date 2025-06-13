package com.example.pizzahutapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzahutapp.AppUtil
import com.example.pizzahutapp.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(modifier: Modifier = Modifier, authViewModel: AuthViewModel = viewModel()) {

    var email by remember {
        mutableStateOf("")
    }

    var nombre by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var context = LocalContext.current

    Column (modifier = Modifier
        .fillMaxSize()
        .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text( text = "Bienvenidos!",
            style = TextStyle(
                fontSize = 30.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text( text = "Registrate con nosotros!",
            style = TextStyle(
                fontSize = 22.sp,
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = email,
            onValueChange = {
            email = it
        }, label = {
            Text(text = "Correo Electrónico")
        },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
        }, label = {
            Text(text = "Nombres")
        },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {
            password = it
        }, label = {
            Text(text = "Contraseña")
        },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            authViewModel.signup(email, nombre, password) {sucess,errorMesagge ->
                if (sucess) {

                } else {
                    AppUtil.showToast(context,errorMesagge?: "Algo salió mal..")
                }
            }
        },
            modifier = Modifier.fillMaxWidth()
                .height(60.dp)
        ) {
            Text(text = "Registrarse", fontSize = 22.sp)
        }

    }
}