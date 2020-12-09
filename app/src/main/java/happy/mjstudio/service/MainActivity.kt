package happy.mjstudio.service

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import happy.mjstudio.service.MyBinderExtendedService.MyBinder
import happy.mjstudio.service.databinding.ActivityMainBinding
import happy.mjstudio.service.util.showSimpleToast

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // region Binder Extended things
    private val binderExtendedServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binderExtendedServiceBinder = service as MyBinder
            showSimpleToast("BinderExtendedService - onServiceConnected")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            binderExtendedServiceBinder = null
            showSimpleToast("BinderExtendedService - onServiceDisconnected")
        }
    }
    private var binderExtendedServiceBinder: MyBinder? = null
    // endregion

    // region Messenger IPC things
    private val messengerIPCHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MyMessengerIPCService.MSG_ADD_RESPONSE -> showSimpleToast("Add response: ${msg.arg1}")
                else -> super.handleMessage(msg)
            }
        }
    }
    private val messengerIPCClient = Messenger(messengerIPCHandler)
    private var messengerIPCService: Messenger? = null
    private val messengerIPCServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            messengerIPCService = Messenger(service).apply {
                send(Message.obtain(null, MyMessengerIPCService.MSG_BIND_CLIENT, 0, 0).apply {
                    replyTo = messengerIPCClient
                })
            }
            showSimpleToast("MessengerIPCService - onServiceConnected")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            messengerIPCService = null
            showSimpleToast("MessengerIPCService - onServiceDisconnected")
        }
    }
    // endregion

    // region AIDL IPC things
    private var aidl: IMyAidlInterface? = null
    private val aidlConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            aidl = IMyAidlInterface.Stub.asInterface(service)
            showSimpleToast("AIDLService - onServiceConnected")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            aidl = null
            showSimpleToast("AIDLService - onServiceDisconnected")
        }
    }
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButtonListers()
    }

    private fun setButtonListers() {
        binding.start.setOnClickListener {
            startBasicService()
        }
        binding.stop.setOnClickListener {
            stopBasicService()
        }
        binding.startForeground.setOnClickListener {
            startForegroundService()
        }
        binding.stopForeground.setOnClickListener {
            stopForegroundService()
        }
        binding.bindBinderExtended.setOnClickListener {
            bindBinderExtendedService()
        }
        binding.unbindBinderExtended.setOnClickListener {
            unbindBinderExtendedService()
        }
        binding.showToastBinderExtendedService.setOnClickListener {
            showBinderExtendServiceToast()
        }
        binding.bindMessenger.setOnClickListener {
            bindMessengerService()
        }
        binding.unbindMessenger.setOnClickListener {
            unbindMessengerService()
        }
        binding.showToastMessenger.setOnClickListener {
            showMessengerIPCServiceToast()
        }
        binding.addMessenger.setOnClickListener {
            invokeAddMessengerIPCService()
        }
        binding.bindAidl.setOnClickListener {
            bindAidlService()
        }
        binding.unbindAidl.setOnClickListener {
            unbindAidlService()
        }
        binding.addAidl.setOnClickListener {
            invokeAddInAidlService()
        }
    }

    private fun startBasicService() {
        Intent(this, MyService::class.java).run {
            startService(this)
        }
    }

    private fun stopBasicService() {
        Intent(this, MyService::class.java).run {
            stopService(this)
        }
    }

    private fun startForegroundService() {
        Intent(this, MyForegroundService::class.java).run {
            if (Build.VERSION.SDK_INT > VERSION_CODES.O) startForegroundService(this)
            else startService(this)
        }
    }

    private fun stopForegroundService() {
        Intent(this, MyForegroundService::class.java).run {
            stopService(this)
        }
    }

    private fun bindBinderExtendedService() {
        Intent(this, MyBinderExtendedService::class.java).run {
            bindService(this, binderExtendedServiceConnection, Service.BIND_AUTO_CREATE)
        }
    }

    private fun unbindBinderExtendedService() {
        unbindService(binderExtendedServiceConnection)
    }

    private fun showBinderExtendServiceToast() {
        binderExtendedServiceBinder?.run {
            service.showToast("Binder extended Service!")
        }
    }

    private fun bindMessengerService() {
        Intent(this, MyMessengerIPCService::class.java).run {
            bindService(this, messengerIPCServiceConnection, Service.BIND_AUTO_CREATE)
        }
    }

    private fun unbindMessengerService() {
        messengerIPCService?.send(Message.obtain(null, MyMessengerIPCService.MSG_UNBIND_CLIENT, 0, 0).apply {
            replyTo = messengerIPCClient
        })
        unbindService(messengerIPCServiceConnection)
    }

    private fun showMessengerIPCServiceToast() {
        messengerIPCService?.send(Message.obtain(null, MyMessengerIPCService.MSG_SHOW_TOAST, 0, 0).apply {
            data = bundleOf(MyMessengerIPCService.MSG_TOAST_TEXT to "Messenger IPC Service!")
        })
    }

    private fun invokeAddMessengerIPCService() {
        messengerIPCService?.send(Message.obtain(null, MyMessengerIPCService.MSG_ADD_REQUEST, 5, 1))
    }

    private fun bindAidlService() {
        bindService(Intent(this, MyAIDLService::class.java), aidlConnection, Service.BIND_AUTO_CREATE)
    }

    private fun unbindAidlService() {
        unbindService(aidlConnection)
    }

    private fun invokeAddInAidlService() {
        showSimpleToast("1 + 2 = ${aidl?.add(1, 2)}")
    }
}