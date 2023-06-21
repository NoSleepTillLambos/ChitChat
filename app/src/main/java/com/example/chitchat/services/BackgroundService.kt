package com.example.chitchat.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.chitchat.data.MESSAGE_COLLECTION
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BackgroundService : Service() {

    val currentUser = Firebase.auth.currentUser
    var onMessageListener: ListenerRegistration? = null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        Thread {
            while (true) {
                try {
                    Log.d("AAA", "RUNNING")

                    if (currentUser != null) {

                        if (onMessageListener == null) {
                            startFirestoreListener()
                        } else {
                            Log.d("AA", "aLREADY LISTENING ")
                        }

                    }

                    Thread.sleep(15000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        onMessageListener = null
        Log.d("AA SERVICE", "LOG DESTROYED")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun startFirestoreListener() {

        val collectionRef = Firebase.firestore
            .collectionGroup(MESSAGE_COLLECTION)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(10)

        onMessageListener = collectionRef.addSnapshotListener { value, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            if (value != null) {

                Log.d("AA RECIEVED REALTIME", value.toString())
                for (dc in value!!.documentChanges) {
                    when (dc.type) {

                        DocumentChange.Type.ADDED ->
                            MyNotifications(
                                this,
                                "New Message",
                                dc.document.data["message"].toString()
                            ).showNotification()
                        DocumentChange.Type.MODIFIED ->
                            Log.d("AAA service working", "mODIFIED : ${dc.document.data}")
                        DocumentChange.Type.REMOVED ->
                            Log.d("AAA REMOVE", "Deleted: ${dc.document.data}")
                    }
                 }
            }
        }

    }

}