package com.iplateia.sori.ExampleApp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.os.RemoteException
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
 * Show a Notification to indicate the running state.
 */
class RecognitionService : Service() {

    // TODO: Replace with your own APP_ID and SECRET_KEY here
    private val APP_ID = "64a24d06b84a40fbb21aaa6e"
    private val SECRET_KEY = "ac9741d14d837fcad50f21b18887a6628b9fe861"

    var builder: NotificationCompat.Builder? = null
    val NOTIFICATION_TITLE = "NOTIFICATION_TITLE"
    val NOTIFICATION_BODY = "NOTIFICATION_BODY"
    private var notificationTitle: String? = null
    private var notificationBody: String? = null

    /**
     * Flag to check if recognition is running
     */
    private var detectingRunning = false

    /**
     * SoriListener는 [Detector#start] 후에 바인드합니다.
     */
    private val soriListener = SoriListener(this)
    private var detector: Detector? = null
    private val CHANNEL_ID = "iplateia_recognize_channel"
    private val CHANNEL_NAME = "iPlateia_RECOGNIZE_CHANNEL"
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /**
     * NotificationService
     */
    override fun onCreate() {
        super.onCreate()
        detectingRunning = false
        prepareDetecting()
        setupNotification()
    }

    /**
     * MainActivity 로부터 액션을 전달받아 Detector 를 실행합니다.
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val what = intent.getIntExtra(EXTRA_WHAT, -1)
        notificationTitle = intent.getStringExtra(NOTIFICATION_TITLE)
        notificationBody = intent.getStringExtra(NOTIFICATION_BODY)
        when (what) {
            REQ_START_DETECTING -> {
                startDetecting()
                setupNotification()
            }
        }
        return START_STICKY
    }

    /**
     * 서비스 종료 이벤트
     *
     * DetectingService 가 종료될 때 Detector 를 정리하여야합니다.
     */
    override fun onDestroy() {
        try {
            if (detectingRunning) {
                stopDetecting()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    private fun setupNotification() {
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
     * 인식을 위한 Detector를 준비합니다.
     *
     * iPlateia로부터 제공받은 APP_ID와 SECRET_KEY를 Detector에 등록합니다.
     * 인식을 위한 오디오팩을 Detector에 설정합니다.
     *
     * Detector에 마이크 설정을 합니다.
     */
    private fun prepareDetecting() {
        Detector.init(APP_ID, SECRET_KEY)
        val authority = "com.example.recognize.fileprovider"
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
     * Detector 를 시작합니다.
     *
     * 주의 : 이미 Detector 가 실행중인 상태에서 Detector 를 다시 시작하면 오동작을 일으킬 수 있습니다.
     */
    private fun startDetecting() {
        detector?.start(this)
        detector?.bind(this, soriListener)
        detectingRunning = true
    }

    /**
     * Detector를 종료합니다.
     */
    private fun stopDetecting() {
        if (detector == null || !detectingRunning) {
            return
        }
        try {
            detector!!.stop()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        detector!!.unbind(this)
        detector!!.terminate(this)
        detectingRunning = false
    }

    /**
     * Detector의 이벤트를 처리하는 리스너
     * ISoriListener의 구현체입니다.
     */
    private class SoriListener(service: RecognitionService) : ISoriListener {
        var service: WeakReference<RecognitionService>? = null
        private val UPDATE_DELAY = 1000 * 5

        init {
            this.service = WeakReference(service)
        }

        override fun onReceiveInputStream(fd: ParcelFileDescriptor?) {}

        /**
         * 오디오 인식 서비스에 정상적으로 연결된 경우 발생하는 이벤트
         */
        override fun onConnected() {
            val service = service?.get()
            if (service == null) {
                return
            }
            try {
                val lastError: Int = service.detector?.getLastError() ?: Const.ERROR_NO_ERROR
                if (lastError != Const.ERROR_NO_ERROR) {
                    service.detectingRunning = false
                    service.stopDetecting()
                    service.setupNotification()
                }

                Thread {
                    try {
                        Thread.sleep(UPDATE_DELAY.toLong())
                        service.detector?.updateAudiopack()
                    } catch (e: java.lang.Exception) {
                        System.err.println(e)
                    }
                }.start()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        /**
         * 오디오 인식 서비스에서 연결이 끊기면 발생하는 이벤트
         */
        override fun onDisconnected() {}

        /**
         * 오디오 인식 라이브러리에서 발생한 에러를 처리합니다.
         *
         * @param err
         */
        override fun onError(err: Int) {
            val service = service!!.get() ?: return
            if (err != Const.ERROR_NO_ERROR) {
                service.detectingRunning = false
                service.stopDetecting()
                service.setupNotification()
            }
        }

        /**
         * 업데이트 완료 후 발생하는 이벤트
         *
         * @param result 현재는 result 파라미터를 사용하지 않습니다.
         */
        override fun onUpdateResult(result: Int) {}

        /**
         * 소재 발견 이벤트
         *
         * 오디오 인식 라이브러리가 iPlateia 서버에 등록된 소재를 인식한 경우 발생하는 이벤트입니다.
         * 이 이벤트에서 `service.onDetected` 를 이용하면 NotificationService에 소재 인식 결과를 전달할 수 있습니다.
         * DetectResponse 는 다음을 포함합니다.
         * - (int) success
         * - (float) score
         * - (Result) result
         *
         * Result 클래스는 다음을 포함합니다.
         * - (String) _id
         * - (String) type: : 타입 (예: cf)
         * - (float) length: 길이
         * - (String) title : 이름
         * - (String) image : 이미지 (썸네일 등)
         *
         * @param res 인식 결과
         */
        override fun onDetected(res: DetectResponse) {
            val service = service!!.get() ?: return
            service.onDetected(res)
        }
    }

    /**
     * SoriListener로부터 전달받은 인식 결과
     *
     * @param res 인식 결과
     */
    private fun onDetected(res: DetectResponse) {
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

        /**
         * APP_ID는 iPlateia로 부터 제공받아 사용하여야 합니다.
         * APP_ID와 SECRET_KEY는 한 쌍입니다.
         */
        private const val APP_ID = "5ffd4183ba482ef7f5f101cc"

        /**
         * SECRET_KEY는는 iPlateia로 부터 제공받아 사용하여야 합니다.
         * APP_ID와 SECRET_KEY는 한 쌍입니다.
         */
        private const val SECRET_KEY =
            "6111bfef8b5574cd71a9806f0582bc6b311af0a151d2672c59f1bbacdfced6a4"
        private const val NOTIF_DETECT = 3
        fun setTimeout(runnable: Runnable, delay: Int) {
            Thread {
                try {
                    Thread.sleep(delay.toLong())
                    runnable.run()
                } catch (e: Exception) {
                    System.err.println(e)
                }
            }.start()
        }
    }
}
