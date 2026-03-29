package com.example.synapse.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Data model for navigation items
 */
data class NavItem(
    val icon: ImageVector,
    val title: String,
    val selectedColor: Color = Color(0xFF6366F1)
)

/**
 * A highly polished, animated bottom navigation bar with a floating bubble
 * and a smooth "moving notch" effect that supports finger gliding and dial functionality.
 */
@Composable
fun SynapseAnimatedBottomNav(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    items: List<NavItem>,
    onDialUpdate: (Float) -> Unit = {}
) {
    // Configuration
    val barHeight = 80.dp 
    val bubbleSize = 64.dp 
    val iconSize = 24.dp
    val isDark = isSystemInDarkTheme()
    
    val density = LocalDensity.current
    var itemWidthPx by remember { mutableStateOf(0f) }
    var fullWidthPx by remember { mutableStateOf(0f) }
    
    // Internal state for the drag position
    var dragOffset by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    // Logic for "Standing up" when Flashcards (Index 2) is selected
    val isFlashcardsSelected = selectedIndex == 2
    val liftOffset by animateDpAsState(
        targetValue = if (isFlashcardsSelected) (-24).dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "liftOffset"
    )

    // Calculate the target X position based on selected index or drag
    val targetX = if (itemWidthPx > 0) {
        if (isDragging) {
            dragOffset.coerceIn(itemWidthPx / 2f, fullWidthPx - itemWidthPx / 2f)
        } else {
            (itemWidthPx * selectedIndex) + (itemWidthPx / 2f)
        }
    } else 0f

    val animatedX by animateFloatAsState(
        targetValue = targetX,
        animationSpec = spring(
            dampingRatio = if (isDragging) 1f else 0.7f,
            stiffness = if (isDragging) Spring.StiffnessHigh else Spring.StiffnessMedium
        ),
        label = "indicatorX"
    )

    // Send position updates to parent if dragging on Flashcards
    LaunchedEffect(animatedX, isDragging) {
        if (isFlashcardsSelected && isDragging) {
            onDialUpdate(animatedX / fullWidthPx)
        }
    }

    // Current active color based on position
    val currentActiveIndex = if (itemWidthPx > 0) (animatedX / itemWidthPx).toInt().coerceIn(0, items.size - 1) else selectedIndex
    val activeColor by animateColorAsState(
        targetValue = items[currentActiveIndex].selectedColor,
        label = "activeColor"
    )
    val inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .onGloballyPositioned { fullWidthPx = it.size.width.toFloat() }
            .background(Color.Transparent)
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    if (!isDragging) {
                        isDragging = true
                        dragOffset = (itemWidthPx * selectedIndex) + (itemWidthPx / 2f)
                    }
                    dragOffset += delta
                },
                onDragStopped = {
                    isDragging = false
                    val newIndex = (dragOffset / itemWidthPx).roundToInt().coerceIn(0, items.size - 1)
                    onItemSelected(newIndex)
                }
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        val notchWidth = with(density) { bubbleSize.toPx() }
        
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight),
            color = MaterialTheme.colorScheme.surface,
            shape = CurvedBarShape(animatedX, notchWidth),
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .onGloballyPositioned { coords ->
                                if (index == 0) itemWidthPx = coords.size.width.toFloat()
                            }
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onItemSelected(index)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            modifier = Modifier.size(iconSize),
                            tint = if (currentActiveIndex == index) Color.Transparent else inactiveColor
                        )
                    }
                }
            }
        }

        // Active Bubble with Dynamic Colored Shadow and Lift Effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight + (bubbleSize / 2))
                .background(Color.Transparent),
            contentAlignment = Alignment.TopStart
        ) {
            Box(
                modifier = Modifier
                    .offset(
                        x = with(density) { (animatedX - (notchWidth / 2)).toDp() },
                        y = liftOffset // Standing up effect
                    )
                    .size(bubbleSize)
                    .drawBehind {
                        if (!isDark || isFlashcardsSelected) {
                            drawIntoCanvas { canvas ->
                                val paint = Paint()
                                val frameworkPaint = paint.asFrameworkPaint()
                                frameworkPaint.color = Color.Transparent.toArgb()
                                frameworkPaint.setShadowLayer(
                                    24.dp.toPx(),
                                    0f,
                                    8.dp.toPx(),
                                    activeColor.copy(alpha = if (isFlashcardsSelected) 0.8f else 0.6f).toArgb()
                                )
                                canvas.drawCircle(
                                    center = Offset(size.width / 2f, size.height / 2f),
                                    radius = size.minDimension / 2.4f,
                                    paint = paint
                                )
                            }
                        }
                    }
                    .shadow(
                        elevation = if (isDark) 12.dp else 2.dp,
                        shape = CircleShape,
                        spotColor = activeColor,
                        ambientColor = activeColor
                    )
                    .background(MaterialTheme.colorScheme.surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Flashcards Dial Icon (Pause style from reference when selected)
                Icon(
                    imageVector = if (isFlashcardsSelected) Icons.Default.Pause else items[currentActiveIndex].icon,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize + 4.dp),
                    tint = activeColor
                )
            }
        }
    }
}

class CurvedBarShape(
    private val centerX: Float,
    private val bubbleSizePx: Float
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val curveWidth = bubbleSizePx * 2.1f 
            val curveHeight = bubbleSizePx * 0.55f 

            moveTo(0f, 0f)
            lineTo(centerX - curveWidth / 2f, 0f)
            
            cubicTo(
                centerX - curveWidth / 4f, 0f,
                centerX - curveWidth / 4f, curveHeight,
                centerX, curveHeight
            )
            cubicTo(
                centerX + curveWidth / 4f, curveHeight,
                centerX + curveWidth / 4f, 0f,
                centerX + curveWidth / 2f, 0f
            )
            
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}
