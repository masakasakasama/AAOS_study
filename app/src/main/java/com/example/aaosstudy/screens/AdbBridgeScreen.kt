package com.example.aaosstudy.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aaosstudy.model.Gear
import com.example.aaosstudy.model.VehicleState
import com.example.aaosstudy.state.SimulatorViewModel
import com.example.aaosstudy.ui.BackHeader
import com.example.aaosstudy.ui.CodeBlock
import com.example.aaosstudy.ui.IviDashboard
import com.example.aaosstudy.ui.SectionCard

/**
 * 「リモコンモード」の入口。サンドボックスの現在値を、実機 / 実 AAOS
 * エミュレータの参照 VHAL に流し込む adb コマンド列へ変換する。
 *
 * 注: cmd car_service のサブコマンド構文は AOSP バージョンで差がある。
 * ここでは概念と実プロパティ ID を学ぶことを主眼に、コピーして使える
 * テンプレートを生成する（ネットワーク ADB 等は行わない）。
 */
private fun gearRaw(g: Gear): Int = when (g) {
    Gear.NEUTRAL -> 0x0001
    Gear.REVERSE -> 0x0002
    Gear.PARK -> 0x0004
    Gear.DRIVE -> 0x0008
}

private fun script(v: VehicleState): String {
    val speedMps = v.speedKph / 3.6f
    val ign = if (v.ignitionOn) 4 else 2 // ON=4, OFF=2
    return """
# --- AAOS エミュレータへ現在のサンドボックス状態を注入 ---
# 事前: emulator 起動済み & `adb devices` で認識されていること
# 構文は AOSP 版により差あり。area/zone 指定は版で異なる
# （位置引数 / -a / -z）。`cmd car_service -h` で要確認。

# PERF_VEHICLE_SPEED (0x11600207) float[m/s]
adb shell cmd car_service inject-vhal-event 291504647 ${"%.2f".format(speedMps)}

# ENGINE_RPM (0x11600305) float
adb shell cmd car_service inject-vhal-event 291504901 ${v.engineRpm.toInt()}

# FUEL_LEVEL (0x11600307) float[ml] (容量比 ${v.fuelPercent.toInt()}%)
adb shell cmd car_service inject-vhal-event 291504903 ${(v.fuelPercent * 500).toInt()}

# GEAR_SELECTION (0x11400400) int  P=4 R=2 N=1 D=8
adb shell cmd car_service inject-vhal-event 289408000 ${gearRaw(v.gear)}

# IGNITION_STATE (0x11400409) int  OFF=2 ON=4
adb shell cmd car_service inject-vhal-event 289408009 $ign

# ENV_OUTSIDE_TEMPERATURE (0x11600703) float[C]
adb shell cmd car_service inject-vhal-event 291505923 ${v.outsideTempC.toInt()}

# HVAC_TEMPERATURE_SET (0x15600503) float[C]  ※seat zone 指定が必要。
# area の渡し方は版依存（-a / -z / 位置引数）。要 `-h` 確認。
adb shell cmd car_service inject-vhal-event 358614275 ${v.hvacSetTempC.toInt()}

# HVAC_FAN_SPEED (0x15400500) int  ※seat zone 指定が必要（同上）
adb shell cmd car_service inject-vhal-event 356517120 ${v.hvacFanSpeed}

# 確認（CarService 全体を dump。出力は長い）
adb shell dumpsys car_service
    """.trim()
}

@Composable
fun AdbBridgeScreen(vm: SimulatorViewModel, onBack: () -> Unit) {
    val vehicle by vm.vehicle.collectAsState()
    val theme by vm.theme.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BackHeader("ADB ブリッジ（リモコン）", onBack)

        IviDashboard(
            vehicle = vehicle,
            theme = theme,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        SectionCard("いまの状態 → 実エミュレータ", Modifier.padding(horizontal = 16.dp)) {
            Text(
                "下のスクリプトは現在のサンドボックス値を、実 AAOS " +
                    "エミュレータの参照 VHAL に流し込む adb コマンド列です。" +
                    "PC でコピペ実行すると、本アプリの画面と実機 IVI が" +
                    "同じ状態になります。VHAL Playground で値を変えてから" +
                    "この画面に戻ると内容も更新されます。",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp),
            )
            CodeBlock(script(vehicle))
        }

        SectionCard("仕組みと注意", Modifier.padding(horizontal = 16.dp)) {
            Text(
                "・各行は『プロパティ ID（10進）+ 値』を inject-vhal-event " +
                    "で注入。ID は VehicleProperty の実値です。\n" +
                    "・cmd car_service のサブコマンド構文は AOSP 版で差が" +
                    "あります。動かない場合は `adb shell cmd car_service -h` " +
                    "で自分の版の構文を確認してください。\n" +
                    "・読み出しは `adb shell dumpsys car_service` で確認" +
                    "（出力は長い）。\n" +
                    "・HVAC など zone 別プロパティは area 指定が必要。" +
                    "渡し方は版依存（-a / -z / 位置引数）。\n" +
                    "・本アプリ自体はネットワーク ADB をしません。学習用に" +
                    "『正しいコマンドを生成する』ところまでを担います。",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
