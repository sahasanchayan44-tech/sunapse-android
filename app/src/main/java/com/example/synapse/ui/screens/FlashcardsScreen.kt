package com.example.synapse.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.toColorInt
import com.example.synapse.auth.AuthViewModel
import com.example.synapse.models.*
import com.example.synapse.ui.components.ChapterCard
import com.example.synapse.ui.components.FlashcardData
import com.example.synapse.ui.components.FlashcardItem
import com.example.synapse.ui.components.TopicListItem
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sin

private fun parseColor(colorString: String, fallback: Color = Color.Gray): Color {
    return try {
        if (colorString.isNotBlank() && colorString.startsWith("#")) {
            Color(colorString.toColorInt())
        } else {
            fallback
        }
    } catch (e: Exception) {
        fallback
    }
}

@Composable
fun TopicListScreen(
    authViewModel: AuthViewModel,
    subject: SubjectModel,
    isDark: Boolean,
    onBack: () -> Unit,
    onTopicClick: (TopicModel) -> Unit
) {
    var topics by remember { mutableStateOf<List<TopicModel>>(emptyList()) }
    val accentColor = parseColor(subject.startColor, MaterialTheme.colorScheme.primary)
    
    LaunchedEffect(subject.id) {
        when (val result = authViewModel.getTopics(subject.id)) {
            is com.example.synapse.data.FirestoreRepository.Result.Success -> {
                topics = result.data
            }
            is com.example.synapse.data.FirestoreRepository.Result.Error -> {
                // Handle error - keep empty list
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
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
                Column {
                    Text(
                        text = subject.subtitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                    Text(
                        text = "TOPICS",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isDark) Color.White else Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(topics) { topic ->
                    TopicListItem(
                        title = topic.name,
                        isDark = isDark,
                        accentColor = accentColor,
                        onClick = { onTopicClick(topic) }
                    )
                }
            }
        }
    }
}

@Composable
fun LessonListScreen(
    authViewModel: AuthViewModel,
    subject: SubjectModel,
    topic: TopicModel,
    isDark: Boolean,
    onBack: () -> Unit,
    onLessonClick: (LessonModel) -> Unit
) {
    var lessons by remember { mutableStateOf<List<LessonModel>>(emptyList()) }
    val accentColor = parseColor(subject.startColor, MaterialTheme.colorScheme.primary)
    
    // UI State for circular animation cards
    var selectedLessonForAnim by remember { mutableStateOf<LessonModel?>(null) }

    LaunchedEffect(topic.id) {
        when (val result = authViewModel.getLessons(topic.id)) {
            is com.example.synapse.data.FirestoreRepository.Result.Success -> {
                lessons = result.data
            }
            is com.example.synapse.data.FirestoreRepository.Result.Error -> {
                // Handle error - keep empty list
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF121212) else Color(0xFFFFFFFF))
            .drawBehind {
                val gridSize = 40.dp.toPx()
                val dotSize = 2.dp.toPx()
                val color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.05f)
                
                for (x in 0..(size.width / gridSize).toInt()) {
                    for (y in 0..(size.height / gridSize).toInt()) {
                        drawCircle(
                            color = color,
                            radius = dotSize,
                            center = Offset(x * gridSize, y * gridSize)
                        )
                    }
                }
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Headerr
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(accentColor, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "TOPIC UNIT",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = topic.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = Color.White)
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                if (lessons.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = accentColor)
                    }
                } else {
                    LessonSnakeTrail(
                        lessons = lessons,
                        accentColor = accentColor,
                        isDark = isDark,
                        onLessonClick = { lesson -> 
                            selectedLessonForAnim = lesson
                        }
                    )
                }
            }
        }

        // Circular Animation Cards Dialog
        selectedLessonForAnim?.let { lesson ->
            CircularAnimationCards(
                lesson = lesson,
                accentColor = accentColor,
                isDark = isDark,
                onDismiss = { selectedLessonForAnim = null },
                onStart = { 
                    selectedLessonForAnim = null
                    onLessonClick(lesson)
                }
            )
        }
    }
}

@Composable
fun CircularAnimationCards(
    lesson: LessonModel,
    accentColor: Color,
    isDark: Boolean,
    onDismiss: () -> Unit,
    onStart: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "rotation")
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(8000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rotation"
            )

            for (i in 0 until 4) {
                val angle = (i * 90f) + rotation
                val radius = 140.dp
                Box(
                    modifier = Modifier
                        .offset(
                            x = (radius.value * Math.cos(Math.toRadians(angle.toDouble()))).dp,
                            y = (radius.value * Math.sin(Math.toRadians(angle.toDouble()))).dp
                        )
                        .size(40.dp)
                        .background(accentColor.copy(alpha = 0.6f), CircleShape)
                        .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                )
            }

            Surface(
                modifier = Modifier
                    .size(280.dp)
                    .scale(1f)
                    .clickable(enabled = false) { },
                shape = CircleShape,
                color = if (isDark) Color(0xFF1E1E1E) else Color.White,
                shadowElevation = 24.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = lesson.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        color = if (isDark) Color.White else Color.Black
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onStart,
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.height(48.dp).fillMaxWidth()
                    ) {
                        Text("START", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

@Composable
fun LessonSnakeTrail(
    lessons: List<LessonModel>,
    accentColor: Color,
    isDark: Boolean,
    onLessonClick: (LessonModel) -> Unit
) {
    val listState = rememberLazyListState()
    
    val nodeIcons = listOf(
        Icons.Default.Star,
        Icons.AutoMirrored.Filled.MenuBook,
        Icons.Default.Mic,
        Icons.Default.Inventory,
        Icons.Default.Videocam,
        Icons.Default.Headphones
    )

    Box(modifier = Modifier.fillMaxSize()) {
        FloatingDecorations(isDark, accentColor)

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 40.dp, bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(lessons.size) { index ->
                val lesson = lessons[index]
                val xOffset = sin(index * 1.2f) * 75.dp.value 
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (index < lessons.size - 1) {
                        val nextXOffset = sin((index + 1) * 1.2f) * 75.dp.value
                        
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .offset(y = 65.dp)
                        ) {
                            val startX = size.width / 2 + xOffset.dp.toPx()
                            val startY = 0f
                            val endX = size.width / 2 + nextXOffset.dp.toPx()
                            val endY = 130.dp.toPx()
                            
                            val path = Path().apply {
                                moveTo(startX, startY)
                                cubicTo(
                                    startX, startY + (endY - startY) * 0.5f,
                                    endX, startY + (endY - startY) * 0.5f,
                                    endX, endY
                                )
                            }
                            
                            drawPath(
                                path = path,
                                color = if (isDark) Color(0xFF333333) else Color(0xFFE5E5E5),
                                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                            )
                            
                            drawPath(
                                path = path,
                                color = accentColor.copy(alpha = 0.5f),
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                    }

                    Box(modifier = Modifier.offset(x = xOffset.dp)) {
                        val icon = nodeIcons[index % nodeIcons.size]
                        LessonNodeCircular(
                            lesson = lesson,
                            icon = icon,
                            isLocked = false,
                            accentColor = accentColor,
                            isDark = isDark,
                            onClick = { onLessonClick(lesson) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FloatingDecorations(isDark: Boolean, accentColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    
    val floatingAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Canvas(modifier = Modifier.fillMaxSize().alpha(0.3f)) {
        drawCircle(
            color = accentColor.copy(alpha = 0.2f),
            radius = 20.dp.toPx(),
            center = Offset(40.dp.toPx(), 100.dp.toPx() + (floatingAnim * 20.dp.toPx()))
        )
        
        drawRect(
            color = accentColor.copy(alpha = 0.15f),
            size = Size(30.dp.toPx(), 30.dp.toPx()),
            topLeft = Offset(size.width - 60.dp.toPx(), 250.dp.toPx() - (floatingAnim * 30.dp.toPx())),
            style = Stroke(width = 4.dp.toPx())
        )
    }
}

@Composable
fun LessonNodeCircular(
    lesson: LessonModel,
    icon: ImageVector,
    isLocked: Boolean,
    accentColor: Color,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .scale(scaleAnim)
                .clickable(enabled = !isLocked) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .offset(y = 6.dp)
                    .background(
                        color = accentColor.darker(0.2f),
                        shape = CircleShape
                    )
            )
            
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(
                        color = accentColor,
                        shape = CircleShape
                    )
                    .border(3.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Unlocked",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
        
        Row(modifier = Modifier.offset(y = (-4).dp)) {
            repeat(3) {
                Icon(
                    Icons.Default.Star, 
                    contentDescription = null, 
                    tint = Color(0xFFFFC107), 
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = lesson.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color.White else Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(110.dp)
        )
    }
}

fun Color.darker(factor: Float): Color {
    return Color(
        red = (red * (1 - factor)).coerceIn(0f, 1f),
        green = (green * (1 - factor)).coerceIn(0f, 1f),
        blue = (blue * (1 - factor)).coerceIn(0f, 1f),
        alpha = alpha
    )
}

@Composable
fun FlashcardDetailScreen(
    subject: SubjectModel,
    topic: TopicModel,
    lesson: LessonModel,
    isDark: Boolean,
    onBack: () -> Unit,
    onOpenChapters: () -> Unit
) {
    val accentColor = parseColor(subject.startColor, MaterialTheme.colorScheme.primary)
    val endColor = parseColor(subject.endColor, MaterialTheme.colorScheme.primary)
    
    val lessonFlashcard = FlashcardData(
        title = topic.name.uppercase(),
        subtitle = lesson.name.uppercase(),
        bottomText = subject.bottomText,
        icon = Icons.Default.Bolt,
        startColor = accentColor,
        endColor = endColor,
        chapters = emptyList()
    )

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
                    text = "LESSON FLASHCARD",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isDark) Color.White else Color.Black
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                FlashcardItem(
                    data = lessonFlashcard,
                    pageOffsetProvider = { 0f },
                    isDark = isDark,
                    onClick = onOpenChapters
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
            
            Button(
                onClick = onOpenChapters,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = "OPEN STUDY CARDS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun FlashcardChaptersScreen(
    subject: SubjectModel,
    topic: TopicModel,
    lesson: LessonModel,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val chapters = lesson.studyPoints
    val accentColor = parseColor(subject.startColor, MaterialTheme.colorScheme.primary)
    
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
                    text = lesson.name.uppercase(),
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
                if (chapters.isEmpty()) {
                    Text("No study cards for this lesson.", color = if (isDark) Color.White else Color.Black)
                } else {
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
                                accentColor = accentColor,
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .height(400.dp)
                                    .graphicsLayer {
                                        translationX = if (isTopCard) offsetX.value else 0f
                                        
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
                }
                
                if (topCardIndex >= chapters.size && chapters.isNotEmpty()) {
                    Text(
                        "All study cards completed!",
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Text(
                "Swipe card to see next study point",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = (if (isDark) Color.White else Color.Black).copy(alpha = 0.5f)
            )
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
