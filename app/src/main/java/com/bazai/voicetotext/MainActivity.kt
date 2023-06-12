package com.bazai.voicetotext

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bazai.voicetotext.ui.theme.VoiceToTextTheme

class MainActivity : ComponentActivity() {
    /**
     * In this code, we declare two member variables:
     * speechRecognizer of type SpeechRecognizer and speechRecognizerIntent of type Intent.
     * We use the lateinit keyword to indicate that these variables will be initialized later.
     */
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechRecognizerIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /***
         * In the onCreate method,
         * we call SpeechRecognizer.createSpeechRecognizer(applicationContext)
         * to create a new instance of SpeechRecognizer and
         * assign it to the speechRecognizer variable.
         * We pass the applicationContext as the parameter to the createSpeechRecognizer method.
         */
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)
        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
        }

        setContent {
            VoiceToTextTheme {
                VoiceToTextScreen()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }

    @Composable
    fun VoiceToTextScreen() {
        val context = LocalContext.current
        val recognizedText: MutableState<String> = remember { mutableStateOf("") }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column {
                Text(text = recognizedText.value)
                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this@MainActivity,
                                arrayOf(Manifest.permission.RECORD_AUDIO),
                                RECORD_AUDIO_PERMISSION_CODE
                            )
                        } else {
                            speechRecognizer.startListening(speechRecognizerIntent)
                        }
                    }
                ) {
                    Text(text = "Start Listening")
                }
            }
        }


        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {}

            override fun onResults(results: Bundle?) {
                val matches =
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    recognizedText.value = matches[0]
                }
                speechRecognizer.startListening(speechRecognizerIntent)
            }

            override fun onPartialResults(partialResults: Bundle?) {}

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    companion object {
        private const val RECORD_AUDIO_PERMISSION_CODE = 1
    }
}

@Preview(showBackground = true)
@Composable
fun VoiceToTextPreview() {
    VoiceToTextTheme {
        MainActivity().VoiceToTextScreen()
    }
}
