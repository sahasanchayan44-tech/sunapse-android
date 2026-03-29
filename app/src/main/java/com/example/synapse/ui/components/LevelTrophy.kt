package com.example.synapse.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LevelTrophy(level: Int, modifier: Modifier = Modifier) {
    val rankIdx = ((level - 1) / 10).coerceIn(0, 9)
    val levelInRank = (level - 1) % 10 // 0 to 9

    // High-end Rank Palettes based on the reference photo
    val rankColors = listOf(
        Color(0xFFFF8A65), // 1: Bronze/Orange (Core)
        Color(0xFF64B5F6), // 2: Sky Blue (Adapt)
        Color(0xFF4DB6AC), // 3: Mint/Teal (Localize)
        Color(0xFF9575CD), // 4: Lavender
        Color(0xFFBA68C8), // 5: Purple
        Color(0xFFFFD54F), // 6: Gold
        Color(0xFFF06292), // 7: Rose
        Color(0xFF81C784), // 8: Emerald
        Color(0xFF90A4AE), // 9: Platinum
        Color(0xFF455A64)  // 10: Obsidian
    )
    val accentColor = rankColors[rankIdx]

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(100.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val baseRadius = size.minDimension / 3.8f

            // 1. Draw Layered Wings - Evolving more distinctly for each level
            if (levelInRank >= 2) {
                drawProWings(center, baseRadius, accentColor, levelInRank)
            }

            // 2. Draw Bottom Ribbons/Banners - Layered and evolving
            if (levelInRank >= 1) {
                drawProBanners(center, baseRadius, accentColor, levelInRank)
            }

            // 3. Draw Main 3D Hexagon Body
            drawProHexagon(center, baseRadius, accentColor)

            // 4. Draw Central Symbol - Unique for each level (0 to 9)
            drawProLevelSymbol(center, baseRadius * 0.5f, Color.White, levelInRank)
            
            // 5. Add "Gloss" Overlay
            drawGlossEffect(center, baseRadius)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawProHexagon(center: Offset, radius: Float, color: Color) {
    val path = Path().apply {
        for (i in 0..5) {
            val angle = i * PI.toFloat() / 3f - PI.toFloat() / 2f
            val x = center.x + radius * cos(angle)
            val y = center.y + radius * sin(angle)
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    // Border Frame (3D look)
    drawPath(path, Color.White, style = Stroke(width = 5.dp.toPx(), join = StrokeJoin.Round))
    drawPath(path, Color.Black.copy(alpha = 0.1f), style = Stroke(width = 7.dp.toPx(), join = StrokeJoin.Round))

    // Shaded Inner Facets
    val points = (0..5).map { i ->
        val angle = i * PI.toFloat() / 3f - PI.toFloat() / 2f
        Offset(center.x + radius * cos(angle), center.y + radius * sin(angle))
    }

    // Shading for 3D depth
    drawPath(Path().apply {
        moveTo(center.x, center.y); lineTo(points[0].x, points[0].y); lineTo(points[1].x, points[1].y); lineTo(points[2].x, points[2].y); close()
    }, color.copy(alpha = 0.7f))
    drawPath(Path().apply {
        moveTo(center.x, center.y); lineTo(points[2].x, points[2].y); lineTo(points[3].x, points[3].y); lineTo(points[4].x, points[4].y); close()
    }, color.copy(alpha = 0.9f))
    drawPath(Path().apply {
        moveTo(center.x, center.y); lineTo(points[4].x, points[4].y); lineTo(points[5].x, points[5].y); lineTo(points[0].x, points[0].y); close()
    }, color)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawProWings(center: Offset, radius: Float, color: Color, level: Int) {
    val wingWidth = radius * 1.1f
    val wingHeight = radius * 0.4f
    val wingLayers = (level / 3) + 1 // 1, 2, or 3 layers of wings
    
    for (i in 0 until wingLayers) {
        val alpha = 0.6f / (i + 1)
        val rotationOffset = i * 15f
        val scaleDown = 1f - (i * 0.2f)

        // Left
        rotate(degrees = -10f - rotationOffset, pivot = center) {
            scale(scaleDown, pivot = center) {
                val p = Path().apply {
                    moveTo(center.x - radius, center.y)
                    cubicTo(center.x - radius - wingWidth/2, center.y - wingHeight, center.x - radius - wingWidth, center.y - wingHeight/4, center.x - radius - wingWidth, center.y)
                    lineTo(center.x - radius - wingWidth*0.6f, center.y + wingHeight/3)
                    close()
                }
                drawPath(p, color.copy(alpha = alpha))
            }
        }
        // Right
        rotate(degrees = 10f + rotationOffset, pivot = center) {
            scale(scaleDown, pivot = center) {
                val p = Path().apply {
                    moveTo(center.x + radius, center.y)
                    cubicTo(center.x + radius + wingWidth/2, center.y - wingHeight, center.x + radius + wingWidth, center.y - wingHeight/4, center.x + radius + wingWidth, center.y)
                    lineTo(center.x + radius + wingWidth*0.6f, center.y + wingHeight/3)
                    close()
                }
                drawPath(p, color.copy(alpha = alpha))
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawProBanners(center: Offset, radius: Float, color: Color, level: Int) {
    val bWidth = radius * 0.5f
    val bHeight = radius * 0.8f
    val bannerCount = if (level < 4) 1 else if (level < 8) 2 else 3

    for (i in 0 until bannerCount) {
        val xOffset = (i - (bannerCount - 1) / 2f) * (bWidth * 1.3f)
        val path = Path().apply {
            moveTo(center.x + xOffset - bWidth/2, center.y + radius * 0.4f)
            lineTo(center.x + xOffset + bWidth/2, center.y + radius * 0.4f)
            lineTo(center.x + xOffset + bWidth/2, center.y + radius + bHeight)
            lineTo(center.x + xOffset, center.y + radius + bHeight * 0.75f)
            lineTo(center.x + xOffset - bWidth/2, center.y + radius + bHeight)
            close()
        }
        drawPath(path, color.copy(alpha = 0.8f - (i * 0.1f)))
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawProLevelSymbol(center: Offset, radius: Float, color: Color, level: Int) {
    when (level) {
        0 -> drawCircle(color, radius * 0.4f, center) // 1: Point
        1 -> { // 2: Triangle
            val p = Path().apply { moveTo(center.x, center.y - radius); lineTo(center.x + radius, center.y + radius/2); lineTo(center.x - radius, center.y + radius/2); close() }
            drawPath(p, color)
        }
        2 -> drawRect(color, topLeft = Offset(center.x - radius/1.5f, center.y - radius/1.5f), size = Size(radius*1.33f, radius*1.33f)) // 3: Box
        3 -> { // 4: Diamond
            val p = Path().apply { moveTo(center.x, center.y - radius); lineTo(center.x + radius, center.y); lineTo(center.x, center.y + radius); lineTo(center.x - radius, center.y); close() }
            drawPath(p, color)
        }
        4 -> { // 5: Pentagon
            val p = Path().apply { for(i in 0..4){ val a = i*2*PI.toFloat()/5f - PI.toFloat()/2f; val x = center.x + radius*cos(a); val y = center.y + radius*sin(a); if(i==0) moveTo(x,y) else lineTo(x,y) }; close() }
            drawPath(p, color)
        }
        5 -> { // 6: Hex Gem
            val p = Path().apply { for(i in 0..5){ val a = i*PI.toFloat()/3f; val x = center.x + radius*cos(a); val y = center.y + radius*sin(a); if(i==0) moveTo(x,y) else lineTo(x,y) }; close() }
            drawPath(p, color)
        }
        6 -> { // 7: 4-Point Star
            val p = Path().apply { moveTo(center.x, center.y-radius); lineTo(center.x+radius*0.3f, center.y-radius*0.3f); lineTo(center.x+radius, center.y); lineTo(center.x+radius*0.3f, center.y+radius*0.3f); lineTo(center.x, center.y+radius); lineTo(center.x-radius*0.3f, center.y+radius*0.3f); lineTo(center.x-radius, center.y); lineTo(center.x-radius*0.3f, center.y-radius*0.3f); close() }
            drawPath(p, color)
        }
        7 -> { // 8: 5-Point Star
            val p = Path().apply { for(i in 0 until 10){ val r = if(i%2==0) radius else radius*0.4f; val a = i*PI.toFloat()/5 - PI.toFloat()/2f; val x = center.x + r*cos(a); val y = center.y + r*sin(a); if(i==0) moveTo(x,y) else lineTo(x,y) }; close() }
            drawPath(p, color)
        }
        8 -> { // 9: 8-Point Star
            val p = Path().apply { for(i in 0 until 16){ val r = if(i%2==0) radius else radius*0.5f; val a = i*PI.toFloat()/8; val x = center.x + r*cos(a); val y = center.y + r*sin(a); if(i==0) moveTo(x,y) else lineTo(x,y) }; close() }
            drawPath(p, color)
        }
        9 -> { // 10: Master Symbol (Star + Ring)
            val p = Path().apply { for(i in 0 until 10){ val r = if(i%2==0) radius else radius*0.5f; val a = i*PI.toFloat()/5 - PI.toFloat()/2f; val x = center.x + r*cos(a); val y = center.y + r*sin(a); if(i==0) moveTo(x,y) else lineTo(x,y) }; close() }
            drawPath(p, color)
            drawCircle(color, radius * 1.3f, center, style = Stroke(width = 2.dp.toPx()))
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGlossEffect(center: Offset, radius: Float) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.3f), Color.Transparent),
            center = center.copy(y = center.y - radius * 0.5f),
            radius = radius
        ),
        radius = radius,
        center = center
    )
}
