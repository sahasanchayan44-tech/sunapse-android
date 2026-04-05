package com.example.synapse.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint as AndroidPaint
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val LIQUID_GLASS_SHADER = """
uniform shader inputTexture;
uniform float2 lensOrigin;
uniform float2 capsuleSize;
uniform float refractionStrength;
uniform float motion;
layout(color) uniform half4 accentTint;

float sdCapsule(float2 p, float2 size, float radius) {
    float2 q = abs(p - size * 0.5) - (size * 0.5 - radius);
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - radius;
}

float hash21(float2 p) {
    p = fract(p * float2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

half4 main(float2 fragCoord) {
    float2 local = fragCoord;
    float2 center = capsuleSize * 0.5;
    float radius = capsuleSize.y * 0.5;
    float maskDistance = sdCapsule(local, capsuleSize, radius);
    float mask = 1.0 - smoothstep(0.0, 1.8, maskDistance);

    if (mask <= 0.001) {
        return half4(0.0);
    }

    float2 normalized = (local - center) / max(radius, 1.0);
    float2 radial = float2(
        normalized.x * (capsuleSize.y / max(capsuleSize.x, 1.0)),
        normalized.y
    );
    float radialDistance = clamp(length(radial), 0.0, 1.0);
    float edgeFactor = smoothstep(0.24, 1.0, radialDistance);
    float centerFactor = 1.0 - radialDistance;
    float2 direction = normalize(local - center + float2(0.001, 0.001));

    float tension = 0.92 + motion * 0.38;
    float ripple = sin(local.x * 0.085 - motion * 7.0) * sin(local.y * 0.055 + motion * 4.5);
    float barrel = refractionStrength * pow(edgeFactor, 1.7) * tension;
    float focusPull = refractionStrength * 0.12 * centerFactor;
    float lowerLens = smoothstep(0.34, 0.96, local.y / capsuleSize.y);
    float upperLens = 1.0 - smoothstep(0.0, 0.42, local.y / capsuleSize.y);
    float2 curvedOffset = float2(
        direction.x * barrel * 1.3,
        direction.y * barrel * 0.52 + lowerLens * edgeFactor * refractionStrength * 0.34 - upperLens * centerFactor * refractionStrength * 0.08
    );
    float2 sampleCoord =
        lensOrigin +
        local +
        curvedOffset -
        radial * focusPull +
        float2(ripple * motion * 1.1, ripple * motion * 0.5);

    half4 refracted =
        inputTexture.eval(sampleCoord) +
        inputTexture.eval(sampleCoord + float2(1.8, 0.0)) +
        inputTexture.eval(sampleCoord + float2(-1.8, 0.0)) +
        inputTexture.eval(sampleCoord + float2(0.0, 1.35)) +
        inputTexture.eval(sampleCoord + float2(0.0, -1.1));
    refracted /= 5.0;

    float innerRim = smoothstep(1.6, 0.14, abs(maskDistance + 0.68));
    float outerCaustic = smoothstep(2.0, 0.08, abs(maskDistance));
    float lowerCaustic = lowerLens * smoothstep(1.8, 0.06, abs(maskDistance));
    float topHighlight = smoothstep(0.28, 0.02, local.y / max(capsuleSize.y, 1.0));
    topHighlight *= smoothstep(1.0, 0.22, abs(normalized.x));
    float lightBand = smoothstep(0.42, 0.16, local.y / capsuleSize.y) * smoothstep(0.98, 0.22, abs(normalized.x));
    float bottomShade = smoothstep(0.72, 1.0, local.y / capsuleSize.y) * 0.012;
    float noise = (hash21(local * 0.13 + motion * 4.7) - 0.5) * 0.012;

    half3 color = refracted.rgb;
    color += accentTint.rgb * (0.026 + 0.036 * centerFactor);
    color += half3(0.010 * mask);
    color += half3(lightBand * 0.022);
    color += half3(topHighlight * 0.045);
    color += half3(innerRim * 0.14 + outerCaustic * 0.06 + lowerCaustic * 0.08);
    color -= half3(bottomShade);
    color += half3(noise);

    float alpha = mask * 0.98;
    return half4(color * alpha, alpha);
}
"""

@Composable
fun LiquidGlassNavBar(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    glassTintColor: Color = accentColor,
    isDark: Boolean = isSystemInDarkTheme()
) {
    val tabHeight = 52.dp
    val horizontalPadding = 16.dp
    val itemSpacing = 8.dp
    val itemHorizontalPadding = 22.dp
    val scrollState = rememberLazyListState()
    val density = LocalDensity.current

    var rootBounds by remember { mutableStateOf<Rect?>(null) }
    var rootSize by remember { mutableStateOf(IntSize.Zero) }
    val itemWindowBounds = remember { mutableStateMapOf<Int, Rect>() }
    val motionPhase = remember { Animatable(0f) }
    var previousTargetCenter by remember { mutableStateOf<Float?>(null) }
    var lastTravelPx by remember { mutableStateOf(0f) }

    val baseTextColor = if (isDark) Color(0xFF333333) else Color(0xFF555555)
    val baseItemColor = if (isDark) Color(0xFFE7EAEE) else Color(0xFFEAECEF)
    val itemFrames: Map<Int, Rect> = remember(rootBounds, itemWindowBounds.toMap()) {
        val root = rootBounds
        if (root == null) {
            emptyMap<Int, Rect>()
        } else {
            itemWindowBounds.mapValues { (_, bounds) ->
                Rect(
                    left = bounds.left - root.left,
                    top = bounds.top - root.top,
                    right = bounds.right - root.left,
                    bottom = bounds.bottom - root.top
                )
            }
        }
    }

    val selectedFrame = itemFrames[selectedIndex]
    val selectedWidth = selectedFrame?.width ?: 0f
    val selectedHeight = selectedFrame?.height ?: with(density) { tabHeight.toPx() }
    val targetCenter = selectedFrame?.center?.x ?: 0f
    val stretchPx = min(selectedWidth * 0.2f, lastTravelPx * 0.2f) * motionPhase.value
    val animatedWidth by animateFloatAsState(
        targetValue = max(selectedWidth + stretchPx, selectedWidth * 0.92f),
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMedium),
        label = "glassWidth"
    )
    val animatedX by animateFloatAsState(
        targetValue = max(0f, targetCenter - (animatedWidth / 2f)),
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMedium),
        label = "glassX"
    )
    val refractionStrengthPx = with(density) { 9.dp.toPx() } + (lastTravelPx * 0.04f) + (motionPhase.value * with(density) { 6.dp.toPx() })
    val backgroundTexture = remember(rootSize, itemFrames, baseItemColor) {
        createNavBarTexture(
            size = rootSize,
            itemFrames = itemFrames.values.toList(),
            itemColor = baseItemColor,
            cornerRadiusPx = with(density) { 23.dp.toPx() }
        )
    }

    LaunchedEffect(selectedIndex) {
        scrollState.animateScrollToItem(selectedIndex)
        selectedFrame?.let { frame ->
            val newCenter = frame.center.x
            lastTravelPx = previousTargetCenter?.let { abs(newCenter - it) } ?: 0f
            previousTargetCenter = newCenter
            motionPhase.snapTo(1f)
            motionPhase.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = 0.58f,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
    }

    Box(
        modifier = modifier
            .height(tabHeight + 24.dp)
            .padding(vertical = 12.dp)
            .onGloballyPositioned { coordinates ->
                rootBounds = coordinates.boundsInWindow()
                rootSize = coordinates.size
            }
    ) {
        if (selectedFrame != null && selectedWidth > 0f) {
            LiquidGlassIndicator(
                modifier = Modifier
                    .width(with(density) { animatedWidth.toDp() })
                    .height(with(density) { selectedHeight.toDp() })
                    .align(Alignment.TopStart)
                    .offset(x = with(density) { animatedX.toDp() }),
                texture = backgroundTexture,
                lensOrigin = Offset(animatedX, selectedFrame.top),
                refractionStrength = refractionStrengthPx,
                motion = motionPhase.value.coerceIn(-0.35f, 1f),
                accentColor = glassTintColor,
                isDark = isDark
            )
        }

        LazyRow(
            state = scrollState,
            horizontalArrangement = Arrangement.spacedBy(itemSpacing),
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            modifier = Modifier.matchParentSize()
        ) {
            itemsIndexed(items) { index, item ->
                val isSelected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .height(tabHeight)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) {
                                Color.Transparent
                            } else {
                                baseItemColor
                            }
                        )
                        .clickable { onItemSelected(index) }
                        .onGloballyPositioned { coordinates ->
                            itemWindowBounds[index] = coordinates.boundsInWindow()
                        }
                        .padding(horizontal = itemHorizontalPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        color = if (isSelected) accentColor else baseTextColor,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun LiquidGlassIndicator(
    modifier: Modifier,
    texture: Bitmap?,
    lensOrigin: Offset,
    refractionStrength: Float,
    motion: Float,
    accentColor: Color,
    isDark: Boolean
) {
    val shape = RoundedCornerShape(50)
    val liftY = (-2).dp

    Box(
        modifier = modifier
            .offset(y = liftY)
            .drawBehind {
                val radius = size.height / 2f
                drawRoundRect(
                    color = if (isDark) {
                        Color.White.copy(alpha = 0.025f)
                    } else {
                        Color.Black.copy(alpha = 0.025f)
                    },
                    topLeft = Offset(0f, 7.dp.toPx()),
                    size = size.copy(height = size.height - 6.dp.toPx()),
                    cornerRadius = CornerRadius(radius, radius)
                )
            }
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .drawWithCache {
                    onDrawBehind {
                        val radius = size.height / 2f

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && texture != null) {
                            drawLiquidGlassShader(
                                texture = texture,
                                width = size.width,
                                height = size.height,
                                lensOrigin = lensOrigin,
                                refractionStrength = refractionStrength,
                                motion = motion,
                                accentColor = accentColor
                            )
                        } else {
                            drawRoundRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.045f),
                                        Color.White.copy(alpha = 0.015f),
                                        Color.Transparent
                                    )
                                ),
                                cornerRadius = CornerRadius(radius, radius)
                            )
                        }

                        drawRoundRect(
                            color = accentColor.copy(alpha = if (isDark) 0.09f else 0.11f),
                            cornerRadius = CornerRadius(radius, radius)
                        )

                        drawRoundRect(
                            color = Color.White.copy(alpha = if (isDark) 0.045f else 0.04f),
                            cornerRadius = CornerRadius(radius, radius)
                        )

                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    accentColor.copy(alpha = 0.08f),
                                    Color.Transparent,
                                    accentColor.copy(alpha = 0.03f)
                                )
                            ),
                            topLeft = Offset(0f, size.height * 0.16f),
                            size = size.copy(height = size.height * 0.38f),
                            cornerRadius = CornerRadius(radius, radius)
                        )

                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.10f),
                                    Color.White.copy(alpha = 0.025f),
                                    Color.Transparent
                                )
                            ),
                            topLeft = Offset(16.dp.toPx(), 6.dp.toPx()),
                            size = size.copy(width = size.width - 32.dp.toPx(), height = size.height * 0.16f),
                            cornerRadius = CornerRadius(radius, radius)
                        )

                        drawRoundRect(
                            color = Color.White.copy(alpha = if (isDark) 0.22f else 0.18f),
                            style = Stroke(width = 1.1.dp.toPx()),
                            cornerRadius = CornerRadius(radius, radius)
                        )
                    }
                }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLiquidGlassShader(
    texture: Bitmap,
    width: Float,
    height: Float,
    lensOrigin: Offset,
    refractionStrength: Float,
    motion: Float,
    accentColor: Color
) {
    val shader = RuntimeShader(LIQUID_GLASS_SHADER).apply {
        setFloatUniform("lensOrigin", lensOrigin.x, lensOrigin.y)
        setFloatUniform("capsuleSize", width, height)
        setFloatUniform("refractionStrength", refractionStrength)
        setFloatUniform("motion", motion)
        setColorUniform(
            "accentTint",
            android.graphics.Color.valueOf(
                accentColor.red,
                accentColor.green,
                accentColor.blue,
                accentColor.alpha
            )
        )
        setInputShader(
            "inputTexture",
            BitmapShader(texture, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        )
    }

    val paint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        this.shader = shader
    }

    drawContext.canvas.nativeCanvas.drawRoundRect(
        0f,
        0f,
        width,
        height,
        height / 2f,
        height / 2f,
        paint
    )
}

private fun createNavBarTexture(
    size: IntSize,
    itemFrames: List<Rect>,
    itemColor: Color,
    cornerRadiusPx: Float
): Bitmap? {
    if (size.width <= 0 || size.height <= 0 || itemFrames.isEmpty()) {
        return null
    }

    val bitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bitmap)
    val fill = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        color = itemColor.toArgb()
    }
    val highlight = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
        shader = android.graphics.LinearGradient(
            0f,
            0f,
            0f,
            size.height.toFloat(),
            intArrayOf(
                Color.White.copy(alpha = 0.16f).toArgb(),
                Color.Transparent.toArgb()
            ),
            null,
            Shader.TileMode.CLAMP
        )
    }

    itemFrames.forEach { rect ->
        canvas.drawRoundRect(
            rect.left,
            rect.top,
            rect.right,
            rect.bottom,
            cornerRadiusPx,
            cornerRadiusPx,
            fill
        )
        canvas.drawRoundRect(
            rect.left,
            rect.top,
            rect.right,
            rect.bottom,
            cornerRadiusPx,
            cornerRadiusPx,
            highlight
        )
    }

    return bitmap
}
