package com.example.chitchat

import android.media.metrics.Event
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.chitchat.data.Events
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class CAViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    val storage: FirebaseStorage
)  : ViewModel() {

    val isInProgress = mutableStateOf(false)
    val popUpNotify = mutableStateOf<Events<String>?>(null)
    val isSignedIn = mutableStateOf(false)

    init {

    }

    fun SignUp(name: String, number: String, email: String, password: String) {
        if (name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()){
            handleException(customMessage = "All fields must be filled in")
        }
    }

    private fun handleException(exception: Exception? = null, customMessage: String = "") {
        Log.e("ChITCHAT", " chat except", exception)
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage?: ""
        val message = if (customMessage.isEmpty()) errorMsg else "$customMessage : $errorMsg"
        popUpNotify.value = Events(message)
        isInProgress.value = false
    }
}