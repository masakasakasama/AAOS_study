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
                DiagramType.API_SEQUENCE -> ApiSequence()
                DiagramType.RRO_FILEMAP -> RroFileMap()
                DiagramType.AREA_ID -> AreaId()
                DiagramType.CHANGE_MODE -> ChangeMode()
                DiagramType.UXR_STATE -> UxrState()
                DiagramType.AUDIO_ZONES -> AudioZones()
                DiagramType.MULTIUSER -> MultiUser()
                DiagramType.UPDATE_FLOW -> UpdateFlow()
                DiagramType.ASSET_MAP -> AssetMap()
                DiagramType.SYSTEMUI_BARS -> SystemUiBars()
                DiagramType.MEDIA_AGG -> MediaAgg()
                DiagramType.DIALER_STACK -> DialerStack()
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

@Composable
private fun Tag(text: String, bg: Color, fg: Color) {
    Text(
        text,
        color = fg,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}

@Composable
private fun ApiSequence() {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Tag(
                "App", MaterialTheme.colorScheme.primary.copy(0.18f),
                MaterialTheme.colorScheme.onSurface,
            )
            Tag(
                "Manager", MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.onSurface,
            )
            Tag(
                "CarService", MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.onSurface,
            )
            Tag(
                "VHAL", MaterialTheme.colorScheme.secondary.copy(0.18f),
                MaterialTheme.colorScheme.onSurface,
            )
        }
        Box(Modifier.size(8.dp))
        SeqLine("① set/get", "App → Manager → CarService → VHAL", true)
        SeqLine("② subscribe 登録", "App → CarService が VHAL を購読", true)
        SeqLine("③ change イベント", "VHAL → CarService → 全 listener へ dispatch", false)
        Text(
            "set の結果も change イベントとして②の経路で返ってくる",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
private fun SeqLine(label: String, desc: String, forward: Boolean) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            if (forward) "→" else "←",
            color = if (forward) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.secondary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 6.dp),
        )
        Column {
            Text(
                label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                desc,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(0.75f),
            )
        }
    }
}

@Composable
private fun RroFileMap() {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            NodeBox(
                "システムアプリ APK",
                "res/values/colors.xml\naccent = #1A73E8",
                modifier = Modifier.weight(1f),
            )
            NodeBox(
                "OEM Overlay APK",
                "res/values/colors.xml\naccent = #E08A1E",
                bg = MaterialTheme.colorScheme.secondary.copy(0.18f),
                modifier = Modifier.weight(1f),
            )
        }
        ArrowDown()
        NodeBox(
            "OverlayManager（同名リソースを解決）",
            "frameworks/base/.../server/om/",
            modifier = Modifier.fillMaxWidth(),
        )
        ArrowDown()
        NodeBox(
            "実行時に accent = #E08A1E",
            "アプリのコードは @color/accent のまま無改修",
            bg = MaterialTheme.colorScheme.primary.copy(0.18f),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun AreaId() {
    Column(Modifier.fillMaxWidth()) {
        NodeBox(
            "areaId = どの“場所”の値か",
            "GLOBAL なら 0、ゾーン別なら VehicleArea* のビット",
            bg = MaterialTheme.colorScheme.primary.copy(0.18f),
            modifier = Modifier.fillMaxWidth(),
        )
        ArrowDown()
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NodeBox("GLOBAL", "areaId = 0\n例: 車速", modifier = Modifier.weight(1f))
            NodeBox(
                "VehicleAreaSeat",
                "ROW_1_LEFT 等\n例: HVAC 温度",
                bg = MaterialTheme.colorScheme.secondary.copy(0.14f),
                modifier = Modifier.weight(1f),
            )
        }
        Box(Modifier.size(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NodeBox("VehicleAreaWindow", "窓・デフロスト", modifier = Modifier.weight(1f))
            NodeBox("VehicleAreaDoor", "ドア・ミラー", modifier = Modifier.weight(1f))
        }
        Text(
            "1 つの propertyId が複数 areaId の値を持つ（座席ごとの温度など）",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
private fun ChangeMode() {
    Column(Modifier.fillMaxWidth()) {
        NodeBox(
            "changeMode = 値の出方",
            "subscribe 時の挙動が変わる",
            bg = MaterialTheme.colorScheme.primary.copy(0.18f),
            modifier = Modifier.fillMaxWidth(),
        )
        ArrowDown()
        NodeBox("STATIC", "ほぼ不変。一度読めば十分（例: 燃料容量）", modifier = Modifier.fillMaxWidth())
        Box(Modifier.size(6.dp))
        NodeBox("ON_CHANGE", "変化したときだけ通知（例: ギア・ドア）", modifier = Modifier.fillMaxWidth())
        Box(Modifier.size(6.dp))
        NodeBox(
            "CONTINUOUS",
            "一定レートで連続通知（例: 車速・RPM）。SENSOR_RATE_* 指定",
            bg = MaterialTheme.colorScheme.secondary.copy(0.14f),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun UxrState() {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Tag(
                "停車中", MaterialTheme.colorScheme.primary.copy(0.18f),
                MaterialTheme.colorScheme.onSurface,
            )
            Box(Modifier.size(6.dp))
            NodeBox(
                "フル UI",
                "長文・動画・キーボード OK",
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Column(Modifier.weight(1f)) {
            Tag(
                "走行中", MaterialTheme.colorScheme.error.copy(0.20f),
                MaterialTheme.colorScheme.onSurface,
            )
            Box(Modifier.size(6.dp))
            NodeBox(
                "制限 UI",
                "項目数↓・入力制限\nrequiresDistractionOptimization",
                bg = MaterialTheme.colorScheme.error.copy(0.10f),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun AudioZones() {
    Column(Modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(40.dp))
                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(40.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    NodeBox(
                        "Zone 0 (前席)",
                        "ナビ音声・メディア",
                        bg = MaterialTheme.colorScheme.primary.copy(0.14f),
                        modifier = Modifier.weight(1f),
                    )
                }
                Box(Modifier.size(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    NodeBox("Zone 1 (後席)", "別音源 (RSE)", modifier = Modifier.weight(1f))
                }
            }
        }
        Text(
            "zone ごとに volume と audio focus を独立管理（CarAudio）",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
private fun MultiUser() {
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NodeBox(
                "User A",
                "設定/アプリ/履歴",
                bg = MaterialTheme.colorScheme.primary.copy(0.14f),
                modifier = Modifier.weight(1f),
            )
            NodeBox("User B", "完全に分離", modifier = Modifier.weight(1f))
        }
        ArrowDown()
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NodeBox("Cluster 画面", "display 0", modifier = Modifier.weight(1f))
            NodeBox("Center IVI", "display 1", modifier = Modifier.weight(1f))
            NodeBox("後席 RSE", "display 2", modifier = Modifier.weight(1f))
        }
        Text(
            "ユーザー切替で空間（設定/アプリ）が入れ替わり、画面は複数同時",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
private fun SystemUiBars() {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        NodeBox(
            "Status Bar（上）",
            "時刻 / 接続 / 通知アイコン",
            bg = MaterialTheme.colorScheme.primary.copy(0.18f),
            modifier = Modifier.fillMaxWidth(),
        )
        Box(Modifier.size(6.dp))
        NodeBox(
            "アプリ表示領域",
            "Launcher / 各アプリ",
            modifier = Modifier.fillMaxWidth(),
        )
        Box(Modifier.size(6.dp))
        NodeBox(
            "Nav Bar（下）",
            "ホーム / 戻る / HVAC ショートカット等",
            bg = MaterialTheme.colorScheme.secondary.copy(0.16f),
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            "バーの有無・高さ・要素は dimens/レイアウト → RRO で差し替え",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
private fun MediaAgg() {
    Column(Modifier.fillMaxWidth()) {
        NodeBox(
            "Car Media（センター）",
            "共通 UI で複数アプリを横断",
            bg = MaterialTheme.colorScheme.primary.copy(0.18f),
            modifier = Modifier.fillMaxWidth(),
        )
        ArrowDown()
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NodeBox("音楽アプリ A", "MediaBrowserService", modifier = Modifier.weight(1f))
            NodeBox("Podcast B", "MediaBrowserService", modifier = Modifier.weight(1f))
            NodeBox("Radio C", "MediaBrowserService", modifier = Modifier.weight(1f))
        }
        Text(
            "各アプリは MediaBrowserService を実装するだけで Car Media に載る",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
private fun DialerStack() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        NodeBox("Car Dialer（UI）", "発信 / 履歴 / 連絡先", bg = MaterialTheme.colorScheme.primary.copy(0.18f), modifier = Modifier.fillMaxWidth())
        ArrowDown()
        NodeBox("Bluetooth HFP / PBAP", "通話と電話帳のプロファイル", modifier = Modifier.fillMaxWidth())
        ArrowDown()
        NodeBox("スマホ（接続中）", "実際の回線はスマホ側", bg = MaterialTheme.colorScheme.secondary.copy(0.16f), modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun AssetMap() {
    Column(Modifier.fillMaxWidth()) {
        NodeBox(
            "アプリ層: 参照アプリ（packages/apps/Car/*）",
            "SystemUI / Settings / Media / Launcher / Dialer …",
            bg = MaterialTheme.colorScheme.primary.copy(0.18f),
            modifier = Modifier.fillMaxWidth(),
        )
        ArrowDown()
        NodeBox(
            "リソース層: overlay 可能なリソース・car テーマ",
            "OEM は RRO で値だけ差し替え（無改修）",
            modifier = Modifier.fillMaxWidth(),
        )
        ArrowDown()
        NodeBox(
            "API/基盤層: android.car・CarService・参照 VHAL",
            "基本そのまま利用（拡張は VHAL/permission）",
            bg = MaterialTheme.colorScheme.secondary.copy(0.16f),
            modifier = Modifier.fillMaxWidth(),
        )
        Box(Modifier.size(8.dp))
        Text(
            "再利用度: ★そのまま ◎RROだけ ○部分改造 △雛形 ☆テスト用",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.secondary,
        )
        Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Tag("★/◎ Media・Settings・SystemUI", MaterialTheme.colorScheme.primary.copy(0.18f), MaterialTheme.colorScheme.onSurface)
        }
        Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Tag("△ Cluster（作り込み）", MaterialTheme.colorScheme.error.copy(0.16f), MaterialTheme.colorScheme.onSurface)
            Tag("☆ KitchenSink（学習）", MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun UpdateFlow() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        listOf(
            "push" to "GitHub にコミットを push",
            "CI build" to "Actions が APK を自動ビルド",
            "release latest" to "app-debug.apk を rolling release に添付",
            "アプリが確認" to "起動時に SHA を比較",
            "更新ダイアログ" to "新しければ取得→インストール",
        ).forEachIndexed { i, (t, s) ->
            NodeBox(
                t, s,
                bg = if (i == 4) MaterialTheme.colorScheme.primary.copy(0.18f)
                else MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth(),
            )
            if (i != 4) ArrowDown()
        }
    }
}
