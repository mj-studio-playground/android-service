package happy.mjstudio.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MyAIDLService : Service() {

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    private val binder = object : IMyAidlInterface.Stub() {
        override fun add(n1: Int, n2: Int): Int {
            return n1 + n2
        }
    }
}