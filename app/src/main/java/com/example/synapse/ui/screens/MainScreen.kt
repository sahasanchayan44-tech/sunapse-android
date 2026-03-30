package com.example.synapse.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.synapse.auth.AuthViewModel
import com.example.synapse.ui.ThemeViewModel
import com.example.synapse.ui.components.NavItem
import com.example.synapse.ui.components.SynapseAnimatedBottomNav
import com.example.synapse.ui.components.FlashcardData
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(themeViewModel: ThemeViewModel, authViewModel: AuthViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedIndex by remember { mutableStateOf(0) }
    var showNotes by remember { mutableStateOf(false) }
    var isProfileOpen by remember { mutableStateOf(false) }
    var isLevelsTrailOpen by remember { mutableStateOf(false) }
    var dialPosition by remember { mutableStateOf(0f) }
    var selectedFlashcard by remember { mutableStateOf<FlashcardData?>(null) }

    val navItems = listOf(
        NavItem(Icons.Default.Dashboard, "Dashboard", Color(0xFF3B82F6)),
        NavItem(Icons.Default.Description, "Quizzes", Color(0xFF10B981)),
        NavItem(Icons.Default.Memory, "Flashcards", Color(0xFFF59E0B)),
        NavItem(Icons.Default.Flag, "Goals", Color(0xFFFF3838))
    )

    val drawerScreens = listOf(
        "Dashboard",
        "Quizzes",
        "Flashcards",
        "Goals",
        "Settings"
    )

    val drawerIcons = listOf(
        Icons.Default.Dashboard,
        Icons.Default.Description,
        Icons.Default.Memory,
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
                if (selectedIndex < 4 && !isProfileOpen && !isLevelsTrailOpen && selectedFlashcard == null) {
                    SynapseAnimatedBottomNav(
                        selectedIndex = selectedIndex,
                        onItemSelected = { selectedIndex = it },
                        items = navItems,
                        onDialUpdate = { dialPosition = it }
                    )
                }
            },
            floatingActionButton = {
                if (selectedIndex == 0 && !isProfileOpen && !isLevelsTrailOpen && selectedFlashcard == null) {
                    FloatingActionButton(
                        onClick = { showNotes = !showNotes },
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        shape = CircleShape,
                        modifier = Modifier.offset(y = (-20).dp)
                    ) {
                        Icon(imageVector = if (showNotes) Icons.Default.Close else Icons.Default.EditNote, contentDescription = "Notes", modifier = Modifier.size(28.dp))
                    }
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .then(if (showNotes) Modifier.blur(12.dp) else Modifier)
                ) {
                    if (isProfileOpen) {
                        ProfileScreen(
                            authViewModel = authViewModel, 
                            themeViewModel = themeViewModel, 
                            onBack = { isProfileOpen = false },
                            onSettingsClick = { 
                                selectedIndex = 4
                                isProfileOpen = false 
                            }
                        )
                    } else if (isLevelsTrailOpen) {
                        LevelsTrailScreen(authViewModel, onBack = { isLevelsTrailOpen = false })
                    } else if (selectedFlashcard != null) {
                        FlashcardChaptersScreen(flashcard = selectedFlashcard!!, isDark = isSystemInDarkTheme(), onBack = { selectedFlashcard = null })
                    } else {
                        when (selectedIndex) {
                            0 -> DashboardScreen(
                                authViewModel = authViewModel, 
                                onProfileClick = { isProfileOpen = true },
                                onTrophyClick = { isLevelsTrailOpen = true },
                                onFlashcardClick = { selectedFlashcard = it },
                                dialPosition = dialPosition
                            )
                            1 -> QuizzesScreen()
                            2 -> FlashcardsScreen(dialPosition = dialPosition)
                            3 -> GoalsScreen()
                            4 -> SettingsScreen(themeViewModel, authViewModel)
                            else -> PlaceholderScreen(drawerScreens[selectedIndex])
                        }
                    }
                }

                NotesFloatingWindow(isVisible = showNotes, onDismiss = { showNotes = false })
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
fun NotesFloatingWindow(isVisible: Boolean, onDismiss: () -> Unit) {
    var noteText by remember { mutableStateOf("") }
    val isDark = isSystemInDarkTheme()

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDark) Color.Black.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(450.dp)
                    .clickable(enabled = false) { },
                shape = RoundedCornerShape(32.dp),
                color = if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.8f),
                border = BorderStroke(
                    1.dp, 
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.5f),
                            Color.White.copy(alpha = 0.1f)
                        )
                    )
                ),
                shadowElevation = if (isDark) 0.dp else 12.dp
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "QUICK NOTES",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            letterSpacing = 1.5.sp,
                            color = if (isDark) Color.White else Color.Black
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(
                                Icons.Default.Close, 
                                contentDescription = "Close", 
                                tint = (if (isDark) Color.White else Color.Black).copy(alpha = 0.6f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        placeholder = { 
                            Text(
                                "Write your thoughts here...", 
                                color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.4f)
                            ) 
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = if (isDark) Color.White else Color.Black,
                            focusedTextColor = if (isDark) Color.White else Color.Black,
                            unfocusedTextColor = if (isDark) Color.White else Color.Black
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = { onDismiss() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) Color.White.copy(alpha = 0.2f) else Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            "SAVE NOTE", 
                            fontWeight = FontWeight.ExtraBold, 
                            fontStyle = FontStyle.Italic, 
                            letterSpacing = 1.sp
                        )
                    }
                }
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
fun SettingsScreen(themeViewModel: ThemeViewModel, authViewModel: AuthViewModel) {
    val isDark = isSystemInDarkTheme()
    val neonColor = if (isDark) Color(0xFFBB86FC) else Color(0xFF6200EE)
    val cardBg = MaterialTheme.colorScheme.surface

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "SETTINGS",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Individual Settings Tabs (Cards)
        SettingsTabCard(
            title = if (themeViewModel.isDarkMode) "Dark Mode" else "Light Mode",
            icon = if (themeViewModel.isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
            neonColor = neonColor,
            cardBg = cardBg
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
            title = "Logout",
            icon = Icons.AutoMirrored.Filled.Logout,
            neonColor = Color.Red,
            cardBg = cardBg,
            onClick = { authViewModel.signOut() }
        ) {
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.Red.copy(alpha = 0.3f))
        }
    }
}
