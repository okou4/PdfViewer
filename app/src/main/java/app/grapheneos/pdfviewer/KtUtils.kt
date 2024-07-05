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

class TapZones(containerWidth: Int) {
    val left: RectF
    val middle: RectF
    val right: RectF

    init {
        // Left and Right zones are 40% each and while the Middle zone is 20%
        val leftAndRightWidth = (containerWidth * 0.4).toFloat()
        val middleWidth = (containerWidth * 0.2).toFloat()

        val leftZoneLeft = 0.0f
        val middleZoneRight = leftAndRightWidth + middleWidth
        val rightZoneRight = middleZoneRight + leftAndRightWidth

        left = RectF(leftZoneLeft, 0f, leftAndRightWidth, 1f)
        middle = RectF(leftAndRightWidth, 0f, middleZoneRight, 1f)
        right = RectF(middleZoneRight, 0f, rightZoneRight, 1f)
    }
}