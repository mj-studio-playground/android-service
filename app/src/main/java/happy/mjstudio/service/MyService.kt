package happy.mjstudio.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import happy.mjstudio.service.util.showSimpleToast

class MyService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showSimpleToast("MyService - onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        showSimpleToast("MyService - onCreate")
        super.onCreate()
    }

    override fun onDestroy() {
        showSimpleToast("MyService - onDestroy")
        super.onDestroy()
    }

    companion object {
        private val TAG = MyService::class.java.simpleName
    }
}