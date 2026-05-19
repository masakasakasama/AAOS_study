package com.example.aaosstudy.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aaosstudy.model.Block
import com.example.aaosstudy.model.CalloutKind
import com.example.aaosstudy.model.Course
import com.example.aaosstudy.model.CourseLevel
import com.example.aaosstudy.model.Curriculum
import com.example.aaosstudy.model.Lesson
import com.example.aaosstudy.state.SimulatorViewModel
import com.example.aaosstudy.ui.BackHeader
import com.example.aaosstudy.ui.CodeBlock
import com.example.aaosstudy.ui.DiagramView
import com.example.aaosstudy.ui.SectionCard

@Composable
fun CoursesHomeScreen(
    vm: SimulatorViewModel,
    onOpenCourse: (CourseLevel) -> Unit,
    onBack: () -> Unit,
) {
    val completed by vm.completed.collectAsState()
    LazyColumn(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp),
    ) {
        item { BackHeader("学習コース", onBack) }
        item {
            Text(
                "体系的に学べる 3 コース。初学者 → 中級 → 上級の順に、" +
                    "AAOS のアプリ層から実プラットフォームまで。各レッスンに" +
                    "図・コード・AOSP の実ファイル対応・確認クイズを収録。",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        items(Curriculum.courses) { course ->
            val done = course.modules
                .flatMap { it.lessons }
                .count { it.id in completed }
            CourseCard(course, done) { onOpenCourse(course.level) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseCard(course: Course, done: Int, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                course.level.jp,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(course.title, style = MaterialTheme.typography.titleLarge)
            Text(
                course.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp),
            )
            Text(
                "${course.lessonCount} レッスン · 約 ${course.totalMinutes} 分 · " +
                    "進捗 $done/${course.lessonCount}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp),
            )
            LinearProgressIndicator(
                progress = {
                    if (course.lessonCount == 0) 0f
                    else done.toFloat() / course.lessonCount
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
            )
        }
    }
}

@Composable
fun CourseDetailScreen(
    level: CourseLevel,
    vm: SimulatorViewModel,
    onOpenLesson: (String) -> Unit,
    onBack: () -> Unit,
) {
    val course = Curriculum.courses.first { it.level == level }
    val completed by vm.completed.collectAsState()

    LazyColumn(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp),
    ) {
        item { BackHeader(course.title, onBack) }
        item {
            Text(
                course.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        course.modules.forEachIndexed { mi, module ->
            item {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Text(
                        "モジュール ${mi + 1}: ${module.title}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        module.summary,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            items(module.lessons) { lesson ->
                LessonRow(lesson, lesson.id in completed) {
                    onOpenLesson(lesson.id)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LessonRow(lesson: Lesson, done: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (done)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            else MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    (if (done) "✓ " else "") + lesson.title,
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    "約 ${lesson.minutes} 分",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
            Text("›", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun LessonScreen(
    lessonId: String,
    vm: SimulatorViewModel,
    onNavigateRoute: (String) -> Unit,
    onBack: () -> Unit,
) {
    val lesson = Curriculum.lesson(lessonId)
    val completed by vm.completed.collectAsState()
    if (lesson == null) {
        Column(Modifier.fillMaxSize()) {
            BackHeader("レッスン", onBack)
            Text("レッスンが見つかりません。", Modifier.padding(16.dp))
        }
        return
    }
    val done = lesson.id in completed

    LazyColumn(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp),
    ) {
        item { BackHeader(lesson.title, onBack) }
        items(lesson.blocks) { block ->
            Box(Modifier.padding(horizontal = 16.dp)) {
                BlockView(block, onNavigateRoute)
            }
        }
        item {
            Button(
                onClick = { vm.toggleCompleted(lesson.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Text(if (done) "完了を取り消す" else "このレッスンを完了にする")
            }
        }
    }
}

@Composable
private fun BlockView(block: Block, onNavigateRoute: (String) -> Unit) {
    when (block) {
        is Block.Heading -> Text(
            block.text,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 8.dp),
        )

        is Block.Para -> Text(
            block.text,
            style = MaterialTheme.typography.bodyMedium,
        )

        is Block.Bullets -> Column {
            block.items.forEach {
                Row(Modifier.padding(vertical = 2.dp)) {
                    Text("・", style = MaterialTheme.typography.bodyMedium)
                    Text(it, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        is Block.Code -> CodeBlock(block.text)

        is Block.Callout -> {
            val color = when (block.kind) {
                CalloutKind.WARN -> MaterialTheme.colorScheme.error
                CalloutKind.TIP -> MaterialTheme.colorScheme.secondary
                CalloutKind.KEYWORD -> MaterialTheme.colorScheme.tertiary
                CalloutKind.NOTE -> MaterialTheme.colorScheme.primary
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.10f))
                    .border(1.dp, color.copy(0.4f), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        "【${block.kind.jp}】",
                        style = MaterialTheme.typography.labelMedium,
                        color = color,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        block.text,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }

        is Block.Diagram -> DiagramView(block.type, block.caption)

        is Block.CaseStudy -> Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.secondary.copy(0.10f))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.secondary.copy(0.4f),
                    RoundedCornerShape(8.dp),
                )
                .padding(14.dp)
        ) {
            Column {
                Text(
                    "実例: ${block.title}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    block.text,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }

        is Block.Quiz -> QuizView(block)

        is Block.TryIt -> SectionCard(block.label) {
            Text(
                block.desc,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp),
            )
            Button(
                onClick = { onNavigateRoute(block.route) },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("サンドボックスで試す →") }
        }

        is Block.FileMap -> Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(14.dp)
        ) {
            Text(
                "ファイル対応: ${block.title}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            block.links.forEach { l ->
                Column(Modifier.padding(top = 10.dp)) {
                    Text(
                        l.from,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        "   └─ 紐づく →",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                    Text(
                        l.to,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        l.note,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        }

        is Block.Defaults -> Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(14.dp)
        ) {
            Text(
                "AOSP デフォルト: ${block.title}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            block.rows.forEach { r ->
                Column(Modifier.padding(top = 10.dp)) {
                    Text(
                        r.item,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "デフォルト値: ${r.aospValue}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                    Text(
                        "定義場所: ${r.definedIn}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.8f),
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizView(quiz: Block.Quiz) {
    var picked by remember { mutableStateOf(-1) }
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.tertiary.copy(0.08f))
            .border(
                1.dp,
                MaterialTheme.colorScheme.tertiary.copy(0.4f),
                RoundedCornerShape(8.dp),
            )
            .padding(14.dp)
    ) {
        Column {
            Text(
                "確認クイズ",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                quiz.question,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp),
            )
            quiz.options.forEachIndexed { i, opt ->
                val show = picked != -1
                val correct = i == quiz.answerIndex
                OutlinedButton(
                    onClick = { picked = i },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                ) {
                    Text(
                        (if (show && correct) "✓ "
                        else if (show && i == picked) "✗ " else "") + opt,
                        color = if (show && correct)
                            MaterialTheme.colorScheme.primary
                        else if (show && i == picked)
                            MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            AnimatedVisibility(picked != -1) {
                Text(
                    quiz.explanation,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}
