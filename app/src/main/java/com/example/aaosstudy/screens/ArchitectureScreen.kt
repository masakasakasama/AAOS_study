package com.example.aaosstudy.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aaosstudy.model.Architecture
import com.example.aaosstudy.ui.BackHeader
import com.example.aaosstudy.ui.SectionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchitectureScreen(onBack: () -> Unit) {
    var expanded by remember { mutableIntStateOf(0) }

    LazyColumn(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp),
    ) {
        item { BackHeader("Architecture Map", onBack) }
        item {
            Text(
                "Tap a layer. Top is your app — where most OEM work " +
                    "happens — down to the vehicle.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        items(Architecture.layers.withIndex().toList()) { (i, layer) ->
            val open = expanded == i
            Card(
                onClick = { expanded = if (open) -1 else i },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (open)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    else MaterialTheme.colorScheme.surface,
                ),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        layer.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        layer.oneLiner,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                    AnimatedVisibility(open) {
                        Column(Modifier.padding(top = 8.dp)) {
                            Text(
                                layer.detail,
                                style = MaterialTheme.typography.bodySmall,
                            )
                            Text(
                                "Key: " + layer.examples.joinToString(" · "),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(top = 8.dp),
                            )
                        }
                    }
                }
            }
        }
        item {
            SectionCard(
                "How RRO actually works",
                Modifier.padding(horizontal = 16.dp),
            ) {
                Text(
                    Architecture.rro,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}
