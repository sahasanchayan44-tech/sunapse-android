package com.example.synapse.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.synapse.auth.AuthViewModel
import com.example.synapse.models.LessonModel
import com.example.synapse.models.SubjectModel
import com.example.synapse.models.TopicModel
import com.example.synapse.ui.components.SynapseAnimatedBottomNav
import com.example.synapse.ui.ThemeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    authViewModel: AuthViewModel = viewModel(),
    themeViewModel: ThemeViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedIndex by remember { mutableIntStateOf(0) }
    var dialPosition by remember { mutableFloatStateOf(0f) }
    
    var isProfileOpen by remember { mutableStateOf(false) }
    var isLevelsTrailOpen by remember { mutableStateOf(false) }
    var selectedSubject by remember { mutableStateOf<SubjectModel?>(null) }
    var selectedTopic by remember { mutableStateOf<TopicModel?>(null) }
    var selectedLesson by remember { mutableStateOf<LessonModel?>(null) }
    var isShowingStudyCards by remember { mutableStateOf(false) }
    var isNavVisible by remember { mutableStateOf(true) }
    var cameFromProfile by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var lastBackPressTime by remember { mutableLongStateOf(0L) }

    BackHandler {
        when {
            drawerState.isOpen -> scope.launch { drawerState.close() }
            isProfileOpen -> isProfileOpen = false
            isLevelsTrailOpen -> isLevelsTrailOpen = false
            isShowingStudyCards -> isShowingStudyCards = false
            selectedLesson != null -> selectedLesson = null
            selectedTopic != null -> selectedTopic = null
            selectedSubject != null -> selectedSubject = null
            selectedIndex != 0 -> selectedIndex = 0
            else -> {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastBackPressTime < 2000) {
                    (context as? Activity)?.finish()
                } else {
                    lastBackPressTime = currentTime
                    Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val navItems = listOf(
        com.example.synapse.ui.components.NavItem(Icons.Default.Dashboard, "Dashboard", Color(0xFF3B82F6)),
        com.example.synapse.ui.components.NavItem(Icons.Default.Description, "Quizzes", Color(0xFF10B981)),
        com.example.synapse.ui.components.NavItem(Icons.Default.Flag, "Goals", Color(0xFFFF3838))
    )

    val drawerScreens = listOf(
        "Dashboard",
        "Quizzes",
        "Goals",
        "Settings"
    )

    val drawerIcons = listOf(
        Icons.Default.Dashboard,
        Icons.Default.Description,
        Icons.Default.Flag,
        Icons.Default.Settings
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.width(300.dp)
            ) {
                DrawerHeader()
                Spacer(modifier = Modifier.height(12.dp))
                drawerScreens.forEachIndexed { index, title ->
                    DrawerItem(
                        title = title,
                        icon = drawerIcons[index],
                        isSelected = selectedIndex == index
                    ) {
                        selectedIndex = index
                        cameFromProfile = false
                        selectedSubject = null // Reset subject navigation when using drawer
                        scope.launch { drawerState.close() }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                Spacer(modifier = Modifier.weight(1f))
                LogoutButton { authViewModel.signOut() }
            }
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                if (selectedIndex < 3 && isNavVisible) {
                    SynapseAnimatedBottomNav(
                        selectedIndex = selectedIndex,
                        onItemSelected = { selectedIndex = it },
                        items = navItems,
                        onDialUpdate = { dialPosition = it }
                    )
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (isProfileOpen) {
                        ProfileScreen(
                            authViewModel = authViewModel, 
                            themeViewModel = themeViewModel, 
                            onBack = { isProfileOpen = false },
                            onSettingsClick = { 
                                selectedIndex = 3 
                                cameFromProfile = true
                                isProfileOpen = false 
                            }
                        )
                    } else if (isLevelsTrailOpen) {
                        LevelsTrailScreen(authViewModel, onBack = { isLevelsTrailOpen = false })
                    } else if (selectedSubject != null) {
                        val isDark = isSystemInDarkTheme()
                        if (isShowingStudyCards && selectedTopic != null && selectedLesson != null) {
                            FlashcardChaptersScreen(
                                subject = selectedSubject!!,
                                topic = selectedTopic!!,
                                lesson = selectedLesson!!,
                                isDark = isDark,
                                onBack = { isShowingStudyCards = false }
                            )
                        } else if (selectedLesson != null && selectedTopic != null) {
                            FlashcardDetailScreen(
                                subject = selectedSubject!!,
                                topic = selectedTopic!!,
                                lesson = selectedLesson!!,
                                isDark = isDark,
                                onBack = { selectedLesson = null },
                                onOpenChapters = { isShowingStudyCards = true }
                            )
                        } else if (selectedTopic != null) {
                            LessonListScreen(
                                authViewModel = authViewModel,
                                subject = selectedSubject!!,
                                topic = selectedTopic!!,
                                isDark = isDark,
                                onBack = { selectedTopic = null },
                                onLessonClick = { selectedLesson = it }
                            )
                        } else {
                            TopicListScreen(
                                authViewModel = authViewModel,
                                subject = selectedSubject!!,
                                isDark = isDark,
                                onBack = { selectedSubject = null },
                                onTopicClick = { selectedTopic = it }
                            )
                        }
                    } else {
                        when (selectedIndex) {
                            0 -> DashboardScreen(
                                authViewModel = authViewModel, 
                                onProfileClick = { isProfileOpen = true },
                                onTrophyClick = { isLevelsTrailOpen = true },
                                onFlashcardClick = { selectedSubject = it },
                                dialPosition = dialPosition
                            )
                            1 -> QuizzesScreen(authViewModel = authViewModel)
                            2 -> GoalsScreen(authViewModel = authViewModel)
                            3 -> SettingsPlaceholder(
                                onBack = {
                                    if (cameFromProfile) {
                                        isProfileOpen = true
                                        selectedIndex = 0 
                                    } else {
                                        selectedIndex = 0 
                                    }
                                }
                            )
                            else -> PlaceholderScreen(drawerScreens.getOrElse(selectedIndex) { "Unknown" })
                        }
                    }
                }
                
                if (authViewModel.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsPlaceholder(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Settings Screen",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack) {
                Text(
                    text = "Back",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
                )
            }
        }
    }
}

@Composable
fun DrawerHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(64.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 4.dp
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "STUDY MANAGER",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 1.sp
                )
            )
        }
    }
}

@Composable
fun ColumnScope.DrawerItem(title: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable { onClick() },
        color = if (isSelected) MaterialTheme.colorScheme.surface.copy(alpha = 0.5f) else Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontStyle = FontStyle.Italic
                )
            )
        }
    }
}

@Composable
fun ColumnScope.LogoutButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .clickable { onClick() },
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "LOGOUT",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                )
            )
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "$title Screen",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}
