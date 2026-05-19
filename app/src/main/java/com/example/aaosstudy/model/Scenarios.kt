package com.example.aaosstudy.model

import androidx.compose.ui.graphics.Color

/** ワンタップで車両状態とテーマをまとめて切り替えるプリセット。 */
data class Scenario(
    val name: String,
    val description: String,
    val vehicle: VehicleState,
    val theme: OemThemeState,
)

object Scenarios {
    val all = listOf(
        Scenario(
            name = "高速巡航",
            description = "ドライブ、110km/h、暖かい車内。VHAL の値に" +
                "合わせて速度計と RPM が反応する様子を見ます。",
            vehicle = VehicleState(
                speedKph = 110f, engineRpm = 2400f, fuelPercent = 48f,
                gear = Gear.DRIVE, outsideTempC = 24f, hvacSetTempC = 21f,
                hvacFanSpeed = 4,
            ),
            theme = OemThemeState(),
        ),
        Scenario(
            name = "北欧ミニマル（ダーク）",
            description = "抑えた北欧風クラスター。ほぼモノクロ、角ばった" +
                "ゲージ、深い暗色背景。",
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
            name = "ウォームクラシック（ライト）",
            description = "明るいクラスター、琥珀のアクセント、丸いゲージ。" +
                "アナログ調の意匠を RRO で再現。",
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
            name = "駐車・充電中",
            description = "パーキング、イグニッション ON、車内を冷やす要求。" +
                "プレコンディショニングの典型状態。",
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
