package com.iplateia.sori.example

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.commonsware.cwac.provider.StreamProvider
import com.iplateia.afplib.Const
import com.iplateia.afplib.DetectResponse
import com.iplateia.afplib.Detector
import com.iplateia.afplib.ISoriListener
import java.lang.ref.WeakReference


/**
 * Launch the audio recognition Foreground Service
 *
 * Show a Notification to indicate the running state.
 * Since the audio recognition service is a foreground service, it must be launched with a notification.
 *
 * This is a sample code to show how to use the audio recognition service.
 */
class RecognitionService : Service() {

    // ----
    // TODO: Replace with your own APP_ID and SECRET_KEY here
    // can be found at https://console.soriapi.com/account/application/
    private val APP_ID = "64a24d06b84a40fbb21aaa6e"
    private val SECRET_KEY = "ac9741d14d837fcad50f21b18887a6628b9fe861"
    // ------

    var builder: NotificationCompat.Builder? = null
    private var notificationTitle: String? = null
    private var notificationBody: String? = null

    private var detector: Detector? = null
    val lastError: Int
        get() = detector?.lastError ?: Const.ERROR_NO_ERROR
    private var soriListener: ISoriListener? = null
    private val CHANNEL_ID = "sori_recognition"
    private val CHANNEL_NAME = "SORI_RECOGNITION"

    /**
     * register listener to handle events from Detector
     */
    fun bind(listener: ISoriListener) {
        soriListener = listener
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.i("Listener", "Service bound")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        prepareDetecting()
        setupNotification()
    }

    /**
     * Fires on service start command
     */
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val what = intent.getIntExtra(EXTRA_WHAT, -1)
        notificationTitle = intent.getStringExtra(NOTIFICATION_TITLE)
        notificationBody = intent.getStringExtra(NOTIFICATION_BODY)
        when (what) {
            REQ_START_DETECTING -> {
                startRecognition()
                setupNotification()
            }
        }
        return START_STICKY // never dismissable unless server explicitly stopped
    }

    /**
     * Fires when the `DetectorService` is destroyed
     *
     * Should stop the foregroundService when the `DetectorService` is destroyed.
     */
    override fun onDestroy() {
        try {
            if (isRunning) {
                stopRecognition()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    fun setupNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)
        builder = NotificationCompat.Builder(this, CHANNEL_ID)
        builder?.setSmallIcon(R.drawable.ic_sori_service)
            ?.setContentTitle(notificationTitle)
            ?.setContentText(notificationBody)
            ?.setContentIntent(pendingIntent)
        startForeground(NOTIF_DETECT, builder?.build())
    }

    /**
     * Update audiopack from server
     *
     * It's wrapped method of [Detector#updateAudiopack]
     */
    fun updateAudiopack() {
        detector?.updateAudiopack()
    }

    /**
     * Prepare recognition resource, setup keys, setup recording source...
     */
    private fun prepareDetecting() {
        // get package name
        Detector.init(APP_ID, SECRET_KEY)
        val authority = "${packageName}.fileprovider"
        val provider = Uri.parse("content://$authority")
        val audioPack = provider.buildUpon()
            .appendPath(StreamProvider.getUriPrefix(authority))
            .appendPath("audiopack/audio.pack")
            .build()

        Detector.setContentProvider(audioPack)
        detector = Detector()
        Detector.setMicInput()
    }

    /**
     * Starts recognition service
     *
     * Warning: If you start the detector while it is already running, it may cause a malfunction.
     */
    private fun startRecognition() {
        detector?.start(this)
        val soriListener = listener?.get()
        if (soriListener != null) {
            detector!!.bind(this, soriListener)
        }
    }

    /**
     * Stops recognition service
     */
    fun stopRecognition() {
        if (detector == null) {
            return
        }
        try {
            detector!!.stop()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        detector!!.unbind(this)
        detector!!.terminate(this)
    }

    /**
     * SoriListener로부터 전달받은 인식 결과
     *
     * @param res 인식 결과
     */
    fun onDetected(res: DetectResponse) {
        Toast.makeText(
            this, """
     소재를 찾았습니다. 
     ${res.result.title}
     """.trimIndent(), Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        const val EXTRA_WHAT = "what"
        const val NOTIFICATION_TITLE = "NOTIFICATION_TITLE"
        const val NOTIFICATION_BODY = "NOTIFICATION_BODY"
        const val REQ_START_DETECTING = 1
        private const val NOTIF_DETECT = 3

//        private var detectingRunning = false

        /**
         * Singleton flag to check if recognition service is running
         *
         * Note: `ActivityManager::getRunningServices()` is deprecated in API 26
         * @see [getRunningServices](https://developer.android.com/reference/android/app/ActivityManager.html#getRunningServices(int))
         * @see [solution](https://stackoverflow.com/a/50384353)
         */
        val isRunning: Boolean
            get() = instance != null

        private var instance: RecognitionService? = null

        /**
         * Singleton instance of RecognitionService
         */
        fun getInstance(): RecognitionService? {
            return instance
        }

        private var listener: WeakReference<ISoriListener>? = null
        fun setListener(impl: ISoriListener) {
            listener = WeakReference(impl)
        }
    }
}
