package com.example.synapse.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.example.synapse.ui.components.LevelTrophy
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Immutable
data class FlashcardData(
    val title: String,
    val category: String,
    val subTopics: List<String>,
    val startColor: Color,
    val endColor: Color
)

@Composable
fun FlashcardsScreen(dialPosition: Float = 0f) {
    val isDark = isSystemInDarkTheme()
    val cards = remember {
        listOf(
            FlashcardData("Mitosis", "Biology", listOf("Prophase", "Metaphase", "Anaphase", "Telophase"), Color(0xFFFF4D00), Color(0xFFFF9500)),
            FlashcardData("Photosynthesis", "Botany", listOf("Chlorophyll", "Light Rxns", "Calvin Cycle", "ATP"), Color(0xFF00D2FF), Color(0xFF3A7BD5)),
            FlashcardData("Quantum Physics", "Physics", listOf("Wave-Particle", "Uncertainty", "Entanglement", "Superposition"), Color(0xFF8E2DE2), Color(0xFF4A00E0)),
            FlashcardData("Blockchain", "Tech", listOf("Hash", "Nodes", "Consensus", "Mining"), Color(0xFF00F260), Color(0xFF0575E6)),
            FlashcardData("Neuroscience", "Brain", listOf("Neurons", "Synapse", "Cortex", "Amygdala"), Color(0xFFFF0099), Color(0xFF493240))
        )
    }

    val pagerState = rememberPagerState(pageCount = { cards.size })

    // Synchronize pager with dial position from nav bar
    LaunchedEffect(dialPosition) {
        val targetPage = (dialPosition * (cards.size)).toInt().coerceIn(0, cards.size - 1)
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    val titleColor = if (isDark) Color.White else Color(0xFF1A1A1A)
    val subtitleColor = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.4f)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            Text(
                text = "Study Flashcards",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = titleColor,
                letterSpacing = (-1).sp
            )
            
            Text(
                text = "Glide using the dial below",
                fontSize = 14.sp,
                color = subtitleColor
            )

            Spacer(modifier = Modifier.height(20.dp))

            // The Dial Markings visual at the top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val centerX = width / 2
                    val radius = 300.dp.toPx()
                    
                    for (i in -20..20) {
                        val angle = (i * 2).toFloat()
                        val radian = Math.toRadians(angle.toDouble() - 90).toFloat()
                        val startX = centerX + (radius - 10.dp.toPx()) * cos(radian.toDouble()).toFloat()
                        val startY = height + (radius - 10.dp.toPx()) * sin(radian.toDouble()).toFloat()
                        val endX = centerX + radius * cos(radian.toDouble()).toFloat()
                        val endY = height + radius * sin(radian.toDouble()).toFloat()
                        
                        drawLine(
                            color = titleColor.copy(alpha = 0.1f),
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }
                
                Text(
                    text = cards[pagerState.currentPage].title.uppercase(),
                    color = titleColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    modifier = Modifier.offset(y = (-20).dp)
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 60.dp),
                pageSpacing = 0.dp,
                beyondViewportPageCount = 1
            ) { page ->
                val pageOffset = remember(pagerState) {
                    derivedStateOf {
                        ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).coerceIn(-1f, 1f)
                    }
                }

                FlashcardItem(
                    data = cards[page],
                    pageOffsetProvider = { pageOffset.value },
                    isDark = isDark
                )
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun FlashcardItem(
    data: FlashcardData,
    pageOffsetProvider: () -> Float,
    isDark: Boolean
) {
    val cardColors = remember(isDark, data) {
        FlashcardColors(
            content = if (isDark) Color.White else Color(0xFF1A1A1A),
            glass = if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.7f),
            gridBg = if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f),
            gridBorder = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f),
            glowBrush = Brush.verticalGradient(listOf(data.startColor, data.endColor.copy(alpha = 0.3f))),
            borderBrush = Brush.verticalGradient(
                listOf(
                    if (isDark) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.6f),
                    if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.2f)
                )
            ),
            accentBrush = Brush.horizontalGradient(listOf(data.startColor, data.endColor))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.72f)
            .graphicsLayer {
                val pageOffset = pageOffsetProvider()
                val absOffset = abs(pageOffset)
                val fraction = 1f - absOffset.coerceIn(0f, 1f)
                
                rotationY = pageOffset * -40f 
                scaleX = lerp(0.82f, 1f, fraction)
                scaleY = lerp(0.82f, 1f, fraction)
                alpha = lerp(0.3f, 1f, fraction)
                cameraDistance = 12f * density
                translationX = pageOffset * -50f
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(0.95f)
                .blur(80.dp)
                .graphicsLayer {
                    val pageOffset = pageOffsetProvider()
                    val absOffset = abs(pageOffset)
                    alpha = if (isDark) 0.8f - (absOffset * 0.5f) else 0.4f - (absOffset * 0.2f)
                }
                .background(cardColors.glowBrush, RoundedCornerShape(40.dp))
        )

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = cardColors.glass,
            shape = RoundedCornerShape(32.dp),
            border = BorderStroke(1.5.dp, cardColors.borderBrush),
            shadowElevation = if (isDark) 0.dp else 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = data.startColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "WORKFORCE IDENTITY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = cardColors.content.copy(alpha = 0.7f),
                        letterSpacing = 1.2.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = data.title,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = cardColors.content,
                    textAlign = TextAlign.Center,
                    lineHeight = 42.sp,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(44.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = cardColors.gridBg,
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(0.5.dp, cardColors.gridBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val rows = remember(data.subTopics) { data.subTopics.chunked(2) }
                        rows.take(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                rowItems.forEach { item ->
                                    TopicGridItem(item, data.startColor, cardColors.content, Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .height(6.dp)
                        .background(cardColors.accentBrush, CircleShape)
                )
            }
        }
    }
}

@Immutable
data class FlashcardColors(
    val content: Color,
    val glass: Color,
    val gridBg: Color,
    val gridBorder: Color,
    val glowBrush: Brush,
    val borderBrush: Brush,
    val accentBrush: Brush
)

@Composable
fun TopicGridItem(text: String, color: Color, contentColor: Color, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(color.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor.copy(alpha = 0.9f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
