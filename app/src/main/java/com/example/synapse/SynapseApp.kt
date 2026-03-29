package com.example.synapse

import android.app.Application

class SynapseApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Firebase is now automatically initialized by the google-services plugin 
        // using the google-services.json file you added.
    }
}
