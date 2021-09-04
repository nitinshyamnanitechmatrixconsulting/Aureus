package com.twilio.video.app.util

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log

val allSupportedDocumentsTypesToExtensions = mapOf(
    "application/pdf" to ".pdf",
    "image/jpeg" to ".jpeg",
    "image/jpeg" to ".jpg",
    "image/jpeg" to ".jpe",
    "image/png" to ".png",
    "video/mp4" to "mp4",
    "audio/mpeg" to "mp3"

)
private val extensionsToTypes = allSupportedDocumentsTypesToExtensions.invert()

fun ContentResolver.queryFileName(uri: Uri): String? {
    val cursor: Cursor = query(uri, null, null, null, null) ?: return null
    val nameIndex: Int = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    cursor.moveToFirst()
    val name: String = cursor.getString(nameIndex)
    cursor.close()
    return appendExtensionIfNeeded(name, uri)
}
fun <K, V> Map<K, V>.invert() = HashMap<V, K>().also { newMap ->
    entries.forEach { newMap.put(it.value, it.key) }
}
private fun ContentResolver.appendExtensionIfNeeded(name: String, uri: Uri): String? {
    return if (hasKnownExtension(name)) {
        name
    } else {
        val type = getType(uri)
        if (type != null && allSupportedDocumentsTypesToExtensions.containsKey(type)) {
            return name + allSupportedDocumentsTypesToExtensions[type]
        }
        Log.e("FileUtils","unknown file type: $type, for file: $name")
        name
    }
}

private fun hasKnownExtension(filename: String): Boolean {
    val lastDotPosition = filename.indexOfLast { it == '.' }
    if (lastDotPosition == -1) {
        return false
    }
    val extension = filename.substring(lastDotPosition)
    return extensionsToTypes.containsKey(extension)
}