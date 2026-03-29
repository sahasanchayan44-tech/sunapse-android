package com.example.synapse.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.example.synapse.ui.components.NeumorphicCard
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GoalsScreen() {
    val currentDate = remember { SimpleDateFormat("MMMM dd", Locale.getDefault()).format(Date()).uppercase() }

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
        // Shadow/Ghost Background Effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(60.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(400.dp, 600.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(shadowColor, Color.Transparent),
                            radius = 800f
                        )
                    )
            )
        }

        // Frosted Glass Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(glassColor)
        )

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 64.dp)
        ) {
            Text(
                text = "QUESTS",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = contentColor,
                letterSpacing = 2.sp
            )
            Text(
                text = currentDate,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Daily XP Goal Card (Duolingo Style Header)
            NeumorphicCard(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                shape = RoundedCornerShape(28.dp),
                elevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Daily Goal",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = contentColor.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "120 / 150 XP",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = contentColor
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { 120f / 150f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(CircleShape),
                            color = Color(0xFF58CC02), // Duolingo Green
                            trackColor = contentColor.copy(alpha = 0.1f)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // Quest Section
            QuestItem(
                title = "Study Streak",
                description = "Maintain your 7-day learning momentum",
                progress = 7f,
                total = 10f,
                icon = Icons.Default.Whatshot,
                iconColor = Color(0xFFFF9600),
                contentColor = contentColor
            )

            QuestItem(
                title = "Flashcard Frenzy",
                description = "Review 25 cards to sharpen your memory",
                progress = 18f,
                total = 25f,
                icon = Icons.Default.Memory,
                iconColor = Color(0xFF6366F1),
                contentColor = contentColor
            )

            QuestItem(
                title = "Quiz Master",
                description = "Score 80%+ in 2 practice quizzes",
                progress = 1f,
                total = 2f,
                icon = Icons.AutoMirrored.Filled.FactCheck,
                iconColor = Color(0xFF10B981),
                contentColor = contentColor
            )

            QuestItem(
                title = "Night Owl",
                description = "Complete one session after 9 PM",
                progress = 1f,
                total = 1f,
                icon = Icons.Default.NightsStay,
                iconColor = Color(0xFF8B5CF6),
                contentColor = contentColor,
                isCompleted = true
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun QuestItem(
    title: String,
    description: String,
    progress: Float,
    total: Float,
    icon: ImageVector,
    iconColor: Color,
    contentColor: Color,
    isCompleted: Boolean = false
) {
    val progressValue = progress / total
    val duolingoGreen = Color(0xFF58CC02)

    Column(
        modifier = Modifier
            .padding(bottom = 24.dp)
            .fillMaxWidth()
    ) {
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quest Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(iconColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isCompleted) duolingoGreen else iconColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title.uppercase(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = contentColor,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = contentColor.copy(alpha = 0.7f),
                        lineHeight = 16.sp
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Progress Bar
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(
                            progress = { progressValue },
                            modifier = Modifier
                                .weight(1f)
                                .height(10.dp)
                                .clip(CircleShape),
                            color = if (isCompleted) duolingoGreen else iconColor,
                            trackColor = contentColor.copy(alpha = 0.1f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "${progress.toInt()}/${total.toInt()}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = contentColor
                        )
                    }
                }

                if (isCompleted) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = duolingoGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

private fun Color.luminance(): Float {
    return 0.2126f * red + 0.7152f * green + 0.0722f * blue
}
