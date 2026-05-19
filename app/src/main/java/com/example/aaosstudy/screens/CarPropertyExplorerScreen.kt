package com.example.aaosstudy.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aaosstudy.model.CarPropertyCatalog
import com.example.aaosstudy.ui.BackHeader
import com.example.aaosstudy.ui.CodeBlock
import com.example.aaosstudy.ui.SectionCard

@Composable
fun CarPropertyExplorerScreen(onBack: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BackHeader("Car API エクスプローラ", onBack)

        Text(
            "実際の CarPropertyManager の使い方。アプリコードに居ながら、" +
                "呼び出し連鎖で裏側を確認できます。",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        CarPropertyCatalog.all.forEach { doc ->
            SectionCard(doc.title, Modifier.padding(horizontal = 16.dp)) {
                Text(
                    doc.propertyId,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 6.dp),
                )
                Text(
                    "area: ${doc.area}    access: ${doc.access}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 2.dp, bottom = 6.dp),
                )
                Text(
                    doc.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 10.dp),
                )
                CodeBlock(doc.appCode)
                Text(
                    "呼び出し連鎖",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                )
                doc.callChain.forEachIndexed { i, step ->
                    Text(
                        "${i + 1}. $step",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 1.dp),
                    )
                }
            }
        }
    }
}
