package com.example.aaosstudy.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aaosstudy.model.OemThemeState
import com.example.aaosstudy.model.VehicleState

/**
 * The simulated IVI / cluster surface. Every visual here is driven purely by
 * [vehicle] (the "VHAL" values) and [theme] (the "RRO" overlay), so editing
 * either input visibly recomposes this — the whole point of the sandbox.
 */
@Composable
fun IviDashboard(
    vehicle: VehicleState,
    theme: OemThemeState,
    modifier: Modifier = Modifier,
) {
    val corner = if (theme.roundGauges) theme.cornerRadiusDp.dp else 2.dp
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(corner)),
        color = theme.clusterBackground,
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    theme.brandName,
                    color = theme.onCluster,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    if (vehicle.ignitionOn) "IGN ON" else "IGN OFF",
                    color = if (vehicle.ignitionOn) theme.accent
                    else theme.onCluster.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Gauge(
                    label = "SPEED",
                    value = vehicle.speedKph,
                    max = 220f,
                    unit = "km/h",
                    theme = theme,
                    modifier = Modifier.weight(1f),
                )
                Gauge(
                    label = "RPM",
                    value = vehicle.engineRpm,
                    max = 8000f,
                    unit = "rpm",
                    theme = theme,
                    modifier = Modifier.weight(1f),
                )
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GearStrip(currentLabel = vehicle.gear.label, theme = theme)
                Text(
                    "${vehicle.outsideTempC.toInt()}°C out",
                    color = theme.onCluster,
                    fontSize = 13.sp,
                )
            }

            FuelBar(
                percent = vehicle.fuelPercent,
                theme = theme,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
            )

            Surface(
                color = theme.accent.copy(alpha = 0.14f),
                shape = RoundedCornerShape(corner),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        "HVAC  ${vehicle.hvacSetTempC.toInt()}°C",
                        color = theme.onCluster,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        "FAN  ${"▮".repeat(vehicle.hvacFanSpeed)}",
                        color = theme.accent,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun Gauge(
    label: String,
    value: Float,
    max: Float,
    unit: String,
    theme: OemThemeState,
    modifier: Modifier = Modifier,
) {
    val fraction = (value / max).coerceIn(0f, 1f)
    val animated by animateFloatAsState(targetValue = fraction, label = label)
    Box(
        modifier = modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxWidth().aspectRatio(1f)) {
            val stroke = size.minDimension * 0.10f
            val sweep = 270f
            val start = 135f
            drawArc(
                color = theme.onCluster.copy(alpha = 0.15f),
                startAngle = start,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(
                    width = stroke,
                    cap = if (theme.roundGauges) StrokeCap.Round
                    else StrokeCap.Butt,
                ),
            )
            drawArc(
                color = theme.accent,
                startAngle = start,
                sweepAngle = sweep * animated,
                useCenter = false,
                style = Stroke(
                    width = stroke,
                    cap = if (theme.roundGauges) StrokeCap.Round
                    else StrokeCap.Butt,
                ),
            )
            if (!theme.roundGauges) {
                drawCircle(
                    color = theme.onCluster.copy(alpha = 0.08f),
                    radius = size.minDimension / 2f,
                    center = Offset(size.width / 2f, size.height / 2f),
                    style = Stroke(width = 1f),
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                value.toInt().toString(),
                color = theme.onCluster,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(unit, color = theme.onCluster.copy(alpha = 0.6f), fontSize = 11.sp)
            Text(
                label,
                color = theme.accent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun GearStrip(currentLabel: String, theme: OemThemeState) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("P", "R", "N", "D").forEach { g ->
            val active = g == currentLabel
            Text(
                g,
                color = if (active) theme.accent
                else theme.onCluster.copy(alpha = 0.35f),
                fontSize = if (active) 22.sp else 16.sp,
                fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun FuelBar(
    percent: Float,
    theme: OemThemeState,
    modifier: Modifier = Modifier,
) {
    val frac by animateFloatAsState(
        targetValue = (percent / 100f).coerceIn(0f, 1f),
        label = "fuel",
    )
    val low = percent < 15f
    Column(modifier) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("FUEL", color = theme.onCluster, fontSize = 12.sp)
            Text(
                "${percent.toInt()}%",
                color = if (low) Color(0xFFE0533D) else theme.onCluster,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Box(
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(if (theme.roundGauges) 6.dp else 0.dp)),
        ) {
            Canvas(Modifier.fillMaxWidth().aspectRatio(28f)) {
                drawRect(color = theme.onCluster.copy(alpha = 0.15f))
                drawRect(
                    color = if (low) Color(0xFFE0533D) else theme.accent,
                    size = Size(size.width * frac, size.height),
                )
            }
        }
    }
}
