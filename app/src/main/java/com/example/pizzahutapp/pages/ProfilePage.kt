package com.example.pizzahutapp.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzahutapp.model.ProfileUiState
import com.example.pizzahutapp.viewmodel.ProfileViewModel

@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()) {
    val uiState by viewModel.userProfileState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when(uiState){
            is ProfileUiState.Loading ->{
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Cargando perfil...")
            }
            is ProfileUiState.Success -> {
                val user = (uiState as ProfileUiState.Success).user
                Text(text = "Perfil del Usuario", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "Nombre: ${user.nombre} ${user.apellidos}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Fecha de Nacimiento: ${user.fechaNacimiento}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Teléfono: ${user.telefono}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Email: ${user.email}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "ID de Usuario: ${user.userId}", style = MaterialTheme.typography.bodySmall)
            }
            is ProfileUiState.Error -> {
                val errorMessage = (uiState as ProfileUiState.Error).message
                Text(text = "Error: $errorMessage", color = MaterialTheme.colorScheme.error)
            }
        }
    }

}