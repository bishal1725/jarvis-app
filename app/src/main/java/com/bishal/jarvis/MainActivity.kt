package com.bishal.jarvis

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val speechRequestCode = 101
    private val permissionRequestCode = 200
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // বেছিক ইউজাৰ ইন্টাৰফেচ
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 100, 50, 50)
            gravity = android.view.Gravity.CENTER_HORIZONTAL
        }

        statusText = TextView(this).apply {
            text = "JARVIS সজাগ হৈ আছে, কওক..."
            textSize = 20f
            gravity = android.view.Gravity.CENTER
            setPadding(0, 0, 0, 50)
        }

        val speakButton = Button(this).apply {
            text = "জাৰ্ভিছৰ লগত কথা পাতক (Tap to Speak)"
            setOnClickListener {
                startSpeechRecognition()
            }
        }

        layout.addView(statusText)
        layout.addView(speakButton)
        setContentView(layout)

        checkPermissions()
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE
        )
        val missing = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), permissionRequestCode)
        }
    }

    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "জাৰ্ভিছে শুনি আছে...")
        }
        try {
            startActivityForResult(intent, speechRequestCode)
        } catch (e: Exception) {
            Toast.makeText(this, "Speech recognition not supported", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == speechRequestCode && resultCode == RESULT_OK && data != null) {
            val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val userCommand = results?.get(0) ?: ""
            statusText.text = "আপুনি ক'লে: $userCommand"
            executeAction(userCommand)
        }
    }

    private fun executeAction(command: String) {
        val text = command.lowercase(Locale.ROOT)
        when {
            text.contains("open youtube") || text.contains("ইউটিউব খোল") -> {
                val intent = packageManager.getLaunchIntentForPackage("com.google.android.youtube")
                if (intent != null) startActivity(intent)
            }
            text.contains("call") || text.contains("ফোন কৰা") -> {
                statusText.text = "কলিং মডিউল সক্ৰিয়..."
            }
            else -> {
                statusText.text = "প্ৰক্ৰিয়াকৰণ হৈ আছে: $command"
            }
        }
    }
}
