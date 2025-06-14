package com.example.pizzahutapp.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahutapp.components.CategoriesView

@Composable
fun FavoritePage(modifier: Modifier = Modifier) {
    Column (modifier = Modifier
        .fillMaxSize()
        .padding(32.dp)
        .height(18.dp)
    ){
        Text(text = "Categorías", style =
            TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        CategoriesView()
    }
}