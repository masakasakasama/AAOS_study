package com.example.aaosstudy.model

/**
 * API エクスプローラ用、よく使う車両プロパティのカタログ。
 * 各項目は実際の CarPropertyManager コードと、その裏で起きることを示す。
 * 「上位レイヤー優先」の教材：アプリコードに居ながら呼び出し連鎖が見える。
 */
data class CarPropertyDoc(
    val title: String,
    val propertyId: String,
    val area: String,
    val access: String,
    val summary: String,
    val appCode: String,
    val callChain: List<String>,
)

object CarPropertyCatalog {

    val connect = CarPropertyDoc(
        title = "Car サービスへ接続する",
        propertyId = "Car（エントリポイント）",
        area = "—",
        access = "—",
        summary = "Car API は必ず Car インスタンスが要る。Car は常駐の" +
            "システムサービス CarService に bind し、そこから " +
            "CarPropertyManager や CarHvacManager などを取得する。",
        appCode = """
val car = Car.createCar(context)
val propertyManager =
    car.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager

// 使い終わったら必ず切断（例: onDestroy）
car.disconnect()
        """.trim(),
        callChain = listOf(
            "アプリ: Car.createCar(context)",
            "Binder IPC → CarService（常駐の特権サービス）",
            "CarService が CarPropertyManager のハンドルを返す",
            "この Manager がアプリ側のプロパティ入出力の窓口",
        ),
    )

    val all = listOf(
        connect,
        CarPropertyDoc(
            title = "車速を読む",
            propertyId = "VehiclePropertyIds.PERF_VEHICLE_SPEED",
            area = "GLOBAL（area = 0）",
            access = "READ（読み取り）",
            summary = "連続センサー。ポーリングせず、コールバックで購読して" +
                "変化時だけ反応する。",
            appCode = """
propertyManager.registerCallback(
    object : CarPropertyManager.CarPropertyEventCallback {
        override fun onChangeEvent(value: CarPropertyValue<*>) {
            val speedMps = value.value as Float   // m/s
            updateSpeedo(speedMps * 3.6f)         // -> km/h
        }
        override fun onErrorEvent(propId: Int, areaId: Int) {}
    },
    VehiclePropertyIds.PERF_VEHICLE_SPEED,
    CarPropertyManager.SENSOR_RATE_UI,            // 約 5 Hz
)
            """.trim(),
            callChain = listOf(
                "アプリが CarPropertyManager にコールバック登録",
                "CarService が VHAL のプロパティを購読",
                "VHAL（ベンダー HAL / エミュレータ）が新しい値を push",
                "CarService が CarPropertyValue をアプリへ配信",
            ),
        ),
        CarPropertyDoc(
            title = "選択中のギアを読む",
            propertyId = "VehiclePropertyIds.GEAR_SELECTION",
            area = "GLOBAL（area = 0）",
            access = "READ（読み取り）",
            summary = "enum 風の int プロパティ。生の int を VehicleGear " +
                "定数（PARK/REVERSE/NEUTRAL/DRIVE）に対応づける。",
            appCode = """
val gear = propertyManager.getIntProperty(
    VehiclePropertyIds.GEAR_SELECTION, /* areaId = */ 0
)
val label = when (gear) {
    VehicleGear.GEAR_PARK    -> "P"
    VehicleGear.GEAR_REVERSE -> "R"
    VehicleGear.GEAR_NEUTRAL -> "N"
    else                     -> "D"
}
            """.trim(),
            callChain = listOf(
                "アプリが getIntProperty(...) を呼ぶ（ブロッキング読み取り）",
                "CarService がキャッシュ/問い合わせた VHAL 値を読む",
                "生の int が返り、アプリが VehicleGear 定数へ変換",
            ),
        ),
        CarPropertyDoc(
            title = "HVAC の設定温度を書く",
            propertyId = "VehiclePropertyIds.HVAC_TEMPERATURE_SET",
            area = "SEAT（座席ゾーンごとの areaId）",
            access = "READ_WRITE（読み書き）",
            summary = "ゾーン別プロパティ。座席エリアごとに値を持つ。" +
                "Car.PERMISSION_CONTROL_CAR_CLIMATE 権限が必要。",
            appCode = """
val driverZone = VehicleAreaSeat.SEAT_ROW_1_LEFT
propertyManager.setFloatProperty(
    VehiclePropertyIds.HVAC_TEMPERATURE_SET,
    driverZone,
    22.0f,
)
            """.trim(),
            callChain = listOf(
                "アプリが setFloatProperty(prop, areaId, value) を呼ぶ",
                "CarService が呼び出し元の車両権限を検査",
                "その座席エリア向けに書き込みを VHAL へ転送",
                "VHAL が確定、変更イベントが購読側へ返る",
            ),
        ),
        CarPropertyDoc(
            title = "燃料 / バッテリー残量を読む",
            propertyId = "VehiclePropertyIds.FUEL_LEVEL",
            area = "GLOBAL（area = 0）",
            access = "READ（読み取り）",
            summary = "ほぼ静的なプロパティ。INFO_FUEL_CAPACITY と" +
                "組み合わせてゲージ用の割合を計算する。",
            appCode = """
val level = propertyManager.getFloatProperty(
    VehiclePropertyIds.FUEL_LEVEL, 0
)
val capacity = propertyManager.getFloatProperty(
    VehiclePropertyIds.INFO_FUEL_CAPACITY, 0
)
val percent = (level / capacity) * 100f
            """.trim(),
            callChain = listOf(
                "アプリが FUEL_LEVEL と INFO_FUEL_CAPACITY を読む",
                "CarService が VHAL の値を返す",
                "アプリ側でゲージの割合を算出",
            ),
        ),
    )
}
