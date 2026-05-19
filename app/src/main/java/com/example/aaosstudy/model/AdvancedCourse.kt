package com.example.aaosstudy.model

/**
 * 上級向け。CarService 内部・VHAL 実装・AOSP ビルド・電源/監査まで。
 */
object AdvancedCourse {

    private val l1 = Lesson(
        id = "a1", title = "CarService 内部アーキテクチャ",
        minutes = 16,
        blocks = listOf(
            h("CarService は『方針』の層"),
            p(
                "CarService は常駐の特権サービス群。CarPropertyService が" +
                    "VHAL 購読を集約し、権限検査・UX 制限・キャッシュをして" +
                    "全アプリへ変化イベントを配信します。",
            ),
            dia(DiagramType.DATA_FLOW, "Service が購読を集約し全リスナーへ分配"),
            fileMap(
                "CarService の主要ファイル対応",
                link(
                    "ICarProperty AIDL（アプリ⇄サービス境界）",
                    "packages/services/Car/car-lib/.../ICarProperty.aidl",
                    "Binder インターフェース定義。",
                ),
                link(
                    "ICarProperty 実装",
                    "packages/services/Car/service/.../CarPropertyService.java",
                    "権限・購読・配信の実体。",
                ),
                link(
                    "VHAL 接続層",
                    "packages/services/Car/service/.../hal/" +
                        "PropertyHalService.java",
                    "VHAL とサービスの橋渡し。",
                ),
            ),
            quiz(
                "権限検査が実際に行われる層は？",
                listOf("アプリ", "CarService", "VHAL"),
                1,
                "Manager は薄いプロキシ。検査・分配は CarService の責務。",
            ),
        ),
    )

    private val l2 = Lesson(
        id = "a2", title = "VHAL 実装とプロパティ追加（AIDL / HIDL）",
        minutes = 18,
        blocks = listOf(
            h("ベンダーが実装する契約"),
            p(
                "VHAL は VehicleProperty 群を実装する HAL。新しい OEM 固有" +
                    "プロパティはベンダー領域の id を採番し、リファレンス VHAL" +
                    "に config と get/set を追加します。",
            ),
            fileMap(
                "VHAL のファイル対応",
                link(
                    "プロパティ ID / area / access 定義 (AIDL)",
                    "hardware/interfaces/automotive/vehicle/aidl/" +
                        "android/hardware/automotive/vehicle/",
                    "型・enum の正本。car-lib の Ids はこれに対応。",
                ),
                link(
                    "リファレンス VHAL 実装と既定値",
                    "hardware/interfaces/automotive/vehicle/aidl/impl/",
                    "default_config に既定、fake impl に get/set。",
                ),
                link(
                    "旧 HIDL 実装（参考）",
                    "hardware/interfaces/automotive/vehicle/2.0/default/",
                    "DefaultConfig.h に C++ で既定値。",
                ),
            ),
            defaults(
                "プロパティ既定値の出どころ",
                def(
                    "速度=0 / ギア=PARK / 燃料容量 等",
                    "リファレンス VHAL の初期 config",
                    "hardware/interfaces/automotive/vehicle/aidl/impl/" +
                        "default_config/",
                ),
            ),
            warn(
                "OEM 固有 id は VENDOR ビット領域で採番。標準 id と衝突" +
                    "させない。互換性は VTS で検証される。",
            ),
            quiz(
                "新規 OEM 固有プロパティの id は？",
                listOf("標準領域を再利用", "VENDOR 領域で採番", "任意の負数"),
                1,
                "標準と衝突しないよう VENDOR 領域で採番するのが規約。",
            ),
        ),
    )

    private val l3 = Lesson(
        id = "a3", title = "AOSP ビルドとエミュレータ・RRO ビルド",
        minutes = 17,
        blocks = listOf(
            h("ソースから car ターゲットを焼く"),
            dia(DiagramType.BUILD_PIPELINE, "source → build → image → emulator"),
            code(
                """
source build/envsetup.sh
lunch sdk_car_x86_64-userdebug   # AAOS エミュレータ
m -j
emulator &
adb install app-debug.apk
                """,
                lang = "bash",
            ),
            fileMap(
                "ビルド構成のファイル対応",
                link(
                    "AAOS エミュレータ device 定義",
                    "device/generic/car/",
                    "lunch ターゲットの中身（含まれる app/overlay）。",
                ),
                link(
                    "製品に含めるパッケージ/オーバーレイ",
                    "device/<oem>/.../*.mk + .../overlay/",
                    "PRODUCT_PACKAGES と RRO の同梱指定。",
                ),
            ),
            tip(
                "学習だけなら Android Studio 同梱の Automotive エミュレータ" +
                    "イメージで十分。本格 RRO 検証時に AOSP ビルドへ。",
            ),
            quiz(
                "AAOS エミュレータ向け lunch ターゲット例は？",
                listOf("aosp_arm-eng", "sdk_car_x86_64-userdebug", "full_x86-user"),
                1,
                "car 系ターゲットで車載構成のイメージが作られる。",
            ),
        ),
    )

    private val l4 = Lesson(
        id = "a4", title = "電源管理・Garage Mode・瞬間起動",
        minutes = 15,
        blocks = listOf(
            h("車の『起動』はスマホと違う"),
            dia(DiagramType.BOOT_FLOW, "OFF→ON→走行→Garage→Suspend"),
            p(
                "AAOS は Suspend-to-RAM/Disk を多用し、ドア解錠で瞬時に" +
                    "復帰。ユーザー不在時に OTA/メンテを行う Garage Mode が" +
                    "あり、アプリは CarPowerManager で状態に追従します。",
            ),
            fileMap(
                "電源管理のファイル対応",
                link(
                    "CarPowerManager（アプリ API）",
                    "packages/services/Car/car-lib/.../CarPowerManager.java",
                    "電源状態リスナー登録。",
                ),
                link(
                    "電源方針の実体",
                    "packages/services/Car/service/.../" +
                        "CarPowerManagementService.java",
                    "状態遷移と Garage Mode を司る。",
                ),
            ),
            quiz(
                "ユーザー不在で更新等を行う状態は？",
                listOf("走行モード", "Garage Mode", "工場出荷モード"),
                1,
                "Garage Mode で OTA/インデックス等のメンテを実施。",
            ),
        ),
    )

    private val l5 = Lesson(
        id = "a5", title = "マルチユーザー / マルチディスプレイ / 音声ゾーン",
        minutes = 15,
        blocks = listOf(
            h("車は『複数人・複数画面・複数音場』"),
            b(
                "マルチユーザー: ドライバー切替で設定/アプリが分離",
                "マルチディスプレイ: クラスター/センター/後席(RSE)",
                "CarAudio: ゾーン別の音量・フォーカス管理",
            ),
            fileMap(
                "関連ファイル対応",
                link(
                    "CarAudioManager / オーディオゾーン",
                    "packages/services/Car/car-lib/.../CarAudioManager.java",
                    "ゾーン別ボリューム・フォーカス API。",
                ),
                link(
                    "オーディオゾーン構成",
                    "packages/services/Car/service/res/xml/" +
                        "car_audio_configuration.xml",
                    "ゾーンとバスのマッピング既定。",
                ),
            ),
            defaults(
                "オーディオゾーンの既定",
                def(
                    "ゾーン数・バス割当",
                    "リファレンス構成（OEM が車種別に上書き）",
                    "packages/services/Car/service/res/xml/" +
                        "car_audio_configuration.xml",
                ),
            ),
            quiz(
                "後席が別音源、運転席はナビ音声…を実現するのは？",
                listOf("単一 AudioManager", "CarAudio のゾーン管理", "Bluetooth のみ"),
                1,
                "CarAudio はゾーン別にフォーカス/音量を分離管理する。",
            ),
        ),
    )

    private val l6 = Lesson(
        id = "a6", title = "CTS/VTS と OEM 認証・実車開発フロー",
        minutes = 14,
        blocks = listOf(
            h("『動く』だけでは出荷できない"),
            p(
                "Google サービス搭載には CTS（互換性）・VTS（VHAL 等の" +
                    "ベンダー IF）合格が必要。OEM 側は ASPICE/機能安全(ISO" +
                    " 26262) のプロセスで SOP（量産）に向かいます。",
            ),
            case(
                "Volvo Cars / Polestar",
                "プラットフォームを共有しつつ車種ごとに RRO・VHAL 構成・" +
                    "オーディオゾーンを差し替える運用。アプリ層を安定 API に" +
                    "寄せておくほど車種展開コストが下がる、という教訓。",
            ),
            b(
                "CTS: Android 互換性テスト",
                "VTS: VHAL/HAL のベンダー IF テスト",
                "ASPICE / ISO 26262: プロセス/機能安全",
            ),
            quiz(
                "VHAL 等ベンダー IF の整合を見るテストは？",
                listOf("CTS", "VTS", "JUnit のみ"),
                1,
                "VTS がベンダーインターフェース（VHAL 等）を検証する。",
            ),
        ),
    )

    private val l7 = Lesson(
        id = "a7", title = "上級総合演習：機能を一気通貫で追う",
        minutes = 12,
        blocks = listOf(
            h("『設定温度を上げる』を全層で説明する"),
            p(
                "アプリの setFloatProperty(HVAC_TEMPERATURE_SET, zone, v)" +
                    " から、CarService の権限検査、PropertyHalService、VHAL、" +
                    "そして echo の change イベントが UI に戻るまでを、" +
                    "ファイル名付きで自分の言葉で説明できれば上級到達です。",
            ),
            dia(DiagramType.DATA_FLOW, "全層トレースを自分で再構成する"),
            tryIt(
                "api", "Car API Explorer で答え合わせ",
                "HVAC エントリの call chain と本演習の説明を突き合わせ。",
            ),
            quiz(
                "set の結果が UI に戻る経路は？",
                listOf(
                    "戻らない",
                    "VHAL→Service→登録 callback で全リスナーへ echo",
                    "アプリが再 read するしかない",
                ),
                1,
                "書込み後の変化は change イベントとして購読側へ返る。",
            ),
        ),
    )

    val course = Course(
        level = CourseLevel.ADVANCED,
        title = "プラットフォームに踏み込む",
        subtitle = "CarService 内部・VHAL 実装・AOSP ビルド・電源・認証まで",
        modules = listOf(
            Module(
                "内部実装",
                "CarService 内部と VHAL 実装",
                listOf(l1, l2),
            ),
            Module(
                "ビルドと運用基盤",
                "AOSP ビルド、電源管理、マルチユーザー/音声",
                listOf(l3, l4, l5),
            ),
            Module(
                "認証と総合",
                "CTS/VTS・実車フロー・総合演習",
                listOf(l6, l7),
            ),
        ),
    )
}
