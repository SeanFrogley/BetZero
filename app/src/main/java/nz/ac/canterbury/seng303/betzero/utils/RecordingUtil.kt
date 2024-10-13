package nz.ac.canterbury.seng303.betzero.utils

import android.content.Context
import android.os.Environment
import java.io.File

object RecordingUtil {

    fun getExternalStorageDir(context: Context): File {
        val externalDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        if (externalDir != null && !externalDir.exists()) {
            externalDir.mkdirs()
        }
        return externalDir ?: context.filesDir
    }
    // Get recording by name
    fun getRecordingFile(context: Context, fileName: String): File {
        return File(getExternalStorageDir(context), fileName)
    }

    // Get all recording files
    fun getAllRecordings(context: Context): List<File> {
        val externalDir = getExternalStorageDir(context)
        return externalDir.listFiles()?.toList() ?: emptyList()
    }

    fun getMoodFromFile(file: File): String? {
        val fileName = file.nameWithoutExtension // Get the file name without the .mp3
        val parts = fileName.split("_") // Split the name using "_"
        return if (parts.size >= 2) parts[1] else null // Return the mood
    }
    fun getTimeStampFromFile(file: File): String? {
        val fileName = file.nameWithoutExtension // Get the file name without the .mp3
        val parts = fileName.split("_") // Split the name using "_"
        return if (parts.size >= 2) parts[2] else null // Return the timestamp
    }
}