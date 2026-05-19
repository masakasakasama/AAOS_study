package com.example.aaosstudy.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.FileProvider
import com.example.aaosstudy.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val apkUrl: String,
    val remoteSha: String,
    val notes: String,
)

/**
 * GitHub の rolling リリース "latest" を見て、自分のビルド（BuildConfig.
 * GIT_SHA）より新しい APK があれば取得・インストールへ誘導する。
 *
 * Play 外のサイドロードなので「自動チェック＋ワンタップ更新」が上限。
 * 実インストールは Android 仕様上ユーザー確認が必須。
 */
object Updater {

    private val API =
        "https://api.github.com/repos/${BuildConfig.UPDATE_REPO}" +
            "/releases/tags/latest"

    private val shaRegex = Regex("[0-9a-f]{7,40}")

    suspend fun check(): UpdateInfo? = withContext(Dispatchers.IO) {
        runCatching {
            val conn = (URL(API).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10_000
                readTimeout = 10_000
                setRequestProperty("Accept", "application/vnd.github+json")
                setRequestProperty("User-Agent", "AAOSStudy-Updater")
            }
            conn.inputStream.use { input ->
                val json = JSONObject(input.bufferedReader().readText())
                val assets = json.optJSONArray("assets") ?: return@use null
                var apkUrl: String? = null
                for (i in 0 until assets.length()) {
                    val a = assets.getJSONObject(i)
                    if (a.optString("name").endsWith(".apk")) {
                        apkUrl = a.optString("browser_download_url")
                        break
                    }
                }
                val body = json.optString("body", "")
                val remoteSha = shaRegex.find(body)?.value ?: ""
                if (apkUrl.isNullOrBlank()) null
                else UpdateInfo(apkUrl, remoteSha, body)
            }
        }.getOrNull()
    }

    /** 自ビルドより新しいリリースか。ローカルビルドは常に false。 */
    fun isUpdate(info: UpdateInfo): Boolean {
        val mine = BuildConfig.GIT_SHA
        if (mine == "local" || info.remoteSha.isBlank()) return false
        val n = minOf(7, mine.length, info.remoteSha.length)
        if (n < 7) return mine != info.remoteSha
        return !mine.startsWith(info.remoteSha.take(n)) &&
            !info.remoteSha.startsWith(mine.take(n))
    }

    suspend fun download(context: Context, url: String): File? =
        withContext(Dispatchers.IO) {
            runCatching {
                val conn =
                    (URL(url).openConnection() as HttpURLConnection).apply {
                        instanceFollowRedirects = true
                        connectTimeout = 15_000
                        readTimeout = 60_000
                        setRequestProperty("User-Agent", "AAOSStudy-Updater")
                    }
                val dir = context.externalCacheDir ?: context.cacheDir
                val file = File(dir, "aaos-study-update.apk")
                conn.inputStream.use { input ->
                    file.outputStream().use { out -> input.copyTo(out) }
                }
                file
            }.getOrNull()
        }

    fun install(context: Context, file: File) {
        if (!context.packageManager.canRequestPackageInstalls()) {
            // 「不明なアプリのインストール」許可画面へ誘導
            context.startActivity(
                Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:${context.packageName}"),
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            return
        }
        val uri = FileProvider.getUriForFile(
            context, "${context.packageName}.fileprovider", file
        )
        context.startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        )
    }
}
