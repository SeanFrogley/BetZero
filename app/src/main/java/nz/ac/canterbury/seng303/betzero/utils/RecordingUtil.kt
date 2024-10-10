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

    fun getRecordingFile(context: Context, fileName: String): File {
        return File(getExternalStorageDir(context), fileName)
    }

    fun getAllRecordings(context: Context): List<File> {
        val externalDir = getExternalStorageDir(context)
        return externalDir.listFiles()?.toList() ?: emptyList()
    }
}