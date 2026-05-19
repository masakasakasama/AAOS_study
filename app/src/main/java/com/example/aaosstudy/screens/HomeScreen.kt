package com.example.aaosstudy.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aaosstudy.Routes

private data class Module(
    val route: String,
    val title: String,
    val subtitle: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onOpen: (String) -> Unit) {
    val modules = listOf(
        Module(
            Routes.COURSES,
            "学習コース（初学者 / 中級 / 上級）",
            "体系カリキュラム。図・コード・AOSP 実ファイル対応・" +
                "クイズ付き。まずはここから。",
        ),
        Module(
            Routes.VHAL,
            "VHAL プレイグラウンド",
            "速度 / RPM / 燃料 / ギア / HVAC のスライダーを動かすと、" +
                "IVI クラスターがその場で反応します。",
        ),
        Module(
            Routes.RRO,
            "RRO テーマラボ",
            "OEM のようにクラスターを再スキン。その見た目を生む" +
                "オーバーレイ XML が自動生成されます。",
        ),
        Module(
            Routes.API,
            "Car API エクスプローラ",
            "プロパティ別の実 CarPropertyManager コードと、" +
                "アプリ → サービス → VHAL の呼び出し連鎖。",
        ),
        Module(
            Routes.ARCH,
            "アーキテクチャ図",
            "AAOS の各層をタップで解説（アプリ層から）。" +
                "RRO の仕組みも。",
        ),
        Module(
            Routes.SCENARIOS,
            "シナリオ プリセット",
            "ワンタップで車両＋テーマの状態を読み込み、見比べる。",
        ),
        Module(
            Routes.ADB,
            "ADB ブリッジ（リモコン）",
            "いまのサンドボックス状態を、実 AAOS エミュレータへ流し込む " +
                "adb コマンド列に変換。PC でコピペ実行。",
        ),
    )

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column {
                Text(
                    "AAOS Study",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    "Android Automotive OS の概念を体で学ぶ" +
                        "インタラクティブ・サンドボックス（アプリ層優先）。" +
                        "実 HAL には接続せず挙動を再現し、操作 → IVI 画面の" +
                        "変化という因果が直感的にわかります。",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
                )
            }
        }
        items(modules) { m ->
            Card(
                onClick = { onOpen(m.route) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        m.title,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        m.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
    }
}
