package com.example.aaosstudy.model

/**
 * AOSP 参照アプリの個別深掘り。各アプリで「AOSP が何をくれるか」「どこを
 * RRO で差し替えるか」「OEM が足す所」を、再利用度の視点で見る。
 *
 * 注: パッケージ名・場所は AOSP バージョンで変わる（特に Cluster / HVAC）。
 * 用語方針: subscribe / dispatch / permission / signing は英語のまま。
 */
object ReferenceAppDeepDive {

    private val systemui = Lesson(
        id = "d1", title = "深掘り: Car SystemUI（土台に最重要）",
        minutes = 13,
        blocks = listOf(
            h("画面の骨格そのもの"),
            dia(DiagramType.SYSTEMUI_BARS, "Status Bar / アプリ領域 / Nav Bar"),
            p(
                "Car SystemUI は status bar・nav bar・通知・音量・ユーザー" +
                    "切替、版によっては HVAC パネルまで担う『画面の枠』。" +
                    "OEM はこれを土台に、RRO で寸法・要素・色を差し替えます。",
            ),
            fileMap(
                "Car SystemUI のファイル対応",
                link(
                    "本体（bar/通知/音量のロジックとレイアウト）",
                    "packages/apps/Car/SystemUI",
                    "frameworks の SystemUI を車向けに拡張。",
                ),
                link(
                    "バー高さ・表示要素の寸法/真偽値",
                    "同 res/values/（dimens.xml / config）",
                    "RRO の主な overlay 対象。",
                ),
            ),
            b(
                "RRO で変える: バー高さ / 表示要素 / 色 / アイコン",
                "OEM が足す: 独自クイック設定 / ブランド時計など",
                "再利用度: ◎（骨格はそのまま使うのが定石）",
            ),
            quiz(
                "OEM がナビバーの高さを変える主手段は？",
                listOf("SystemUI を fork して再ビルド", "RRO で dimens を overlay", "不可能"),
                1,
                "寸法は dimens としてリソース化されており RRO で差し替える。",
            ),
        ),
    )

    private val settings = Lesson(
        id = "d2", title = "深掘り: Car Settings（項目を足す）",
        minutes = 11,
        blocks = listOf(
            h("設定の参照実装。項目追加が主作業"),
            p(
                "Car Settings は Wi-Fi・Bluetooth・表示・ユーザーなど" +
                    "豊富な設定画面を提供。OEM は項目の追加/削除と意匠の" +
                    "差し替えが中心で、ゼロから作ることは稀です。",
            ),
            fileMap(
                "Car Settings のファイル対応",
                link(
                    "設定アプリ本体（各 Preference 画面）",
                    "packages/apps/Car/Settings",
                    "Preference 階層と表示ロジック。",
                ),
                link(
                    "設定項目の構成 XML",
                    "同 res/xml/（preference 階層）",
                    "項目の出し分けは RRO/設定で調整。",
                ),
            ),
            b(
                "RRO で変える: 表示項目の有無 / 文言 / 色",
                "OEM が足す: 車種固有設定（ドライブモード等）",
                "再利用度: ◎",
            ),
            quiz(
                "Car Settings に対する OEM の典型作業は？",
                listOf("全画面を自作", "項目の追加/削除と意匠差し替え", "使わず独自設定アプリ必須"),
                1,
                "豊富な参照実装を土台に、差分（項目・意匠）を当てる。",
            ),
        ),
    )

    private val media = Lesson(
        id = "d3", title = "深掘り: Car Media（ほぼそのまま）",
        minutes = 12,
        blocks = listOf(
            h("自分で書かず、3rd party を束ねる枠"),
            dia(DiagramType.MEDIA_AGG, "MediaBrowserService を実装したアプリが載る"),
            p(
                "Car Media は共通 UI で複数のメディアアプリを横断します。" +
                    "各メディアアプリは MediaBrowserService を実装するだけで" +
                    "この枠に載るため、OEM/開発者の改変は最小です。",
            ),
            fileMap(
                "Car Media のファイル対応",
                link(
                    "メディアセンター UI 本体",
                    "packages/apps/Car/Media",
                    "MediaBrowser/MediaSession を共通 UI で表示。",
                ),
                link(
                    "あなたのメディアアプリ側",
                    "MediaBrowserServiceCompat を実装",
                    "これだけで Car Media に載る（個別 UI 不要）。",
                ),
            ),
            b(
                "RRO で変える: 色・アイコン程度",
                "開発者が足す: 自分のアプリに MediaBrowserService",
                "再利用度: ★（ほぼそのまま）",
            ),
            quiz(
                "自作の音楽アプリを Car Media に載せるには？",
                listOf("Car Media を改造", "MediaBrowserService を実装する", "RRO を書く"),
                1,
                "共通 UI 側は触らず、アプリが MediaBrowserService を出すだけ。",
            ),
        ),
    )

    private val launcher = Lesson(
        id = "d4", title = "深掘り: Car Launcher（置換も多い）",
        minutes = 10,
        blocks = listOf(
            h("ホーム。RRO か、独自置換か"),
            p(
                "Car Launcher はアプリグリッドに加え、地図やメディアの" +
                    "カードを並べる『起点』。RRO で足りる場合もあれば、OEM が" +
                    "独自ランチャーに置換する場合も多い領域です。",
            ),
            fileMap(
                "Car Launcher のファイル対応",
                link(
                    "ランチャー本体（カード/グリッド）",
                    "packages/apps/Car/Launcher",
                    "ホーム画面の構成ロジック。",
                ),
                link(
                    "カード構成・列数などのリソース",
                    "同 res/values/",
                    "RRO で調整、または独自実装に差し替え。",
                ),
            ),
            b(
                "RRO で変える: 列数 / カード構成 / 色",
                "OEM が足す/置換: 独自ホーム体験",
                "再利用度: ○〜◎（置換判断が分かれる）",
            ),
            quiz(
                "Car Launcher の扱いとして現実的なのは？",
                listOf("必ず自作", "RRO で足りるか独自置換かを要件で判断", "改変不可"),
                1,
                "ホームは差別化点になりやすく、RRO/置換の判断が分かれる。",
            ),
        ),
    )

    private val dialer = Lesson(
        id = "d5", title = "深掘り: Car Dialer（Bluetooth 前提）",
        minutes = 10,
        blocks = listOf(
            h("回線はスマホ。UI と接続が役割"),
            dia(DiagramType.DIALER_STACK, "Dialer → Bluetooth HFP/PBAP → スマホ"),
            p(
                "Car Dialer は発信・履歴・連絡先の UI を提供し、実際の通話は" +
                    "Bluetooth HFP、電話帳は PBAP でスマホと連携します。" +
                    "ハードと profile に依存するため、UI 改変は RRO 中心。",
            ),
            fileMap(
                "Car Dialer のファイル対応",
                link(
                    "Dialer 本体（UI と通話制御）",
                    "packages/apps/Car/Dialer",
                    "Bluetooth プロファイル経由で発着信。",
                ),
                link(
                    "Bluetooth profile（HFP/PBAP）",
                    "Bluetooth stack（フレームワーク側）",
                    "通話・電話帳の実体はここ。",
                ),
            ),
            b(
                "RRO で変える: 配色・アイコン・レイアウト寸法",
                "依存: Bluetooth 接続中のスマホ",
                "再利用度: ◎〜○",
            ),
            quiz(
                "Car Dialer の通話の実体はどこ？",
                listOf("車載 SIM が必須", "Bluetooth 接続したスマホ", "クラウド経由"),
                1,
                "多くの構成で回線はスマホ側、車は HFP/PBAP で連携する。",
            ),
        ),
    )

    private val cluster = Lesson(
        id = "d6", title = "深掘り: Cluster（雛形・作り込み前提）",
        minutes = 12,
        blocks = listOf(
            h("安全要件が高く、作り込みが要る領域"),
            dia(DiagramType.CLUSTER_VS_IVI, "クラスターは別ディスプレイ・別アプリ"),
            p(
                "Cluster（メーター）の参照実装（DirectRenderingCluster や " +
                    "ClusterHomeSample 等）は版差が大きく、多くは雛形寄り。" +
                    "速度・ギア・警告灯を確実・低遅延で出す必要があり、OEM が" +
                    "作り込む前提の領域です。",
            ),
            fileMap(
                "Cluster のファイル対応",
                link(
                    "参照クラスター/サンプル群",
                    "packages/apps/Car/Cluster*（※版で大きく変動）",
                    "DirectRenderingCluster / ClusterHomeSample 等。",
                ),
                link(
                    "クラスターへ情報を渡す API",
                    "android.car.cluster.*（car-lib）",
                    "ナビ等の情報をクラスター面へ。",
                ),
            ),
            b(
                "RRO で変える: 意匠（色・目盛り）",
                "OEM が作り込む: レイアウト・アニメ・安全要件対応",
                "再利用度: △（雛形。作り込み前提）",
            ),
            tryIt(
                "scenarios", "Scenario でクラスター意匠を比較",
                "Nordic / Classic などで意匠差を体感できます。",
            ),
            quiz(
                "Cluster の再利用度が低めな理由は？",
                listOf("AOSP に何も無いから", "雛形寄りで安全要件の作り込みが要るから", "RRO 不可だから"),
                1,
                "参照はあるが版差・要件が大きく、作り込み前提になりやすい。",
            ),
        ),
    )

    val module = Module(
        "参照アプリ深掘り",
        "主要な参照アプリを個別に。何が来て、どこを RRO/作り込みするか",
        listOf(systemui, settings, media, launcher, dialer, cluster),
    )
}
