package com.example.chitchat.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chitchat.BottomNavigationItem
import com.example.chitchat.CAViewModel
import com.example.chitchat.ProgressSpinner
import com.example.chitchat.bottomNavigationMenu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationsPage(navController: NavController, vm: CAViewModel) {
    val inProgress  = vm.chatsInProgress.value
    if (inProgress)
        ProgressSpinner()
    else {
        val chats = vm.chats.value
        val userData = vm.usersData.value

        val showDialog = remember {
            mutableStateOf(false)
        }
        val onClick: () -> Unit = {showDialog.value = true}
        val onDismiss: () -> Unit = {showDialog.value = false}
        val onAddChat: (String) -> Unit = {showDialog.value = false}

        Scaffold(floatingActionButton = {
            FAB(showDialog = showDialog.value,
            onClick = onClick,
            onDismiss = onDismiss,
            onAddChat = onAddChat
            )
        },
        content = {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(it))
            {
                if (chats.isEmpty())
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .weight(2f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("You have no chats, get chatting!")
                    }
                else {
                    // chat column

                }
               bottomNavigationMenu(selectedItem = BottomNavigationItem.CONVERSATIONSPAGE,
                   navController = navController)
            }
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAB(
    showDialog: Boolean,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    onAddChat: (String) -> Unit
) {
    val addChatNumber = remember {
        mutableStateOf("")
    }
    if (showDialog)
        AlertDialog(onDismissRequest = {
            onDismiss.invoke()
            addChatNumber.value = ""
        },
        confirmButton = {
            Button(onClick = {onAddChat(addChatNumber.value)
            addChatNumber.value = ""}) {
                Text("New chat")
            }
        },
            title = { Text(text = "Add new chat")},
            text = {
                OutlinedTextField(value = addChatNumber.value, onValueChange = {addChatNumber.value = it},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        )
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.secondary,
    shape = CircleShape,
    modifier = Modifier.padding(40.dp)) {
        Icon(imageVector = Icons.Rounded.Add, contentDescription = null, )
    }
}