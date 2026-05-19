package com.example.aaosstudy.model

/**
 * Catalog of the most-used car properties for the API Explorer screen.
 *
 * Each entry shows the real CarPropertyManager code an app-layer developer
 * writes, plus what happens underneath. This is the "upper layer first"
 * material: you stay in app code, but you can see the call chain.
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
        title = "Connecting to Car service",
        propertyId = "Car (entry point)",
        area = "—",
        access = "—",
        summary = "Every Car API call needs a Car instance. Car binds to " +
            "CarService (a persistent system service). From it you get " +
            "managers like CarPropertyManager, CarHvacManager, etc.",
        appCode = """
val car = Car.createCar(context)
val propertyManager =
    car.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager

// Always disconnect when done (e.g. in onDestroy)
car.disconnect()
        """.trim(),
        callChain = listOf(
            "App: Car.createCar(context)",
            "Binder IPC -> CarService (system_server-side persistent service)",
            "CarService returns CarPropertyManager handle",
            "Manager is your app-side proxy for all property I/O",
        ),
    )

    val all = listOf(
        connect,
        CarPropertyDoc(
            title = "Read vehicle speed",
            propertyId = "VehiclePropertyIds.PERF_VEHICLE_SPEED",
            area = "GLOBAL (area = 0)",
            access = "READ",
            summary = "Continuous sensor. Subscribe with a callback rather " +
                "than polling so you only react to changes.",
            appCode = """
propertyManager.registerCallback(
    object : CarPropertyManager.CarPropertyEventCallback {
        override fun onChangeEvent(value: CarPropertyValue<*>) {
            val speedMps = value.value as Float   // metres / second
            updateSpeedo(speedMps * 3.6f)         // -> km/h
        }
        override fun onErrorEvent(propId: Int, areaId: Int) {}
    },
    VehiclePropertyIds.PERF_VEHICLE_SPEED,
    CarPropertyManager.SENSOR_RATE_UI,            // ~5 Hz
)
            """.trim(),
            callChain = listOf(
                "App registers callback on CarPropertyManager",
                "CarService subscribes to the VHAL property",
                "VHAL (vendor HAL / emulator) pushes new sensor samples",
                "CarService dispatches CarPropertyValue back to the app",
            ),
        ),
        CarPropertyDoc(
            title = "Read selected gear",
            propertyId = "VehiclePropertyIds.GEAR_SELECTION",
            area = "GLOBAL (area = 0)",
            access = "READ",
            summary = "Enum-style int property. Map the raw int to " +
                "VehicleGear constants (PARK/REVERSE/NEUTRAL/DRIVE).",
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
                "App calls getIntProperty(...) (blocking read)",
                "CarService reads the cached/queried VHAL value",
                "Raw int returned; app maps it to a VehicleGear constant",
            ),
        ),
        CarPropertyDoc(
            title = "Set HVAC temperature",
            propertyId = "VehiclePropertyIds.HVAC_TEMPERATURE_SET",
            area = "SEAT (per-zone areaId)",
            access = "READ_WRITE",
            summary = "Zoned property: each seat area has its own value. " +
                "Requires the Car.PERMISSION_CONTROL_CAR_CLIMATE permission.",
            appCode = """
val driverZone = VehicleAreaSeat.SEAT_ROW_1_LEFT
propertyManager.setFloatProperty(
    VehiclePropertyIds.HVAC_TEMPERATURE_SET,
    driverZone,
    22.0f,
)
            """.trim(),
            callChain = listOf(
                "App calls setFloatProperty(prop, areaId, value)",
                "CarService checks the caller's car permission",
                "Write forwarded to VHAL for that seat area",
                "VHAL confirms; a change event echoes back to listeners",
            ),
        ),
        CarPropertyDoc(
            title = "Read fuel / battery level",
            propertyId = "VehiclePropertyIds.FUEL_LEVEL",
            area = "GLOBAL (area = 0)",
            access = "READ",
            summary = "Static-ish property. Pair with INFO_FUEL_CAPACITY to " +
                "compute a percentage for the gauge.",
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
                "App reads FUEL_LEVEL and INFO_FUEL_CAPACITY",
                "CarService serves values from VHAL",
                "App derives the gauge percentage in app code",
            ),
        ),
    )
}
