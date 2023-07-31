package com.iplateia.sori.example

import android.os.ParcelFileDescriptor
import android.os.RemoteException
import android.util.Log
import com.iplateia.afplib.Const
import com.iplateia.afplib.DetectResponse
import com.iplateia.afplib.ISoriListener

/**
 * A listener implementation to handle events from Detector
 */
open class SoriListener() : ISoriListener {

    @Deprecated("This method is only useful when using PCM stream instead MIC input source.")
    override fun onReceiveInputStream(fd: ParcelFileDescriptor?) {}

    /**
     * Handles event when connected to audio recognition service
     *
     * You can check if the connection is successful by checking `service.detector.lastError`.
     * and up to date new audiopack from console by calling `service.detector.updateAudiopack()`.
     * Since `updateAudiopack()` can be called only after service was bound,
     * `onConnected()` is the best place to call it.
     */
    override fun onConnected() {
        val service = RecognitionService.getInstance()
        Log.i("RecognitionService", "Service connected $service")
        service?.setupNotification()
        try {
            val lastError: Int = service?.lastError ?: Const.ERROR_NO_ERROR
            if (lastError != Const.ERROR_NO_ERROR) {
                service?.stopRecognition()
                Log.i("RecognitionService", "Service started")
            }

            // Update audiopack
            //
            // The Service was bound successfuly,
            // there is some delay because Android's NOTIFICATION Channel calls.
            // So we need to wait a little bit before calling updateAudiopack().
            val updateDelay = 1000 * 3 // 3 seconds
            Thread {
                try {
                    Thread.sleep(updateDelay.toLong())
                    service?.updateAudiopack()
                    Log.i("RecognitionService", "Recognition source sync completed")
                } catch (e: java.lang.Exception) {
                    // if update failed by some reason(network, etc),
                    // but the recognition service is still available, with old audiopack.
                    // So we don't need to stop the service.
                    Log.e("RecognitionService", "Failed to update audiopack")
                }
            }.start()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * Handle event when disconnected from audio recognition service
     */
    override fun onDisconnected() {}

    /**
     * Handle error from audio recognition service.
     *
     * Implement this method to handle errors.
     * ex) restart service, show error message, etc.
     *
     * @param err
     */
    override fun onError(err: Int) {
        val service = RecognitionService.getInstance()
        if (err != Const.ERROR_NO_ERROR) {
            service?.stopRecognition()
            service?.setupNotification()
        }
    }

    /**
     * Handle event when audiopack update completed
     *
     * Implement whatever do After update completed
     * @param result The result of update
     */
    override fun onUpdateResult(result: Int) {}

    /**
     * Handle event when audio recognition service found a campaign that related to the audio.
     *
     * When an audio is recognized, additional http request is sent to server internally.
     * If the server found a campaign that related to the audio, this event will be fired.
     *
     * The response contains:
     *
     * `DetectResponse`
     * - (int) success   : Whether the recognition was successful
     * - (float) score   : Confidence score. represents negative float value. greater is better.
     * - (Result) result : The result of recognition
     * - (Campaign?) campaign: The campaign that related to the audio. This value can be null.
     *
     * `Result`
     * - (String) _id   : Campaign ID
     * - (String) type: : resource audio type. This value always be "cf" at this moment.
     * - (float) length : source audio length in seconds
     * - (String) title : title of the campaign
     * - (String) image : image url of the campaign. This value can be null.
     *                    image resolution will be automatically adjusted by the client device width.
     * - (String) actionUrl: action url of the campaign. Generally, this value is a web url.
     *
     * `Campaign`
     * - (String) _id       : Campaign ID
     * - (String) title     : title of the campaign
     * - (String) image     : image url of the campaign. This value can be null.
     * - (String) actionUrl : action url of the campaign. Generally, this value is a web url.
     * - (Location[]) locations: locations of the campaign. This value can be null.
     *
     * `Location`
     * - (String) name         : Location Name
     * - (float[]) coordinates : Location coordinates. [longitude, latitude]
     * @param res Result of detection
     */
    override fun onDetected(res: DetectResponse) {
        // implement this
        Log.d("SoriListener", "onDetected: $res");
        val service = RecognitionService.getInstance()
        service?.onDetected(res);
    }
}