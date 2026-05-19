package com.example.aaosstudy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aaosstudy.model.DiagramType

@Composable
fun DiagramView(type: DiagramType, caption: String, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            when (type) {
                DiagramType.LAYER_STACK -> LayerStack()
                DiagramType.DATA_FLOW -> DataFlow()
                DiagramType.RRO_OVERLAY -> RroOverlay()
                DiagramType.ECU_NETWORK -> EcuNetwork()
                DiagramType.HVAC_ZONES -> HvacZones()
                DiagramType.CLUSTER_VS_IVI -> ClusterVsIvi()
                DiagramType.BOOT_FLOW -> BootFlow()
                DiagramType.PERMISSION_FLOW -> PermissionFlow()
                DiagramType.BUILD_PIPELINE -> BuildPipeline()
                DiagramType.PROPERTY_ANATOMY -> PropertyAnatomy()
            }
        }
        Text(
            "図: $caption",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
private fun NodeBox(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    bg: Color = MaterialTheme.colorScheme.surface,
    fg: Color = MaterialTheme.colorScheme.onSurface,
) {
    Column(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(0.35f),
                RoundedCornerShape(8.dp),
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            title,
            color = fg,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        if (subtitle != null) {
            Text(
                subtitle,
                color = fg.copy(0.7f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ArrowDown() {
    Text(
        "▼",
        color = MaterialTheme.colorScheme.primary,
        fontSize = 14.sp,
        modifier = Modifier.padding(vertical = 3.dp),
    )
}

@Composable
private fun BiArrowDown() {
    Text(
        "▲▼",
        color = MaterialTheme.colorScheme.secondary,
        fontSize = 12.sp,
        modifier = Modifier.padding(vertical = 3.dp),
    )
}

@Composable
private fun LayerStack() {
    val layers = listOf(
        "アプリ層" to "Activity / Compose UI / ViewModel",
        "Car API ライブラリ" to "Car, CarPropertyManager …",
        "Car Service" to "権限・購読・UX制限・キャッシュ",
        "VHAL" to "VehicleProperty 契約 (id/area/access)",
        "車両 HW / エミュレータ" to "CAN・ECU / 参照 VHAL",
    )
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        layers.forEachIndexed { i, (t, s) ->
            NodeBox(
                t, s,
                bg = if (i == 0)
                    MaterialTheme.colorScheme.primary.copy(0.18f)
                else MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth(),
            )
            if (i != layers.lastIndex) BiArrowDown()
        }
        Text(
            "↑ アプリ層ほど学習コスパが高い（90%の仕事はここ）",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun DataFlow() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        NodeBox("あなたのアプリ", "setFloatProperty / callback", Modifier.fillMaxWidth())
        ArrowDown()
        NodeBox("CarPropertyManager", "Binder プロキシ", Modifier.fillMaxWidth())
        ArrowDown()
        NodeBox("CarService", "権限チェック・分配", Modifier.fillMaxWidth())
        ArrowDown()
        NodeBox("VHAL", "set/get/subscribe", Modifier.fillMaxWidth())
        ArrowDown()
        NodeBox("車両 / エミュレータ", "ECU・CAN・参照VHAL", Modifier.fillMaxWidth())
        Text(
            "値変更は逆向きに callback で全リスナーへ返る",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun RroOverlay() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            NodeBox(
                "システムアプリ APK",
                "@color/cluster_accent を参照",
                modifier = Modifier.weight(1f),
            )
            NodeBox(
                "OEM オーバーレイ APK",
                "同名リソースを別値で再定義",
                bg = MaterialTheme.colorScheme.secondary.copy(0.18f),
                modifier = Modifier.weight(1f),
            )
        }
        ArrowDown()
        NodeBox(
            "OverlayManager + リソース解決",
            "有効なオーバーレイ値を優先",
            Modifier.fillMaxWidth(),
        )
        ArrowDown()
        NodeBox(
            "実行時の画面",
            "アプリのコードは無改修のまま見た目が変わる",
            bg = MaterialTheme.colorScheme.primary.copy(0.18f),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun EcuNetwork() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        NodeBox(
            "AAOS（IVI SoC）",
            "VHAL 経由でバスへ橋渡し",
            bg = MaterialTheme.colorScheme.primary.copy(0.18f),
            modifier = Modifier.fillMaxWidth(),
        )
        ArrowDown()
        Box(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .background(MaterialTheme.colorScheme.secondary.copy(0.4f))
                .padding(2.dp)
        ) { Text("  CAN / Automotive Ethernet バス  ", fontSize = 11.sp) }
        Row(
            Modifier.fillMaxWidth().padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            NodeBox("エンジン/EV ECU", null, modifier = Modifier.weight(1f))
            NodeBox("ボディ ECU", "ドア・灯火", modifier = Modifier.weight(1f))
            NodeBox("空調 ECU", "HVAC", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun HvacZones() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(40.dp))
                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(40.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    NodeBox("運転席", "ROW_1_LEFT", modifier = Modifier.weight(1f))
                    NodeBox("助手席", "ROW_1_RIGHT", modifier = Modifier.weight(1f))
                }
                Box(Modifier.size(8.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    NodeBox("後左", "ROW_2_LEFT", modifier = Modifier.weight(1f))
                    NodeBox("後右", "ROW_2_RIGHT", modifier = Modifier.weight(1f))
                }
            }
        }
        Text(
            "ゾーン別プロパティは areaId（VehicleAreaSeat）で指定",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun ClusterVsIvi() {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            NodeBox(
                "インストルメントクラスター",
                "速度/RPM/警告灯。安全要件が高い",
                bg = MaterialTheme.colorScheme.primary.copy(0.18f),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            NodeBox(
                "センター IVI",
                "ナビ/メディア/HVAC/設定アプリ",
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun BootFlow() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        listOf(
            "OFF" to "電源断",
            "ON / ドア解錠" to "高速起動 (Suspend からの復帰)",
            "走行" to "通常稼働",
            "Garage Mode" to "ユーザー不在で OTA/更新",
            "Suspend-to-RAM" to "次回の瞬間起動のため保持",
        ).forEachIndexed { i, (t, s) ->
            NodeBox(t, s, modifier = Modifier.fillMaxWidth())
            if (i != 4) ArrowDown()
        }
    }
}

@Composable
private fun PermissionFlow() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        NodeBox("アプリ: setProperty 呼び出し", null, Modifier.fillMaxWidth())
        ArrowDown()
        NodeBox(
            "CarService: 権限チェック",
            "Car.PERMISSION_CONTROL_CAR_CLIMATE 等",
            Modifier.fillMaxWidth(),
        )
        ArrowDown()
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            NodeBox(
                "許可あり → VHAL へ書込",
                null,
                bg = MaterialTheme.colorScheme.primary.copy(0.18f),
                modifier = Modifier.weight(1f),
            )
            NodeBox(
                "署名/権限なし → SecurityException",
                null,
                bg = MaterialTheme.colorScheme.error.copy(0.18f),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun BuildPipeline() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        listOf(
            "AOSP ソース + device tree" to "manifest / lunch ターゲット",
            "ビルド (soong/make)" to "system / vendor イメージ",
            "エミュレータ or 実機" to "aaos / car target",
            "アプリ + RRO を adb install" to "挙動確認",
        ).forEachIndexed { i, (t, s) ->
            NodeBox(t, s, modifier = Modifier.fillMaxWidth())
            if (i != 3) ArrowDown()
        }
    }
}

@Composable
private fun PropertyAnatomy() {
    Column(Modifier.fillMaxWidth()) {
        NodeBox(
            "VehiclePropertyIds.HVAC_TEMPERATURE_SET",
            "1 つのプロパティ ID",
            bg = MaterialTheme.colorScheme.primary.copy(0.18f),
            modifier = Modifier.fillMaxWidth(),
        )
        ArrowDown()
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NodeBox("area", "GLOBAL / SEAT / 窓 …", modifier = Modifier.weight(1f))
            NodeBox("access", "READ / WRITE / RW", modifier = Modifier.weight(1f))
        }
        Box(Modifier.size(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NodeBox("changeMode", "STATIC/ONCHANGE/CONTINUOUS", modifier = Modifier.weight(1f))
            NodeBox("型", "Int / Float / …", modifier = Modifier.weight(1f))
        }
    }
}
