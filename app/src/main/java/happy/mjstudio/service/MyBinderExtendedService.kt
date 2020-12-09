package happy.mjstudio.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import happy.mjstudio.service.util.showSimpleToast

class MyBinderExtendedService : Service() {
    fun showToast(text: String) {
        showSimpleToast(text)
    }

    override fun onBind(intent: Intent): IBinder {
        return MyBinder()
    }

    inner class MyBinder : Binder() {
        val service: MyBinderExtendedService
            get() = this@MyBinderExtendedService
    }
}