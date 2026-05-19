package com.example.aaosstudy.model

/**
 * A tiny stand-in for the values that, on a real device, live behind the
 * Vehicle HAL (VHAL) and are read/written through CarPropertyManager.
 *
 * On real AAOS each field below maps to a VehiclePropertyId, e.g.:
 *   speedKph        -> VehiclePropertyIds.PERF_VEHICLE_SPEED
 *   engineRpm       -> VehiclePropertyIds.ENGINE_RPM
 *   fuelPercent     -> VehiclePropertyIds.FUEL_LEVEL / INFO_FUEL_CAPACITY
 *   gear            -> VehiclePropertyIds.GEAR_SELECTION
 *   ignitionOn      -> VehiclePropertyIds.IGNITION_STATE
 *   outsideTempC    -> VehiclePropertyIds.ENV_OUTSIDE_TEMPERATURE
 *   hvacSetTempC    -> VehiclePropertyIds.HVAC_TEMPERATURE_SET
 *   hvacFanSpeed    -> VehiclePropertyIds.HVAC_FAN_SPEED
 */
data class VehicleState(
    val speedKph: Float = 0f,
    val engineRpm: Float = 800f,
    val fuelPercent: Float = 62f,
    val gear: Gear = Gear.PARK,
    val ignitionOn: Boolean = true,
    val outsideTempC: Float = 18f,
    val hvacSetTempC: Float = 21f,
    val hvacFanSpeed: Int = 3,
)

enum class Gear(val label: String) {
    PARK("P"), REVERSE("R"), NEUTRAL("N"), DRIVE("D")
}
