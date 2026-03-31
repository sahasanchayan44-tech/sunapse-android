package com.example.synapse.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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

            Text(
                text = "Flashcards",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = primaryText,
                letterSpacing = (-1).sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = accentColor)
                } else if (subjects.isEmpty()) {
                    // This will only show if data is finished loading and still empty
                    Text("No subjects found. Use 'Seed DB' button to add data.", color = primaryText.copy(alpha = 0.5f))
                } else {
                    DashboardFlashcardPager(subjects, isDark, onFlashcardClick, dialPosition)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
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
                    else -> Icons.Default.Bolt
                },
                startColor = Color(android.graphics.Color.parseColor(subject.startColor)),
                endColor = Color(android.graphics.Color.parseColor(subject.endColor)),
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
