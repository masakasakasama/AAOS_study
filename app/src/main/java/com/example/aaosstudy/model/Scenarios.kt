package com.example.aaosstudy.model

import androidx.compose.ui.graphics.Color

/** One-tap presets that mutate both vehicle + theme state at once. */
data class Scenario(
    val name: String,
    val description: String,
    val vehicle: VehicleState,
    val theme: OemThemeState,
)

object Scenarios {
    val all = listOf(
        Scenario(
            name = "Highway cruise",
            description = "Drive gear, 110 km/h, warm cabin. Watch the " +
                "speedo and RPM react to the VHAL values.",
            vehicle = VehicleState(
                speedKph = 110f, engineRpm = 2400f, fuelPercent = 48f,
                gear = Gear.DRIVE, outsideTempC = 24f, hvacSetTempC = 21f,
                hvacFanSpeed = 4,
            ),
            theme = OemThemeState(),
        ),
        Scenario(
            name = "Nordic minimal (dark)",
            description = "A restrained Scandinavian-style cluster: near " +
                "monochrome, square gauges, deep dark background.",
            vehicle = VehicleState(
                speedKph = 64f, engineRpm = 1500f, gear = Gear.DRIVE,
            ),
            theme = OemThemeState(
                brandName = "Nordic Concept",
                accent = Color(0xFFE6E1DA),
                clusterBackground = Color(0xFF05070A),
                onCluster = Color(0xFFF2F2F2),
                cornerRadiusDp = 4,
                roundGauges = false,
                nightMode = true,
            ),
        ),
        Scenario(
            name = "Warm classic (light)",
            description = "Light cluster, amber accent, rounded gauges — a " +
                "traditional analog-inspired skin via RRO.",
            vehicle = VehicleState(
                speedKph = 40f, engineRpm = 1200f, gear = Gear.DRIVE,
                fuelPercent = 80f,
            ),
            theme = OemThemeState(
                brandName = "Classic Line",
                accent = Color(0xFFE08A1E),
                clusterBackground = Color(0xFFF4EFE7),
                onCluster = Color(0xFF241B0E),
                cornerRadiusDp = 28,
                roundGauges = true,
                nightMode = false,
            ),
        ),
        Scenario(
            name = "Parked & charging",
            description = "Park gear, ignition on, cool cabin request — a " +
                "typical pre-conditioning state.",
            vehicle = VehicleState(
                speedKph = 0f, engineRpm = 0f, gear = Gear.PARK,
                fuelPercent = 35f, outsideTempC = 9f, hvacSetTempC = 24f,
                hvacFanSpeed = 5,
            ),
            theme = OemThemeState(
                brandName = "EV Reference",
                accent = Color(0xFF2ECC71),
                clusterBackground = Color(0xFF0B1410),
                onCluster = Color(0xFFDFF5E8),
            ),
        ),
    )
}
