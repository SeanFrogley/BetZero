package nz.ac.canterbury.seng303.betzero.utils

import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import java.io.File
import java.io.IOException

class VoiceRecorder(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: String? = null

    fun startRecording(mood: String) {
        val externalDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        if (externalDir != null && !externalDir.exists()) {
            externalDir.mkdirs()
        }    // Timestamp to make file name unique
        val timeStamp = System.currentTimeMillis()

        val sanitizedMood = mood.replace(Regex("[^a-zA-Z0-9]"), "_")

        // File name will contain the mood and the timestamp
        outputFile = "${externalDir?.absolutePath}/audiorecord_${sanitizedMood}_$timeStamp.mp3"


        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile)
            try {
                prepare()
                start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun stopRecording(): String? {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        return outputFile
    }
}