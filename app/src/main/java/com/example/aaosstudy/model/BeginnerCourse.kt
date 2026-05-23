package com.example.aaosstudy.model

/**
 * 初学者向けコース。AAOS の世界観を、アプリ層を中心に体で理解する。
 */
object BeginnerCourse {

    private val l1 = Lesson(
        id = "b1", title = "AAOS とは何か（普通の Android との違い）",
        minutes = 12,
        blocks = listOf(
            h("AAOS = クルマに組み込まれた Android"),
            p(
                "Android Automotive OS (AAOS) は、スマホの Android と同じ" +
                    "AOSP をベースに、車載 IVI（センターディスプレイ）や" +
                    "メーターに最適化したものです。Android Auto（スマホを" +
                    "映すだけ）とは別物で、AAOS は車そのものの OS です。",
            ),
            dia(DiagramType.CLUSTER_VS_IVI, "クラスターと IVI は別アプリ・別画面"),
            dia(DiagramType.LAYER_STACK, "全体像の予告：アプリ → … → 車両 の5層"),
            h("スマホ Android と何が違う？"),
            b(
                "車両データ（速度・燃料・空調…）に触れる Car API がある",
                "運転中の操作制限（UX Restrictions）が OS レベルで効く",
                "OEM がアプリを改修せず見た目を変える RRO 前提の作り",
                "起動が一瞬（Suspend/Resume が基本、毎回コールドではない）",
                "マルチユーザー・マルチディスプレイが標準で重要",
            ),
            note(
                "本アプリは実 HAL には接続しません。挙動を忠実に模した" +
                    "サンドボックスで『操作 → IVI 画面の変化』を直感的に" +
                    "つかむことを目的にしています。",
            ),
            term(
                "IVI = In-Vehicle Infotainment。ナビ/メディア/設定などの" +
                    "センター画面システム。",
            ),
            quiz(
                "Android Auto と AAOS の関係として正しいのは？",
                listOf(
                    "同じもの。呼び方が違うだけ",
                    "Android Auto はスマホ投影、AAOS は車載 OS 本体",
                    "AAOS はスマホ用、Android Auto は車用",
                ),
                1,
                "Android Auto はスマホ画面を車に映す仕組み。AAOS は車に" +
                    "組み込まれた OS そのもので、ネット接続もアプリも車側。",
            ),
            tryIt(
                "vhal", "VHAL Playground を触ってみる",
                "速度や燃料を動かすと IVI クラスターがどう変わるか、" +
                    "まず体感してから次へ進むと理解が早いです。",
            ),
        ),
    )

    private val l2 = Lesson(
        id = "b2", title = "クルマのソフト全体像（ECU・CAN・IVI・クラスター）",
        minutes = 12,
        blocks = listOf(
            h("車は何十個もの小さなコンピュータの集合"),
            p(
                "エンジン、ブレーキ、ドア、空調…それぞれに ECU（小型の" +
                    "制御コンピュータ）があり、CAN などの車内ネットワークで" +
                    "つながっています。AAOS が動く IVI もこのネットワークの" +
                    "一員で、VHAL を通じて値を読み書きします。",
            ),
            dia(DiagramType.ECU_NETWORK, "AAOS は CAN/Ethernet の先の世界と VHAL で会話"),
            dia(DiagramType.CLUSTER_VS_IVI, "同じ AAOS 上のクラスターとセンター IVI"),
            h("学習者が押さえる構図"),
            b(
                "クラスター = 速度計など、安全要件が高い表示専用に近い画面",
                "IVI = アプリが載るリッチな画面（あなたの主戦場）",
                "両者は別ディスプレイ・別アプリだが同じ AAOS 上で動く",
            ),
            case(
                "Volvo / Polestar",
                "Polestar 2（2020年）は Google ビルトインの AAOS を" +
                    "量産車で世界初採用。Volvo Cars はクラスターも AAOS 化し、" +
                    "ナビをメーター内に出すなど IVI とクラスターを統合 UX に" +
                    "しています。『1 つの OS で 2 画面』の好例です。",
            ),
            quiz(
                "クラスター画面の特徴として最も適切なのは？",
                listOf(
                    "自由にサードパーティアプリを載せる場所",
                    "速度計など安全性要件が高い表示が中心",
                    "実は IVI と同じ 1 枚の画面",
                ),
                1,
                "クラスターは走行に直結する情報表示が中心で要件が高い。" +
                    "アプリの主戦場はリッチな IVI 側です。",
            ),
        ),
    )

    private val l3 = Lesson(
        id = "b3", title = "AAOS のレイヤー構造（アプリ層が主戦場）",
        minutes = 14,
        blocks = listOf(
            h("上から下へ 5 層"),
            dia(DiagramType.LAYER_STACK, "アプリ → Car API → CarService → VHAL → 車両"),
            p(
                "OEM 開発の体感の 9 割は最上段『アプリ層』です。実機の" +
                    "特別ビルドが無くても、Car API の使い方・リソース設計・" +
                    "UX 制限対応の学習はここで完結します。",
            ),
            fileMap(
                "各層が AOSP のどこにあるか",
                link(
                    "あなたのアプリ (app/src/...)",
                    "android.car.* （Car API ライブラリ）",
                    "アプリは Car API を import して使う。実体は次行。",
                ),
                link(
                    "android.car.Car / CarPropertyManager",
                    "packages/services/Car/car-lib/src/android/car/",
                    "クライアント側 SDK。Binder で CarService を呼ぶ。",
                ),
                link(
                    "CarService (CarPropertyService 等)",
                    "packages/services/Car/service/src/com/android/car/",
                    "permission・subscribe・UX 制限を司る常駐システムサービス。",
                ),
                link(
                    "VehiclePropertyIds 契約",
                    "hardware/interfaces/automotive/vehicle/ (AIDL)",
                    "Android と車両の安定インターフェース定義。",
                ),
            ),
            note(
                "『どのファイルがどこに紐づくか』はこのコース全体で" +
                    "繰り返し出てきます。最初は層の名前と場所だけ覚えれば OK。",
            ),
            tryIt(
                "arch", "Architecture Map で層をタップ",
                "各層を開いて、役割と代表クラスを確認しましょう。",
            ),
            quiz(
                "OEM のアプリ開発で最も時間を使う層は？",
                listOf("VHAL", "アプリ層", "Linux カーネル"),
                1,
                "アプリ層が主戦場。だからこのコースもアプリ層を優先します。",
            ),
        ),
    )

    private val l4 = Lesson(
        id = "b4", title = "はじめての Car アプリ（Car.createCar）",
        minutes = 13,
        blocks = listOf(
            h("Car API の入口は必ず Car オブジェクト"),
            p(
                "車両データに触るには、まず Car インスタンスを作り、" +
                    "そこから目的の Manager を取り出します。使い終わったら" +
                    "必ず disconnect します。",
            ),
            code(
                """
val car = Car.createCar(context)
val props = car.getCarManager(Car.PROPERTY_SERVICE)
        as CarPropertyManager
// ... 使う ...
car.disconnect()
                """,
            ),
            dia(DiagramType.DATA_FLOW, "アプリ → Manager → CarService → VHAL → 車両"),
            dia(DiagramType.API_SEQUENCE, "set/get と subscribeの往復"),
            fileMap(
                "この 1 行が何に紐づくか",
                link(
                    "Car.createCar(context)",
                    "packages/services/Car/car-lib/.../Car.java",
                    "ここで CarService へ bind する。",
                ),
                link(
                    "Car.PROPERTY_SERVICE 文字列キー",
                    "CarPropertyManager（同 car-lib 内）",
                    "キーに対応する Manager 実体が返る。",
                ),
            ),
            warn(
                "AndroidManifest で <uses-library android:name=" +
                    "\"android.car\"/> が必要。さらに書込み系は車両 permissionが要る" +
                    "（中級で詳説）。",
            ),
            tryIt(
                "api", "Car API Explorer を見る",
                "接続・速度subscribe・HVAC 設定など、実コードと呼び出し連鎖を" +
                    "プロパティ別に確認できます。",
            ),
            quiz(
                "Car API を使い終わったら必ず何をする？",
                listOf("car.finish()", "car.disconnect()", "何もしなくてよい"),
                1,
                "disconnect() で CarService への接続を解放する。" +
                    "Activity の onDestroy 等で呼ぶのが定石。",
            ),
        ),
    )

    private val l5 = Lesson(
        id = "b5", title = "リソースと XML の基礎（なぜ色を直書きしないか）",
        minutes = 13,
        blocks = listOf(
            h("色・文字・寸法はコードに埋め込まない"),
            dia(DiagramType.RRO_OVERLAY, "リソース名そのまま、値だけ overlay で差し替え"),
            dia(DiagramType.RRO_FILEMAP, "同名リソースがファイル単位でどう紐づくか"),
            p(
                "AAOS では OEM がアプリを再コンパイルせずに見た目だけ" +
                    "差し替えます。だから色や文字列はリソース名で参照し、" +
                    "実値は XML 側に置きます。これが RRO（次レッスン）の前提。",
            ),
            code(
                """
<!-- res/values/colors.xml -->
<resources>
    <color name="cluster_accent">#1A73E8</color>
</resources>
                """,
                lang = "xml",
            ),
            code(
                """
// 直書き ✗
val c = Color(0xFF1A73E8)
// リソース参照 ○（OEM が上書き可能）
val c = colorResource(R.color.cluster_accent)
                """,
            ),
            defaults(
                "AOSP のリソース既定",
                def(
                    "アプリ既定テーマ/色",
                    "AOSP リファレンス値（OEM 無指定時に使われる）",
                    "各アプリの res/values/ + frameworks/base/core/res/",
                ),
                def(
                    "framework 全体の設定既定",
                    "config_* キーの初期値",
                    "frameworks/base/core/res/res/values/config.xml",
                ),
            ),
            tryIt(
                "rro", "RRO Theme Lab で色を変える",
                "リソース名はそのまま、値だけ変えると同じ UI がどう" +
                    "変わるかを体感してください。",
            ),
            quiz(
                "色をコードに直書きするとなぜ困る？",
                listOf(
                    "コンパイルが遅くなる",
                    "OEM が再コンパイルせずに見た目を変えられない",
                    "色が表示されない",
                ),
                1,
                "RRO はリソースを実行時に差し替える仕組み。直書きは" +
                    "差し替え対象にならず、OEM がブランド化できない。",
            ),
        ),
    )

    private val l6 = Lesson(
        id = "b6", title = "ケーススタディ：Polestar 2（世界初の量産 AAOS）",
        minutes = 11,
        blocks = listOf(
            h("実車で AAOS はどう使われたか"),
            dia(DiagramType.CLUSTER_VS_IVI, "Polestar/Volvo はクラスターと IVI を1つの OS で統合"),
            dia(DiagramType.RRO_OVERLAY, "ブランド色/ロゴは RRO で差し替え"),
            case(
                "Polestar 2 / Volvo Cars",
                "Polestar 2 は 2020 年、Google ビルトイン（Google マップ・" +
                    "アシスタント・Play）を載せた AAOS を量産車で世界初採用。" +
                    "Volvo はその後ライン全体へ展開し、メーター内ナビなど" +
                    "クラスターと IVI を 1 つの OS で統合しました。",
            ),
            p(
                "学習者の観点で重要なのは『OEM はアプリを書き換えず、" +
                    "RRO とプロパティ設定でブランド体験を作っている』点。" +
                    "あなたが書くアプリも同じ作法に従えば OEM に載ります。",
            ),
            b(
                "ブランド色/ロゴ/アイコン → RRO で差し替え",
                "速度・電費・空調 → VHAL プロパティで取得",
                "走行中の制限 → CarUxRestrictions に従う",
            ),
            tryIt(
                "scenarios", "Scenario Presets で雰囲気を比較",
                "『Nordic minimal』など、北欧的なクラスター表現を" +
                    "ワンタップで再現して見比べられます。",
            ),
            quiz(
                "Polestar 2 が AAOS 史で特筆される理由は？",
                listOf(
                    "最初の Android Auto 対応車",
                    "Google ビルトイン AAOS を量産車で世界初採用",
                    "AAOS を使わない独自 OS だった",
                ),
                1,
                "スマホ投影の Android Auto ではなく、車載 OS 本体として" +
                    "の AAOS + Google サービスを量産で初めて載せた。",
            ),
        ),
    )

    private val l7 = Lesson(
        id = "b7", title = "用語ミニ辞典 + 初学者総まとめ",
        minutes = 9,
        blocks = listOf(
            h("ここまでの用語を一気に整理"),
            dia(DiagramType.LAYER_STACK, "復習：5層と用語の位置関係"),
            dia(DiagramType.DATA_FLOW, "復習：値が流れる道筋"),
            term("VHAL: 車両と Android の安定インターフェース。"),
            term("CarPropertyManager: アプリ側から車両値を読み書きする窓口。"),
            term("RRO: 実行時にリソースを差し替える OEM 向け仕組み。"),
            term("Cluster: 速度計などのメーター画面。"),
            term("UX Restrictions: 走行中の操作制限ポリシー。"),
            p("中級では CarPropertyManager と RRO を実装レベルで掘ります。"),
            quiz(
                "次のうち『アプリ側の窓口クラス』は？",
                listOf("VHAL", "CarPropertyManager", "CAN バス"),
                1,
                "VHAL は契約、CAN は物理ネットワーク。アプリが触るのは" +
                    "CarPropertyManager。",
            ),
        ),
    )

    private val l8 = Lesson(
        id = "b8", title = "AAOS エミュレータを入手して起動する",
        minutes = 12,
        blocks = listOf(
            h("実機が無くても学べる"),
            dia(DiagramType.BUILD_PIPELINE, "ソース → ビルド → エミュレータ → adb install"),
            p(
                "Android Studio の Device Manager で『Automotive』系の" +
                    "システムイメージを選ぶと、AAOS エミュレータが使えます。" +
                    "本アプリで概念をつかみ、エミュレータで実物を触る、の" +
                    "二段構えが最短ルートです。",
            ),
            b(
                "Android Studio → Device Manager → Create device",
                "Category で Automotive を選択",
                "Google APIs / Play 付きイメージを選ぶ",
                "起動後 `adb devices` で認識を確認",
            ),
            tryIt(
                "adb", "ADB ブリッジを見る",
                "本アプリの状態を、起動したエミュレータへ流し込む" +
                    "コマンドが自動生成されます。",
            ),
            quiz(
                "AAOS エミュレータを作るとき選ぶカテゴリは？",
                listOf("Phone", "Automotive", "TV"),
                1,
                "Automotive カテゴリの car 系イメージが AAOS。",
            ),
        ),
    )

    private val l9 = Lesson(
        id = "b9", title = "Android Auto と AAOS の境界を一枚で",
        minutes = 9,
        blocks = listOf(
            h("混同しやすい 2 つを整理"),
            dia(DiagramType.CLUSTER_VS_IVI, "投影 (Auto) と OS 本体 (AAOS)"),
            b(
                "Android Auto: スマホ画面を車に投影。アプリはスマホ側",
                "AAOS: 車に組込んだ OS。アプリは車側で動く",
                "学習対象は AAOS（このアプリも AAOS を扱う）",
            ),
            quiz(
                "アプリが車側で動くのはどっち？",
                listOf("Android Auto", "AAOS", "どちらも同じ"),
                1,
                "AAOS は OS 本体。Auto は投影でアプリはスマホ側。",
            ),
        ),
    )

    private val l10 = Lesson(
        id = "b10", title = "このアプリ自体の自動更新の仕組み",
        minutes = 7,
        blocks = listOf(
            h("学んだ流れが、このアプリの更新にも使われている"),
            dia(DiagramType.UPDATE_FLOW, "push → CI build → release → アプリが確認 → 更新"),
            p(
                "このアプリは Play Store 外の sideload（手動配布）なので、" +
                    "GitHub Actions が push のたびに APK を build し、" +
                    "rolling release に添付します。アプリは起動時に最新の " +
                    "commit SHA を比較し、新しければダイアログを出します。",
            ),
            b(
                "ファイル名は常に app-debug.apk（中身だけ差し替え）",
                "見分けはホーム最下部の『ビルド 0.1.N+sha』表示で",
                "実インストールは Android 仕様でユーザー確認が必須",
            ),
            note(
                "完全無確認の自動更新は OS が許可しないため、" +
                    "『自動チェック＋ワンタップ更新』が上限です。",
            ),
            quiz(
                "sideload アプリの更新で最後に必ず必要なのは？",
                listOf("何も不要で自動完了", "ユーザーのインストール確認", "Play Store 審査"),
                1,
                "提供元不明のアプリ許可とインストール確認は OS 必須。",
            ),
        ),
    )

    private val l11 = Lesson(
        id = "b11", title = "AOSP には『土台アプリ』が標準で付いてくる",
        minutes = 8,
        blocks = listOf(
            h("ゼロから作らない。標準アセットを使う"),
            dia(DiagramType.ASSET_MAP, "アプリ / リソース / API・基盤 の3層"),
            p(
                "AAOS には設定・メディア・電話・SystemUI などの参照アプリと、" +
                    "Car API/基盤が標準で付いてきます。OEM はこれを土台に、" +
                    "RRO で見た目を変え、足りない所だけ作ります。",
            ),
            assetTable("代表的な参照アプリ", AospAssets.apps.take(5)),
            note(
                "『再利用度』＝そのまま使える(★)〜RROだけ(◎)〜作り込み(△)。" +
                    "中級コースで全体像と使い分けを詳しく扱います。",
            ),
            quiz(
                "AOSP 参照アプリの一番うれしい点は？",
                listOf("毎回自作が必要", "土台として再利用でき、差分だけ作れる", "改変できない"),
                1,
                "標準アセットを土台に差分開発できるのが最大の利点。",
            ),
        ),
    )

    val course = Course(
        level = CourseLevel.BEGINNER,
        title = "AAOS をはじめる",
        subtitle = "アプリ層を中心に、AAOS の世界観と最初の一歩を体で覚える",
        modules = listOf(
            Module(
                "全体像をつかむ",
                "AAOS とは何か、車のソフト構造、レイヤー",
                listOf(l1, l2, l3),
            ),
            Module(
                "最初のコードと作法",
                "Car API の入口、リソース設計の考え方",
                listOf(l4, l5),
            ),
            Module(
                "実例と総まとめ",
                "Polestar 2 ケース、用語整理",
                listOf(l6, l7),
            ),
            Module(
                "実機への橋渡し",
                "エミュレータ起動と Auto/AAOS の整理",
                listOf(l8, l9),
            ),
            Module(
                "AOSP の土台を知る",
                "標準で付いてくる参照アプリと再利用",
                listOf(l11),
            ),
            Module(
                "このアプリの裏側",
                "自動更新の仕組み（学んだ流れの実例）",
                listOf(l10),
            ),
        ),
    )
}
