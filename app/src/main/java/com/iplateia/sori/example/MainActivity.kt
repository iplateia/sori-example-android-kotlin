package com.iplateia.sori.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.iplateia.sori.example.databinding.ActivityMainBinding
import com.iplateia.sorisdk.SORIAudioRecognizer
import com.iplateia.sorisdk.SORICampaign
import com.iplateia.sorisdk.SORIListener
import com.iplateia.sorisdk.ServiceState

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: CampaignAdapter
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var sori: SORIAudioRecognizer

    /**
     * Checks necessary permissions for the audio recognition service.
     * The permissions include:
     * - `RECORD_AUDIO`: Required for audio recording.
     * - `POST_NOTIFICATIONS`: Required for foreground service notifications (Android 13 and above).
     */
    private fun checkPermission(): Boolean {
        val recordingEnabled = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        val notificationEnabled = NotificationManagerCompat.from(this).areNotificationsEnabled()

        return recordingEnabled && notificationEnabled
    }

    /**
     * Requests necessary permissions for the audio recognition service.
     * The permissions include:
     * - `RECORD_AUDIO`: Required for audio recording.
     * - `POST_NOTIFICATIONS`: Required for foreground service notifications (Android 13 and above).
     */
    private fun requestPermission() {
        var permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
        // if api version is upper than tiramisu, we need to acquire post notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions += Manifest.permission.POST_NOTIFICATIONS
        }
        ActivityCompat.requestPermissions(this, permissions, 1)
    }

    override fun onStart() {
        super.onStart()

        // Check if the app has the necessary permissions and request them if not
        if (!checkPermission()) {
            try {
                requestPermission()
            } catch (e: Exception) {
                Log.e("SORI", "Error while requesting permissions: ${e.message}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the SORI SDK with the app ID and secret key
        sori = SORIAudioRecognizer(
            getString(R.string.SORI_APP_ID),
            getString(R.string.SORI_SECRET_KEY),
        )

        // Prepare listener for events
        val listener = object : SORIListener() {
            override fun onStateChanged(state: String) {
                println("State changed: $state")

                // update the FAB based on the current state
                updateFab(state)
            }
            override fun onCampaignFound(campaign: SORICampaign) {
                println("Campaign found: ${campaign.name}")
                adapter.addItem(campaign)

                // hide guide text if any campaign is found
                findViewById<TextView>(R.id.guide_text).visibility = View.GONE
            }
        }

        sori.setListener(this, listener)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        updateFab(ServiceState.STOPPED) // set the default state to STOPPED

        val campaignTimeline = findViewById<RecyclerView>(R.id.campaign_list)
        adapter = CampaignAdapter(mutableListOf())
        campaignTimeline.adapter = adapter
        campaignTimeline.layoutManager = LinearLayoutManager(this)
    }


    /**
     * Switches the Floating Action Button (FAB) based on the current state of the SORI service.
     *
     * @param state The current state of the SORI service.
     */
    private fun updateFab(state: String) {
        when (state) {
            ServiceState.STARTED -> {
                binding.fab.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
                binding.fab.setOnClickListener { view ->
                    sori.stopRecognition(this)
                    Snackbar.make(view, "Stop recognition...", Snackbar.LENGTH_SHORT)
                        .setAnchorView(R.id.fab).show()
                }
            }
            else -> {
                binding.fab.setImageResource(android.R.drawable.ic_btn_speak_now)
                binding.fab.setOnClickListener { view ->
                    sori.startRecognition(this)
                    Snackbar.make(view, "Start recognition...", Snackbar.LENGTH_SHORT)
                        .setAnchorView(R.id.fab).show()
                }
            }
        }
    }
}
