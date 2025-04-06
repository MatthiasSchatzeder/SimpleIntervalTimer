package com.example.simpleintervaltimer.common.navigation

import android.content.pm.ActivityInfo
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.simpleintervaltimer.R
import com.example.simpleintervaltimer.common.presentation.LockScreenOrientation
import com.example.simpleintervaltimer.timer.domain.models.TimeInterval
import com.example.simpleintervaltimer.timer.presentation.edit_interval.EditStoredTimeIntervalScreen
import com.example.simpleintervaltimer.timer.presentation.home.HomeScreen
import com.example.simpleintervaltimer.timer.presentation.interval_list.IntervalListScreen
import com.example.simpleintervaltimer.timer.presentation.timer.TimerScreen
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Serializable
private object HomeRoute

@Serializable
private object IntervalListRoute

@Serializable
private data class TimerRoute(val timeInterval: TimeInterval)

@Serializable
private data class EditStoredTimeIntervalRoute(val storedTimeIntervalIdHexString: String)

@Composable
fun App() {
	val navController = rememberNavController()
	Scaffold(
		bottomBar = { BottomNavBar(navController) }
	) { innerPadding ->
		LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
		NavHost(
			navController = navController,
			startDestination = HomeRoute,
			modifier = Modifier.padding(innerPadding),
			enterTransition = { EnterTransition.None },
			exitTransition = { ExitTransition.None }
		) {
			composable<HomeRoute> {
				HomeScreen(
					onStartTimer = { timeInterval ->
						navController.navigate(TimerRoute(timeInterval))
					}
				)
			}

			composable<IntervalListRoute> {
				IntervalListScreen(
					onStartTimer = { timeInterval ->
						navController.navigate(TimerRoute(timeInterval))
					},
					onEditTimeInterval = { storedTimeIntervalIdHexString ->
						navController.navigate(EditStoredTimeIntervalRoute(storedTimeIntervalIdHexString))
					}
				)
			}

			composable<EditStoredTimeIntervalRoute> {
				EditStoredTimeIntervalScreen(
					storedTimeIntervalIdHexString = it.toRoute<EditStoredTimeIntervalRoute>().storedTimeIntervalIdHexString,
					onEditFinished = {
						navController.popBackStack()
					}
				)
			}

			composable<TimerRoute>(
				typeMap = mapOf(
					typeOf<TimeInterval>() to TimeInterval.CustomNavType
				)
			) {
				val timerRoute = it.toRoute<TimerRoute>()
				TimerScreen(
					timeInterval = timerRoute.timeInterval,
					onEndTimer = { navController.popBackStack() }
				)
			}
		}
	}
}

private data class TopLevelRoute<T : Any>(@StringRes val nameRes: Int, val route: T, val icon: ImageVector)

private val topLevelRoutes = listOf(
	TopLevelRoute(R.string.home, HomeRoute, Icons.Filled.Home),
	TopLevelRoute(R.string.my_intervals, IntervalListRoute, Icons.AutoMirrored.Filled.List)
)

@Composable
private fun BottomNavBar(navController: NavHostController) {
	val navBackStackEntry by navController.currentBackStackEntryAsState()
	val currentDestination = navBackStackEntry?.destination
	AnimatedVisibility(
		visible = showBottomNavigation(currentDestination),
		enter = expandVertically(),
		exit = shrinkVertically()
	) {
		NavigationBar {
			topLevelRoutes.forEach { topLevelRoute ->
				NavigationBarItem(
					label = { Text(stringResource(topLevelRoute.nameRes)) },
					icon = { Icon(topLevelRoute.icon, stringResource(topLevelRoute.nameRes)) },
					selected = currentDestination?.hierarchy?.any { it.hasRoute(topLevelRoute.route::class) } == true,
					onClick = {
						navController.navigate(topLevelRoute.route) {
							popUpTo(navController.graph.findStartDestination().id) {
								saveState = true
							}
							launchSingleTop = true
							restoreState = true
						}
					}
				)
			}
		}
	}
}

private fun showBottomNavigation(navDestination: NavDestination?): Boolean {
	return topLevelRoutes.any { navDestination?.hasRoute(it.route::class) == true }
}
