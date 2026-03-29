package com.example.synapse.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
 * and a smooth "moving notch" effect.
 */
@Composable
fun SynapseAnimatedBottomNav(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    items: List<NavItem>
) {
    // Configuration
    val barHeight = 110.dp 
    val bubbleSize = 72.dp 
    val iconSize = 28.dp
    
    val activeItem = items[selectedIndex]
    val activeColor by animateColorAsState(
        targetValue = activeItem.selectedColor,
        label = "activeColor"
    )
    val inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    
    // We calculate the center X for the selected item to animate the bubble and the notch
    val density = LocalDensity.current
    var itemWidthPx by remember { mutableStateOf(0f) }
    
    // Smoothly animate the horizontal center position using Spring physics
    val targetX = if (itemWidthPx > 0) (itemWidthPx * selectedIndex) + (itemWidthPx / 2f) else 0f
    val animatedX by animateFloatAsState(
        targetValue = targetX,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "indicatorX"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(barHeight + 30.dp) // Increased box height for shadow
            .background(Color.Transparent),
        contentAlignment = Alignment.BottomCenter
    ) {
        // 1. The main bar with the dynamic notch and a custom shadow
        val shadowColor = Color.Black.copy(alpha = 0.15f)
        val notchWidth = with(density) { bubbleSize.toPx() }
        
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .drawBehind {
                    drawIntoCanvas { canvas ->
                        val paint = Paint()
                        val frameworkPaint = paint.asFrameworkPaint()
                        frameworkPaint.color = Color.Transparent.toArgb()
                        frameworkPaint.setShadowLayer(
                            12.dp.toPx(), // shadow radius
                            0f,
                            (-6).dp.toPx(), // offset shadow upwards
                            shadowColor.toArgb()
                        )
                        val outline = CurvedBarShape(animatedX, notchWidth)
                            .createOutline(size, layoutDirection, density)
                        canvas.drawOutline(outline, paint)
                    }
                },
            color = MaterialTheme.colorScheme.surface,
            shape = CurvedBarShape(animatedX, notchWidth),
            shadowElevation = 0.dp // Using custom shadow instead
        ) {
            // Row of icons
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
                        // Inactive icons
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            modifier = Modifier.size(iconSize),
                            tint = if (selectedIndex == index) Color.Transparent else inactiveColor
                        )
                    }
                }
            }
        }

        // 2. The Floating Bubble with the Active Icon
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = with(LocalDensity.current) { (animatedX - (bubbleSize.toPx() / 2)).toDp() }),
            contentAlignment = Alignment.TopStart
        ) {
            Surface(
                modifier = Modifier
                    .size(bubbleSize)
                    .offset(y = 4.dp)
                    .shadow(16.dp, CircleShape),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = items[selectedIndex].icon,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize),
                        tint = activeColor
                    )
                }
            }
        }
    }
}

/**
 * Custom Shape that draws a rectangle with a smooth concave "dip" at a specific X position.
 */
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
            val curveWidth = bubbleSizePx * 2.2f 
            val curveHeight = bubbleSizePx * 0.65f 

            moveTo(0f, 0f)
            
            // Line to the start of the notch
            lineTo(centerX - curveWidth / 2f, 0f)
            
            // The Concave Notch (The "Dip")
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
            
            // Line to Top Right
            lineTo(size.width, 0f)
            
            // Close the rectangle
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }

    private fun Dp.toPx(density: Density): Float = with(density) { this@toPx.toPx() }
}
