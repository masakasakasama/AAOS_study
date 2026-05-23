package com.example.aaosstudy.model

/**
 * AOSP が標準提供する「アセット」のカタログ。
 * 大方針＝これらを土台に、足りない所だけ作る/RRO で差し替える。
 *
 * 注: パッケージ名・場所は AOSP バージョンで変わる（特に Cluster と HVAC）。
 * reuseTier: 5=ほぼそのまま / 4=RRO だけ / 3=部分改造 / 2=雛形 / 1=テスト用。
 */
object AospAssets {

    /** アプリ層: packages/apps/Car 配下などの参照アプリ群。 */
    val apps: List<AssetRow> = listOf(
        asset(
            "Car SystemUI", "system bars / 通知 / 音量 / ユーザー切替 /(HVACパネル)",
            "packages/apps/Car/SystemUI", 4, "◎ 土台に最重要",
        ),
        asset(
            "Car Settings", "設定アプリ（項目が豊富）",
            "packages/apps/Car/Settings", 4, "◎ 項目追加が主作業",
        ),
        asset(
            "Car Media", "メディアセンター（MediaBrowserService を束ねる）",
            "packages/apps/Car/Media", 5, "★ ほぼそのまま",
        ),
        asset(
            "Car Launcher", "ホーム / アプリグリッド / 地図・メディアカード",
            "packages/apps/Car/Launcher", 3, "○ 独自置換も多い",
        ),
        asset(
            "Car Dialer", "電話 / 連絡先（Bluetooth HFP）",
            "packages/apps/Car/Dialer", 4, "◎〜○",
        ),
        asset(
            "Car Messenger", "通知読み上げ / 定型返信（SMS・MAP）",
            "packages/apps/Car/Messenger", 3, "○ 部分改造",
        ),
        asset(
            "Car Radio", "放送ラジオ（BroadcastRadio HAL 依存）",
            "packages/apps/Car/Radio (※版差)", 3, "○ HW 依存",
        ),
        asset(
            "Cluster 系", "メーター描画（DirectRenderingCluster 等）",
            "packages/apps/Car/Cluster* (※版で大変動)", 2, "△ 雛形・作り込み前提",
        ),
        asset(
            "CarDeveloperOptions", "開発者向け設定",
            "packages/apps/Car/DeveloperOptions (※)", 5, "★ そのまま",
        ),
        asset(
            "EmbeddedKitchenSinkApp", "Car API 総当たりサンプル / テスト",
            "packages/services/Car/tests/EmbeddedKitchenSinkApp", 1,
            "☆ 学習に最強・製品外",
        ),
    )

    /** API/基盤層: そのまま使う標準素材。 */
    val platform: List<AssetRow> = listOf(
        asset(
            "android.car (各 Manager)",
            "CarPropertyManager / CarUxRestrictionsManager / CarAudioManager 他",
            "packages/services/Car/car-lib", 5, "★ そのまま使う",
        ),
        asset(
            "CarService",
            "permission・subscribe 集約・dispatch・UX restriction",
            "packages/services/Car/service", 5, "★ プラットフォーム提供",
        ),
        asset(
            "参照 VHAL（default config 付き）",
            "プロパティの既定値・get/set/subscribe の参照実装",
            "hardware/interfaces/automotive/vehicle/aidl/impl", 4,
            "◎ 値・対応を差し替え",
        ),
    )
}
