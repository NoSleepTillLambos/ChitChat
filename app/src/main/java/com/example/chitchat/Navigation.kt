package com.example.chitchat

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


enum class AuthRoutes {
    Login,
    Register
}
// generating navigation navhost
@Composable
fun Navigation(navController: NavHostController = rememberNavController()) {

    // identifying our navhost and use our navcontroller
    NavHost (
        navController = navController,
        startDestination = AuthRoutes.Login.name
            ){
        composable(route = AuthRoutes.Login.name) {
            // my login screen
//            Login()
        }
        composable(route = AuthRoutes.Register.name) {
            // my register screen
            Register()
        }
    }
}