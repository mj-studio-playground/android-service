package happy.mjstudio.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import happy.mjstudio.service.util.showSimpleToast

class MyMessengerIPCService : Service() {
    private lateinit var messenger: Messenger

    internal class IncomingHandler(service: Service, private val context: Context = service.applicationContext) :
        Handler(Looper.getMainLooper()) {

        private val clients = mutableListOf<Messenger>()

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_BIND_CLIENT -> clients.add(msg.replyTo)
                MSG_UNBIND_CLIENT -> clients.remove(msg.replyTo)
                MSG_SHOW_TOAST -> context.showSimpleToast(msg.data.getString(MSG_TOAST_TEXT, "no text"))
                MSG_ADD_REQUEST -> add(msg.arg1, msg.arg2)
                else -> super.handleMessage(msg)
            }
        }

        private fun add(n1: Int, n2: Int) {
            val message = Message.obtain(null, MSG_ADD_RESPONSE, n1 + n2, 0)

            clients.forEach {
                it.send(message)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        messenger = Messenger(IncomingHandler(this))
        return messenger.binder
    }

    companion object {
        const val MSG_BIND_CLIENT = 2
        const val MSG_UNBIND_CLIENT = 3
        const val MSG_SHOW_TOAST = 1
        const val MSG_ADD_REQUEST = 4
        const val MSG_ADD_RESPONSE = 5
        const val MSG_TOAST_TEXT = "toast_text"
    }
}