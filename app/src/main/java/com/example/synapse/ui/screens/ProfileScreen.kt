package com.example.synapse.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.synapse.auth.AuthViewModel
import com.example.synapse.ui.ThemeViewModel
import com.example.synapse.ui.components.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel, 
    themeViewModel: ThemeViewModel, 
    onBack: () -> Unit,
    onSettingsClick: () -> Unit = {}
) {
    val user = authViewModel.currentUser
    val stats = authViewModel.userStats
    val primaryText = MaterialTheme.colorScheme.onSurface
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant
    val accentColor = MaterialTheme.colorScheme.primary
    
    var showStudyPlanner by remember { mutableStateOf(false) }
    var selectedDayForPlanner by remember { mutableStateOf<String?>(null) }
    var showFullStats by remember { mutableStateOf(false) }

    // Dynamic IST Week Calculation
    val weekData = remember {
        val istTimeZone = TimeZone.getTimeZone("Asia/Kolkata")
        val calendar = Calendar.getInstance(istTimeZone)
        val todayDate = calendar.get(Calendar.DAY_OF_MONTH)
        val todayMonth = calendar.get(Calendar.MONTH)
        
        // Go back to Monday of the current week
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        
        (0..6).map {
            val dayName = SimpleDateFormat("EEE", Locale.ENGLISH).format(calendar.time)
            val dayDate = calendar.get(Calendar.DAY_OF_MONTH)
            val dayMonth = calendar.get(Calendar.MONTH)
            val isToday = dayDate == todayDate && dayMonth == todayMonth
            
            val result = Triple(dayName, dayDate.toString(), isToday)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            result
        }
    }

    AnimatedContent(
        targetState = showStudyPlanner,
        transitionSpec = {
            val duration = 400
            if (targetState) {
                (fadeIn(animationSpec = tween(duration, easing = EaseOutCubic)) + 
                 slideInVertically(animationSpec = tween(duration, easing = EaseOutCubic)) { it / 12 } +
                 scaleIn(initialScale = 0.95f, animationSpec = tween(duration, easing = EaseOutCubic)))
                    .togetherWith(fadeOut(animationSpec = tween(duration / 2)))
            } else {
                (fadeIn(animationSpec = tween(duration, easing = EaseOutCubic)))
                    .togetherWith(fadeOut(animationSpec = tween(duration, easing = EaseInCubic)) + 
                                 slideOutVertically(animationSpec = tween(duration, easing = EaseInCubic)) { it / 12 } +
                                 scaleOut(targetScale = 0.95f, animationSpec = tween(duration, easing = EaseInCubic)))
            }
        },
        label = "StudyPlannerTransition"
    ) { isPlannerOpen ->
        if (isPlannerOpen) {
            StudyPlannerScreen(
                selectedDay = selectedDayForPlanner ?: "Today",
                accentColor = accentColor,
                authViewModel = authViewModel,
                onBack = { showStudyPlanner = false }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Header Row with Back and Settings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryText)
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = primaryText)
                    }
                }

                // Profile Image
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = accentColor,
                            style = Stroke(
                                width = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                        )
                    }
                    Surface(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        color = Color.LightGray
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(20.dp),
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name and Handle
                Text(
                    text = user?.displayName ?: "Scholar",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = primaryText,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "@${user?.email?.split("@")?.get(0) ?: "student"}",
                    fontSize = 14.sp,
                    color = secondaryText,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatItem(stats.syncCoins.toString(), "Sync Coins", Icons.Default.MonetizationOn, Color(0xFFFFD700))
                    VerticalDivider(modifier = Modifier.height(40.dp), color = secondaryText.copy(alpha = 0.2f))
                    StatItem(stats.level.toString(), "Level", Icons.Default.Verified, accentColor)
                    VerticalDivider(modifier = Modifier.height(40.dp), color = secondaryText.copy(alpha = 0.2f))
                    StatItem(stats.totalDays.toString(), "Days", Icons.Default.CalendarToday, accentColor)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // GRAPHS MOVED FROM DASHBOARD (Above Dates Bar)
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        ActivityCard(
                            modifier = Modifier.weight(1f),
                            onClick = { showFullStats = !showFullStats }
                        ) {
                            AccuracyMetricContent(
                                accuracy = stats.accuracy,
                                correct = stats.correctAnswers,
                                wrong = stats.wrongAnswers
                            )
                        }
                        ActivityCard(modifier = Modifier.weight(1f), timeframe = "MONTHLY") {
                            BigMetricWithBarsContent("12", "chapters done", Color(0xFFFFAB40), showLabels = true)
                        }
                    }

                    // Animated Stats View
                    AnimatedVisibility(
                        visible = showFullStats,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        YearlyAccuracyGraph(stats.yearlyStats, primaryText)
                    }
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        ActivityCard(modifier = Modifier.weight(1f)) {
                            SubjectListContent(stats.subjectsStudiedToday, primaryText)
                        }
                        ActivityCard(modifier = Modifier.weight(1f)) {
                            BigMetricWithGraphContent("6h 45m", "total study", accentColor)
                        }
                    }

                    // Elevation Graph (also a graph, kept with others)
                    StudyProgressGraph()
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Weekly Calendar (IST) - "Dates Bar"
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(weekData) { day ->
                        CalendarDayItem(
                            day = day.first,
                            date = day.second,
                            isToday = day.third,
                            accentColor = accentColor,
                            onClick = {
                                selectedDayForPlanner = "${day.first}, ${day.second}"
                                showStudyPlanner = true
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun StudyPlannerScreen(
    selectedDay: String,
    accentColor: Color,
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val primaryText = MaterialTheme.colorScheme.onSurface
    
    var tasks by remember { 
        mutableStateOf(listOf(
            "Review Physics: Quantum Mechanics" to true,
            "Complete Math Quiz" to false,
            "Read History: World War II" to false
        ))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryText)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Study Planner",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = primaryText
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(authViewModel.userStats.syncCoins.toString(), fontWeight = FontWeight.Black, color = primaryText)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = selectedDay.uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Daily Schedule",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = primaryText
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "TASKS (50 PTS EACH)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = primaryText.copy(alpha = 0.5f),
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(tasks.size) { index ->
                val (task, isCompleted) = tasks[index]
                PlannerTaskItem(
                    task = task,
                    isCompleted = isCompleted,
                    accentColor = accentColor,
                    onToggle = {
                        val willBeCompleted = !isCompleted
                        tasks = tasks.toMutableList().apply {
                            this[index] = task to willBeCompleted
                        }
                        if (willBeCompleted) {
                            authViewModel.addPoints(50)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* Add Task logic */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = accentColor)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("ADD NEW TASK", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PlannerTaskItem(
    task: String,
    isCompleted: Boolean,
    accentColor: Color,
    onToggle: () -> Unit
) {
    val primaryText = MaterialTheme.colorScheme.onSurface
    
    NeumorphicCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .clickable { onToggle() },
                color = if (isCompleted) accentColor else Color.Transparent,
                border = BorderStroke(2.dp, if (isCompleted) accentColor else primaryText.copy(alpha = 0.2f))
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.padding(4.dp))
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = task,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (isCompleted) primaryText.copy(alpha = 0.3f) else primaryText,
                style = if (isCompleted) androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough) else androidx.compose.ui.text.TextStyle.Default
            )
        }
    }
}

@Composable
fun StatItem(value: String, label: String, icon: ImageVector, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = color)
            Spacer(modifier = Modifier.width(4.dp))
            Text(value, fontWeight = FontWeight.Black, fontSize = 18.sp)
        }
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun CalendarDayItem(
    day: String, 
    date: String, 
    isToday: Boolean, 
    accentColor: Color,
    onClick: () -> Unit
) {
    val containerColor = if (isToday) accentColor else MaterialTheme.colorScheme.surface
    val contentColor = if (isToday) Color.White else MaterialTheme.colorScheme.onSurface
    
    Surface(
        modifier = Modifier
            .width(60.dp)
            .height(100.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        border = if (isToday) null else BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
        shadowElevation = if (isToday) 12.dp else 4.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isToday) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(3) { Box(modifier = Modifier.size(4.dp).background(Color.White, CircleShape)) }
                }
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                Box(modifier = Modifier.size(4.dp).background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), CircleShape))
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = day, 
                fontSize = 12.sp, 
                color = if(isToday) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant, 
                fontWeight = if(isToday) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = date, 
                fontSize = 18.sp, 
                fontWeight = FontWeight.Black, 
                color = contentColor
            )
        }
    }
}
