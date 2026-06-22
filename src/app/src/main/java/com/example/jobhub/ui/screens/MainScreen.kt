package com.example.jobhub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jobhub.ui.theme.BackgroundLight
import com.example.jobhub.ui.theme.BluePrimary
import com.example.jobhub.ui.viewmodel.ApplicationsViewModel
import com.example.jobhub.ui.viewmodel.BookmarksViewModel
import com.example.jobhub.ui.viewmodel.DashboardViewModel
import com.example.jobhub.ui.viewmodel.HomeViewModel
import com.example.jobhub.ui.viewmodel.ProfileViewModel

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : BottomNavItem("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Jobs : BottomNavItem("jobs", "Jobs", Icons.Default.Work)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}

@Composable
fun MainScreen(
    dashboardViewModel: DashboardViewModel,
    homeViewModel: HomeViewModel,
    profileViewModel: ProfileViewModel,
    applicationsViewModel: ApplicationsViewModel,
    bookmarksViewModel: BookmarksViewModel,
    onNavigateToJobDetail: (Int) -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Determine if we're on a main tab (show top & bottom bars) or a sub-screen
    val mainRoutes = listOf(
        BottomNavItem.Jobs.route,
        BottomNavItem.Dashboard.route,
        BottomNavItem.Profile.route
    )
    val isMainRoute = currentRoute in mainRoutes

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            if (isMainRoute && currentRoute != BottomNavItem.Profile.route) {
                CenterAlignedTopAppBar(
                    title = { Text("JOBHUB", fontWeight = FontWeight.Bold, color = BluePrimary) },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.navigate(BottomNavItem.Jobs.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = BluePrimary
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate(BottomNavItem.Profile.route) }) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        color = Color(0xFFE8EAF6),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = BluePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    ),
                    modifier = Modifier.shadow(2.dp)
                )
            }
        },
        bottomBar = {
            if (isMainRoute) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Jobs.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Jobs.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onJobClick = { jobId -> onNavigateToJobDetail(jobId) }
                )
            }
            composable(BottomNavItem.Dashboard.route) {
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onApplicationClick = { jobId -> onNavigateToJobDetail(jobId) },
                    onBrowseJobsClick = {
                        navController.navigate(BottomNavItem.Jobs.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onViewAllApplicationsClick = {
                        navController.navigate("applications")
                    },
                    onViewBookmarksClick = {
                        navController.navigate("bookmarks")
                    },
                    onLogout = onLogout
                )
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    viewModel = profileViewModel,
                    onLogout = onLogout
                )
            }
            composable("applications") {
                ApplicationsScreen(
                    viewModel = applicationsViewModel,
                    onBackClick = { navController.popBackStack() },
                    onApplicationClick = { jobId -> onNavigateToJobDetail(jobId) }
                )
            }
            composable("bookmarks") {
                BookmarksScreen(
                    viewModel = bookmarksViewModel,
                    onBackClick = { navController.popBackStack() },
                    onJobClick = { jobId -> onNavigateToJobDetail(jobId) }
                )
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Jobs,
        BottomNavItem.Dashboard,
        BottomNavItem.Profile
    )
    
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BluePrimary,
                    selectedTextColor = BluePrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = BluePrimary.copy(alpha = 0.1f)
                )
            )
        }
    }
}
