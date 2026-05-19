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
            "VHAL Playground",
            "Move sliders for speed / RPM / fuel / gear / HVAC and watch " +
                "the IVI cluster react live.",
        ),
        Module(
            Routes.RRO,
            "RRO Theme Lab",
            "Reskin the cluster like an OEM. See the generated overlay XML " +
                "that produces the look.",
        ),
        Module(
            Routes.API,
            "Car API Explorer",
            "Real CarPropertyManager code per property + the App → Service " +
                "→ VHAL call chain.",
        ),
        Module(
            Routes.ARCH,
            "Architecture Map",
            "Tap through the AAOS layers, app-layer first. Plus how RRO " +
                "really works.",
        ),
        Module(
            Routes.SCENARIOS,
            "Scenario Presets",
            "One tap to load combined vehicle + theme states and compare.",
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
                    "An interactive sandbox for Android Automotive OS " +
                        "concepts — app layer first. Nothing here talks to " +
                        "a real HAL; it models the behavior so you can see " +
                        "cause → effect on the IVI screen.",
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
