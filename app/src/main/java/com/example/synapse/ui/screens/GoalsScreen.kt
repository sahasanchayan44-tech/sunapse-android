package com.example.synapse.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.synapse.auth.AuthViewModel
import com.example.synapse.models.TaskModel
import com.example.synapse.models.UserStatsModel
import com.example.synapse.ui.components.NeumorphicCard
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(authViewModel: AuthViewModel) {
    val currentDate = remember { SimpleDateFormat("MMMM dd", Locale.getDefault()).format(Date()).uppercase() }
    var selectedTab by remember { mutableIntStateOf(0) } // 0 for Quests, 1 for Leaderboard

    val bgColor = MaterialTheme.colorScheme.background
    val contentColor = MaterialTheme.colorScheme.onBackground
    val isDark = bgColor.luminance() < 0.5f
    
    val shadowColor = if (isDark) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.15f)
    val glassColor = if (isDark) Color.Black.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.3f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Background Effects
        Box(modifier = Modifier.fillMaxSize().blur(60.dp), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.size(400.dp, 600.dp).background(Brush.radialGradient(listOf(shadowColor, Color.Transparent), radius = 800f)))
        }
        Box(modifier = Modifier.fillMaxSize().background(glassColor))

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 32.dp)) {
            // Screen Header - Moved from QuestsTab
            Row(
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween, 
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DAILY QUESTS", 
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 24.sp, 
                            fontWeight = FontWeight.Black, 
                            color = contentColor, 
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = currentDate, 
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontSize = 14.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = contentColor.copy(alpha = 0.6f)
                        )
                    )
                }
                Icon(Icons.Default.AutoAwesome, contentDescription = "Generated", tint = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tab Selector - Under the daily quests text
            NeumorphicCard(
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(30.dp),
                elevation = 4.dp
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                    val tabWidth = maxWidth / 2
                    val indicatorOffset by animateDpAsState(
                        targetValue = if (selectedTab == 1) tabWidth else 0.dp,
                        animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioNoBouncy),
                        label = "tabIndicator"
                    )
                    
                    Box(
                        modifier = Modifier
                            .width(tabWidth)
                            .fillMaxHeight()
                            .offset(x = indicatorOffset)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(26.dp))
                    )
                    
                    Row(modifier = Modifier.fillMaxSize()) {
                        GoalsTabButton(
                            text = "Quests",
                            isSelected = selectedTab == 0,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedTab = 0 }
                        )
                        GoalsTabButton(
                            text = "Leaderboard",
                            isSelected = selectedTab == 1,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedTab = 1 }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "GoalsTabTransition"
            ) { tab ->
                if (tab == 0) {
                    QuestsTab(authViewModel, contentColor)
                } else {
                    LeaderboardTab(authViewModel, contentColor)
                }
            }
        }
    }
}

@Composable
fun GoalsTabButton(text: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        animationSpec = tween(300),
        label = "tabTextColor"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(26.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null 
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                color = contentColor,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            ),
            modifier = Modifier.zIndex(1f)
        )
    }
}

@Composable
fun QuestsTab(authViewModel: AuthViewModel, contentColor: Color) {
    // Generate random tasks on first launch or daily reset
    val tasks = remember {
        val questTitles = listOf(
            "Study Streak", "Flashcard Frenzy", "Night Owl", "Quiz Master",
            "Speed Reader", "Concept Builder", "Memory Master", "Daily Seeker"
        )
        val questDescriptions = listOf(
            "Complete a learning path", "Review 10 flashcards", "Complete a session after 9PM",
            "Score 100% in a quiz", "Read 5 study points", "Unlock a new topic",
            "Finish 3 lessons", "Log in for 2 consecutive days"
        )
        
        List(4) { i ->
            TaskModel(
                id = i.toString(),
                title = questTitles.random(),
                isCompleted = false,
                rewardCoins = (5..20).random()
            )
        }
    }

    var currentTasks by remember { mutableStateOf(tasks) }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        // Daily Goal XP Card
        NeumorphicCard(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), shape = RoundedCornerShape(24.dp), elevation = 4.dp) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Daily Progress", 
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, color = contentColor.copy(alpha = 0.6f))
                    )
                    val completedCount = currentTasks.count { it.isCompleted }
                    Text(
                        text = "$completedCount / ${currentTasks.size} Quests Done", 
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp, fontWeight = FontWeight.Black, color = contentColor)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { completedCount.toFloat() / currentTasks.size.toFloat() }, 
                        modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape), 
                        color = Color(0xFF58CC02), 
                        trackColor = contentColor.copy(alpha = 0.1f)
                    )
                }
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(40.dp).padding(start = 12.dp))
            }
        }

        currentTasks.forEach { task ->
            QuestItem(task = task, contentColor = contentColor) { isCompleted ->
                currentTasks = currentTasks.map { if (it.id == task.id) {
                    if (isCompleted) authViewModel.addPoints(it.rewardCoins * 10)
                    it.copy(isCompleted = isCompleted)
                } else it }
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun LeaderboardTab(authViewModel: AuthViewModel, contentColor: Color) {
    var leaderboardUsers by remember { mutableStateOf<List<UserStatsModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        leaderboardUsers = listOf(
            UserStatsModel("1", "Alex Quantum", 30, 2500),
            UserStatsModel("2", "Bio Hazard", 28, 2100),
            UserStatsModel("3", "Synapse Seeker", 25, 1200),
            UserStatsModel("4", "Math Wizard", 22, 1150),
            UserStatsModel("5", "Code Ninja", 15, 900)
        ).sortedByDescending { it.totalPoints }
        isLoading = false
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "HALL OF FAME", 
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp, fontWeight = FontWeight.Black, color = contentColor, letterSpacing = 1.sp)
        )
        Text(
            text = "Top performing students this week", 
            style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = contentColor.copy(alpha = 0.6f))
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(leaderboardUsers) { user ->
                    val rank = leaderboardUsers.indexOf(user) + 1
                    LeaderboardItem(rank = rank, user = user, contentColor = contentColor)
                }
            }
        }
    }
}

@Composable
fun LeaderboardItem(rank: Int, user: UserStatsModel, contentColor: Color) {
    val rankColor = when(rank) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> Color.Transparent
    }

    NeumorphicCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(if (rank <= 3) rankColor else Color.Transparent, CircleShape)
                    .border(if (rank > 3) 1.dp else 0.dp, contentColor.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rank.toString(),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = if (rank <= 3) Color.White else contentColor
                    )
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                )
                Text(
                    text = "Level ${user.level}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = contentColor.copy(alpha = 0.6f)
                    )
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${user.totalPoints} XP",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "Total Earned",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = contentColor.copy(alpha = 0.4f)
                    )
                )
            }
        }
    }
}

@Composable
fun QuestItem(task: TaskModel, contentColor: Color, onToggleComplete: (Boolean) -> Unit) {
    val duolingoGreen = Color(0xFF58CC02)
    val iconColor = if (task.isCompleted) duolingoGreen else Color(0xFF6366F1)

    NeumorphicCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), shape = RoundedCornerShape(24.dp), elevation = 4.dp) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(56.dp).background(iconColor.copy(alpha = 0.15f), CircleShape).clickable { onToggleComplete(!task.isCompleted) }, contentAlignment = Alignment.Center) {
                Icon(imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.TaskAlt, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title.uppercase(), 
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.Black, 
                        color = contentColor, 
                        letterSpacing = 1.sp, 
                        textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                    )
                )
                Text(
                    text = "Reward: ${task.rewardCoins} SyncCoins", 
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, color = contentColor.copy(alpha = 0.7f))
                )
            }
            if (task.isCompleted) {
                Box(modifier = Modifier.background(duolingoGreen.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(
                        text = "DONE", 
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = duolingoGreen)
                    )
                }
            }
        }
    }
}

private fun Color.luminance(): Float {
    return 0.2126f * red + 0.7152f * green + 0.0722f * blue
}
