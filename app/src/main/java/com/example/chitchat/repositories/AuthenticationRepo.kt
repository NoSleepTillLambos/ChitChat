package com.example.chitchat.repositories

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


// repo for all firebase auth functionality

class AuthenticationRepo {
    val currentUser: FirebaseUser? = Firebase.auth.currentUser

    fun hasUser(): Boolean = Firebase.auth.currentUser != null

    fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty()

    // ADD our sign in/ up and out functionality
    suspend fun registerUser(
      email: String,
      password: String,
      createdUser:(String) -> Unit // created user will return with success or not
    ) = withContext(Dispatchers.IO) {
        Firebase.auth
            .createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                if(it.isComplete) {
                    // successfully created a user
                    Log.d("Register successful", it.result.user?.uid.toString())
                    it.result.user?.uid?.let { it1 -> createdUser.invoke(it1) }
                } else {
                    // error
                    Log.d("Error with registering user", it.exception?.localizedMessage.toString())
                    createdUser.invoke("")
                }
            }.await()
    }

    // ADD our sign in/ up and out functionality
    suspend fun loginUser(
        email: String,
        password: String,
        isCompleted:(Boolean) -> Unit // created user will return with success or not
    ) = withContext(Dispatchers.IO) {
        Firebase.auth
            .signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                if(it.isComplete) {
                    // successfully created a user
                    Log.d("Login in successful", it.result.user?.uid.toString())
                    isCompleted.invoke(true)
                } else {
                    // error
                    Log.d("Error with registering user", it.exception?.localizedMessage.toString())
                    isCompleted.invoke(false)
                }
            }.await()
    }

    // SIGNING OUT A USER
    fun signOffUser() {
        Firebase.auth.signOut()
    }
}