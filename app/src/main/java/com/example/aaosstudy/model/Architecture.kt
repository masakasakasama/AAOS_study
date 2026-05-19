package com.example.aaosstudy.model

/** AAOS スタックの各層（上＝あなたのアプリ、下＝ハードウェア）。 */
data class ArchLayer(
    val name: String,
    val oneLiner: String,
    val detail: String,
    val examples: List<String>,
)

object Architecture {
    val layers = listOf(
        ArchLayer(
            name = "1. あなたのアプリ（アプリ層）",
            oneLiner = "普通の Android アプリ。ただし Car API を呼ぶ。",
            detail = "自分のプロセスで動作。UI は Jetpack/Compose、車両" +
                "データは Car ライブラリを使う。OEM の作業の約9割は" +
                "ここ：HVAC・クラスター・メディア・設定。AAOS の特別" +
                "ビルドが無くても学べる層。",
            examples = listOf("Activity / Service", "Compose UI", "ViewModel"),
        ),
        ArchLayer(
            name = "2. Car API ライブラリ（android.car）",
            oneLiner = "クライアント側 SDK：Car と各種 *Manager。",
            detail = "Car.createCar() が CarService に bind。各 Manager" +
                "（CarPropertyManager・CarHvacManager・CarUxRestrictions…）" +
                "は Binder 越しに呼ぶ薄いプロキシ。あなたがコードを" +
                "書く境界。",
            examples = listOf("Car", "CarPropertyManager", "CarHvacManager"),
        ),
        ArchLayer(
            name = "3. Car Service（システムサービス）",
            oneLiner = "常駐の特権サービス。方針（ポリシー）の層。",
            detail = "車両権限の検査、購読管理、走行中の UX 制限、" +
                "プロパティのキャッシュ、変更イベントを全アプリへ" +
                "配信する。",
            examples = listOf("CarPropertyService", "CarPowerService"),
        ),
        ArchLayer(
            name = "4. Vehicle HAL（VHAL）",
            oneLiner = "Android と車両の安定した契約。",
            detail = "VehicleProperty 群を id + area + access + " +
                "changeMode で定義。ベンダーが実装し、エミュレータは" +
                "学習用の参照 VHAL を提供する。",
            examples = listOf("VehiclePropertyIds", "areaId", "アクセス種別"),
        ),
        ArchLayer(
            name = "5. 車両ハードウェア / エミュレータ",
            oneLiner = "実 ECU をバスで、または AAOS エミュレータ。",
            detail = "実機では VHAL が CAN/車載ネットワークへ橋渡し。" +
                "学習ではエミュレータの VHAL に車なしで値を注入できる。",
            examples = listOf("CAN バス", "AAOS エミュレータ", "参照 VHAL"),
        ),
    )

    val rro = """
RRO（Runtime Resource Overlay）— OEM がアプリを fork せず再スキンする方法:

1. システムアプリはリソースを通常通り参照する: @color/cluster_accent。
2. OEM は対象パッケージを狙う小さなオーバーレイ APK を別に配布。
3. オーバーレイがそのリソース名を新しい値で再定義する。
4. OverlayManager が有効化し、リソース解決が実行時にオーバーレイ値を
   返す。アプリのコード/バイトコードは無改修のまま。

アプリ層での要点: リソース名で書き、色・文字・寸法を直書きしない。
そうすれば、どの OEM でもあなたのアプリをブランド化できる。
    """.trim()
}
