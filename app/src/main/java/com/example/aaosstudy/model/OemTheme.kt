package com.example.aaosstudy.model

import androidx.compose.ui.graphics.Color

/**
 * Stands in for what an OEM ships as a Runtime Resource Overlay (RRO).
 *
 * On real AAOS the carrier/OEM does NOT recompile the system app. They ship an
 * overlay APK that replaces resources (colors, drawables, dimens, bools) at
 * runtime. The framework's resource manager picks the overlaid value, so the
 * exact same UI code renders differently. The fields here are the resources a
 * cluster/IVI overlay typically targets.
 */
data class OemThemeState(
    val brandName: String = "AOSP Reference",
    val accent: Color = Color(0xFF1A73E8),
    val clusterBackground: Color = Color(0xFF0E1116),
    val onCluster: Color = Color(0xFFE8EAED),
    val cornerRadiusDp: Int = 16,
    val roundGauges: Boolean = true,
    val nightMode: Boolean = true,
) {
    /** The overlay XML an OEM would author to produce this exact look. */
    fun toOverlayXml(): String = """
<!-- res/values/overlay_config.xml  (shipped in an RRO APK, NOT in the system app) -->
<resources>
    <color name="cluster_accent">${accent.toHex()}</color>
    <color name="cluster_background">${clusterBackground.toHex()}</color>
    <color name="cluster_on_surface">${onCluster.toHex()}</color>
    <dimen name="cluster_corner_radius">${cornerRadiusDp}dp</dimen>
    <bool name="cluster_round_gauges">$roundGauges</bool>
    <bool name="config_night_mode_default">$nightMode</bool>
    <string name="oem_brand_name">$brandName</string>
</resources>
    """.trim()
}

private fun Color.toHex(): String {
    val a = (alpha * 255).toInt()
    val r = (red * 255).toInt()
    val g = (green * 255).toInt()
    val b = (blue * 255).toInt()
    return String.format("#%02X%02X%02X%02X", a, r, g, b)
}
