package com.example.synapse.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.synapse.auth.AuthViewModel
import com.example.synapse.ui.components.LevelTrophy
import com.example.synapse.ui.components.NeumorphicCard
import kotlin.math.sin

@Composable
fun DashboardScreen(authViewModel: AuthViewModel, onProfileClick: () -> Unit, onTrophyClick: () -> Unit) {
    val stats = authViewModel.userStats
    val rankInfo = stats.rankInfo
    
    val primaryText = MaterialTheme.colorScheme.onSurface
    val accentColor = MaterialTheme.colorScheme.primary
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Rank Header Row (Top tab showing current rank)
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

            // Swapped Rows of Activity Cards
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                // Now showing the row that was previously below (Focus and chapters)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    ActivityCard(modifier = Modifier.weight(1f)) {
                        BigMetricWithWaveContent("88", "% accuracy", Color(0xFFFF4D00))
                    }
                    ActivityCard(modifier = Modifier.weight(1f)) {
                        BigMetricWithBarsContent("12", "chapters", Color(0xFFFFAB40))
                    }
                }
                
                // Now showing the row that was previously above (Subjects and study time)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    ActivityCard(modifier = Modifier.weight(1f)) {
                        SubjectListContent(primaryText)
                    }
                    ActivityCard(modifier = Modifier.weight(1f)) {
                        BigMetricWithGraphContent("6h 45m", "total study", accentColor)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Study Progress Graph (Elevation graph)
            StudyProgressGraph()
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ActivityCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    NeumorphicCard(
        modifier = modifier.aspectRatio(0.85f),
        shape = RoundedCornerShape(28.dp),
        elevation = 6.dp
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Stats", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("TODAY", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
            }
            Box(modifier = Modifier.fillMaxSize().padding(top = 20.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SubjectListContent(primaryText: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
        SubjectItem("Physics", Icons.Default.Bolt, Color(0xFF00D2FF), primaryText)
        SubjectItem("Math", Icons.Default.Functions, Color(0xFFFF4D00), primaryText)
        SubjectItem("History", Icons.Default.HistoryEdu, Color(0xFFFFAB40), primaryText)
    }
}

@Composable
fun SubjectItem(name: String, icon: ImageVector, iconColor: Color, textColor: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(40.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = textColor)
        }
    }
}

@Composable
fun BigMetricWithGraphContent(value: String, subtext: String, graphColor: Color) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(value, fontSize = 28.sp, fontWeight = FontWeight.Black)
        Text(subtext, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.weight(1f))
        Canvas(modifier = Modifier.fillMaxWidth().height(40.dp)) {
            val path = Path()
            val points = listOf(0.8f, 0.7f, 0.4f, 0.35f, 0.5f, 0.45f, 0.2f, 0.3f, 0.6f)
            points.forEachIndexed { index, p ->
                val x = size.width * index / (points.size - 1)
                val y = size.height * p
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(
                path = path,
                color = graphColor,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            val fillPath = path.apply {
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(listOf(graphColor.copy(alpha = 0.3f), Color.Transparent))
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            repeat(3) { i ->
                Box(modifier = Modifier.padding(2.dp).size(4.dp).background(if(i==1) graphColor else Color.Gray.copy(alpha = 0.3f), CircleShape))
            }
        }
    }
}

@Composable
fun BigMetricWithWaveContent(value: String, subtext: String, waveColor: Color) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, fontSize = 32.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.width(4.dp))
            Text(subtext.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
        }
        Spacer(modifier = Modifier.weight(1f))
        Canvas(modifier = Modifier.fillMaxWidth().height(50.dp)) {
            val width = size.width
            val height = size.height
            val points = 50
            val path = Path()
            for (i in 0..points) {
                val x = width * i / points
                val y = height / 2 + sin(i.toFloat() / 5) * (height / 3)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path, waveColor, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
            val path2 = Path()
            for (i in 0..points) {
                val x = width * i / points
                val y = height / 2 + sin(i.toFloat() / 5 + 2f) * (height / 4)
                if (i == 0) path2.moveTo(x, y) else path2.lineTo(x, y)
            }
            drawPath(path2, waveColor.copy(alpha = 0.3f), style = Stroke(width = 1.dp.toPx()))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            repeat(3) { i ->
                Box(modifier = Modifier.padding(2.dp).size(4.dp).background(if(i==0) waveColor else Color.Gray.copy(alpha = 0.3f), CircleShape))
            }
        }
    }
}

@Composable
fun BigMetricWithBarsContent(value: String, subtext: String, barColor: Color) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, fontSize = 32.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.width(4.dp))
            Text(subtext, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.padding(bottom = 4.dp))
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth().height(60.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            val heights = listOf(0.4f, 0.8f, 0.5f, 1f, 0.6f, 0.9f, 0.3f)
            heights.forEachIndexed { i, h ->
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight(h)
                        .background(if(i==3) barColor else Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            repeat(3) { i ->
                Box(modifier = Modifier.padding(2.dp).size(4.dp).background(if(i==2) barColor else Color.Gray.copy(alpha = 0.3f), CircleShape))
            }
        }
    }
}

@Composable
fun StudyProgressGraph() {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    
    NeumorphicCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "Elevation",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = onSurface
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val paddingLeft = 40.dp.toPx()
                    val paddingBottom = 30.dp.toPx()
                    val paddingTop = 10.dp.toPx()
                    val paddingRight = 10.dp.toPx()
                    
                    val graphWidth = width - paddingLeft - paddingRight
                    val graphHeight = height - paddingTop - paddingBottom
                    
                    val points = listOf(
                        Offset(0.0f, 0.5f),      // 0
                        Offset(0.1f, 0.45f),     // 1
                        Offset(0.25f, 0.42f),    // 2.5
                        Offset(0.45f, 0.58f),    // 4.5
                        Offset(0.6f, 0.40f),     // 6
                        Offset(0.85f, 0.45f),    // 8.5
                        Offset(1.0f, 0.15f)      // 10
                    )

                    val path = Path()
                    points.forEachIndexed { index, point ->
                        val x = paddingLeft + point.x * graphWidth
                        val y = paddingTop + point.y * graphHeight
                        if (index == 0) path.moveTo(x, y)
                        else {
                            val prevX = paddingLeft + points[index-1].x * graphWidth
                            val prevY = paddingTop + points[index-1].y * graphHeight
                            path.cubicTo(
                                (prevX + x) / 2, prevY,
                                (prevX + x) / 2, y,
                                x, y
                            )
                        }
                    }

                    for (i in 0 until 7) {
                        val y = paddingTop + (graphHeight * i / 6)
                        drawLine(
                            color = onSurface.copy(alpha = 0.05f),
                            start = Offset(paddingLeft, y),
                            end = Offset(width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    drawPath(
                        path = path,
                        color = Color.Black.copy(alpha = 0.15f),
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round),
                    )

                    drawPath(
                        path = path,
                        color = primaryColor,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )

                    drawCircle(
                        color = onSurface,
                        radius = 6.dp.toPx(),
                        center = Offset(paddingLeft + points[0].x * graphWidth, paddingTop + points[0].y * graphHeight),
                        style = Stroke(width = 3.dp.toPx())
                    )

                    val orangePoint = points[2]
                    drawCircle(
                        color = Color(0xFFFF9500).copy(alpha = 0.3f),
                        radius = 14.dp.toPx(),
                        center = Offset(paddingLeft + orangePoint.x * graphWidth, paddingTop + orangePoint.y * graphHeight)
                    )
                    drawCircle(
                        color = Color(0xFFFF9500),
                        radius = 8.dp.toPx(),
                        center = Offset(paddingLeft + orangePoint.x * graphWidth, paddingTop + orangePoint.y * graphHeight)
                    )

                    val endPoint = points.last()
                    drawCircle(
                        color = onSurface,
                        radius = 8.dp.toPx(),
                        center = Offset(paddingLeft + endPoint.x * graphWidth, paddingTop + endPoint.y * graphHeight),
                        style = Stroke(width = 3.dp.toPx())
                    )
                    drawCircle(
                        color = onSurface,
                        radius = 3.dp.toPx(),
                        center = Offset(paddingLeft + endPoint.x * graphWidth, paddingTop + endPoint.y * graphHeight)
                    )
                }
                
                Box(modifier = Modifier.fillMaxSize()) {
                    val yLabels = listOf("200", "150", "100", "50", "0", "-50", "-100")
                    yLabels.forEachIndexed { index, label ->
                        Text(
                            text = label,
                            color = onSurface.copy(alpha = 0.3f),
                            fontSize = 10.sp,
                            modifier = Modifier
                                .offset(x = 4.dp, y = (10 + index * 30).dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in 0..10) {
                        Text(
                            text = i.toString(),
                            color = onSurface.copy(alpha = 0.3f),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
