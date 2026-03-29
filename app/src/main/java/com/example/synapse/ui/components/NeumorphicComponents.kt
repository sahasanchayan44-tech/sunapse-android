package com.example.synapse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.synapse.ui.theme.NeuBackground
import com.example.synapse.ui.theme.NeuShadowDark
import com.example.synapse.ui.theme.NeuShadowLight

@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    elevation: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .neuShadow(elevation = elevation, shape = shape)
            .background(NeuBackground, shape)
    ) {
        content()
    }
}

fun Modifier.neuShadow(
    elevation: Dp = 8.dp,
    shape: Shape = RoundedCornerShape(24.dp),
    lightShadowColor: Color = NeuShadowLight,
    darkShadowColor: Color = NeuShadowDark
): Modifier = this.drawBehind {
    val shadowRadius = elevation.toPx()
    val shadowOffset = shadowRadius / 2f

    drawIntoCanvas { canvas ->
        val outline = shape.createOutline(size, layoutDirection, this)
        
        // Light shadow (top-left)
        val paintLight = Paint().apply {
            color = lightShadowColor
        }
        val frameworkPaintLight = paintLight.asFrameworkPaint()
        frameworkPaintLight.setShadowLayer(
            shadowRadius,
            -shadowOffset,
            -shadowOffset,
            lightShadowColor.toArgb()
        )
        canvas.drawOutline(outline, paintLight)

        // Dark shadow (bottom-right)
        val paintDark = Paint().apply {
            color = NeuBackground
        }
        val frameworkPaintDark = paintDark.asFrameworkPaint()
        frameworkPaintDark.setShadowLayer(
            shadowRadius,
            shadowOffset,
            shadowOffset,
            darkShadowColor.toArgb()
        )
        canvas.drawOutline(outline, paintDark)
    }
}
