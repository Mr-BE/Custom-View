package com.udacity

import android.animation.ObjectAnimator
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.utils.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var URL = ""
    private var selectedDownload = ""
    private lateinit var downloadManager: DownloadManager

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    //Edit text
    private lateinit var urlEditText: EditText

    //forward button
    private lateinit var proceedButton: ImageButton

    private lateinit var radioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //init and hide views
        urlEditText = findViewById(R.id.url_ET)
        urlEditText.visibility = View.GONE

        proceedButton = findViewById(R.id.proceedButton)
        proceedButton.visibility = View.GONE

        radioGroup = findViewById(R.id.download_RG)



        custom_button.currentState = false



        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        createChannel(
            getString(R.string.app_notification_channel_id),
            getString(R.string.app_notification_channel_name)
        )

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            download()
        }
    }

    override fun onResume() {
        super.onResume()

        //TODO: Move logic to view model

        //receive intent
        val backIntent = intent

        if (backIntent.hasExtra(getString(R.string.back_key))) {
            //show edit text when user leaves via notification
            urlEditText.visibility = View.VISIBLE

            //get inputted url
            if (urlEditText.visibility == View.VISIBLE && urlEditText.text.isNotEmpty()) {
                //remove whitespace from user input text
                val inputtedUrl = urlEditText.text.trim()
                //ensure url validity
                if (Patterns.WEB_URL.matcher(inputtedUrl).matches()) {
                    URL = inputtedUrl.toString()
                    selectedDownload = URL
                } else {
                    Toast.makeText(
                        this, "$inputtedUrl is not a valid URL",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        if (custom_button.currentState) {
            postDownload()
        }
        disableRadioGroup()

    }

    //clear previous selection
    private fun disableRadioGroup() {
        for (i in 0 until radioGroup.childCount) {
            (radioGroup.getChildAt(i) as RadioButton).isEnabled = false
        }

    }

    private fun postDownload() {
        custom_button.visibility = View.GONE
        proceedButton.visibility = View.VISIBLE
        animateProceedButton()
    }

    private fun animateProceedButton() {
        val animator = ObjectAnimator.ofFloat(
            proceedButton,
            View.TRANSLATION_X, 20f
        )
        animator.duration = 2000
        animator.repeatCount = 3
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.start()

    }

    //Set up notifications
    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = getColor(R.color.primaryColor)
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notif_desc)

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id != null) {
                val cursor = downloadManager.query(DownloadManager.Query().setFilterById(id))
                while (cursor.moveToNext()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val downloadSuccessStatus = (status == DownloadManager.STATUS_SUCCESSFUL)
                    val downloadFailedStatus = (status == DownloadManager.STATUS_FAILED)
                    if (context != null) {
                        notificationManager = ContextCompat.getSystemService(
                            context, NotificationManager::class.java
                        ) as NotificationManager
                        custom_button.downloadComplete()
                        notificationManager.sendNotification(
                            selectedDownload,
                            context,
                            downloadSuccessStatus
                        )
                    }
                }
            }
        }
    }


    private fun download() {

        if (URL.isNotEmpty()) {
            val request =
                DownloadManager.Request(Uri.parse(URL))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
            custom_button.downloadBegin()
        } else Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show()


    }

    //select url
    fun selectUrl(view: View) {

        when (view.id) {
            R.id.glide_button -> {
                URL = getString(R.string.glide_url)
                selectedDownload = getString(R.string.glide_label)
            }
            R.id.loadApp_button -> {
                URL = getString(R.string.loadApp_url)
                selectedDownload = getString(R.string.loadApp_label)
            }
            R.id.retrofit_button -> {
                URL = getString(R.string.retrofit_url)
                selectedDownload = getString(R.string.retrofit_label)
            }
            else -> {
                URL = ""
                selectedDownload = ""
                Toast.makeText(this, "No URL found", Toast.LENGTH_SHORT).show()
            }
        }

    }

}
