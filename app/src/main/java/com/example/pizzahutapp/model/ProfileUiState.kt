package com.example.pizzahutapp.model

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: UserModel) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}