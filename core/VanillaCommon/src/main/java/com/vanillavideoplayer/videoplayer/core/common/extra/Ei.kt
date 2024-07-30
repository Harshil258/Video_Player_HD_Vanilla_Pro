package com.vanillavideoplayer.videoplayer.core.common.extra

import android.app.UiModeManager
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.Cursor
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.text.isDigitsOnly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import kotlin.math.pow
import kotlin.math.roundToInt


fun Context.hasSAFChooser(): Boolean {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    intent.setType("video/*")
    return intent.resolveActivity(packageManager) != null
}

val VIDEO_CONTENT_URI: Uri
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

fun Context.isTvBox(): Boolean {
    val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    if (uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) {
        return true
    }

    if (packageManager.hasSystemFeature("amazon.hardware.fire_tv")) {
        return true
    }

    if (!hasSAFChooser()) {
        return true
    }

    if (Build.VERSION.SDK_INT < 30) {
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN)) {
            return true
        }

        if (packageManager.hasSystemFeature("android.hardware.hdmi.cec")) {
            return true
        }

        if (Build.MANUFACTURER.equals("zidoo", ignoreCase = true)) {
            return true
        }
    }
    return false
}

fun Context.convertGivenCharsetToUTF8(uri: Uri, charset: Charset?): Uri {
    try {
        if (uri.scheme?.lowercase()?.startsWith("http") == true) return uri
        contentResolver.openInputStream(uri)?.use { inputStream ->
            val bufferedInputStream = BufferedInputStream(inputStream)
            val detectedCharset = charset ?: detectGivenCharset(bufferedInputStream)
            return convertGivenCharsetToUTF8(uri, bufferedInputStream.reader(detectedCharset))
        }
    } catch (exception: Exception) {
        exception.printStackTrace()
    }
    return uri
}

fun detectGivenCharset(inputStream: BufferedInputStream): Charset {
    val bufferSize = 8000
    inputStream.mark(bufferSize)
    val rawInput = ByteArray(bufferSize)

    var rawLength = 0
    var remainingLength = bufferSize
    while (remainingLength > 0) {

        val bytesRead = inputStream.read(rawInput, rawLength, remainingLength)
        if (bytesRead <= 0) {
            break
        }
        rawLength += bytesRead
        remainingLength -= bytesRead
    }
    inputStream.reset()

    val charsets = listOf("UTF-8", "ISO-8859-1", "ISO-8859-7").map { Charset.forName(it) }

    for (charset in charsets) {
        try {
            val decodedBytes = charset.decode(ByteBuffer.wrap(rawInput))
            if (!decodedBytes.contains("�")) {
                return charset
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return StandardCharsets.UTF_8
        }
    }

    return StandardCharsets.UTF_8
}


fun Context.getFNameFromUri(uri: Uri): String {
    return if (ContentResolver.SCHEME_FILE.equals(uri.scheme, ignoreCase = true)) {
        File(uri.toString()).name
    } else {
        getFNameFromContentUri(uri) ?: uri.lastPathSegment ?: ""
    }
}

fun openLinkInBrowser(context: Context, link: String?) {
    try {
        if (link == null || link.trim { it <= ' ' }.isEmpty()) {
            Toast.makeText(context, "Invalid link", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        context.startActivity(intent)
    } catch (e: java.lang.Exception) {
        // Handle exception
    }
}

fun Context.visibleToast(string: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, string, duration).show()
}

suspend fun Context.scanNewPaths(
    paths: List<String>, callback: ((String?, Uri?) -> Unit)? = null
) = withContext(Dispatchers.IO) {
    MediaScannerConnection.scanFile(
        this@scanNewPaths, paths.toTypedArray(), arrayOf("video/*"), callback
    )
}

suspend fun Context.scanNewPath(file: File) {
    withContext(Dispatchers.IO) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { scanNewPath(it) }
        } else {
            scanNewPaths(listOf(file.path))
        }
    }
}

suspend fun Context.scanStoragePath(callback: ((String?, Uri?) -> Unit)? = null) = withContext(Dispatchers.IO) {
    val storagePath = Environment.getExternalStorageDirectory()?.path
    if (storagePath != null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            scanNewPaths(listOf(storagePath), callback)
        } else {
            scanNewPath(File(storagePath))
        }
    } else {
        callback?.invoke(null, null)
    }
}

private fun Context.getDataFromCursor(
    uri: Uri, selection: String? = null, selectionArgs: Array<String>? = null
): String? {
    var cursor: Cursor? = null
    val column = MediaStore.Images.Media.DATA
    val projection = arrayOf(column)
    try {
        cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(index)
        }
    } catch (e: Exception) {
        return null
    } finally {
        cursor?.close()
    }
    return null
}

fun Context.convertGivenCharsetToUTF8(inputUri: Uri, inputStreamReader: InputStreamReader): Uri {
    if (!StandardCharsets.UTF_8.displayName().equals(inputStreamReader.encoding)) {
        val fileName = getFNameFromUri(inputUri)
        val file = File(cacheDir, fileName)
        val bufferedReader = BufferedReader(inputStreamReader)
        val bufferedWriter = BufferedWriter(FileWriter(file))

        val buffer = CharArray(512)
        var bytesRead: Int

        while (bufferedReader.read(buffer).also { bytesRead = it } != -1) {
            bufferedWriter.write(buffer, 0, bytesRead)
        }

        bufferedWriter.close()
        bufferedReader.close()

        return Uri.fromFile(file)
    }
    return inputUri
}

fun Context.clearCacheFolder() {
    try {
        cacheDir.listFiles()?.onEach {
            if (it.isFile) it.delete()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

suspend fun Context.deleteFilesFromUris(uris: List<Uri>, intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>) = withContext(Dispatchers.IO) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intentSender = MediaStore.createDeleteRequest(contentResolver, uris).intentSender
            intentSenderLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
        } else {
            for (uri in uris) {
                contentResolver.delete(uri, null, null)
            }
        }
    } catch (e: Exception) {

    }
}

fun Context.getPathFromUri(uri: Uri): String? {
    if (DocumentsContract.isDocumentUri(this, uri)) {
        when {
            uri.isEsDoc -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().path + "/" + split[1]
                }


            }

            uri.isDownDoc -> {
                val docId = DocumentsContract.getDocumentId(uri)
                if (docId.isDigitsOnly()) {
                    return try {
                        val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), docId.toLong()
                        )
                        getDataFromCursor(contentUri, null, null)
                    } catch (e: Exception) {
                        null
                    }
                }
            }

            uri.isMediaDoc -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return contentUri?.let { getDataFromCursor(it, selection, selectionArgs) }
            }
        }
    } else if (ContentResolver.SCHEME_CONTENT.equals(uri.scheme, ignoreCase = true)) {
        return if (uri.isGooglePhoto) {
            uri.lastPathSegment
        } else {
            getDataFromCursor(uri, null, null)
        }
    } else if (ContentResolver.SCHEME_FILE.equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }
    return null
}


val Uri.isEsDoc: Boolean
    get() = "com.android.externalstorage.documents" == authority

fun File.isSubFile(): Boolean {
    val subtitleExtensions = listOf("srt", "ssa", "ass", "vtt", "ttml")
    return extension in subtitleExtensions
}

fun String.getFileThumb(): File? {
    val filePathWithoutExtension = this.substringBeforeLast(".")
    val imageExtensions = listOf("png", "jpg", "jpeg")
    for (imageExtension in imageExtensions) {
        val file = File("$filePathWithoutExtension.$imageExtension")
        if (file.exists()) return file
    }
    return null
}


fun File.getFileSub(): List<File> {
    val mediaName = this.nameWithoutExtension
    val subs = this.parentFile?.listFiles { file ->
        file.nameWithoutExtension.startsWith(mediaName) && file.isSubFile()
    }?.toList() ?: emptyList()

    return subs
}

val File.prettyName: String
    get() = this.name.takeIf { this.path != Environment.getExternalStorageDirectory()?.path } ?: "Internal Storage"

fun Float.roundTheFloat(decimalPlaces: Int): Float {
    return (this * 10.0.pow(decimalPlaces.toDouble())).roundToInt() / 10.0.pow(decimalPlaces.toDouble()).toFloat()
}

fun Context.getMediaFromUri(uri: Uri): Uri? {
    val path = getPathFromUri(uri) ?: return null

    var cursor: Cursor? = null
    val column = MediaStore.Video.Media._ID
    val projection = arrayOf(column)
    try {
        cursor = contentResolver.query(
            VIDEO_CONTENT_URI, projection, "${MediaStore.Images.Media.DATA} = ?", arrayOf(path), null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow(column)
            val id = cursor.getLong(index)
            return ContentUris.withAppendedId(VIDEO_CONTENT_URI, id)
        }
    } catch (e: Exception) {
        return null
    } finally {
        cursor?.close()
    }
    return null
}

val Uri.isMediaDoc: Boolean
    get() = "com.android.providers.media.documents" == authority

val Uri.isDownDoc: Boolean
    get() = "com.android.providers.downloads.documents" == authority

val Uri.isGooglePhoto: Boolean
    get() = "com.google.android.apps.photos.content" == authority

fun Context.getFNameFromContentUri(uri: Uri): String? {
    val projection = arrayOf(
        OpenableColumns.DISPLAY_NAME
    )

    try {
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        }
    } catch (e: Exception) {
        return null
    }
    return null
}

