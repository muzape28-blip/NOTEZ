package com.zaba.notez.markdown

import android.content.Context
import android.net.Uri
import android.webkit.WebResourceResponse
import java.io.File
import java.io.FileInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.util.Locale

class RemoteImageCache(context: Context) {
    private val cacheDir = File(context.filesDir, CACHE_DIR_NAME).apply { mkdirs() }

    fun cachedWebUrlFor(source: String): String? {
        val key = keyFor(source)
        val data = dataFile(key)
        val meta = mimeFile(key)
        return if (data.isFile && meta.isFile) "$WEB_CACHE_PREFIX$key" else null
    }

    fun responseFor(uri: Uri): WebResourceResponse? {
        if (!isCacheUri(uri)) return null
        val key = uri.lastPathSegment?.lowercase(Locale.US) ?: return null
        if (!HEX_64.matches(key)) return null
        val data = dataFile(key)
        val mime = mimeFile(key).takeIf { it.isFile }?.readText()?.trim().orEmpty()
        if (!data.isFile || mime !in ALLOWED_MIME_TYPES) return null
        return WebResourceResponse(
            mime,
            null,
            FileInputStream(data)
        )
    }

    fun download(source: String): DownloadResult {
        val uri = try { Uri.parse(source) } catch (_: Exception) { null }
        val scheme = uri?.scheme?.lowercase(Locale.US)
        if (scheme !in setOf("http", "https")) {
            return DownloadResult(false, "Sumber gambar tidak didukung")
        }

        val key = keyFor(source)
        if (dataFile(key).isFile && mimeFile(key).isFile) {
            return DownloadResult(true, "Gambar sudah ada di cache")
        }

        val connection = try {
            (URL(source).openConnection() as HttpURLConnection).apply {
                instanceFollowRedirects = true
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                setRequestProperty("User-Agent", "NOTEZ/${System.currentTimeMillis()}")
                setRequestProperty("Accept", "image/png,image/jpeg,image/webp,image/gif,*/*;q=0.2")
            }
        } catch (_: Exception) {
            return DownloadResult(false, "URL gambar tidak valid")
        }

        return try {
            val code = connection.responseCode
            if (code !in 200..299) {
                return DownloadResult(false, "Gagal mengambil gambar: HTTP $code")
            }
            val length = connection.contentLengthLong
            if (length > MAX_IMAGE_BYTES) {
                return DownloadResult(false, "Gambar terlalu besar untuk cache NOTEZ")
            }
            val bytes = connection.inputStream.use { input ->
                val output = java.io.ByteArrayOutputStream()
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                var total = 0L
                while (true) {
                    val read = input.read(buffer)
                    if (read < 0) break
                    total += read
                    if (total > MAX_IMAGE_BYTES) {
                        return DownloadResult(false, "Gambar terlalu besar untuk cache NOTEZ")
                    }
                    output.write(buffer, 0, read)
                }
                output.toByteArray()
            }
            val detectedMime = normalizeMime(connection.contentType) ?: sniffMime(bytes)
            if (detectedMime == null || detectedMime !in ALLOWED_MIME_TYPES) {
                return DownloadResult(
                    false,
                    "Tipe gambar belum didukung. Untuk sekarang pakai PNG, JPG, WebP, atau GIF."
                )
            }
            val tmp = File(cacheDir, "$key.tmp")
            tmp.writeBytes(bytes)
            if (!tmp.renameTo(dataFile(key))) {
                dataFile(key).writeBytes(bytes)
                tmp.delete()
            }
            mimeFile(key).writeText(detectedMime)
            sourceFile(key).writeText(source)
            DownloadResult(true, "Gambar disimpan lokal dan bisa dibaca offline")
        } catch (_: Exception) {
            DownloadResult(false, "Gagal mengambil gambar")
        } finally {
            connection.disconnect()
        }
    }

    fun clear(): ClearResult {
        var files = 0
        var bytes = 0L
        cacheDir.listFiles()?.forEach { file ->
            bytes += file.length()
            if (file.delete()) files += 1
        }
        return ClearResult(files, bytes)
    }

    fun sizeBytes(): Long = cacheDir.listFiles()?.sumOf { it.length() } ?: 0L

    fun formattedSize(): String {
        val bytes = sizeBytes()
        if (bytes < 1024) return "$bytes B"
        val kb = bytes / 1024.0
        if (kb < 1024) return String.format(Locale.US, "%.1f KB", kb)
        return String.format(Locale.US, "%.1f MB", kb / 1024.0)
    }

    private fun dataFile(key: String) = File(cacheDir, "$key.img")
    private fun mimeFile(key: String) = File(cacheDir, "$key.mime")
    private fun sourceFile(key: String) = File(cacheDir, "$key.src")

    private fun keyFor(source: String): String = sha256(source.trim())

    data class DownloadResult(val success: Boolean, val message: String)
    data class ClearResult(val files: Int, val bytes: Long)

    companion object {
        private const val CACHE_DIR_NAME = "remote_image_cache"
        private const val NOTEZ_HOST = "notez.local"
        private const val CACHE_PATH_PREFIX = "/cache/image/"
        const val WEB_CACHE_PREFIX = "https://notez.local/cache/image/"
        private const val MAX_IMAGE_BYTES = 5L * 1024L * 1024L
        private const val CONNECT_TIMEOUT_MS = 15_000
        private const val READ_TIMEOUT_MS = 20_000
        private val HEX_64 = Regex("^[a-f0-9]{64}$")
        private val ALLOWED_MIME_TYPES = setOf("image/png", "image/jpeg", "image/webp", "image/gif")

        fun isCacheUri(uri: Uri): Boolean =
            uri.scheme.equals("https", ignoreCase = true) &&
                uri.host.equals(NOTEZ_HOST, ignoreCase = true) &&
                (uri.encodedPath ?: "").startsWith(CACHE_PATH_PREFIX)

        private fun normalizeMime(value: String?): String? = value
            ?.substringBefore(';')
            ?.trim()
            ?.lowercase(Locale.US)
            ?.let { mime ->
                when (mime) {
                    "image/jpg" -> "image/jpeg"
                    else -> mime
                }
            }

        private fun sniffMime(bytes: ByteArray): String? {
            if (bytes.size >= 8 &&
                bytes[0] == 0x89.toByte() && bytes[1] == 0x50.toByte() && bytes[2] == 0x4E.toByte() && bytes[3] == 0x47.toByte()
            ) return "image/png"
            if (bytes.size >= 3 && bytes[0] == 0xFF.toByte() && bytes[1] == 0xD8.toByte() && bytes[2] == 0xFF.toByte()) {
                return "image/jpeg"
            }
            if (bytes.size >= 12 &&
                bytes.copyOfRange(0, 4).toString(Charsets.US_ASCII) == "RIFF" &&
                bytes.copyOfRange(8, 12).toString(Charsets.US_ASCII) == "WEBP"
            ) return "image/webp"
            if (bytes.size >= 6) {
                val header = bytes.copyOfRange(0, 6).toString(Charsets.US_ASCII)
                if (header == "GIF87a" || header == "GIF89a") return "image/gif"
            }
            return null
        }

        private fun sha256(value: String): String = MessageDigest
            .getInstance("SHA-256")
            .digest(value.toByteArray(Charsets.UTF_8))
            .joinToString("") { byte -> "%02x".format(byte) }
    }
}
