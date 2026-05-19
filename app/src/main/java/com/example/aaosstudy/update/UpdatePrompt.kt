package com.example.aaosstudy.update

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

/**
 * 起動時に自動でアップデート確認。新しいビルドがあればダイアログを出し、
 * 「更新」で APK を取得してインストール画面へ。
 */
@Composable
fun UpdatePrompt() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var info by remember { mutableStateOf<UpdateInfo?>(null) }
    var phase by remember { mutableStateOf("idle") }

    LaunchedEffect(Unit) {
        val latest = Updater.check()
        if (latest != null && Updater.isUpdate(latest)) {
            info = latest
            phase = "ask"
        }
    }

    val current = info
    if (current == null || phase == "idle") return

    AlertDialog(
        onDismissRequest = { if (phase != "downloading") phase = "idle" },
        title = { Text("新しいバージョンがあります") },
        text = {
            Text(
                when (phase) {
                    "downloading" -> "ダウンロード中…完了するとインストール" +
                        "画面が開きます。"
                    "error" -> "取得に失敗しました。通信環境を確認して" +
                        "もう一度お試しください。"
                    else -> "最新のビルドが公開されています。今すぐ" +
                        "更新しますか？（インストール時に許可を求められ" +
                        "る場合があります）"
                }
            )
        },
        confirmButton = {
            if (phase == "ask" || phase == "error") {
                TextButton(onClick = {
                    phase = "downloading"
                    scope.launch {
                        val f = Updater.download(context, current.apkUrl)
                        if (f != null) {
                            Updater.install(context, f)
                            phase = "idle"
                        } else {
                            phase = "error"
                        }
                    }
                }) { Text(if (phase == "error") "再試行" else "今すぐ更新") }
            }
        },
        dismissButton = {
            if (phase != "downloading") {
                TextButton(onClick = { phase = "idle" }) {
                    Text("あとで")
                }
            }
        },
    )
}
