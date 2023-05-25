package com.example.chitchat

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chitchat.screens.LoginScreen
import com.example.chitchat.screens.RegisterScreen


enum class AuthRoutes {
    Login,
    Register
}
// generating navigation navhost
@Composable
fun Navigation(navController: NavHostController = rememberNavController()) {

    // identifying our navhost and should use navcontroller
    NavHost (
        navController = navController,
        startDestination = AuthRoutes.Login.name
            ){
        composable(route = AuthRoutes.Login.name) {
            // my login screen
            LoginScreen(
                navToRegister = {
                    navController.navigate(AuthRoutes.Register.name
                    ) {
                        launchSingleTop = true
                        popUpTo(route = AuthRoutes.Login.name) {
                            inclusive = true
                        }
                    }
                })
        }
        composable(route = AuthRoutes.Register.name) {
            // my register screen
            RegisterScreen(
                navToLogin = {
                    navController.navigate(AuthRoutes.Login.name
                    ) {
                        launchSingleTop = true
                        popUpTo(route = AuthRoutes.Register.name) {
                            inclusive = true
                        }
                    }
                })
        }
    }
}