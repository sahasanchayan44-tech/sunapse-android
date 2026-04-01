package com.example.synapse.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.synapse.auth.AuthViewModel
import com.example.synapse.models.SubjectModel
import com.example.synapse.ui.components.LevelTrophy
import com.example.synapse.ui.components.NeumorphicCard
import com.example.synapse.ui.components.FlashcardData
import com.example.synapse.ui.components.FlashcardItem
import kotlin.math.cos
import kotlin.math.sin

private fun parseDashboardColor(colorString: String, fallback: Color = Color(0xFF8E2DE2)): Color {
    return try {
        if (colorString.isNotBlank() && colorString.startsWith("#")) {
            Color(android.graphics.Color.parseColor(colorString))
        } else {
            fallback
        }
    } catch (e: Exception) {
        fallback
    }
}

@Composable
fun DashboardScreen(
    authViewModel: AuthViewModel, 
    onProfileClick: () -> Unit, 
    onTrophyClick: () -> Unit,
    onFlashcardClick: (SubjectModel) -> Unit,
    dialPosition: Float = 0f
) {
    val stats = authViewModel.userStats
    val rankInfo = stats.rankInfo
    val isDark = isSystemInDarkTheme()
    
    val primaryText = MaterialTheme.colorScheme.onSurface
    val accentColor = MaterialTheme.colorScheme.primary
    
    val subjects = authViewModel.subjects
    val isLoading = authViewModel.isLoading

    val categories = listOf("All", "Science", "Maths", "Humanities", "Business", "Computer Science")
    
    var selectedCategory by remember { mutableStateOf("All") }
    
    val filteredSubjects = remember(subjects, selectedCategory) {
        when (selectedCategory) {
            "All" -> subjects
            "Science" -> subjects.filter { 
                it.category.equals("Science", ignoreCase = true) || 
                it.subtitle.uppercase() in listOf("PHYSICS", "CHEMISTRY", "BIOLOGY") 
            }
            "Maths" -> subjects.filter { 
                it.category.equals("Maths", ignoreCase = true) || 
                it.subtitle.uppercase() == "MATHEMATICS" 
            }
            "Humanities" -> subjects.filter { 
                it.category.equals("Humanities", ignoreCase = true) || 
                it.subtitle.uppercase() == "HISTORY" 
            }
            "Business" -> subjects.filter { 
                it.category.equals("Business", ignoreCase = true) || 
                it.subtitle.uppercase() == "ECONOMICS" 
            }
            "Computer Science" -> subjects.filter { 
                it.category.uppercase() in listOf("CS", "COMPUTER SCIENCE") || 
                it.subtitle.uppercase() == "STRUCTURES" 
            }
            else -> subjects.filter { it.category == selectedCategory }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Rank Header Row
            NeumorphicCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LevelTrophy(level = stats.level, modifier = Modifier.size(40.dp).clickable { onTrophyClick() })
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = rankInfo.rankName.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = accentColor,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = rankInfo.levelName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryText
                        )
                    }
                    NeumorphicCard(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        elevation = 2.dp
                    ) {
                        IconButton(onClick = onProfileClick) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = primaryText)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Category Bar Tab
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 24.dp)
            ) {
                items(categories) { category ->
                    val isSelected = category == selectedCategory
                    CategoryTab(
                        name = category,
                        isSelected = isSelected,
                        onClick = { selectedCategory = category }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = accentColor)
                } else if (filteredSubjects.isEmpty()) {
                    Text("No subjects found in this category.", color = primaryText.copy(alpha = 0.5f))
                } else {
                    key(selectedCategory) { // Reset pager state when category changes
                        DashboardFlashcardPager(filteredSubjects, isDark, onFlashcardClick, dialPosition)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun CategoryTab(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        label = "bg"
    )
    val contentColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        label = "content"
    )

    Surface(
        modifier = Modifier
            .clickable { onClick() }
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}

@Composable
fun DashboardFlashcardPager(
    subjects: List<SubjectModel>,
    isDark: Boolean,
    onCardClick: (SubjectModel) -> Unit,
    dialPosition: Float
) {
    val pagerState = rememberPagerState(pageCount = { subjects.size })

    LaunchedEffect(dialPosition) {
        val targetPage = (dialPosition * subjects.size).toInt().coerceIn(0, subjects.size - 1)
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(
                targetPage,
                animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioLowBouncy)
            )
        }
    }

    val titleColor = if (isDark) Color.White else Color(0xFF1A1A1A)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val centerX = width / 2
                val radius = 250.dp.toPx()
                
                for (i in -15..15) {
                    val angle = (i * 2).toFloat()
                    val radian = Math.toRadians(angle.toDouble() - 90).toFloat()
                    val startX = centerX + (radius - 6.dp.toPx()) * cos(radian.toDouble()).toFloat()
                    val startY = height + (radius - 6.dp.toPx()) * sin(radian.toDouble()).toFloat()
                    val endX = centerX + radius * Math.cos(radian.toDouble()).toFloat()
                    val endY = height + radius * Math.sin(radian.toDouble()).toFloat()
                    
                    drawLine(
                        color = titleColor.copy(alpha = 0.15f),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 48.dp),
            pageSpacing = 0.dp,
            beyondViewportPageCount = 1,
            flingBehavior = PagerDefaults.flingBehavior(
                state = pagerState,
                snapPositionalThreshold = 0.3f
            )
        ) { page ->
            val pageOffset = remember(pagerState) {
                derivedStateOf {
                    ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).coerceIn(-1f, 1f)
                }
            }

            val subject = subjects[page]
            val startColor = parseDashboardColor(subject.startColor)
            val endColor = parseDashboardColor(subject.endColor)
            // Map Firestore model back to UI model
            val flashcardData = FlashcardData(
                title = subject.title,
                subtitle = subject.subtitle,
                bottomText = subject.bottomText,
                icon = when(subject.iconName) {
                    "Bolt" -> Icons.Default.Bolt
                    "Science" -> Icons.Default.Science
                    "Functions" -> Icons.Default.Functions
                    "Spa" -> Icons.Default.Spa
                    "Business" -> Icons.Default.BusinessCenter
                    "History" -> Icons.Default.HistoryEdu
                    "Computer" -> Icons.Default.Computer
                    else -> Icons.Default.Bolt
                },
                startColor = startColor,
                endColor = endColor,
                chapters = emptyList() // Chapters are fetched on the next screen
            )

            FlashcardItem(
                data = flashcardData,
                pageOffsetProvider = { pageOffset.value },
                isDark = isDark,
                onClick = { onCardClick(subject) }
            )
        }
    }
}
