package com.example.aaosstudy.state

import androidx.lifecycle.ViewModel
import com.example.aaosstudy.model.Gear
import com.example.aaosstudy.model.OemThemeState
import com.example.aaosstudy.model.Scenario
import com.example.aaosstudy.model.VehicleState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.ui.graphics.Color

/**
 * Single source of truth shared by every screen — the app-side mirror of
 * what CarPropertyManager would expose. Editing a "VHAL" value here is the
 * teaching analog of the emulator pushing a property change: the IVI
 * preview recomposes from this state, exactly like a real cluster app
 * recomposing from a CarPropertyValue callback.
 */
class SimulatorViewModel : ViewModel() {

    private val _vehicle = MutableStateFlow(VehicleState())
    val vehicle: StateFlow<VehicleState> = _vehicle.asStateFlow()

    private val _theme = MutableStateFlow(OemThemeState())
    val theme: StateFlow<OemThemeState> = _theme.asStateFlow()

    private val _completed = MutableStateFlow<Set<String>>(emptySet())
    val completed: StateFlow<Set<String>> = _completed.asStateFlow()

    fun toggleCompleted(lessonId: String) {
        _completed.value = _completed.value.toMutableSet().apply {
            if (!add(lessonId)) remove(lessonId)
        }
    }

    fun setSpeed(kph: Float) = update { it.copy(speedKph = kph) }
    fun setRpm(rpm: Float) = update { it.copy(engineRpm = rpm) }
    fun setFuel(percent: Float) = update { it.copy(fuelPercent = percent) }
    fun setGear(gear: Gear) = update { it.copy(gear = gear) }
    fun setIgnition(on: Boolean) = update { it.copy(ignitionOn = on) }
    fun setOutsideTemp(c: Float) = update { it.copy(outsideTempC = c) }
    fun setHvacTemp(c: Float) = update { it.copy(hvacSetTempC = c) }
    fun setFanSpeed(level: Int) = update { it.copy(hvacFanSpeed = level) }

    fun setAccent(color: Color) = updateTheme { it.copy(accent = color) }
    fun setClusterBackground(color: Color) =
        updateTheme { it.copy(clusterBackground = color) }
    fun setOnCluster(color: Color) = updateTheme { it.copy(onCluster = color) }
    fun setCornerRadius(dp: Int) = updateTheme { it.copy(cornerRadiusDp = dp) }
    fun setRoundGauges(round: Boolean) =
        updateTheme { it.copy(roundGauges = round) }
    fun setNightMode(night: Boolean) =
        updateTheme { it.copy(nightMode = night) }

    fun applyScenario(scenario: Scenario) {
        _vehicle.value = scenario.vehicle
        _theme.value = scenario.theme
    }

    fun resetAll() {
        _vehicle.value = VehicleState()
        _theme.value = OemThemeState()
    }

    private inline fun update(block: (VehicleState) -> VehicleState) {
        _vehicle.value = block(_vehicle.value)
    }

    private inline fun updateTheme(block: (OemThemeState) -> OemThemeState) {
        _theme.value = block(_theme.value)
    }
}
