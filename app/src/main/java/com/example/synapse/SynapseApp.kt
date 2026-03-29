package com.example.synapse

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class SynapseApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        val options = FirebaseOptions.Builder()
            .setApiKey("AIzaSyCpdyyczl4w7Rsrbg_4aILmH4lVLGA9GIE")
            .setApplicationId("1:1062036038534:web:5fbc7bfad1df7599ccc207")
            .setProjectId("synapse-ea4a3")
            .setStorageBucket("synapse-ea4a3.firebasestorage.app")
            .setGcmSenderId("1062036038534")
            .build()

        FirebaseApp.initializeApp(this, options)
    }
}
