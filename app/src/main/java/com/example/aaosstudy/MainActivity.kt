package com.example.aaosstudy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.aaosstudy.model.CourseLevel
import com.example.aaosstudy.screens.AdbBridgeScreen
import com.example.aaosstudy.screens.ArchitectureScreen
import com.example.aaosstudy.screens.CarPropertyExplorerScreen
import com.example.aaosstudy.screens.CourseDetailScreen
import com.example.aaosstudy.screens.CoursesHomeScreen
import com.example.aaosstudy.screens.HomeScreen
import com.example.aaosstudy.screens.LessonScreen
import com.example.aaosstudy.screens.RroThemeLabScreen
import com.example.aaosstudy.screens.ScenariosScreen
import com.example.aaosstudy.screens.VhalPlaygroundScreen
import com.example.aaosstudy.state.SimulatorViewModel
import com.example.aaosstudy.ui.theme.AAOSStudyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AAOSStudyApp() }
    }
}

object Routes {
    const val HOME = "home"
    const val VHAL = "vhal"
    const val RRO = "rro"
    const val API = "api"
    const val ARCH = "arch"
    const val SCENARIOS = "scenarios"
    const val COURSES = "courses"
    const val COURSE = "course"
    const val LESSON = "lesson"
    const val ADB = "adb"
}

@Composable
fun AAOSStudyApp() {
    AAOSStudyTheme {
        val nav = rememberNavController()
        val vm: SimulatorViewModel = viewModel()
        Scaffold { inner ->
            NavHost(
                navController = nav,
                startDestination = Routes.HOME,
                modifier = Modifier.padding(inner),
            ) {
                composable(Routes.HOME) {
                    HomeScreen(onOpen = { nav.navigate(it) })
                }
                composable(Routes.VHAL) {
                    VhalPlaygroundScreen(vm) { nav.popBackStack() }
                }
                composable(Routes.RRO) {
                    RroThemeLabScreen(vm) { nav.popBackStack() }
                }
                composable(Routes.API) {
                    CarPropertyExplorerScreen { nav.popBackStack() }
                }
                composable(Routes.ARCH) {
                    ArchitectureScreen { nav.popBackStack() }
                }
                composable(Routes.SCENARIOS) {
                    ScenariosScreen(vm) { nav.popBackStack() }
                }
                composable(Routes.ADB) {
                    AdbBridgeScreen(vm) { nav.popBackStack() }
                }
                composable(Routes.COURSES) {
                    CoursesHomeScreen(
                        vm = vm,
                        onOpenCourse = {
                            nav.navigate("${Routes.COURSE}/${it.name}")
                        },
                        onBack = { nav.popBackStack() },
                    )
                }
                composable(
                    "${Routes.COURSE}/{level}",
                    arguments = listOf(navArgument("level") {
                        type = NavType.StringType
                    }),
                ) { entry ->
                    val level = CourseLevel.valueOf(
                        entry.arguments?.getString("level")
                            ?: CourseLevel.BEGINNER.name
                    )
                    CourseDetailScreen(
                        level = level,
                        vm = vm,
                        onOpenLesson = {
                            nav.navigate("${Routes.LESSON}/$it")
                        },
                        onBack = { nav.popBackStack() },
                    )
                }
                composable(
                    "${Routes.LESSON}/{id}",
                    arguments = listOf(navArgument("id") {
                        type = NavType.StringType
                    }),
                ) { entry ->
                    LessonScreen(
                        lessonId = entry.arguments?.getString("id") ?: "",
                        vm = vm,
                        onNavigateRoute = { nav.navigate(it) },
                        onBack = { nav.popBackStack() },
                    )
                }
            }
        }
    }
}
