package com.example.chitchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chitchat.screens.ConversationsPage
import com.example.chitchat.screens.IndividualChat
import com.example.chitchat.screens.LoginScreen
import com.example.chitchat.screens.ProfileScreen
import com.example.chitchat.screens.RegisterScreen
import com.example.chitchat.screens.SingleStatus
import com.example.chitchat.screens.StatusScreen

import com.example.chitchat.ui.theme.ChitChatTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

sealed class DestinationScreen(val route: String) {
    object RegisterScreen : DestinationScreen("register")
    object Login : DestinationScreen("login")
    object Profile : DestinationScreen("profile")
    object ConversationPage : DestinationScreen("conversationPage")
    object IndividualChat: DestinationScreen("individualChat/{chatId") {
        fun createRoute(id: String) = "individualChat/$id"
    }
    object StatusScreen : DestinationScreen("statusScreen")
    object SingleStatus : DestinationScreen("singleStatus/{statusId") {
        fun createRoute(id: String) = "singleStatus/$id"
    }
}
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChitChatTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(),
                color= MaterialTheme.colorScheme.background) {
                    ChitChatNavigation()
                }
            }
        }
        

    }
}

@Composable
fun ChitChatNavigation() {
    val navController = rememberNavController()
    val vm = hiltViewModel<CAViewModel>()
    
    notificationMessage(vm = vm)
    
    NavHost(navController = navController, startDestination = DestinationScreen.RegisterScreen.route) {
        composable(DestinationScreen.RegisterScreen.route) {
            RegisterScreen(navController, vm)
        }
        composable(DestinationScreen.Login.route) {
            LoginScreen(navController, vm)
        }
        composable(DestinationScreen.Profile.route) {
            ProfileScreen(vm = vm, navController = navController)
        }
        composable(DestinationScreen.StatusScreen.route) {
            StatusScreen(navController = navController)
        }
        composable(DestinationScreen.SingleStatus.route) {
            SingleStatus(StatusId = "123")
        }
        composable(DestinationScreen.ConversationPage.route) {
            ConversationsPage(navController = navController, vm = vm)
        }
        composable(DestinationScreen.IndividualChat.route) {
            IndividualChat(chatId = "123")
        }
    }
}
