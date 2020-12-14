package com.example.customnotif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.customnotif.NotificationService.Companion.CHANNEL_ID
import com.example.customnotif.NotificationService.Companion.CHANNEL_NAME
import com.example.customnotif.NotificationService.Companion.REPLY_ACTION

class ReplyActivity : AppCompatActivity() {

    companion object {
        private const val KEY_MESSAGE_ID = "key_message_id"
        private const val KEY_NOTIF_ID = "key_notif_id"

        fun getReplyMessageIntent(context: Context, notifId: Int, messageId: Int): Intent {
            val intent = Intent(context, ReplyActivity::class.java)
            intent.action = REPLY_ACTION
            intent.putExtra(KEY_MESSAGE_ID, messageId)
            intent.putExtra(KEY_NOTIF_ID, notifId)
            return intent
        }
    }

    private var mMessageId: Int = 0
    private var mNotifId: Int = 0

    private lateinit var mEditReply: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply)

        val intent = intent

        if (REPLY_ACTION == intent.action) {
            mMessageId = intent.getIntExtra(KEY_MESSAGE_ID, 0)
            mNotifId = intent.getIntExtra(KEY_NOTIF_ID, 0)
        }

        mEditReply = findViewById<EditText>(R.id.edit_reply)
        val sendButton = findViewById<Button>(R.id.btn_send)

        sendButton.setOnClickListener{ sendMessage(mNotifId, mMessageId) }
    }

    private fun sendMessage(notifId: Int, messageId: Int) {
        updateNotification(notifId)

        val message = mEditReply.text.toString().trim{it <= ' ' }
        Toast.makeText(this, "Message ID: $messageId\n Message: $message", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun updateNotification(notifId: Int) {
        val notificationManagerCompat = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(getString(R.string.notif_title_sent))
            .setContentText(getString(R.string.notif_content_sent))

        /*
        Untuk android Oreo ke atas perlu menambahkan notification channel
         */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Create or update
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)

            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)

            builder.setChannelId(CHANNEL_ID)

            notificationManagerCompat.createNotificationChannel(channel)
        }

        val notification = builder.build()

        notificationManagerCompat.notify(notifId, notification)
    }
}