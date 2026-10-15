package com.ferreteria.mobile

import android.app.Application
import com.ferreteria.mobile.util.SessionManager

class FerreteriaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionManager.iniciar(this)
    }
}
