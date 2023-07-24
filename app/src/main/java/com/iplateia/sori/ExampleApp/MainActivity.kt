package com.iplateia.sori.ExampleApp

import android.Manifest
import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.iplateia.sori.ExampleApp.ui.theme.SORIExampleTheme


class MainActivity : ComponentActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private var soriServiceIntent: Intent? = null
    private val notificationTitle = "SORI Example"
    private val notificationBody = "Listening for audio..."

    /**
     * Check if the app has permission to record audio
     */
    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onStart() {
        super.onStart()

        // Check if the app has permission to record audio, and if not, request permission.
        if (!checkPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecognitionScene(
                toggleRecognition = ::toggleRecognition
            ) // render the scene
        }
    }

    fun startRecognitionService() {
//        Toast.makeText(this, "Starting Audio Recognition", Toast.LENGTH_SHORT).show()
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
//        Toast.makeText(this, "Stopping Audio Recognition", Toast.LENGTH_SHORT).show()
        try {
            if (_checkServiceRunning(RecognitionService::class.java)) {
                stopService(Intent(this@MainActivity, RecognitionService::class.java))
                soriServiceIntent = null
                return
            }
        } catch (e: java.lang.Error) {
            e.printStackTrace()
        }
    }

    fun _checkServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun toggleRecognition() {
        if (checkPermission()) {
            if (_checkServiceRunning(RecognitionService::class.java)) {
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
                    .height(150.dp)
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
                modifier = Modifier
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TODO: change this result card by corresponding result of recognition service
                RecognitionResult(
                    "Item Title",
                    "Item subtitle",
                    imageUrl = "https://i.sori.io/data/sori/2b/2baa9940becd44cf9e4791734a3f29c5.png?size=1024",
                    actionUrl = "https://www.iplateia.com"
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = """Recognition result will be shown here.
                    """.trimMargin(),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}