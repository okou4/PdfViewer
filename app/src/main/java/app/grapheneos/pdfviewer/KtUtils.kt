package app.grapheneos.pdfviewer

import android.content.Context
import android.graphics.RectF
import android.net.Uri
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

@Throws(
    FileNotFoundException::class,
    IOException::class,
    IllegalArgumentException::class,
    OutOfMemoryError::class
)
fun saveAs(context: Context, existingUri: Uri, saveAs: Uri) {

    context.asInputStream(existingUri)?.use { inputStream ->
        context.asOutputStream(saveAs)?.use { outputStream ->
            outputStream.write(inputStream.readBytes())
        }
    }

}

@Throws(FileNotFoundException::class)
private fun Context.asInputStream(uri: Uri): InputStream? = contentResolver.openInputStream(uri)

@Throws(FileNotFoundException::class)
private fun Context.asOutputStream(uri: Uri): OutputStream? = contentResolver.openOutputStream(uri)

data class TapZones(val left: RectF, val middle: RectF, val right: RectF)