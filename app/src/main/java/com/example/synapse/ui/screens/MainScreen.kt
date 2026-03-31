package com.example.synapse.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.synapse.auth.AuthViewModel
import com.example.synapse.models.LessonModel
import com.example.synapse.models.SubjectModel
import com.example.synapse.models.TopicModel
import com.example.synapse.ui.ThemeViewModel
import com.example.synapse.ui.components.NavItem
import com.example.synapse.ui.components.SynapseAnimatedBottomNav
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(themeViewModel: ThemeViewModel, authViewModel: AuthViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedIndex by remember { mutableIntStateOf(0) }
    var isProfileOpen by remember { mutableStateOf(false) }
    var isLevelsTrailOpen by remember { mutableStateOf(false) }
    var dialPosition by remember { mutableFloatStateOf(0f) }
    
    // Detailed navigation states for Flashcards from Firestore
    var selectedSubject by remember { mutableStateOf<SubjectModel?>(null) }
    var selectedTopic by remember { mutableStateOf<TopicModel?>(null) }
    var selectedLesson by remember { mutableStateOf<LessonModel?>(null) }
    var isShowingStudyCards by remember { mutableStateOf(false) }
    
    val isNavVisible = !isProfileOpen && !isLevelsTrailOpen && selectedSubject == null
    
    // Track if we came to settings from profile to provide correct back navigation
    var cameFromProfile by remember { mutableStateOf(false) }

    val navItems = listOf(
        NavItem(Icons.Default.Dashboard, "Dashboard", Color(0xFF3B82F6)),
        NavItem(Icons.Default.Description, "Quizzes", Color(0xFF10B981)),
        NavItem(Icons.Default.Flag, "Goals", Color(0xFFFF3838))
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
                            1 -> QuizzesScreen()
                            2 -> GoalsScreen()
                            3 -> SettingsScreen(
                                themeViewModel = themeViewModel, 
                                authViewModel = authViewModel,
                                onBack = {
                                    if (cameFromProfile) {
                                        isProfileOpen = true
                                        selectedIndex = 0 
                                    } else {
                                        selectedIndex = 0 
                                    }
                                }
                            )
                            else -> PlaceholderScreen(drawerScreens[selectedIndex])
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
fun SettingsTabCard(
    title: String,
    icon: ImageVector,
    neonColor: Color,
    cardBg: Color,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = neonColor.copy(alpha = 0.4f),
                ambientColor = neonColor.copy(alpha = 0.4f)
            ),
        shape = RoundedCornerShape(20.dp),
        color = cardBg,
        border = BorderStroke(
            1.dp, 
            Brush.linearGradient(
                listOf(Color.White.copy(alpha = 0.3f), Color.Transparent)
            )
        )
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(neonColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = neonColor, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            content()
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
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurface
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
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontStyle = FontStyle.Italic
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
                tint = Color.Red,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Logout",
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PlaceholderScreen(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

@Composable
fun SettingsScreen(
    themeViewModel: ThemeViewModel, 
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val neonColor = if (isDark) Color(0xFFBB86FC) else Color(0xFF6200EE)
    val cardBg = MaterialTheme.colorScheme.surface

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SETTINGS",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        SettingsTabCard(
            title = if (themeViewModel.isDarkMode) "Dark Mode" else "Light Mode",
            icon = if (themeViewModel.isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
            neonColor = neonColor,
            cardBg = cardBg,
            onClick = { themeViewModel.toggleDarkMode() }
        ) {
            Switch(
                checked = themeViewModel.isDarkMode,
                onCheckedChange = { themeViewModel.toggleDarkMode() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = neonColor,
                    checkedTrackColor = neonColor.copy(alpha = 0.5f)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsTabCard(
            title = "Cloud Sync",
            icon = Icons.Default.CloudSync,
            neonColor = Color(0xFF3B82F6),
            cardBg = cardBg
        ) {
            Text("Active", color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fixed Seed DB button here by using a custom hex color for Orange
        val maintenanceColor = Color(0xFFFFA500)
        SettingsTabCard(
            title = "Maintenance",
            icon = Icons.Default.Build,
            neonColor = maintenanceColor,
            cardBg = cardBg,
            onClick = { authViewModel.seedData() }
        ) {
            Text(
                text = "SEED DB",
                color = maintenanceColor,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsTabCard(
            title = "Account",
            icon = Icons.AutoMirrored.Filled.Logout,
            neonColor = Color.Red,
            cardBg = cardBg,
            onClick = { authViewModel.signOut() }
        ) {
            Text(
                text = "LOGOUT",
                color = Color.Red,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }
    }
}
