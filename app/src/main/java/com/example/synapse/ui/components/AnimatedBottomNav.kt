package com.example.synapse.ui.components

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
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
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
    val title: String
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
    val barHeight = 80.dp
    val bubbleSize = 56.dp
    val iconSize = 24.dp
    val activeColor = Color.Black
    val inactiveColor = Color.LightGray.copy(alpha = 0.6f)
    
    // We calculate the center X for the selected item to animate the bubble and the notch
    val density = LocalDensity.current
    var itemWidthPx by remember { mutableStateOf(0f) }
    
    // Smoothly animate the horizontal center position using Spring physics
    val targetX = if (itemWidthPx > 0) (itemWidthPx * selectedIndex) + (itemWidthPx / 2f) else 0f
    val animatedX by animateFloatAsState(
        targetValue = targetX,
        animationSpec = spring(
            dampingRatio = 0.7f, // Adds a slight springy bounce
            stiffness = Spring.StiffnessMedium
        ),
        label = "indicatorX"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(barHeight + 20.dp) // Extra height for the bubble overlap
            .background(Color.Transparent),
        contentAlignment = Alignment.BottomCenter
    ) {
        // 1. The main white bar with the dynamic notch
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .padding(horizontal = 16.dp),
            color = Color.White,
            shape = CurvedBarShape(animatedX - with(density) { 16.dp.toPx() }, with(density) { bubbleSize.toPx() }),
            shadowElevation = 8.dp
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
                                indication = null // Remove default ripple to keep it clean like the video
                            ) {
                                onItemSelected(index)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Inactive icons (Active icon is handled by the floating bubble)
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
        // This bubble glides over the notch
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = with(LocalDensity.current) { (animatedX - (bubbleSize.toPx() / 2)).toDp() }),
            contentAlignment = Alignment.TopStart
        ) {
            Surface(
                modifier = Modifier
                    .size(bubbleSize)
                    .offset(y = 4.dp) // Slight lift above the bar
                    .shadow(12.dp, CircleShape),
                shape = CircleShape,
                color = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = items[selectedIndex].icon,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize + 2.dp),
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
            val curveWidth = bubbleSizePx * 1.8f
            val curveHeight = bubbleSizePx * 0.45f
            val cornerRadius = 32.dp.toPx(density) // Rounded bar corners

            moveTo(0f, cornerRadius)
            
            // Top Left Corner
            quadraticTo(0f, 0f, cornerRadius, 0f)
            
            // Line to the start of the notch
            lineTo(centerX - curveWidth / 2f, 0f)
            
            // The Concave Notch (The "Dip")
            // We use cubic Bezier to create a smooth, fluid transition
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
            
            // Line to Top Right Corner
            lineTo(size.width - cornerRadius, 0f)
            
            // Top Right Corner
            quadraticTo(size.width, 0f, size.width, cornerRadius)
            
            // Bottom Right
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }

    // Helper to convert Dp to Px inside the class
    private fun Dp.toPx(density: Density): Float = with(density) { this@toPx.toPx() }
}
