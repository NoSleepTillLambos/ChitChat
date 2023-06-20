package com.example.chitchat

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.example.chitchat.data.CHAT_COLLECTION
import com.example.chitchat.data.ChatData
import com.example.chitchat.data.ChatUser
import com.example.chitchat.data.Events
import com.example.chitchat.data.USER_IN_COLLECTION
import com.example.chitchat.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import java.util.UUID
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
    val usersData = mutableStateOf<UserData?>(null)


    val chats = mutableStateOf<List<ChatData>>(listOf())
    val chatsInProgress = mutableStateOf(false)
    init {
        logOut()
        auth.signOut()
        val currentLoggedInUser = auth.currentUser
        isSignedIn.value = currentLoggedInUser != null
        currentLoggedInUser?.uid?.let {uid ->
            getUserData(uid)
        }
    }

    fun signUp(name: String, number: String, email: String, password: String) {
        if (name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()){
            handleException(customMessage = "All fields must be filled in")
            return
        }
        isInProgress.value = true
        db.collection(USER_IN_COLLECTION).whereEqualTo("number", number)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty)
                    auth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener {
                            task -> if (task.isSuccessful) {
                                isSignedIn.value = true
                            createOrUpdateProfile(name = name,number = number)
                            // creating the user here
                            } else
                                handleException(task.exception, "Something went wrong...")

                        }
                else
                        handleException(customMessage = "Oops!, that Number already exists")
                isInProgress.value = false

            }
            .addOnFailureListener {
                handleException(it)
            }
    }

    fun login(email: String, password: String) {
        if(email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "All fields must be filled in")
            return

        }
        isInProgress.value = true
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isSignedIn.value = true
                    isInProgress.value = false
                    auth.currentUser?.uid?.let {
                        getUserData(it)
                    }
                }
                else
                    handleException(task.exception, "There was an issue with your email and password, please try again")

            }
            .addOnFailureListener {
                handleException(it, "Login failed")
            }
    }

    private fun createOrUpdateProfile(
        name: String? = null,
        number: String? = null,
        imageUrl: String? = null
    ) {
        val uid = auth.currentUser?.uid
        val userData = UserData(
            userId = uid,
            name = name ?: usersData.value?.name,
            number = number ?: usersData.value?.number,
            imageUrl = imageUrl ?: usersData.value?.imageUrl
        )

        uid?.let { uid ->
            isInProgress.value = true
            db.collection(USER_IN_COLLECTION).document(uid)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        // updating user creds
                        it.reference.update(userData.toMap())
                            .addOnSuccessListener {
                                isInProgress.value = false
                            }
                            .addOnFailureListener {
                                handleException(it, "unable to update user")
                            }
                    } else {
                        // create user as is not in the system
                        db.collection(USER_IN_COLLECTION).document(uid)
                            .set(userData)
                        isInProgress.value = false
                        getUserData(uid)
                    }
                }
                .addOnFailureListener {
                    handleException(it, "user cannot be found")
                }
        }
    }

    fun updateProfile(name: String, number: String) {
        createOrUpdateProfile(name = name, number = number)
    }

    private fun getUserData(uid: String) {
        isInProgress.value = true
        db.collection(USER_IN_COLLECTION).document(uid)
            .addSnapshotListener { value, error ->
                if (error != null)
                    handleException(error, "There has been a problem fetching the user")
                if (value != null ) {
                    val user = value.toObject<UserData>()
                    usersData.value = user
                    isInProgress.value = false
                }
            }
    }

    fun logOut() {
        auth.signOut()
        isSignedIn.value = false
        usersData.value = null
        popUpNotify.value = Events("You have been logged out")
    }

    private fun handleException(exception: Exception? = null, customMessage: String = "") {
        Log.e("ChITCHAT", " chat except", exception)
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage?: ""
        val message = if (customMessage.isEmpty()) errorMsg else "$customMessage : $errorMsg"
        popUpNotify.value = Events(message)
        isInProgress.value = false
    }

    private fun uploadingImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        isInProgress.value = true


        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("image/$uuid")
        val uploadImage = imageRef.putFile(uri)

        // incase of failure
        uploadImage
            .addOnSuccessListener {
                val result = it.metadata?.reference?.downloadUrl
                result?.addOnSuccessListener(onSuccess)
                isInProgress.value = false
            }
            .addOnFailureListener {
                handleException(it)
            }
    }
    fun uploadProfileImage(uri: Uri) {
        uploadingImage(uri) {
            createOrUpdateProfile(imageUrl = it.toString())
        }
    }


    fun addChat(number: String) {
        if (number.isEmpty() or !number.isDigitsOnly())
            handleException(customMessage = "Number must only contain digits")
        else {
            db.collection(CHAT_COLLECTION)
                .where(
                    Filter.or(
                        Filter.and(
                            Filter.equalTo("user1.number",number),
                            Filter.equalTo("user2.number", usersData.value?.number)
                        ),
                        Filter.and(
                            Filter.equalTo("user1.number", usersData.value?.number),
                            Filter.equalTo("user2.number", number),
                        )
                    )
                )
                .get()
                .addOnSuccessListener {
                    if(it.isEmpty) {
                        db.collection(USER_IN_COLLECTION).whereEqualTo("number", number)
                            .get()
                            .addOnSuccessListener {
                                if(it.isEmpty)
                                    handleException(customMessage = "Cannot find $number")
                                else {
                                    val chatPartner = it.toObjects<UserData>()[0]
                                    val chatId = db.collection(CHAT_COLLECTION).document().id
                                    val chat = ChatData(
                                        chatId,
                                        ChatUser(
                                            usersData.value?.userId,
                                            usersData.value?.name,
                                            usersData.value?.imageUrl,
                                            usersData.value?.number
                                        ),
                                        ChatUser(
                                            chatPartner.userId,
                                            chatPartner.name,
                                            chatPartner.imageUrl,
                                            chatPartner.number
                                        )
                                    )
                                    db.collection(CHAT_COLLECTION).document(chatId).set(chat)
                                }
                            }
                            .addOnFailureListener {
                                handleException(it)
                            }
                    } else {
                        handleException(customMessage = "You have already created this chat")
                    }
                }
        }
    }

    private fun getChats() {
        chatsInProgress.value = true
        db.collection(CHAT_COLLECTION).where(
            Filter.or(
                Filter.equalTo("user1.userId", usersData.value?.userId),
                Filter.equalTo("user2.userId", usersData.value?.userId)
            )
        )
            .addSnapshotListener { value, error ->
                if(error != null)
                    handleException(error)
                if (value != null)
                    chats.value = value.documents.mapNotNull { it.toObject<ChatData>() }
                isInProgress.value = false
            }
    }

}