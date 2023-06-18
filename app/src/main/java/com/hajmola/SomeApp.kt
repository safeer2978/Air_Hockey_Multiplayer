package com.hajmola

import android.app.Application
import com.google.firebase.FirebaseApp

class SomeApp: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this.applicationContext)

    }
}