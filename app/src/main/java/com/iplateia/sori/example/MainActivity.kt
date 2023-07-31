package com.iplateia.sori.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.iplateia.afplib.DetectResponse
import com.iplateia.afplib.Result
import com.iplateia.sori.example.ui.theme.SORIExampleTheme
import java.lang.ref.WeakReference


class MainActivity : ComponentActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private var soriServiceIntent: Intent? = null

    /**
     * Foreground Service Notification title.
     *
     * This will be shown on the notification. Please change this to your own app name.
     * ex) "ACME Cinema", "DOBI Tv", ...
     */
    private val notificationTitle = "SORI Example"

    /**
     * Foreground Service Notification body.
     *
     * Please change this to your own message
     * ex) "Finding rewards...", "Shhh... I'm listening" ...
     */
    private val notificationBody = "Listening for audio..."

    /**
     * Check if the app has permission to record audio and post notification
     */
    private fun checkPermission(): Boolean {
        val recordingEnabled = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        val notificationEnabled = NotificationManagerCompat.from(this).areNotificationsEnabled()

        return recordingEnabled && notificationEnabled
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStart() {
        super.onStart()

        // Check if the app has permission to record audio, and if not, request permission.
        // Check if the app has permission to post notification, and if not, request permission.
        if (!checkPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecognitionScene(
                toggleRecognition = ::toggleRecognition, // pass toggleRecognition function to RecognitionScene
            ) // render the scene
        }
    }

    /**
     * A Wrapper method of [RecognitionService#startRecognition]
     */
    fun startRecognitionService() {
        Toast.makeText(this, "Starting Audio Recognition", Toast.LENGTH_SHORT).show()
        try {
            soriServiceIntent = Intent(this, RecognitionService::class.java)
            soriServiceIntent!!.putExtra(
                RecognitionService.EXTRA_WHAT,
                RecognitionService.REQ_START_DETECTING
            )
            soriServiceIntent!!.putExtra(RecognitionService.NOTIFICATION_TITLE, notificationTitle)
            soriServiceIntent!!.putExtra(RecognitionService.NOTIFICATION_BODY, notificationBody)
            startService(soriServiceIntent)
        } catch (e: Error) {
            e.printStackTrace()
        }
    }

    fun stopDetectingService() {
        Toast.makeText(this, "Stopping Audio Recognition", Toast.LENGTH_SHORT).show()
        try {
            if (RecognitionService.isRunning) {
                stopService(Intent(this@MainActivity, RecognitionService::class.java))
                soriServiceIntent = null
                return
            }
        } catch (e: java.lang.Error) {
            e.printStackTrace()
        }
    }

    fun toggleRecognition() {
        if (checkPermission()) {
            if (RecognitionService.isRunning) {
                stopDetectingService()
            } else {
                // log
                Log.d("MainActivity", "Starting Audio Recognition")
                startRecognitionService()
            }
        } else {
            Toast.makeText(this, "Please grant permission to record audio", Toast.LENGTH_SHORT)
                .show()
        }
    }
}

/**
 * Card UI for recognition result
 */
@Composable
fun RecognitionResult(title: String, subTitle: String, imageUrl: String, actionUrl: String) {
    val uriHandler = LocalUriHandler.current
    Card(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.clickable {
                // open url on default browser on click
                // implement here whatever you want to do on click
                uriHandler.openUri(actionUrl)
            }
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(
                        shape = MaterialTheme.shapes.small
                    )
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subTitle,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun RecognitionScene(
    toggleRecognition: () -> Unit = {},
) {
    val (isRunning, setIsRunning) = remember { mutableStateOf(false) }

    /** The list of recognition results */
    val results = remember { mutableStateOf(listOf<DetectResponse>()) }

    /**
     * Override [SoriListener] to handle recognition result to results closure
     */
    open class EventHandler : SoriListener() {
        override fun onDetected(res: DetectResponse) {
            super.onDetected(res)
            Log.i("RecognitionScene", "Detected: $res")
            results.value = results.value + res
        }

        override fun onConnected() {
            super.onConnected()
            Log.i("RecognitionScene", "Connected to recognition service")
            results.value += DetectResponse().apply {
                success = 1
                score = -20.0f
                position = 0.0f
                result = Result().apply {
                    _id = "id"
                    type = "cf"
                    length = 0.0f
                    title = "title"
                    image = "https://i.sori.io/data/sori/2b/2baa9940becd44cf9e4791734a3f29c5.png?size=1024"
                }
            }
        }
    }

    /** To hold the reference of [EventHandler] */
    var handler = EventHandler()

    /** Set [handler] as a listener of [RecognitionService] */
    RecognitionService.setListener(handler)

    SORIExampleTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("SORI Example") },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                Icons.Filled.Menu,
                                contentDescription = "Navigation icon"
                            )
                        }
                    },
                )
            },
            floatingActionButtonPosition = androidx.compose.material3.FabPosition.Center,
            floatingActionButton = {
                if (isRunning) {
                    Button(
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        onClick = {
                            toggleRecognition()
                            setIsRunning(false)
                        }
                    ) {
                        Text("Stop Recognition")
                    }
                } else {
                    Button(onClick = {
                        toggleRecognition()
                        setIsRunning(true)
                    }) {
                        Text("Start Recognition")
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier.padding(it), // it is the padding from Scaffold
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TODO: change this result card by corresponding result of recognition service
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(
                        count = results.value.size,
                        itemContent = { res ->
                            RecognitionResult(
                                "title",
                                "subtitle",
                                imageUrl = "https://i.sori.io/data/sori/2b/2baa9940becd44cf9e4791734a3f29c5.png?size=1024",
                                actionUrl = "https://www.iplateia.com"
                            )
                        }
                    )
                    item {
                        if (results.value.isEmpty()) {
                            Text(
                                text = "Recognition result will be shown here.",
                                modifier = Modifier.padding(16.dp)
                            )
                            if (!isRunning) {
                                Text(
                                    text = "Please Touch 'Start Recognition' button to start recognition.",
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                        RecognitionResult(
                            "Item Title",
                            "Item subtitle",
                            imageUrl = "https://i.sori.io/data/sori/2b/2baa9940becd44cf9e4791734a3f29c5.png?size=1024",
                            actionUrl = "https://www.iplateia.com"
                        )
                    }
                }
            }
        }
    }
}