package com.example.pizzahutapp.pages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzahutapp.GlobalNavigation.navController
import com.example.pizzahutapp.viewmodel.ProfileUiState
import com.example.pizzahutapp.viewmodel.ProfileViewModel
import com.example.pizzahutapp.viewmodel.UpdateStatus

@Composable
fun EditProfilePage(
    modifier: Modifier,
    profileViewModel: ProfileViewModel = viewModel()
){

    val uiState by profileViewModel.userProfileState.collectAsState()
    val updateStatus by profileViewModel.updateStatus.collectAsState()
    val context = LocalContext.current

    var userName by remember { mutableStateOf("") }
    var userSurname by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if(uiState is ProfileUiState.Success){
            val user = (uiState as ProfileUiState.Success).user
            userName = user.nombre
            userSurname = user.apellidos
        }
    }

    LaunchedEffect(updateStatus) {
        when(updateStatus){
            UpdateStatus.SUCCESS -> {
                Toast.makeText(context, "Perfil actualizado con exito", Toast.LENGTH_SHORT)
                profileViewModel.resetUpdateStatus()
                navController.popBackStack()
            }
            UpdateStatus.ERROR -> {
                Toast.makeText(context, "Error al actualizar el perfil", Toast.LENGTH_SHORT)
                profileViewModel.resetUpdateStatus()
            }
            else -> {}
        }
    }



    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Editar Perfil",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(32.dp))

        when(uiState){
            is ProfileUiState.Loading ->{
                CircularProgressIndicator()
                Text(text = "Cargando datos")
            }
            is ProfileUiState.Error ->{
                Text(text = "Error al cargar el perfil: ${(uiState as ProfileUiState.Error).message}", color = MaterialTheme.colorScheme.error)
            }
            is ProfileUiState.Success -> {
                OutlinedTextField(
                    value =  userName,
                    onValueChange = {userName = it},
                    label = {Text("Nombre")},
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

            }
        }

    }

}