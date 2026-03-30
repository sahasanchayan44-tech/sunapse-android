package com.example.synapse.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.synapse.ui.components.ChapterCard
import com.example.synapse.ui.components.FlashcardData
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun FlashcardsScreen(dialPosition: Float = 0f) {
    // This screen is now a placeholder as the main interaction has moved to the Dashboard.
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Flashcards are now available on your Dashboard.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

@Composable
fun FlashcardChaptersScreen(
    flashcard: FlashcardData,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val chapters = flashcard.chapters
    var topCardIndex by remember { mutableIntStateOf(0) }
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = if (isDark) Color.White else Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = flashcard.subtitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isDark) Color.White else Color.Black
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val dragProgress = (offsetX.value / 500f).coerceIn(-1.5f, 1.5f)
                val absDragProgress = abs(dragProgress).coerceIn(0f, 1f)

                // Stack of cards
                chapters.asReversed().forEachIndexed { indexFromEnd, chapter ->
                    val index = chapters.size - 1 - indexFromEnd
                    if (index >= topCardIndex) {
                        val isTopCard = index == topCardIndex
                        val distanceToTop = index - topCardIndex
                        
                        ChapterCard(
                            title = chapter,
                            isDark = isDark,
                            accentColor = flashcard.startColor,
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(400.dp)
                                .graphicsLayer {
                                    translationX = if (isTopCard) offsetX.value else 0f
                                    
                                    // Smoothly transition the offset, scale and alpha of cards based on drag
                                    val currentY = distanceToTop * 20.dp.toPx()
                                    val nextY = (distanceToTop - 1).coerceAtLeast(0) * 20.dp.toPx()
                                    translationY = androidx.compose.ui.util.lerp(currentY, nextY, absDragProgress)
                                    
                                    val currentScale = 1f - distanceToTop * 0.05f
                                    val nextScale = 1f - (distanceToTop - 1).coerceAtLeast(0) * 0.05f
                                    val scale = androidx.compose.ui.util.lerp(currentScale, nextScale, absDragProgress)
                                    scaleX = scale
                                    scaleY = scale
                                    
                                    val currentAlpha = 1f - distanceToTop * 0.2f
                                    val nextAlpha = 1f - (distanceToTop - 1).coerceAtLeast(0) * 0.2f
                                    alpha = androidx.compose.ui.util.lerp(currentAlpha, nextAlpha, absDragProgress).coerceIn(0f, 1f)

                                    rotationZ = if (isTopCard) offsetX.value / 25f else 0f
                                }
                                .then(
                                    if (isTopCard) {
                                        Modifier.draggable(
                                            orientation = Orientation.Horizontal,
                                            state = rememberDraggableState { delta ->
                                                scope.launch { offsetX.snapTo(offsetX.value + delta) }
                                            },
                                            onDragStopped = { velocity ->
                                                val shouldDismiss = abs(velocity) > 1000f || abs(offsetX.value) > 300f
                                                if (shouldDismiss) {
                                                    val target = if (velocity > 0 || (velocity == 0f && offsetX.value > 0)) 1000f else -1000f
                                                    offsetX.animateTo(
                                                        targetValue = target,
                                                        initialVelocity = velocity,
                                                        animationSpec = tween(400, easing = LinearOutSlowInEasing)
                                                    )
                                                    if (topCardIndex < chapters.size - 1) {
                                                        topCardIndex++
                                                        offsetX.snapTo(0f)
                                                    } else {
                                                        onBack()
                                                    }
                                                } else {
                                                    offsetX.animateTo(
                                                        targetValue = 0f,
                                                        initialVelocity = velocity,
                                                        animationSpec = spring(
                                                            dampingRatio = Spring.DampingRatioLowBouncy,
                                                            stiffness = Spring.StiffnessMediumLow
                                                        )
                                                    )
                                                }
                                            }
                                        )
                                    } else Modifier
                                )
                        )
                    }
                }
                
                if (topCardIndex >= chapters.size) {
                    Text(
                        "All chapters completed!",
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color.Black
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Text(
                "Swipe card to see next chapter",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.5f)
            )
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
