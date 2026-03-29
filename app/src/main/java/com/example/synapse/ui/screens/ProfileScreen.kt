package com.example.synapse.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.synapse.auth.AuthViewModel
import com.example.synapse.ui.components.NeumorphicCard

@Composable
fun ProfileScreen(authViewModel: AuthViewModel, onBack: () -> Unit) {
    val user = authViewModel.currentUser
    val isDark = isSystemInDarkTheme()
    val primaryText = MaterialTheme.colorScheme.onSurface
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant
    val accentColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Back Button
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = primaryText)
        }

        // Profile Image with Dashed Border
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
            text = user?.displayName ?: "Mr. Bobrovsky",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = primaryText,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = "@${user?.email?.split("@")?.get(0) ?: "bobrovsky"}",
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
            StatItem("70%", "Win/Lose", Icons.Default.PieChart, accentColor)
            VerticalDivider(modifier = Modifier.height(40.dp), color = secondaryText.copy(alpha = 0.2f))
            StatItem("8", "Level", Icons.Default.Verified, accentColor)
            VerticalDivider(modifier = Modifier.height(40.dp), color = secondaryText.copy(alpha = 0.2f))
            StatItem("76", "Days", Icons.Default.CalendarToday, accentColor)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Weekly Calendar
        val days = listOf("Mon" to "19", "Tue" to "20", "Wen" to "21", "Thu" to "22", "Fri" to "23", "Sat" to "24", "Sun" to "25")
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(days) { day ->
                CalendarDayItem(day.first, day.second, day.second == "22", accentColor)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Bottom Cards Grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Friends Card
            NeumorphicCard(
                modifier = Modifier.weight(0.4f).height(160.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
                    Row {
                        repeat(3) {
                            Surface(
                                modifier = Modifier.size(32.dp).offset(x = (it * -10).dp),
                                shape = CircleShape,
                                border = BorderStroke(2.dp, MaterialTheme.colorScheme.surface),
                                color = Color.LightGray
                            ) {}
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Friends", fontWeight = FontWeight.Bold, color = primaryText)
                    Text("14 online", fontSize = 12.sp, color = accentColor)
                }
            }

            // Keep it up Card
            NeumorphicCard(
                modifier = Modifier.weight(0.6f).height(160.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Keep it up!", fontWeight = FontWeight.Bold, color = primaryText)
                        Text("35 days in a row", fontSize = 12.sp, color = secondaryText)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("you are here!", fontSize = 10.sp, color = secondaryText)
                    }
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = accentColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Complete new tasks card
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth().height(100.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Task, contentDescription = null, tint = accentColor)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Complete new tasks", fontWeight = FontWeight.Bold, color = primaryText)
                    Text("Get a bonus on your winnings", fontSize = 12.sp, color = secondaryText)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
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
fun CalendarDayItem(day: String, date: String, isSelected: Boolean, accentColor: Color) {
    val containerColor = if (isSelected) accentColor.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface
    
    Surface(
        modifier = Modifier
            .width(60.dp)
            .height(100.dp),
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        border = if (isSelected) BorderStroke(2.dp, accentColor) else null,
        shadowElevation = if (isSelected) 0.dp else 4.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isSelected) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(3) { Box(modifier = Modifier.size(4.dp).background(accentColor, CircleShape)) }
                }
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                Box(modifier = Modifier.size(4.dp).background(MaterialTheme.colorScheme.onSurfaceVariant, CircleShape))
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(day, fontSize = 12.sp, color = contentColor.copy(alpha = 0.6f))
            Text(date, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = contentColor)
        }
    }
}
