package com.example.synapse.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

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

@Composable
fun FlashcardsScreen(dialPosition: Float = 0f) {
    val isDark = isSystemInDarkTheme()
    val cards = remember {
        listOf(
            FlashcardData("DEEP", "SLEEP", "PILL", Icons.Default.Bolt, Color(0xFF00D2FF), Color(0xFF3A7BD5), listOf("Biology of Sleep", "REM Cycles", "Circadian Rhythm", "Sleep Hygiene")),
            FlashcardData("INSIDE", "NATURE", "BREATHE", Icons.Default.Nature, Color(0xFF11998E), Color(0xFF38EF7D), listOf("Ecosystems", "Photosynthesis", "Flora & Fauna", "Conservation")),
            FlashcardData("FORGOTTEN", "2096", "MEMORY", Icons.Default.Monitor, Color(0xFFF43F5E), Color(0xFF881337), listOf("Cyberpunk History", "Neural Links", "Digital Ghost", "The Archive")),
            FlashcardData("QUANTUM", "PHYSICS", "CORE", Icons.Default.AutoAwesome, Color(0xFF8E2DE2), Color(0xFF4A00E0), listOf("Wave-Particle Dualism", "Uncertainty Principle", "Entanglement", "Superposition")),
            FlashcardData("NEURO", "SYNAPSE", "BRAIN", Icons.Default.AutoAwesome, Color(0xFFFFA000), Color(0xFFFF5722), listOf("Neurons", "Neurotransmitters", "Brain Plasticity", "Cognitive Functions"))
        )
    }

    var selectedFlashcard by remember { mutableStateOf<FlashcardData?>(null) }

    AnimatedContent(
        targetState = selectedFlashcard,
        transitionSpec = {
            if (targetState != null) {
                (slideInVertically { it } + fadeIn()).togetherWith(slideOutVertically { -it } + fadeOut())
            } else {
                (slideInVertically { -it } + fadeIn()).togetherWith(slideOutVertically { it } + fadeOut())
            }
        },
        label = "FlashcardTransition"
    ) { flashcard ->
        if (flashcard == null) {
            FlashcardList(cards, dialPosition, isDark) { selectedFlashcard = it }
        } else {
            FlashcardChaptersScreen(flashcard, isDark) { selectedFlashcard = null }
        }
    }
}

@Composable
fun FlashcardList(
    cards: List<FlashcardData>,
    dialPosition: Float,
    isDark: Boolean,
    onCardClick: (FlashcardData) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { cards.size })

    LaunchedEffect(dialPosition) {
        val targetPage = (dialPosition * (cards.size)).toInt().coerceIn(0, cards.size - 1)
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    val titleColor = if (isDark) Color.White else Color(0xFF1A1A1A)
    val subtitleColor = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.4f)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        Text(
            text = "Flashcard Dials",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = titleColor,
            letterSpacing = (-1).sp
        )
        
        Text(
            text = "3D Float & Glide",
            fontSize = 14.sp,
            color = subtitleColor
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
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
                    val startX = centerX + (radius - 8.dp.toPx()) * cos(radian.toDouble()).toFloat()
                    val startY = height + (radius - 8.dp.toPx()) * sin(radian.toDouble()).toFloat()
                    val endX = centerX + radius * cos(radian.toDouble()).toFloat()
                    val endY = height + radius * sin(radian.toDouble()).toFloat()
                    
                    drawLine(
                        color = titleColor.copy(alpha = 0.15f),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }
            
            Text(
                text = cards[pagerState.currentPage].subtitle.uppercase(),
                color = titleColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                modifier = Modifier.offset(y = (-10).dp)
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 64.dp),
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
                isDark = isDark,
                onClick = { onCardClick(cards[page]) }
            )
        }
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}

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
            animation = tween(2000, easing = EaseInOutSine),
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
                alpha = lerp(0.3f, 1f, fraction)
                cameraDistance = 12f * density
            }
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(0.9f)
                .blur(60.dp)
                .graphicsLayer {
                    val pageOffset = pageOffsetProvider()
                    alpha = (0.7f - (abs(pageOffset) * 0.4f)).coerceIn(0f, 1f)
                }
                .background(cardColors.glowBrush, RoundedCornerShape(40.dp))
        )

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = if (isDark) Color(0xFF121212).copy(alpha = 0.9f) else Color.White.copy(alpha = 0.9f),
            shape = RoundedCornerShape(32.dp),
            border = BorderStroke(
                1.5.dp, 
                Brush.linearGradient(
                    listOf(Color.White.copy(alpha = 0.4f), Color.Transparent, Color.White.copy(alpha = 0.1f))
                )
            ),
            shadowElevation = if (isDark) 0.dp else 20.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.6f)
                        .align(Alignment.Center)
                        .blur(40.dp)
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
                            modifier = Modifier.size(40.dp).blur(1.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(0.3f))

                    Text(
                        text = data.subtitle,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isDark) Color.White else Color.Black,
                        letterSpacing = (-1).sp,
                        lineHeight = 44.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "20",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.3f)
                        )
                        Text(
                            text = data.bottomText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color.Black,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "26",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FlashcardChaptersScreen(
    flashcard: FlashcardData,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val chapters = flashcard.chapters
    var topCardIndex by remember { mutableStateOf(0) }
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
                // Stack of cards
                chapters.asReversed().forEachIndexed { indexFromEnd, chapter ->
                    val index = chapters.size - 1 - indexFromEnd
                    if (index >= topCardIndex) {
                        val isTopCard = index == topCardIndex
                        val cardOffset = (index - topCardIndex) * 20
                        
                        ChapterCard(
                            title = chapter,
                            isDark = isDark,
                            accentColor = flashcard.startColor,
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(400.dp)
                                .graphicsLayer {
                                    val dragProgress = if (isTopCard) offsetX.value / 500f else 0f
                                    translationX = if (isTopCard) offsetX.value else 0f
                                    translationY = (cardOffset.dp.toPx() * (1f - abs(dragProgress))).coerceAtLeast(0f)
                                    scaleX = 1f - (index - topCardIndex) * 0.05f + abs(dragProgress) * 0.05f
                                    scaleY = 1f - (index - topCardIndex) * 0.05f + abs(dragProgress) * 0.05f
                                    alpha = 1f - (index - topCardIndex) * 0.2f + abs(dragProgress) * 0.2f
                                    rotationZ = if (isTopCard) offsetX.value / 20f else 0f
                                }
                                .then(
                                    if (isTopCard) {
                                        Modifier.draggable(
                                            orientation = Orientation.Horizontal,
                                            state = rememberDraggableState { delta ->
                                                scope.launch { offsetX.snapTo(offsetX.value + delta) }
                                            },
                                            onDragStopped = {
                                                if (abs(offsetX.value) > 300f) {
                                                    val target = if (offsetX.value > 0) 1000f else -1000f
                                                    offsetX.animateTo(target, tween(300))
                                                    if (topCardIndex < chapters.size - 1) {
                                                        topCardIndex++
                                                        offsetX.snapTo(0f)
                                                    } else {
                                                        onBack()
                                                    }
                                                } else {
                                                    offsetX.animateTo(0f, spring())
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
                color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.5f)
            )
            
            Spacer(modifier = Modifier.height(20.dp))
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

@Immutable
data class FlashcardColors(
    val content: Color,
    val glass: Color,
    val glowBrush: Brush,
    val accentBrush: Brush
)
