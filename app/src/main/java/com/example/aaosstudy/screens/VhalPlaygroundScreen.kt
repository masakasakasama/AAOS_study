package com.example.aaosstudy.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aaosstudy.model.Gear
import com.example.aaosstudy.state.SimulatorViewModel
import com.example.aaosstudy.ui.BackHeader
import com.example.aaosstudy.ui.IviDashboard
import com.example.aaosstudy.ui.SectionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VhalPlaygroundScreen(vm: SimulatorViewModel, onBack: () -> Unit) {
    val vehicle by vm.vehicle.collectAsState()
    val theme by vm.theme.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BackHeader("VHAL プレイグラウンド", onBack)

        IviDashboard(
            vehicle = vehicle,
            theme = theme,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        SectionCard(
            "車両プロパティ",
            Modifier.padding(horizontal = 16.dp),
        ) {
            Text(
                "各操作は擬似的な VHAL プロパティを1つ書き換えます。実 AAOS " +
                    "では、エミュレータのプロパティ注入や " +
                    "CarPropertyManager.set*Property() 呼び出しに相当します。",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 8.dp),
            )

            LabeledSlider(
                "PERF_VEHICLE_SPEED — ${vehicle.speedKph.toInt()} km/h",
                vehicle.speedKph, 0f, 220f,
            ) { vm.setSpeed(it) }

            LabeledSlider(
                "ENGINE_RPM — ${vehicle.engineRpm.toInt()} rpm",
                vehicle.engineRpm, 0f, 8000f,
            ) { vm.setRpm(it) }

            LabeledSlider(
                "FUEL_LEVEL — ${vehicle.fuelPercent.toInt()} %",
                vehicle.fuelPercent, 0f, 100f,
            ) { vm.setFuel(it) }

            LabeledSlider(
                "ENV_OUTSIDE_TEMPERATURE — ${vehicle.outsideTempC.toInt()} °C",
                vehicle.outsideTempC, -20f, 50f,
            ) { vm.setOutsideTemp(it) }

            LabeledSlider(
                "HVAC_TEMPERATURE_SET — ${vehicle.hvacSetTempC.toInt()} °C",
                vehicle.hvacSetTempC, 16f, 30f,
            ) { vm.setHvacTemp(it) }

            LabeledSlider(
                "HVAC_FAN_SPEED — level ${vehicle.hvacFanSpeed}",
                vehicle.hvacFanSpeed.toFloat(), 0f, 7f,
            ) { vm.setFanSpeed(it.toInt()) }

            Text(
                "GEAR_SELECTION",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Gear.entries.forEach { g ->
                    FilterChip(
                        selected = vehicle.gear == g,
                        onClick = { vm.setGear(g) },
                        label = { Text(g.label) },
                    )
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "IGNITION_STATE",
                    style = MaterialTheme.typography.labelLarge,
                )
                Switch(
                    checked = vehicle.ignitionOn,
                    onCheckedChange = { vm.setIgnition(it) },
                )
            }

            Button(
                onClick = { vm.resetAll() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            ) { Text("AOSP 既定値に戻す") }
        }
    }
}

@Composable
private fun LabeledSlider(
    label: String,
    value: Float,
    min: Float,
    max: Float,
    onChange: (Float) -> Unit,
) {
    Column(Modifier.padding(top = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Slider(
            value = value,
            onValueChange = onChange,
            valueRange = min..max,
        )
    }
}
