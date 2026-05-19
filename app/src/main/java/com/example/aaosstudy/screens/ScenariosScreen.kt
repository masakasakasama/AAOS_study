package com.example.aaosstudy.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aaosstudy.model.Scenarios
import com.example.aaosstudy.state.SimulatorViewModel
import com.example.aaosstudy.ui.BackHeader
import com.example.aaosstudy.ui.IviDashboard
import com.example.aaosstudy.ui.SectionCard

@Composable
fun ScenariosScreen(vm: SimulatorViewModel, onBack: () -> Unit) {
    val vehicle by vm.vehicle.collectAsState()
    val theme by vm.theme.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BackHeader("シナリオ プリセット", onBack)

        IviDashboard(
            vehicle = vehicle,
            theme = theme,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Scenarios.all.forEach { scenario ->
            SectionCard(scenario.name, Modifier.padding(horizontal = 16.dp)) {
                Text(
                    scenario.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
                Button(
                    onClick = { vm.applyScenario(scenario) },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("このシナリオを適用") }
            }
        }
    }
}
