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
import com.example.chitchat.data.MESSAGE_COLLECTION
import com.example.chitchat.data.Message
import com.example.chitchat.data.USER_IN_COLLECTION
import com.example.chitchat.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CAViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    val storage: FirebaseStorage
) : ViewModel() {

    val isInProgress = mutableStateOf(false)
    val popUpNotify = mutableStateOf<Events<String>?>(null)
    val isSignedIn = mutableStateOf(false)
    val userData = mutableStateOf<UserData?>(null)


    val chats = mutableStateOf<List<ChatData>>(listOf())
    val chatsInProgress = mutableStateOf(false)

    val chatMessages = mutableStateOf<List<Message>>(listOf())
    val inProgressChatMessages = mutableStateOf(false)

    // listening for changes in the chat and populating thereafter
    var currentChatMessagesListener: ListenerRegistration? = null

    init {
        logOut()
        auth.signOut()
        val currentLoggedInUser = auth.currentUser
        isSignedIn.value = currentLoggedInUser != null
        currentLoggedInUser?.uid?.let { uid ->
            getUserData(uid)
        }
    }

    fun signUp(name: String, number: String, email: String, password: String) {
        if (name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "All fields must be filled in")
            return
        }
        isInProgress.value = true
        db.collection(USER_IN_COLLECTION).whereEqualTo("number", number)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty)
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                isSignedIn.value = true
                                createOrUpdateProfile(name = name, number = number)
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
        if (email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "All fields must be filled in")
            return

        }
        isInProgress.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isSignedIn.value = true
                    isInProgress.value = false
                    auth.currentUser?.uid?.let {
                        getUserData(it)
                    }
                } else
                    handleException(
                        task.exception,
                        "There was an issue with your email and password, please try again"
                    )

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
            name = name ?: userData.value?.name,
            number = number ?: userData.value?.number,
            imageUrl = imageUrl ?: userData.value?.imageUrl
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
                if (value != null) {
                    val user = value.toObject<UserData>()
                    userData.value = user
                    isInProgress.value = false
                    getChats()
                }
            }
    }

    fun logOut() {
        auth.signOut()
        isSignedIn.value = false
        userData.value = null
        popUpNotify.value = Events("You have been logged out")
        chats.value = listOf()
    }

    private fun handleException(exception: Exception? = null, customMessage: String = "") {
        Log.e("Chitchat", " chat except", exception)
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
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

        // in the case of failure
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
                            Filter.equalTo("user1.number", number),
                            Filter.equalTo("user2.number", userData.value?.number)
                        ),
                        Filter.and(
                            Filter.equalTo("user1.number", userData.value?.number),
                            Filter.equalTo("user2.number", number),
                        )
                    )
                )
                .get()
                .addOnSuccessListener {
                    if (it.isEmpty) {
                        db.collection(USER_IN_COLLECTION).whereEqualTo("number", number)
                            .get()
                            .addOnSuccessListener {
                                if (it.isEmpty)
                                    handleException(customMessage = "Cannot find $number")
                                else {
                                    val chatPartner = it.toObjects<UserData>()[0]
                                    val id = db.collection(CHAT_COLLECTION).document().id
                                    val chat = ChatData(
                                        id,
                                        ChatUser(
                                            userData.value?.userId,
                                            userData.value?.name,
                                            userData.value?.imageUrl,
                                            userData.value?.number
                                        ),
                                        ChatUser(
                                            chatPartner.userId,
                                            chatPartner.name,
                                            chatPartner.imageUrl,
                                            chatPartner.number
                                        )
                                    )
                                    db.collection(CHAT_COLLECTION).document(id).set(chat)
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
        isInProgress.value = true
        db.collection(CHAT_COLLECTION).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId)
            )
        )
            .addSnapshotListener { value, error ->
                if (error != null)
                    handleException(error)
                if (value != null)
                    chats.value = value.documents.mapNotNull { it.toObject<ChatData>() }
                isInProgress.value = false
            }
    }

    fun onSendReply(chatId: String, message: String) {

        // get timestamp
        val time = Calendar.getInstance().time.toString()
        val message = Message(userData.value?.userId, message, time)

        db.collection(CHAT_COLLECTION)
            .document(chatId)
            .collection(MESSAGE_COLLECTION)
            .document()
            .set(message)

    }

    fun populateChat(chatId: String) {
        inProgressChatMessages.value = true
        currentChatMessagesListener = db.collection(CHAT_COLLECTION)
            .document(chatId)
            .collection(MESSAGE_COLLECTION)
            .addSnapshotListener { value, error ->
                if (error != null)
                    handleException(error)
                if (value != null)
                    chatMessages.value = value.documents
                        .mapNotNull { it.toObject<Message>() }
                        .sortedBy { it.timestamp }
                inProgressChatMessages.value = false
            }
    }

    fun depopulateChat() {
        currentChatMessagesListener = null
        chatMessages.value = listOf()
    }


}