package com.example.aaosstudy.model

/** Layers of the AAOS stack, top (your app) to bottom (hardware). */
data class ArchLayer(
    val name: String,
    val oneLiner: String,
    val detail: String,
    val examples: List<String>,
)

object Architecture {
    val layers = listOf(
        ArchLayer(
            name = "1. Your App (App layer)",
            oneLiner = "An ordinary Android app — but talks to Car APIs.",
            detail = "Runs in its own process. Uses Jetpack/Compose for UI " +
                "and the Car library for vehicle data. This is where you " +
                "spend ~90% of OEM app work: HVAC apps, cluster, media, " +
                "settings. No special build of Android needed to learn it.",
            examples = listOf("Activity / Service", "Compose UI", "ViewModel"),
        ),
        ArchLayer(
            name = "2. Car API library (android.car)",
            oneLiner = "Client-side SDK: Car + *Manager classes.",
            detail = "Car.createCar() binds to CarService. Managers " +
                "(CarPropertyManager, CarHvacManager, CarUxRestrictions...) " +
                "are thin proxies that marshal calls over Binder. This is " +
                "the boundary you code against.",
            examples = listOf("Car", "CarPropertyManager", "CarHvacManager"),
        ),
        ArchLayer(
            name = "3. Car Service (system service)",
            oneLiner = "Persistent privileged service; the policy layer.",
            detail = "Enforces car permissions, manages subscriptions, " +
                "applies UX restrictions while driving, caches properties, " +
                "and fans out change events to all listening apps.",
            examples = listOf("CarPropertyService", "CarPowerService"),
        ),
        ArchLayer(
            name = "4. Vehicle HAL (VHAL)",
            oneLiner = "Stable contract between Android and the vehicle.",
            detail = "A defined set of VehicleProperty ids with area + " +
                "access + change-mode. Vendors implement it; the emulator " +
                "ships a reference VHAL you can poke for learning.",
            examples = listOf("VehiclePropertyIds", "areaId", "access mode"),
        ),
        ArchLayer(
            name = "5. Vehicle hardware / Emulator",
            oneLiner = "Real ECUs over a bus — or the AAOS emulator.",
            detail = "On hardware, the VHAL bridges to CAN/automotive " +
                "networks. For study, the emulator's VHAL lets you inject " +
                "values with no car present.",
            examples = listOf("CAN bus", "AAOS emulator", "reference VHAL"),
        ),
    )

    val rro = """
RRO (Runtime Resource Overlay) — how OEMs reskin without forking apps:

1. The system app references resources normally: @color/cluster_accent.
2. The OEM ships a separate, tiny overlay APK targeting that package.
3. The overlay redefines those resource names with new values.
4. OverlayManager enables it; the resource framework returns the
   overlaid value at runtime. The app's code/bytecode is unchanged.

Why it matters at the app layer: write code against resource names,
never hard-code colors/strings/dimens, and any OEM can rebrand you.
    """.trim()
}
