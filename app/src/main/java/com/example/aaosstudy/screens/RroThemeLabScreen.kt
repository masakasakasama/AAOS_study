package com.example.aaosstudy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.aaosstudy.state.SimulatorViewModel
import com.example.aaosstudy.ui.BackHeader
import com.example.aaosstudy.ui.CodeBlock
import com.example.aaosstudy.ui.IviDashboard
import com.example.aaosstudy.ui.SectionCard

private val accents = listOf(
    Color(0xFF1A73E8), Color(0xFF2ECC71), Color(0xFFE08A1E),
    Color(0xFFE0533D), Color(0xFF9B59B6), Color(0xFFE6E1DA),
)
private val backgrounds = listOf(
    Color(0xFF0E1116), Color(0xFF05070A), Color(0xFFF4EFE7),
    Color(0xFF101A22), Color(0xFF1A1410),
)

@Composable
fun RroThemeLabScreen(vm: SimulatorViewModel, onBack: () -> Unit) {
    val vehicle by vm.vehicle.collectAsState()
    val theme by vm.theme.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BackHeader("RRO Theme Lab", onBack)

        IviDashboard(
            vehicle = vehicle,
            theme = theme,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        SectionCard("Overlay resources", Modifier.padding(horizontal = 16.dp)) {
            Text(
                "An OEM never edits the cluster app. They ship an RRO APK " +
                    "that replaces these resource values. Change them here " +
                    "and watch the same UI re-render.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 8.dp),
            )

            Text("@color/cluster_accent", style = MaterialTheme.typography.labelLarge)
            Swatches(accents, theme.accent) { vm.setAccent(it) }

            Text(
                "@color/cluster_background",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 12.dp),
            )
            Swatches(backgrounds, theme.clusterBackground) {
                vm.setClusterBackground(it)
                vm.setOnCluster(
                    if (it.luminanceIsDark()) Color(0xFFE8EAED)
                    else Color(0xFF1A1A1A)
                )
            }

            Text(
                "@dimen/cluster_corner_radius — ${theme.cornerRadiusDp}dp",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 16.dp),
            )
            Slider(
                value = theme.cornerRadiusDp.toFloat(),
                onValueChange = { vm.setCornerRadius(it.toInt()) },
                valueRange = 0f..40f,
            )

            ToggleRow("@bool/cluster_round_gauges", theme.roundGauges) {
                vm.setRoundGauges(it)
            }
            ToggleRow("@bool/config_night_mode_default", theme.nightMode) {
                vm.setNightMode(it)
            }
        }

        SectionCard(
            "Generated overlay XML",
            Modifier.padding(horizontal = 16.dp),
        ) {
            Text(
                "This is the file an OEM would author to reproduce exactly " +
                    "the look above:",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 8.dp),
            )
            CodeBlock(theme.toOverlayXml())
        }

        Box(Modifier.padding(bottom = 16.dp))
    }
}

@Composable
private fun Swatches(
    options: List<Color>,
    selected: Color,
    onPick: (Color) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        options.forEach { c ->
            Box(
                Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(c)
                    .border(
                        width = if (c == selected) 3.dp else 1.dp,
                        color = if (c == selected)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(0.3f),
                        shape = CircleShape,
                    )
                    .clickable { onPick(c) }
            )
        }
    }
}

@Composable
private fun ToggleRow(label: String, value: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Switch(checked = value, onCheckedChange = onChange)
    }
}

private fun Color.luminanceIsDark(): Boolean =
    (0.299 * red + 0.587 * green + 0.114 * blue) < 0.5
