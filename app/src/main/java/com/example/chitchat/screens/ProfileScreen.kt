package com.example.chitchat.screens

import android.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chitchat.BottomNavigationItem
import com.example.chitchat.CAViewModel
import com.example.chitchat.DestinationScreen
import com.example.chitchat.ProgressSpinner
import com.example.chitchat.SectionDivider
import com.example.chitchat.bottomNavigationMenu
import com.example.chitchat.navigateTo

@Composable
fun ProfileScreen(navController: NavController, vm : CAViewModel) {

    val inProgress = vm.isInProgress.value
    if (inProgress)
        ProgressSpinner()
    else {

        val userData = vm.usersData.value
        var name by rememberSaveable { mutableStateOf(userData?.name ?: "") }
        var number by rememberSaveable { mutableStateOf(userData?.number ?: "") }

        val scrollingState = rememberScrollState()
        val focus = LocalFocusManager.current

        Column {
            ProfileContent(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollingState)
                    .padding(5.dp),
                vm = vm,
                name = name,
                number = number,
                onNameChange = { name ="it"},
                onNumberChange = { number = "it"},
                onSave = {
                    focus.clearFocus(force = true)
                    // updating profile using vm

                },
                onBack = {
                    focus.clearFocus(force = true)
                    navigateTo(navController, DestinationScreen.ConversationPage.route)
                },
                onLogout = {
                    vm.logOut()
                    navigateTo(navController, DestinationScreen.Login.route)
                }


            )

            bottomNavigationMenu(selectedItem = BottomNavigationItem.PROFILE, navController =  navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    modifier: Modifier,
    vm: CAViewModel,
    name: String,
    number: String,
    onNameChange: (String) -> Unit,
    onNumberChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    // all content for the profile
    Column(modifier = modifier) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(),
        horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Head back", modifier = Modifier.clickable { onBack.invoke() })
            Text(text = "Save", modifier = Modifier.clickable { onSave.invoke() })
        }
        SectionDivider()

        ProfileImage()

        // simple for dividing sections
        SectionDivider()

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding()) {
            Text(text = "Your name", modifier = Modifier.width(100.dp))
            TextField(
                value = name,
                onValueChange = onNameChange,
                colors = TextFieldDefaults.textFieldColors(
                    textColor = androidx.compose.ui.graphics.Color.Black
                    , containerColor = androidx.compose.ui.graphics.Color.Transparent
                ),
            )
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically) {
            
            Text(text = number, modifier = Modifier.width(80.dp))
            TextField(value = number, onValueChange = onNumberChange,
                colors = TextFieldDefaults.textFieldColors(
                    textColor = androidx.compose.ui.graphics.Color.Black
                    , containerColor = androidx.compose.ui.graphics.Color.Transparent
                ),)
            
        }
        
        SectionDivider()
        
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)) {
            // calling the logout function and sending us to login screen
            Text(text = "Logout", modifier = Modifier.clickable { onLogout.invoke() })
        }
    }

}
@Composable
fun ProfileImage() {

}