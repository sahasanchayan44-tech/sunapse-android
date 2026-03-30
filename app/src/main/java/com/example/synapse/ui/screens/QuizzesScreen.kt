package com.example.synapse.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.synapse.ui.components.NeumorphicCard

@Composable
fun QuizzesScreen() {
    var screenState by remember { mutableStateOf<QuizzesScreenState>(QuizzesScreenState.Browsing) }
    val primaryText = MaterialTheme.colorScheme.onSurface
    val accentColor = MaterialTheme.colorScheme.primary

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AnimatedContent(
            targetState = screenState,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "ScreenSwitch"
        ) { state ->
            when (state) {
                is QuizzesScreenState.Browsing -> {
                    BrowsingContent(
                        primaryText = primaryText,
                        accentColor = accentColor,
                        onStartQuiz = { category ->
                            screenState = QuizzesScreenState.ActiveQuiz(category, getMockQuestions(category.name))
                        }
                    )
                }
                is QuizzesScreenState.ActiveQuiz -> {
                    QuizGameplay(
                        category = state.category,
                        questions = state.questions,
                        onFinish = { screenState = QuizzesScreenState.Browsing },
                        accentColor = accentColor
                    )
                }
            }
        }
    }
}

sealed class QuizzesScreenState {
    object Browsing : QuizzesScreenState()
    data class ActiveQuiz(val category: QuizCategory, val questions: List<QuizQuestion>) : QuizzesScreenState()
}

@Composable
fun BrowsingContent(
    primaryText: Color,
    accentColor: Color,
    onStartQuiz: (QuizCategory) -> Unit
) {
    var isChatMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "QUIZZES",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = primaryText,
            letterSpacing = 2.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        // Mode Toggle
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(30.dp)
        ) {
            Row(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                TabButton(
                    text = "Static",
                    isSelected = !isChatMode,
                    modifier = Modifier.weight(1f),
                    onClick = { isChatMode = false }
                )
                TabButton(
                    text = "AI Chatbot",
                    isSelected = isChatMode,
                    modifier = Modifier.weight(1f),
                    onClick = { isChatMode = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedContent(
            targetState = isChatMode,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "ModeSwitch"
        ) { targetChatMode ->
            if (targetChatMode) {
                QuizChatbot(primaryText, accentColor, onStartAiQuiz = onStartQuiz)
            } else {
                StaticQuizList(primaryText, accentColor, onStartQuiz = onStartQuiz)
            }
        }
    }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(26.dp))
            .clickable { onClick() },
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun QuizGameplay(
    category: QuizCategory,
    questions: List<QuizQuestion>,
    onFinish: () -> Unit,
    accentColor: Color
) {
    var currentQuestionIdx by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableStateOf(0) }
    var isQuizFinished by remember { mutableStateOf(false) }

    val currentQuestion = questions[currentQuestionIdx]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onFinish) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "${currentQuestionIdx + 1} / ${questions.size}",
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Box(modifier = Modifier.size(48.dp)) // Spacer
        }

        if (!isQuizFinished) {
            Spacer(modifier = Modifier.height(32.dp))
            
            NeumorphicCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = currentQuestion.text,
                    modifier = Modifier.padding(24.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                currentQuestion.options.forEachIndexed { index, option ->
                    val isSelected = selectedAnswer == index
                    val containerColor = if (isSelected) accentColor else MaterialTheme.colorScheme.surface
                    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

                    NeumorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedAnswer = index },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = containerColor,
                            contentColor = contentColor
                        ) {
                            Text(
                                text = option,
                                modifier = Modifier.padding(16.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (selectedAnswer == currentQuestion.correctIdx) {
                        score++
                    }
                    if (currentQuestionIdx < questions.size - 1) {
                        currentQuestionIdx++
                        selectedAnswer = null
                    } else {
                        isQuizFinished = true
                    }
                },
                enabled = selectedAnswer != null,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("NEXT")
            }
        } else {
            // Result Screen
            Spacer(modifier = Modifier.height(64.dp))
            Icon(
                Icons.Default.EmojiEvents,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = Color(0xFFFFD700)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("Quiz Completed!", fontSize = 24.sp, fontWeight = FontWeight.Black)
            Text("Your Score: $score / ${questions.size}", fontSize = 18.sp, color = accentColor)
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("BACK TO MENU")
            }
        }
    }
}

@Composable
fun StaticQuizList(primaryText: Color, accentColor: Color, onStartQuiz: (QuizCategory) -> Unit) {
    val quizCategories = listOf(
        QuizCategory("Mathematics", "Calculus & Geometry", 15, Icons.Default.Functions, Color(0xFF3B82F6)),
        QuizCategory("Physics", "Quantum & Mechanics", 12, Icons.Default.Bolt, Color(0xFFFFAB40)),
        QuizCategory("Chemistry", "Organic & Periodic", 10, Icons.Default.Science, Color(0xFF10B981)),
        QuizCategory("History", "World Wars & Empires", 20, Icons.Default.HistoryEdu, Color(0xFFF43F5E))
    )

    LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        items(quizCategories) { category ->
            NeumorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStartQuiz(category) },
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(category.color.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(category.icon, contentDescription = null, tint = category.color)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(category.name, fontWeight = FontWeight.Black, color = primaryText)
                        Text(category.desc, fontSize = 12.sp, color = primaryText.copy(alpha = 0.6f))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${category.qCount}", fontWeight = FontWeight.Bold, color = accentColor)
                        Text("Questions", fontSize = 10.sp, color = primaryText.copy(alpha = 0.4f))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizChatbot(
    primaryText: Color, 
    accentColor: Color,
    onStartAiQuiz: (QuizCategory) -> Unit
) {
    var message by remember { mutableStateOf("") }
    val chatHistory = remember { mutableStateListOf<ChatMessage>() }
    var showFilter by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val subjectCategories = listOf(
        SubjectGroup("Science", listOf("Physics", "Chemistry", "Biology", "Astronomy", "Environmental Science"), Icons.Default.Science, Color(0xFF10B981)),
        SubjectGroup("Mathematics", listOf("Algebra", "Calculus", "Statistics", "Geometry", "Trigonometry"), Icons.Default.Functions, Color(0xFF3B82F6)),
        SubjectGroup("Tech", listOf("Coding", "Artificial Intelligence", "Cybersecurity", "Blockchain", "Data Science"), Icons.Default.Computer, Color(0xFF8B5CF6)),
        SubjectGroup("Humanities", listOf("History", "Literature", "Philosophy", "Psychology", "Sociology"), Icons.Default.HistoryEdu, Color(0xFFF43F5E)),
        SubjectGroup("Business", listOf("Economics", "Marketing", "Finance", "Management", "Entrepreneurship"), Icons.Default.BusinessCenter, Color(0xFFFFAB40))
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize(), reverseLayout = true) {
                items(chatHistory.reversed()) { msg ->
                    ChatBubble(msg, primaryText, accentColor, onStartAiQuiz)
                }
                item {
                    ChatBubble(
                        ChatMessage("Hello! I'm your AI Quiz assistant. Type a subject or use the filter icon to select one!", false),
                        primaryText, accentColor, onStartAiQuiz
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask AI for a quiz...") },
                shape = RoundedCornerShape(24.dp),
                leadingIcon = {
                    IconButton(onClick = { showFilter = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter Subjects", tint = accentColor)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentColor,
                    unfocusedBorderColor = primaryText.copy(alpha = 0.1f)
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Surface(
                modifier = Modifier.size(50.dp).clip(CircleShape).clickable {
                    if (message.isNotBlank()) {
                        val userMsg = message
                        chatHistory.add(ChatMessage(userMsg, true))
                        message = ""
                        chatHistory.add(ChatMessage("I've generated a random quiz about '$userMsg'. Ready to start?", false))
                        chatHistory.add(ChatMessage("START_QUIZ_ACTION|$userMsg", false))
                    }
                },
                color = accentColor
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.padding(12.dp))
            }
        }
    }

    if (showFilter) {
        ModalBottomSheet(
            onDismissRequest = { showFilter = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 48.dp)
            ) {
                Text(
                    text = "Select a Subject",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                LazyColumn(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    items(subjectCategories) { group ->
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(group.icon, contentDescription = null, tint = group.color, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = group.name.uppercase(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = group.color,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(group.subjects) { subject ->
                                    FilterChip(
                                        onClick = {
                                            message = subject
                                            showFilter = false
                                        },
                                        label = { Text(subject) },
                                        selected = false,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    msg: ChatMessage, 
    primaryText: Color, 
    accentColor: Color,
    onStartAiQuiz: (QuizCategory) -> Unit
) {
    if (msg.text.startsWith("START_QUIZ_ACTION")) {
        val subject = msg.text.split("|").getOrElse(1) { "AI Subject" }
        Button(
            onClick = { 
                onStartAiQuiz(QuizCategory(subject, "AI Generated Quiz", 5, Icons.Default.AutoAwesome, Color(0xFFBB86FC)))
            },
            modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(0.6f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("START AI QUIZ")
        }
        return
    }

    val alignment = if (msg.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bgColor = if (msg.isUser) accentColor else MaterialTheme.colorScheme.surface
    val contentColor = if (msg.isUser) Color.White else primaryText

    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), contentAlignment = alignment) {
        Surface(
            color = bgColor,
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (msg.isUser) 20.dp else 0.dp,
                bottomEnd = if (msg.isUser) 0.dp else 20.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = msg.text,
                modifier = Modifier.padding(12.dp),
                color = contentColor,
                fontSize = 14.sp
            )
        }
    }
}

data class SubjectGroup(val name: String, val subjects: List<String>, val icon: ImageVector, val color: Color)
data class QuizCategory(val name: String, val desc: String, val qCount: Int, val icon: ImageVector, val color: Color)
data class QuizQuestion(val text: String, val options: List<String>, val correctIdx: Int)
data class ChatMessage(val text: String, val isUser: Boolean)

fun getMockQuestions(category: String): List<QuizQuestion> {
    return when (category) {
        "Mathematics" -> listOf(
            QuizQuestion("What is the square root of 144?", listOf("10", "12", "14", "16"), 1),
            QuizQuestion("What is 7 * 8?", listOf("54", "56", "64", "48"), 1)
        )
        "Physics" -> listOf(
            QuizQuestion("What is the unit of force?", listOf("Newton", "Joule", "Watt", "Pascal"), 0),
            QuizQuestion("What is the speed of light?", listOf("300,000 km/s", "150,000 km/s", "1,000,000 km/s", "500,000 km/s"), 0)
        )
        else -> listOf(
            QuizQuestion("Random AI Question: What is 2+2?", listOf("3", "4", "5", "6"), 1),
            QuizQuestion("Random AI Question: Is Kotlin fun?", listOf("Yes", "No", "Maybe", "Depends"), 0)
        )
    }
}
