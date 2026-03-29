package com.example.synapse.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.synapse.models.DailyQuest
import com.example.synapse.models.RankInfo
import com.example.synapse.models.UserStats
import com.example.synapse.utils.ALL_RANKS
import com.example.synapse.utils.LEVEL_NAMES

@Composable
fun DashboardScreen() {
    val stats = remember { UserStats.initial() }
    val tier = ((stats.level - 1) / 10).coerceIn(0, 9)
    val rank = ALL_RANKS[tier]
    val levelName = LEVEL_NAMES[stats.level] ?: "Mysterious Seeker"
    val progress = (if (stats.level % 10 == 0) 10 else stats.level % 10) / 10.0

    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 40.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        HeroSection(stats, rank, levelName)
        Spacer(modifier = Modifier.height(32.dp))
        DashboardTabs(selectedTab) { selectedTab = it }
        Spacer(modifier = Modifier.height(24.dp))
        
        when (selectedTab) {
            0 -> OverviewTab(stats, rank, progress)
            1 -> RankTab(stats, rank)
            2 -> QuestsTab(stats)
        }
    }
}

@Composable
fun HeroSection(stats: UserStats, rank: RankInfo, levelName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Glow effect simulation
                Surface(
                    modifier = Modifier.size(150.dp),
                    shape = CircleShape,
                    color = rank.color.copy(alpha = 0.1f),
                    shadowElevation = 0.dp
                ) {}
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = rank.icon,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = rank.color
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "LVL ${stats.level}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic
                    )
                }

                // Badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 10.dp),
                    color = Color(0xFF0F172A).copy(alpha = 0.8f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, rank.color.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = levelName.uppercase(),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp,
                        color = rank.color
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                StatCard(
                    label = "STUDY STREAK",
                    value = "${stats.streak}",
                    unit = "Days",
                    icon = Icons.Default.Whatshot,
                    color = Color.Yellow,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                MomentumCard(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, unit: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(40.dp),
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = label, fontSize = 8.sp, fontWeight = FontWeight.Black, color = color.copy(alpha = 0.7f), letterSpacing = 1.sp)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = unit, fontSize = 10.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun MomentumCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(
                progress = { 0.75f },
                modifier = Modifier.size(40.dp),
                strokeWidth = 6.dp,
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.White.copy(alpha = 0.05f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "FOCUS MOMENTUM", fontSize = 8.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f), letterSpacing = 1.sp)
                Text(text = "AURA ACTIVE", fontSize = 12.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic)
            }
        }
    }
}

@Composable
fun DashboardTabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            TabItem("Overview", Icons.Default.Dashboard, selectedTab == 0, modifier = Modifier.weight(1f)) { onTabSelected(0) }
            TabItem("Rank", Icons.Default.EmojiEvents, selectedTab == 1, modifier = Modifier.weight(1f)) { onTabSelected(1) }
            TabItem("Quests", Icons.Default.AdsClick, selectedTab == 2, modifier = Modifier.weight(1f)) { onTabSelected(2) }
        }
    }
}

@Composable
fun TabItem(label: String, icon: ImageVector, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .clickable { onClick() },
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = if (isSelected) Color.White else Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = if (isSelected) Color.White else Color.Gray,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun OverviewTab(stats: UserStats, rank: RankInfo, progress: Double) {
    Column(modifier = Modifier.padding(top = 20.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                MaterialTheme.colorScheme.background,
                                Color.Magenta.copy(alpha = 0.1f)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(text = "RANK TIER ${rank.tier}/10", fontSize = 10.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            Text(text = rank.name, fontSize = 24.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic)
                        }
                        Text(text = "Level ${stats.level} / 100", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, fontStyle = FontStyle.Italic)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = "RANK TIER PROGRESS ${(progress * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.White.copy(alpha = 0.05f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${stats.xpToNextLevel} XP until Level ${stats.level + 1}",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        color = Color.DarkGray,
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Boost", fontSize = 18.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RankTab(stats: UserStats, currentRank: RankInfo) {
    Column(modifier = Modifier.padding(top = 20.dp)) {
        ALL_RANKS.forEachIndexed { index, rank ->
            val isUnlocked = stats.level >= (index * 10 + 1)
            val isCurrent = currentRank.tier == rank.tier
            
            RankRow(rank, isUnlocked, isCurrent)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun RankRow(rank: RankInfo, isUnlocked: Boolean, isCurrent: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isUnlocked) 1f else 0.4f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Surface(
                color = Color.Transparent,
                border = BorderStroke(1.dp, if (isCurrent) Color.Yellow else Color.White.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "LEVELS ${rank.levelRange}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isCurrent) Color.Yellow else Color.Gray
                )
            }
            Text(text = rank.name, fontSize = 18.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic, color = if (isCurrent) Color.Yellow else Color.White)
            Text(text = rank.desc, textAlign = TextAlign.Right, fontSize = 12.sp, color = Color.Gray, fontStyle = FontStyle.Italic)
        }
        Spacer(modifier = Modifier.width(20.dp))
        Box(contentAlignment = Alignment.BottomEnd) {
            Surface(
                modifier = Modifier.size(64.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (isCurrent) Color.Yellow else Color.White.copy(alpha = 0.05f))
            ) {
                Icon(
                    imageVector = if (isUnlocked) rank.icon else Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp),
                    tint = if (isCurrent) Color.Yellow else if (isUnlocked) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
            Surface(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Text(text = "${rank.tier}", modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun QuestsTab(stats: UserStats) {
    val quests = remember {
        listOf(
            DailyQuest("1", "Complete 3 Flashcard sets", 1, 3, 50),
            DailyQuest("2", "Score 80% in a Quiz", 0, 1, 100),
            DailyQuest("3", "Study for 30 minutes", 15, 30, 75)
        )
    }

    Column(modifier = Modifier.padding(top = 20.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Adjust, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Daily Quest Log", fontSize = 20.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic)
                    }
                    Surface(
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, Color.Yellow),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "14H REMAINING", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.Yellow)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                quests.forEach { quest ->
                    QuestItem(quest)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            Surface(
                modifier = Modifier.weight(1f),
                color = Color.Yellow,
                shape = RoundedCornerShape(32.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(text = "SyncCoins", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Black.copy(alpha = 0.54f))
                    Text(text = "${stats.coins}", fontSize = 32.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic, color = Color.Black)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Redeem", fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic)
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Surface(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(32.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Whatshot, contentDescription = null, tint = Color(0xFFF97316), modifier = Modifier.size(32.dp))
                    Text(text = "Active Streak", fontSize = 14.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic)
                    Text(text = "Weekly consistent!", fontSize = 10.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "${stats.streak} Days", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFFF97316))
                }
            }
        }
    }
}

@Composable
fun QuestItem(quest: DailyQuest) {
    val isCompleted = quest.current >= quest.target
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(36.dp),
                color = (if (isCompleted) Color.Green else MaterialTheme.colorScheme.primary).copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Adjust,
                    contentDescription = null,
                    tint = if (isCompleted) Color.Green else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = quest.task, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF97316), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "+${quest.reward}", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFFF97316))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = { (quest.current.toFloat() / quest.target).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (isCompleted) Color.Green else MaterialTheme.colorScheme.primary,
                        trackColor = Color.White.copy(alpha = 0.05f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${quest.current}/${quest.target}", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = { },
                enabled = isCompleted,
                modifier = Modifier.height(32.dp).width(70.dp),
                contentPadding = PaddingValues(horizontal = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color.White.copy(alpha = 0.05f)
                )
            ) {
                Text(text = if (isCompleted) "CLAIM" else "LOCKED", fontSize = 8.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}
