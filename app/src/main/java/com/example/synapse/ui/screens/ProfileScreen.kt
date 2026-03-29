package com.example.synapse.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.synapse.auth.AuthViewModel
import com.example.synapse.ui.ThemeViewModel
import com.example.synapse.ui.components.LevelTrophy
import com.example.synapse.ui.components.NeumorphicCard
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(authViewModel: AuthViewModel, themeViewModel: ThemeViewModel, onBack: () -> Unit) {
    val user = authViewModel.currentUser
    val isDark = isSystemInDarkTheme()
    val primaryText = MaterialTheme.colorScheme.onSurface
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant
    val accentColor = MaterialTheme.colorScheme.primary

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Back Button
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryText)
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
            StatItem("70%", "Win/Lose", Icons.Default.PieChart, accentColor)
            VerticalDivider(modifier = Modifier.height(40.dp), color = secondaryText.copy(alpha = 0.2f))
            StatItem(authViewModel.userStats.level.toString(), "Level", Icons.Default.Verified, accentColor)
            VerticalDivider(modifier = Modifier.height(40.dp), color = secondaryText.copy(alpha = 0.2f))
            StatItem(authViewModel.userStats.totalDays.toString(), "Days", Icons.Default.CalendarToday, accentColor)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Weekly Calendar - Now Synchronized with IST
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(weekData) { day ->
                CalendarDayItem(day.first, day.second, day.third, accentColor)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Settings Section Header
        Text(
            text = "SETTINGS",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            color = primaryText,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Integrated Settings Options
        val neonColor = if (isDark) Color(0xFFBB86FC) else Color(0xFF6200EE)
        val cardBg = MaterialTheme.colorScheme.surface

        SettingsOptionCard(
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

        SettingsOptionCard(
            title = "Logout",
            icon = Icons.AutoMirrored.Filled.Logout,
            neonColor = Color.Red,
            cardBg = cardBg,
            onClick = { authViewModel.signOut() }
        ) {
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.Red.copy(alpha = 0.3f))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Friends and Streak Cards
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
                    Text("${authViewModel.userStats.friendsOnline} online", fontSize = 12.sp, color = accentColor)
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
                        Text("${authViewModel.userStats.currentStreak} days in a row", fontSize = 12.sp, color = secondaryText)
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
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun SettingsOptionCard(
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
fun CalendarDayItem(day: String, date: String, isToday: Boolean, accentColor: Color) {
    val containerColor = if (isToday) accentColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
    val contentColor = if (isToday) accentColor else MaterialTheme.colorScheme.onSurface
    
    Surface(
        modifier = Modifier
            .width(60.dp)
            .height(100.dp),
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        border = if (isToday) BorderStroke(2.dp, accentColor) else null,
        shadowElevation = if (isToday) 8.dp else 4.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isToday) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(3) { Box(modifier = Modifier.size(4.dp).background(accentColor, CircleShape)) }
                }
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                Box(modifier = Modifier.size(4.dp).background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), CircleShape))
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(day, fontSize = 12.sp, color = contentColor.copy(alpha = 0.6f), fontWeight = if(isToday) FontWeight.Bold else FontWeight.Normal)
            Text(date, fontSize = 18.sp, fontWeight = FontWeight.Black, color = contentColor)
        }
    }
}
