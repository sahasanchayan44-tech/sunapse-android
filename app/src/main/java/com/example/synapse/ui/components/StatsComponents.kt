package com.example.synapse.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.synapse.auth.MonthlyStat
import kotlin.math.sin

@Composable
fun ActivityCard(
    modifier: Modifier = Modifier, 
    timeframe: String = "TODAY", 
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    NeumorphicCard(
        modifier = modifier
            .aspectRatio(0.85f)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(28.dp),
        elevation = 6.dp
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Stats", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(timeframe, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
            }
            Box(modifier = Modifier.fillMaxSize().padding(top = 20.dp)) {
                content()
            }
        }
    }
}

@Composable
fun AccuracyMetricContent(accuracy: Int, correct: Int, wrong: Int) {
    val waveColor = Color(0xFFFF4D00)
    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text("$accuracy", fontSize = 32.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.width(4.dp))
            Text("% ACCURACY", fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(bottom = 6.dp))
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("CORRECT", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                Text("$correct", fontSize = 14.sp, fontWeight = FontWeight.Black)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("WRONG", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF43F5E))
                Text("$wrong", fontSize = 14.sp, fontWeight = FontWeight.Black)
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        
        Canvas(modifier = Modifier.fillMaxWidth().height(40.dp)) {
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
        }
    }
}

@Composable
fun BigMetricWithBarsContent(value: String, subtext: String, barColor: Color, showLabels: Boolean = false) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, fontSize = 32.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.width(4.dp))
            Text(subtext, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.padding(bottom = 4.dp))
        }
        Spacer(modifier = Modifier.weight(1f))
        Column {
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
            if (showLabels) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val months = listOf("J", "F", "M", "A", "M", "J", "J")
                    months.forEach { month ->
                        Text(month, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BigMetricWithGraphContent(value: String, subtext: String, graphColor: Color) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(value, fontSize = 28.sp, fontWeight = FontWeight.Black)
        Text(subtext, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.weight(1f))
        Canvas(modifier = Modifier.fillMaxWidth().height(50.dp)) {
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
    }
}

@Composable
fun YearlyAccuracyGraph(stats: List<MonthlyStat>, primaryText: Color) {
    NeumorphicCard(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "YEARLY ACCURACY",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                stats.forEach { stat ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(8.dp)
                                .fillMaxHeight(0.8f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(primaryText.copy(alpha = 0.05f)),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(stat.accuracy / 100f)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                                        )
                                    )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stat.month, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = primaryText.copy(alpha = 0.4f))
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectListContent(subjects: List<String>, primaryText: Color) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "STUDIED TODAY",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        if (subjects.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "No study recorded yet.",
                    fontSize = 12.sp,
                    color = primaryText.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                subjects.take(3).forEach { subject ->
                    val (icon, color) = getSubjectMetadata(subject)
                    SubjectStatsItem(subject, icon, color, primaryText)
                }
            }
        }
    }
}

private fun getSubjectMetadata(subject: String): Pair<ImageVector, Color> {
    return when (subject.lowercase()) {
        "physics" -> Icons.Default.Bolt to Color(0xFF00D2FF)
        "math", "mathematics" -> Icons.Default.Functions to Color(0xFFFF4D00)
        "chemistry" -> Icons.Default.Science to Color(0xFF10B981)
        "history" -> Icons.Default.HistoryEdu to Color(0xFFFFAB40)
        "biology", "nature" -> Icons.Default.Nature to Color(0xFF38EF7D)
        else -> Icons.Default.AutoAwesome to Color(0xFF8B5CF6)
    }
}

@Composable
fun SubjectStatsItem(name: String, icon: ImageVector, iconColor: Color, textColor: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(36.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(name, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = textColor)
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
