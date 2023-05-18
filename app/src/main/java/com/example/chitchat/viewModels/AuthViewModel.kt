package com.example.chitchat.viewModels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chitchat.repositories.AuthenticationRepo
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue

class AuthViewModel(private val repository: AuthenticationRepo = AuthenticationRepo()): ViewModel() {

    val currentUser = repository.currentUser
    val hasUser: Boolean = repository.hasUser()

    var authUiState by mutableStateOf(AuthUiState())
        private set

    fun handleInputStateChanges(target: String, value: String) {
        if(target == "loginEmail") {
            authUiState = authUiState.copy(loginEmail = value)
        } else if (target == "loginPassword") {
            authUiState = authUiState.copy(loginPassword = value)
        } else if (target == "registerUsername") {
            authUiState = authUiState.copy(registerUsername = value)
        } else if (target == "registerEmail") {
            authUiState = authUiState.copy(registerEmail = value)
        } else if (target == "registerPassword") {
            authUiState = authUiState.copy(registerPassword = value)
        }

    }
    // CREATING USER
    fun createUser(context: Context) = viewModelScope.launch {
        try {
            if(authUiState.registerUsername.isBlank() || authUiState.registerEmail.isBlank() || authUiState.registerPassword.isBlank()) {
                authUiState = authUiState.copy(errorMessage = "Please fill in all of the fields")
            } else {
                authUiState = authUiState.copy(isLoading = true)

                // call functionality
                repository.registerUser(
                    authUiState.registerEmail,
                    authUiState.registerPassword
                ) {userId ->
                    if(userId.isNotBlank()) {
                        // success
                        // TODO: ADD user to Firestore
                        Log.d("registered successfully ", userId)
                        Toast.makeText(
                            context,
                            "Registration complete",
                            Toast.LENGTH_SHORT).show()


                        authUiState = authUiState.copy(authSuccess = true)
                    } else {
                        // register failed
                        Log.d("Error registering", "Something went wrong")
                        authUiState = authUiState.copy(authSuccess = false)
                        Toast.makeText(
                            context,
                            "Registration error",
                            Toast.LENGTH_SHORT).show()
                    }

                }

            }

        } catch (e: Exception) {
            Log.d("Error registering: ", e.localizedMessage)
            e.printStackTrace()
        } finally {
            authUiState = authUiState.copy(isLoading = false)
        }
    }

    // LOGGING IN USER
    fun LoginUser(context: Context) = viewModelScope.launch {
        try {
            if(authUiState.loginEmail.isBlank() || authUiState.loginPassword.isBlank()) {
                authUiState = authUiState.copy(errorMessage = "Please fill in all of the fields")
            } else {
                authUiState = authUiState.copy(isLoading = true)

                // call functionality
                repository.loginUser(
                    authUiState.loginEmail,
                    authUiState.loginPassword
                ) {isCompleted ->
                    if(isCompleted) {
                        // success

                        Log.d("registered successfully ", "Yay")
                        Toast.makeText(
                            context,
                            "login complete",
                            Toast.LENGTH_SHORT).show()


                        authUiState = authUiState.copy(authSuccess = true)
                    } else {
                        // register failed
                        Log.d("Error logging in", "Something went wrong")
                        authUiState = authUiState.copy(authSuccess = false)
                        Toast.makeText(
                            context,
                            "Login error",
                            Toast.LENGTH_SHORT).show()
                    }

                }

            }

        } catch (e: Exception) {
            Log.d("Error registering: ", e.localizedMessage)
            e.printStackTrace()
        } finally {
            authUiState = authUiState.copy(isLoading = false)
        }
    }
}

data class AuthUiState (
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val authSuccess: Boolean = false,
    // state values for my login
    val loginEmail: String = "",
    val loginPassword: String = "",
    // state values for my register
    val registerUsername: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",

        )