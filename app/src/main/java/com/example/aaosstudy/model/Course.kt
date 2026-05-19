package com.example.aaosstudy.model

/**
 * Curriculum model. A [Course] (level) holds [Module]s, each holding
 * [Lesson]s. A lesson is an ordered list of [Block]s rendered top to bottom.
 *
 * Content is authored in Japanese for the learner. Helper functions at the
 * bottom keep lesson definitions terse.
 */
enum class CourseLevel(val jp: String, val tag: String) {
    BEGINNER("初学者向け", "BEGINNER"),
    INTERMEDIATE("中級向け", "INTERMEDIATE"),
    ADVANCED("上級向け", "ADVANCED"),
}

data class Course(
    val level: CourseLevel,
    val title: String,
    val subtitle: String,
    val modules: List<Module>,
) {
    val lessonCount: Int get() = modules.sumOf { it.lessons.size }
    val totalMinutes: Int get() =
        modules.sumOf { m -> m.lessons.sumOf { it.minutes } }
}

data class Module(
    val title: String,
    val summary: String,
    val lessons: List<Lesson>,
)

data class Lesson(
    val id: String,
    val title: String,
    val minutes: Int,
    val blocks: List<Block>,
)

sealed interface Block {
    data class Heading(val text: String) : Block
    data class Para(val text: String) : Block
    data class Bullets(val items: List<String>) : Block
    data class Code(val text: String, val lang: String = "kotlin") : Block
    data class Callout(val kind: CalloutKind, val text: String) : Block
    data class Diagram(val type: DiagramType, val caption: String) : Block
    data class CaseStudy(val title: String, val text: String) : Block
    data class Quiz(
        val question: String,
        val options: List<String>,
        val answerIndex: Int,
        val explanation: String,
    ) : Block
    data class TryIt(
        val route: String,
        val label: String,
        val desc: String,
    ) : Block
    data class FileMap(val title: String, val links: List<FileLink>) : Block
    data class Defaults(val title: String, val rows: List<DefaultRow>) : Block
}

/** "このファイルの ○○ が、このファイルの ×× に紐づく" を表す。 */
data class FileLink(
    val from: String,
    val to: String,
    val note: String,
)

/** AOSP のデフォルト値 + それがどこで定義されているか。 */
data class DefaultRow(
    val item: String,
    val aospValue: String,
    val definedIn: String,
)

enum class CalloutKind(val jp: String) {
    NOTE("ポイント"), TIP("ヒント"), WARN("注意"), KEYWORD("用語"),
}

/** Compose-drawn diagrams (no bitmap assets needed). */
enum class DiagramType {
    LAYER_STACK,
    DATA_FLOW,
    RRO_OVERLAY,
    ECU_NETWORK,
    HVAC_ZONES,
    CLUSTER_VS_IVI,
    BOOT_FLOW,
    PERMISSION_FLOW,
    BUILD_PIPELINE,
    PROPERTY_ANATOMY,
}

// --- terse authoring helpers ---
fun h(text: String): Block = Block.Heading(text)
fun p(text: String): Block = Block.Para(text)
fun b(vararg items: String): Block = Block.Bullets(items.toList())
fun code(text: String, lang: String = "kotlin"): Block =
    Block.Code(text.trim(), lang)
fun note(text: String): Block = Block.Callout(CalloutKind.NOTE, text)
fun tip(text: String): Block = Block.Callout(CalloutKind.TIP, text)
fun warn(text: String): Block = Block.Callout(CalloutKind.WARN, text)
fun term(text: String): Block = Block.Callout(CalloutKind.KEYWORD, text)
fun dia(type: DiagramType, caption: String): Block =
    Block.Diagram(type, caption)
fun case(title: String, text: String): Block = Block.CaseStudy(title, text)
fun quiz(
    question: String,
    options: List<String>,
    answerIndex: Int,
    explanation: String,
): Block = Block.Quiz(question, options, answerIndex, explanation)
fun tryIt(route: String, label: String, desc: String): Block =
    Block.TryIt(route, label, desc)
fun link(from: String, to: String, note: String): FileLink =
    FileLink(from, to, note)
fun fileMap(title: String, vararg links: FileLink): Block =
    Block.FileMap(title, links.toList())
fun def(item: String, aospValue: String, definedIn: String): DefaultRow =
    DefaultRow(item, aospValue, definedIn)
fun defaults(title: String, vararg rows: DefaultRow): Block =
    Block.Defaults(title, rows.toList())

object Curriculum {
    val courses: List<Course> by lazy {
        listOf(BeginnerCourse.course, IntermediateCourse.course, AdvancedCourse.course)
    }
    fun lesson(id: String): Lesson? =
        courses.asSequence()
            .flatMap { it.modules }
            .flatMap { it.lessons }
            .firstOrNull { it.id == id }
}
