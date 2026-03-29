package com.example.synapse.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.synapse.auth.AuthViewModel
import com.example.synapse.ui.components.LevelTrophy
import kotlin.math.sin

@Composable
fun LevelsTrailScreen(authViewModel: AuthViewModel, onBack: () -> Unit) {
    val currentLevel = authViewModel.userStats.level
    val primaryText = MaterialTheme.colorScheme.onSurface
    val accentColor = MaterialTheme.colorScheme.primary

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp, top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val totalLevels = 100
            val levels = (1..totalLevels).toList()
            
            itemsIndexed(levels) { index, level ->
                val rankIdx = (level - 1) / 10
                val isRankHeader = (level - 1) % 10 == 0
                
                if (isRankHeader) {
                    RankHeader(rankIdx, accentColor)
                }

                LevelNode(
                    level = level,
                    isCurrent = level == currentLevel,
                    isLocked = level > currentLevel,
                    accentColor = accentColor,
                    index = index
                )
            }
        }

        // Top Navigation Bar
        Surface(
            modifier = Modifier.fillMaxWidth().height(70.dp),
            color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryText)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Progress Path",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = primaryText
                )
            }
        }
    }
}

@Composable
fun RankHeader(rankIdx: Int, accentColor: Color) {
    val rankNames = listOf(
        "Novice", "Seeker", "Scholar", "Adept", "Sage", 
        "Expert", "Master", "Grandmaster", "Legend", "Transcendent"
    )
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = accentColor.copy(alpha = 0.15f),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, accentColor.copy(alpha = 0.4f))
        ) {
            Text(
                text = "${rankNames[rankIdx].uppercase()} REALM",
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor,
                letterSpacing = 3.sp
            )
        }
    }
}

@Composable
fun LevelNode(
    level: Int,
    isCurrent: Boolean,
    isLocked: Boolean,
    accentColor: Color,
    index: Int
) {
    // Duolingo-style snake offset
    val horizontalOffset = (sin(index.toFloat() * 0.7f) * 90).dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .offset(x = horizontalOffset),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (isCurrent) {
                Surface(
                    color = accentColor,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.offset(y = (-8).dp).shadow(8.dp, RoundedCornerShape(12.dp))
                ) {
                    Text(
                        "CURRENT",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .shadow(
                        elevation = if (isCurrent) 12.dp else 4.dp,
                        shape = CircleShape,
                        spotColor = if (isLocked) Color.Transparent else accentColor
                    )
                    .background(
                        if (isLocked) Color.Gray.copy(alpha = 0.1f) 
                        else MaterialTheme.colorScheme.surface,
                        CircleShape
                    )
                    .clip(CircleShape)
                    .clickable(enabled = !isLocked) { /* Start Level */ },
                contentAlignment = Alignment.Center
            ) {
                if (isLocked) {
                    Icon(
                        Icons.Default.Lock, 
                        contentDescription = null, 
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    LevelTrophy(level = level, modifier = Modifier.size(65.dp))
                }
            }
            
            Text(
                text = "Level $level",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isLocked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
