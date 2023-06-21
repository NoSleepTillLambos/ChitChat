package com.example.chitchat.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chitchat.BottomNavigationItem
import com.example.chitchat.bottomNavigationMenu


@Composable
fun StatusScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Statuses",
        fontSize = 30.sp,
        color = Color (0xFFF7CF4A))

        Card(modifier = Modifier.background(Color.LightGray)) {

        }
        bottomNavigationMenu(selectedItem = BottomNavigationItem.STATUSSCREEN, navController = navController)
    }
}