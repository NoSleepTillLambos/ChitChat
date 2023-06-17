package com.example.chitchat.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        Text(text = "statuses yaa")
        bottomNavigationMenu(selectedItem = BottomNavigationItem.STATUSSCREEN, navController = navController)
    }
}