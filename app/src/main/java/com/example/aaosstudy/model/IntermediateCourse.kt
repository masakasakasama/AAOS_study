package com.example.aaosstudy.model

/**
 * 中級向け。Car API を実装レベルで使いこなし、RRO とクラスターを作る。
 */
object IntermediateCourse {

    private val l1 = Lesson(
        id = "i1", title = "CarPropertyManager 詳細（read / write / subscribe）",
        minutes = 16,
        blocks = listOf(
            h("3 つのアクセス様式"),
            b(
                "単発 read: getIntProperty / getFloatProperty",
                "単発 write: setIntProperty / setFloatProperty",
                "購読: registerCallback（連続センサーはこれ）",
            ),
            code(
                """
propertyManager.registerCallback(
    object : CarPropertyManager.CarPropertyEventCallback {
        override fun onChangeEvent(v: CarPropertyValue<*>) {
            val mps = v.value as Float
            updateSpeedo(mps * 3.6f)   // m/s -> km/h
        }
        override fun onErrorEvent(p: Int, a: Int) {}
    },
    VehiclePropertyIds.PERF_VEHICLE_SPEED,
    CarPropertyManager.SENSOR_RATE_UI,
)
                """,
            ),
            dia(DiagramType.DATA_FLOW, "購読の値は逆向きに callback で返る"),
            fileMap(
                "API がどこに紐づくか",
                link(
                    "CarPropertyManager.registerCallback()",
                    "packages/services/Car/service/.../CarPropertyService.java",
                    "Binder 越しに購読登録され、Service が VHAL を購読。",
                ),
                link(
                    "VehiclePropertyIds.PERF_VEHICLE_SPEED",
                    "hardware/interfaces/automotive/vehicle/.../VehicleProperty",
                    "プロパティ ID の正体は VHAL 契約の enum。",
                ),
            ),
            warn("連続値をポーリングしない。必ず購読し、変化時だけ処理する。"),
            tryIt(
                "vhal", "VHAL Playground",
                "速度スライダーを動かし、購読相当でメーターが追従する" +
                    "様子を確認しましょう。",
            ),
            quiz(
                "連続的に変わる速度を扱う最適解は？",
                listOf("ループで getFloatProperty", "registerCallback で購読", "setProperty"),
                1,
                "連続センサーは購読。ポーリングは無駄と遅延の元。",
            ),
        ),
    )

    private val l2 = Lesson(
        id = "i2", title = "プロパティの解剖（id / area / access / changeMode）",
        minutes = 14,
        blocks = listOf(
            h("1 つのプロパティは 4 つの属性を持つ"),
            dia(DiagramType.PROPERTY_ANATOMY, "id を軸に area/access/changeMode/型"),
            b(
                "area: GLOBAL か、座席/窓などゾーン別か（areaId）",
                "access: READ / WRITE / READ_WRITE",
                "changeMode: STATIC / ONCHANGE / CONTINUOUS",
                "型: Int / Float / Int[] など",
            ),
            code(
                """
val zone = VehicleAreaSeat.SEAT_ROW_1_LEFT
propertyManager.setFloatProperty(
    VehiclePropertyIds.HVAC_TEMPERATURE_SET, zone, 22.0f)
                """,
            ),
            defaults(
                "参照 VHAL のデフォルト値はどこ？",
                def(
                    "各プロパティの初期値・対応エリア",
                    "リファレンス実装の既定値（速度0、ギアP 等）",
                    "hardware/interfaces/automotive/vehicle/aidl/impl/" +
                        "default_config/ (DefaultProperties.json 等)",
                ),
                def(
                    "旧 HIDL 実装の既定",
                    "DefaultConfig.h に C++ で定義",
                    "hardware/interfaces/automotive/vehicle/2.0/default/",
                ),
            ),
            quiz(
                "座席ごとに温度が違う HVAC は何が複数ある？",
                listOf("propertyId", "areaId", "changeMode"),
                1,
                "ゾーン別プロパティは 1 つの id に複数の areaId を持つ。",
            ),
        ),
    )

    private val l3 = Lesson(
        id = "i3", title = "HVAC 制御とゾーン・権限",
        minutes = 14,
        blocks = listOf(
            h("空調はゾーン × 権限の典型例"),
            dia(DiagramType.HVAC_ZONES, "areaId(VehicleAreaSeat) で座席を指定"),
            dia(DiagramType.PERMISSION_FLOW, "書込みは CarService が権限を検査"),
            p(
                "HVAC 書込みには Car.PERMISSION_CONTROL_CAR_CLIMATE が" +
                    "必要。権限が無い・署名が合わないと SecurityException。",
            ),
            fileMap(
                "権限はどこで定義/検査されるか",
                link(
                    "Car.PERMISSION_CONTROL_CAR_CLIMATE",
                    "packages/services/Car/car-lib/.../Car.java",
                    "権限文字列の定義場所。",
                ),
                link(
                    "権限の宣言（protectionLevel）",
                    "packages/services/Car/service/AndroidManifest.xml",
                    "signature|privileged 等の保護レベルがここ。",
                ),
            ),
            tryIt(
                "vhal", "VHAL Playground で HVAC を操作",
                "設定温度・ファン段を変えると IVI の HVAC 表示が即変化。",
            ),
            quiz(
                "HVAC 書込みで SecurityException が出る主因は？",
                listOf("値が範囲外", "車両権限/署名の不足", "areaId が 0"),
                1,
                "制御系プロパティは保護レベルが高く、権限と署名が要る。",
            ),
        ),
    )

    private val l4 = Lesson(
        id = "i4", title = "UX Restrictions（走行中の操作制限）",
        minutes = 13,
        blocks = listOf(
            h("走行中は OS が UI を制限する"),
            p(
                "CarUxRestrictionsManager を購読し、走行中は文字量・" +
                    "キーボード・動画などを抑制します。これは法規/安全要件で" +
                    "あり、OEM 審査で必ず見られます。",
            ),
            code(
                """
val uxr = car.getCarManager(Car.CAR_UX_RESTRICTION_SERVICE)
        as CarUxRestrictionsManager
uxr.registerListener { r ->
    val limited = r.isRequiresDistractionOptimization
    setUiToDriveSafe(limited)
}
                """,
            ),
            defaults(
                "UXR の既定ポリシー",
                def(
                    "走行中に許す UI の度合い",
                    "速度等の条件付き既定（OEM が上書き可）",
                    "packages/services/Car/service/res/xml/" +
                        "car_ux_restrictions_map.xml",
                ),
            ),
            case(
                "Volvo / Polestar",
                "走行中はリスト項目数や入力 UI を制限し、停車時に解放する" +
                    "設計。アプリは制限フラグに従うだけで OEM 横断で整合。",
            ),
            quiz(
                "走行中 UI 制限の根拠は？",
                listOf("見た目の好み", "安全/法規要件", "電池節約"),
                1,
                "ドライバー気そらし防止の安全・法規要件。必須対応。",
            ),
        ),
    )

    private val l5 = Lesson(
        id = "i5", title = "RRO 徹底解説（OverlayManager / target / overlay）",
        minutes = 16,
        blocks = listOf(
            h("OEM はアプリを再ビルドしない"),
            dia(DiagramType.RRO_OVERLAY, "base APK + overlay APK → 解決 → 画面"),
            p(
                "OEM は対象パッケージのリソース名を別 APK（オーバーレイ）で" +
                    "再定義し、OverlayManager で有効化します。アプリの" +
                    "バイトコードは無改修のまま見た目が変わります。",
            ),
            code(
                """
<!-- overlay APK 側 AndroidManifest.xml -->
<overlay android:targetPackage="com.android.car.settings"
         android:targetName="CarSettings"
         android:isStatic="true" android:priority="1"/>
                """,
                lang = "xml",
            ),
            fileMap(
                "RRO のファイル対応",
                link(
                    "システムアプリの @color/... 参照",
                    "OEM オーバーレイ APK の res/values/*.xml 同名定義",
                    "名前が一致した値が実行時に優先される。",
                ),
                link(
                    "オーバーレイ APK の置き場所",
                    "device/<oem>/.../overlay/ または vendor/.../overlay/",
                    "ビルドに同梱され OverlayManager が認識。",
                ),
                link(
                    "有効化を司る本体",
                    "frameworks/base/services/core/.../om/ (OverlayManager)",
                    "どのオーバーレイを優先するか解決する。",
                ),
            ),
            defaults(
                "RRO 無効時（= AOSP デフォルト）",
                def(
                    "色/ロゴ/寸法",
                    "システムアプリ同梱の AOSP リファレンス値",
                    "packages/apps/Car/<App>/res/values/",
                ),
            ),
            tryIt(
                "rro", "RRO Theme Lab",
                "色・角丸・夜間モードを変えると生成オーバーレイ XML が" +
                    "更新されます。OEM が書くファイルそのものです。",
            ),
            quiz(
                "RRO で見た目が変わってもアプリ側は？",
                listOf("再コンパイルが必要", "コード/バイトコードは無改修", "起動不能"),
                1,
                "リソース解決が差し替わるだけ。コードは触らない。",
            ),
        ),
    )

    private val l6 = Lesson(
        id = "i6", title = "インストルメントクラスターアプリの作り方",
        minutes = 15,
        blocks = listOf(
            h("クラスターは『安全な表示』が主役"),
            dia(DiagramType.CLUSTER_VS_IVI, "クラスターは要件が高い別アプリ"),
            p(
                "クラスターは速度・ギア・警告灯などを購読して描画。" +
                    "AOSP には参照クラスターアプリがあり、これを土台に OEM が" +
                    "RRO で意匠を当てます。",
            ),
            fileMap(
                "クラスター関連のファイル対応",
                link(
                    "参照クラスター/インスト群",
                    "packages/apps/Car/ 配下（Cluster 系アプリ）",
                    "OEM はこれを RRO でブランド化、または置換。",
                ),
                link(
                    "クラスター表示の枠組み",
                    "android.car.cluster.* (car-lib)",
                    "ナビ等の情報をクラスター面へ渡す API。",
                ),
            ),
            tryIt(
                "scenarios", "Scenario でクラスター表現を比較",
                "Highway / Nordic / Classic を切替えて意匠差を体感。",
            ),
            quiz(
                "クラスターアプリの設計で最優先は？",
                listOf("派手なアニメ", "安全に関わる情報の確実な表示", "広告枠"),
                1,
                "走行安全に直結。確実・低遅延・規制準拠が最優先。",
            ),
        ),
    )

    private val l7 = Lesson(
        id = "i7", title = "署名・システムアプリ・権限の現実",
        minutes = 12,
        blocks = listOf(
            h("なぜ普通の APK では動かない API があるか"),
            p(
                "制御系プロパティや一部 Manager は signature|privileged" +
                    "権限が必要。OEM のプラットフォーム鍵で署名し、" +
                    "privapp-permissions allowlist に載って初めて使えます。",
            ),
            fileMap(
                "権限許可リストの対応",
                link(
                    "アプリが要求する車両権限",
                    "etc/permissions/privapp-permissions-*.xml",
                    "privileged アプリの許可ホワイトリスト。",
                ),
                link(
                    "プラットフォーム署名鍵",
                    "build/make/target/product/security/ (AOSP 既定鍵)",
                    "実 OEM は独自鍵に差し替える。",
                ),
            ),
            warn(
                "学習目的のサンドボックス（本アプリ）は権限不要。実機の" +
                    "制御系を触るには署名・allowlist が前提と理解しておく。",
            ),
            quiz(
                "制御系 Car API を一般 APK で呼ぶと？",
                listOf("普通に動く", "SecurityException 等で弾かれる", "警告だけ"),
                1,
                "signature|privileged 権限と allowlist が無いと拒否される。",
            ),
        ),
    )

    val course = Course(
        level = CourseLevel.INTERMEDIATE,
        title = "Car API を使いこなす",
        subtitle = "プロパティ・HVAC・UX 制限・RRO・クラスターを実装視点で",
        modules = listOf(
            Module(
                "プロパティ実装",
                "read/write/subscribe とプロパティ構造、HVAC",
                listOf(l1, l2, l3),
            ),
            Module(
                "OEM 作法",
                "UX 制限と RRO の徹底理解",
                listOf(l4, l5),
            ),
            Module(
                "クラスターと配備",
                "クラスター実装、署名と権限の現実",
                listOf(l6, l7),
            ),
        ),
    )
}
