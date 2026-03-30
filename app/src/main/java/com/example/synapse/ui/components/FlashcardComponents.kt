package com.example.synapse.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import kotlin.math.abs

@Immutable
data class FlashcardData(
    val title: String,
    val subtitle: String,
    val bottomText: String,
    val icon: ImageVector,
    val startColor: Color,
    val endColor: Color,
    val chapters: List<String>
)

@Immutable
data class FlashcardColors(
    val content: Color,
    val glass: Color,
    val glowBrush: Brush,
    val accentBrush: Brush
)

@Composable
fun FlashcardItem(
    data: FlashcardData,
    pageOffsetProvider: () -> Float,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bobbing"
    )

    val cardColors = remember(isDark, data) {
        FlashcardColors(
            content = Color.White,
            glass = if (isDark) Color.Black.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.8f),
            glowBrush = Brush.verticalGradient(listOf(data.startColor, data.endColor.copy(alpha = 0.3f))),
            accentBrush = Brush.horizontalGradient(listOf(data.startColor, data.endColor))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.65f)
            .graphicsLayer {
                val pageOffset = pageOffsetProvider()
                val absOffset = abs(pageOffset)
                val fraction = 1f - absOffset.coerceIn(0f, 1f)
                
                rotationY = pageOffset * -35f 
                rotationZ = pageOffset * -5f
                translationY = floatAnim + (absOffset * 40f)
                scaleX = lerp(0.85f, 1f, fraction)
                scaleY = lerp(0.85f, 1f, fraction)
                
                // Keep tiles fully opaque
                alpha = 1f 
                
                cameraDistance = 12f * density
                
                // FIXED: Corrected property names for 3D shadow effect
                shadowElevation = 40f * density
                ambientShadowColor = Color.Black.copy(alpha = if (isDark) 0.6f else 0.4f)
                spotShadowColor = Color.Black.copy(alpha = if (isDark) 0.6f else 0.4f)
                
                // Clipping to remove sharp corner artifacts
                shape = RoundedCornerShape(32.dp)
                clip = true
            }
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(0.9f)
                .blur(20.dp) 
                .graphicsLayer {
                    val pageOffset = pageOffsetProvider()
                    alpha = (0.7f - (abs(pageOffset) * 0.4f)).coerceIn(0f, 1f)
                }
                .background(cardColors.glowBrush, RoundedCornerShape(40.dp))
        )

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = if (isDark) Color(0xFF121212) else Color.White,
            shape = RoundedCornerShape(32.dp),
            border = BorderStroke(
                1.5.dp, 
                Brush.linearGradient(
                    listOf(Color.White.copy(alpha = 0.4f), Color.Transparent, Color.White.copy(alpha = 0.1f))
                )
            ),
            // Set elevation to 0 here because it's handled by the parent graphicsLayer
            shadowElevation = 0.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.6f)
                        .align(Alignment.Center)
                        .blur(15.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(data.startColor.copy(alpha = 0.4f), Color.Transparent)
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = data.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Light,
                        color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.4f),
                        letterSpacing = 8.sp
                    )

                    Spacer(modifier = Modifier.weight(0.2f))

                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .graphicsLayer {
                                rotationY = floatAnim * 2 
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(80.dp, 160.dp)) {
                            val path = Path().apply {
                                addRoundRect(
                                    RoundRect(
                                        rect = Rect(Offset.Zero, size),
                                        cornerRadius = CornerRadius(size.width / 2)
                                    )
                                )
                            }
                            clipPath(path) {
                                drawRect(
                                    brush = Brush.verticalGradient(
                                        listOf(data.startColor, data.endColor)
                                    ),
                                    topLeft = Offset(0f, size.height * 0.4f),
                                    size = Size(size.width, size.height * 0.6f)
                                )
                            }
                            drawPath(
                                path = path,
                                color = Color.White.copy(alpha = 0.3f),
                                style = Stroke(width = 2.dp.toPx())
                            )
                            drawOval(
                                color = Color.White.copy(alpha = 0.2f),
                                topLeft = Offset(size.width * 0.2f, size.height * 0.1f),
                                size = Size(size.width * 0.2f, size.height * 0.3f)
                            )
                        }
                        
                        Icon(
                            imageVector = data.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(0.3f))

                    Text(
                        text = data.subtitle,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isDark) Color.White else Color.Black,
                        letterSpacing = (-1).sp,
                        lineHeight = 36.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Visible,
                        softWrap = false,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChapterCard(
    title: String,
    isDark: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        color = if (isDark) Color(0xFF1E1E1E) else Color.White,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f)),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(accentColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                color = if (isDark) Color.White else Color.Black
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Tap to open chapter details and start learning.",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.6f)
            )
        }
    }
}
