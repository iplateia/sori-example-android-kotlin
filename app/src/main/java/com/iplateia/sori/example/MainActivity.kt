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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Refresh
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.iplateia.sori.example.ui.theme.SORIExampleTheme
import kotlinx.coroutines.launch
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


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

//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStart() {
        super.onStart()

        // Check if the app has permission to record audio, and if not, request permission.
        var permissions = arrayOf(Manifest.permission.RECORD_AUDIO)

        // if api version is upper than tiramisu, we need to acquire post notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions += Manifest.permission.POST_NOTIFICATIONS
        }

        if (!checkPermission()) {
            ActivityCompat.requestPermissions(this, permissions, 1)
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
 * Calculate distance between two geo points
 * @param lat1 latitude of point 1
 * @param lon1 longitude of point 1
 * @param lat2 latitude of point 2
 * @param lon2 longitude of point 2
 * @return distance in meters
 */
fun calculateGeoDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val theta = lon1 - lon2
    var dist =
        sin(Math.toRadians(lat1)) * sin(Math.toRadians(lat2)) + cos(
            Math.toRadians(
                lat1
            )
        ) * cos(Math.toRadians(lat2)) * cos(Math.toRadians(theta))
    dist = acos(dist)
    dist = Math.toDegrees(dist)
    // covert to meters
    dist *= 60 * 1.1515 * 1.609344
    return dist
}

/**
 * Check if the geo point of item is within the range
 * @param item the item to check
 * @param range the range in meters (default=50 m)
 * @return true if the item is within the range
 */
fun isGeoWithin(item: Object, range: Float = 50.0f): Boolean {
    // TODO: implement this after item spec is ready
    return false
}

/**
 * Card UI for recognition result
 */
@Composable
fun RecognizedItemCard(title: String, subTitle: String?, imageUrl: String, actionUrl: String) {
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
                if (subTitle != null)
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

    /** The list of recognition responses */
    val items = remember { mutableStateOf(listOf<DetectResponse>()) }

    /** The state of [LazyColumn] */
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    /**
     * Override [SoriListener] to handle recognition result
     */
    open class EventHandler : SoriListener() {
        override fun onDetected(res: DetectResponse) {
            super.onDetected(res)
            // add result to results
            items.value = items.value + res

            // scroll to top
            coroutineScope.launch {
                listState.animateScrollToItem(index = items.value.size - 1)
            }
        }

        override fun onConnected() {
            super.onConnected()
            Log.i("RecognitionScene", "Connected to recognition service")
            setIsRunning(true)  // change button state
        }
    }

    /** instance of [EventHandler] */
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
                    actions = {
                        IconButton(
                            enabled = isRunning,
                            onClick = {
                            // clear recognition state
                            // because SoriSDK remembers the last state so we need to clear it
                            // for recognize same item again
                            RecognitionService.getInstance()?.clearState()

                            // clear results to redraw the list
                            items.value = listOf()
                        }) {
                            Icon(
                                Icons.Outlined.Refresh,
                                contentDescription = "Clear state"
                            )
                        }
                    }
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
                            setIsRunning(false) // change button state
                        }
                    ) {
                        Text("Stop Recognition")
                    }
                } else {
                    Button(onClick = {
                        toggleRecognition()
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
                LazyColumn(
                    state = listState,
                    reverseLayout = true,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    // render the guide text if there is no result
                    item {
                        if (items.value.isEmpty()) {
                            Text(
                                text = "Recognition result will be shown here.",
                                modifier = Modifier.padding(16.dp)
                            )
                            // show the guide text if the recognition is not running
                            if (!isRunning) {
                                Text(
                                    text = "Please Touch 'Start Recognition' button to start recognition.",
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                    items(
                        count = items.value.size,
                        itemContent = { res ->
                            val item = items.value[res]
                            RecognizedItemCard(
                                item.result.title,
                                subTitle = null,
                                imageUrl = item.result.image,
                                actionUrl = "https://www.iplateia.com"
                            )
                        }
                    )
                }
            }
        }
    }
}