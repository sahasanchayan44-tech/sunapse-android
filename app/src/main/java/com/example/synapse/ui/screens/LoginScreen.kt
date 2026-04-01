package com.example.synapse.ui.screens

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.synapse.R
import com.example.synapse.auth.AuthViewModel
import com.example.synapse.ui.components.NeumorphicCard
import com.example.synapse.ui.theme.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

enum class AuthScreenState {
    LOGIN, SIGNUP, VERIFY
}

@Composable
fun LoginScreen(authViewModel: AuthViewModel) {
    var screenState by remember { mutableStateOf(AuthScreenState.LOGIN) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onSurface
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    val credential = GoogleAuthProvider.getCredential(idToken, null)
                    authViewModel.signInWithCredential(credential)
                } else {
                    authViewModel.setErrorMessage("Google Sign-In failed: No ID token received")
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Sign in failed: code=${e.statusCode}")
                authViewModel.setErrorMessage("Google Sign-In failed: ${e.message}")
            }
        }
    }

    BackHandler(enabled = screenState != AuthScreenState.LOGIN) {
        screenState = AuthScreenState.LOGIN
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        AnimatedContent(
            targetState = screenState,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "AuthScreenTransition"
        ) { state ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                
                Text(
                    text = "SYNAPSE",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 4.sp,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(48.dp))
                
                when (state) {
                    AuthScreenState.LOGIN -> {
                        AuthContent(
                            title = "Log In",
                            email = email,
                            onEmailChange = { email = it },
                            password = password,
                            onPasswordChange = { password = it },
                            showPassword = showPassword,
                            onPasswordToggle = { showPassword = !showPassword },
                            rememberMe = rememberMe,
                            onRememberMeChange = { rememberMe = it },
                            primaryActionText = "Log In",
                            onPrimaryAction = { 
                                if (email == "otp@test.com") screenState = AuthScreenState.VERIFY
                                else authViewModel.signInWithEmail(email, password) 
                            },
                            secondaryActionText = "Don't have an account? Create Account",
                            onSecondaryAction = { screenState = AuthScreenState.SIGNUP },
                            socialLogins = true,
                            onGoogleLogin = {
                                val webClientId = context.getString(R.string.default_web_client_id)
                                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken(webClientId)
                                    .requestEmail()
                                    .build()
                                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                                launcher.launch(googleSignInClient.signInIntent)
                            },
                            isLoading = authViewModel.isLoading,
                            primaryColor = primaryColor,
                            textColor = textColor,
                            textSecondary = textSecondary
                        )
                    }
                    AuthScreenState.SIGNUP -> {
                        AuthContent(
                            title = "Create Account",
                            email = email,
                            onEmailChange = { email = it },
                            password = password,
                            onPasswordChange = { password = it },
                            confirmPassword = confirmPassword,
                            onConfirmPasswordChange = { confirmPassword = it },
                            showPassword = showPassword,
                            onPasswordToggle = { showPassword = !showPassword },
                            rememberMe = rememberMe,
                            onRememberMeChange = { rememberMe = it },
                            rememberMeLabel = "I agree to the Terms of Service",
                            primaryActionText = "Create Account", 
                            onPrimaryAction = { authViewModel.signUpWithEmail(email, password) },
                            secondaryActionText = "Do you have an account? Log In",
                            onSecondaryAction = { screenState = AuthScreenState.LOGIN },
                            socialLogins = true,
                            onGoogleLogin = { /* Google Login */ },
                            isLoading = authViewModel.isLoading,
                            primaryColor = primaryColor,
                            textColor = textColor,
                            textSecondary = textSecondary
                        )
                    }
                    AuthScreenState.VERIFY -> {
                        VerifyContent(
                            email = email,
                            otpCode = otpCode,
                            onOtpChange = { if (it.length <= 4) otpCode = it },
                            onContinue = { /* Verify OTP logic */ },
                            onResend = { /* Resend OTP */ },
                            onBack = { screenState = AuthScreenState.LOGIN },
                            primaryColor = primaryColor,
                            textColor = textColor,
                            textSecondary = textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ColumnScope.AuthContent(
    title: String,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String? = null,
    onConfirmPasswordChange: ((String) -> Unit)? = null,
    showPassword: Boolean,
    onPasswordToggle: () -> Unit,
    rememberMe: Boolean,
    onRememberMeChange: (Boolean) -> Unit,
    rememberMeLabel: String = "Remember Me",
    primaryActionText: String,
    onPrimaryAction: () -> Unit,
    secondaryActionText: String,
    onSecondaryAction: () -> Unit,
    socialLogins: Boolean = false,
    onGoogleLogin: (() -> Unit)? = null,
    isLoading: Boolean,
    primaryColor: Color,
    textColor: Color,
    textSecondary: Color
) {
    Text(
        text = title,
        fontSize = 28.sp,
        fontWeight = FontWeight.Black,
        color = textColor
    )

    Spacer(modifier = Modifier.height(32.dp))

    NeumorphicCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AuthTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email",
                placeholder = "jamesbond123@gmail.com",
                leadingIcon = Icons.Default.Email,
                primaryColor = primaryColor,
                textColor = textColor,
                textSecondary = textSecondary
            )

            AuthTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Password",
                placeholder = "********",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                showPassword = showPassword,
                onPasswordToggle = onPasswordToggle,
                primaryColor = primaryColor,
                textColor = textColor,
                textSecondary = textSecondary
            )

            if (confirmPassword != null && onConfirmPasswordChange != null) {
                AuthTextField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = "Confirm Password",
                    placeholder = "********",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    showPassword = showPassword,
                    onPasswordToggle = onPasswordToggle,
                    primaryColor = primaryColor,
                    textColor = textColor,
                    textSecondary = textSecondary
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Remember me and Forgot Password
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = onRememberMeChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = primaryColor,
                    uncheckedColor = textSecondary.copy(alpha = 0.4f),
                    checkmarkColor = textColor
                )
            )
            Text(
                text = rememberMeLabel,
                color = textSecondary,
                fontSize = 14.sp
            )
        }
        
        if (title == "Log In") {
            Text(
                text = "Forgotten Password?",
                color = primaryColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { /* Handle forgot password */ }
            )
        }
    }

    Spacer(modifier = Modifier.height(32.dp))

    // Main Action Button
    Button(
        onClick = onPrimaryAction,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
        } else {
            Text(
                text = primaryActionText.uppercase(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                color = Color.White
            )
        }
    }

    if (socialLogins) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "OR",
            color = textSecondary.copy(alpha = 0.6f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        NeumorphicCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable { onGoogleLogin?.invoke() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(textSecondary.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "G",
                        color = Color(0xFFEA4335),
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Sign in with Google",
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }

    Spacer(modifier = Modifier.weight(1f))

    // Switch between states
    val splitText = secondaryActionText.split("?")
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = if (splitText.size > 1) splitText[0] + "?" else splitText[0],
            color = textSecondary,
            fontSize = 14.sp
        )
        Text(
            text = if (splitText.size > 1) splitText[1] else "",
            color = primaryColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onSecondaryAction() }
        )
    }
}

@Composable
fun ColumnScope.VerifyContent(
    email: String,
    otpCode: String,
    onOtpChange: (String) -> Unit,
    onContinue: () -> Unit,
    onResend: () -> Unit,
    onBack: () -> Unit,
    primaryColor: Color,
    textColor: Color,
    textSecondary: Color
) {
    Text(
        text = "Enter OTP",
        fontSize = 28.sp,
        fontWeight = FontWeight.Black,
        color = textColor
    )
    
    Spacer(modifier = Modifier.height(8.dp))
    
    Text(
        text = "OTP sent to your email address\n$email. Enter the code to proceed.",
        fontSize = 14.sp,
        color = textSecondary,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(48.dp))

    // OTP Boxes
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
    ) {
        repeat(4) { index ->
            val char = otpCode.getOrNull(index)?.toString() ?: ""
            NeumorphicCard(
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                elevation = if (char.isNotEmpty()) 4.dp else 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = char,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (char.isNotEmpty()) primaryColor else textColor
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(48.dp))

    Button(
        onClick = onContinue,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
    ) {
        Text("CONTINUE", fontSize = 16.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic, color = Color.White)
    }

    Spacer(modifier = Modifier.height(24.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Don't receive the OTP? ",
            color = textSecondary,
            fontSize = 14.sp
        )
        Text(
            text = "Resend OTP",
            color = primaryColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onResend() }
        )
    }

    Spacer(modifier = Modifier.weight(1f))
    
    // Numeric Keypad with Synapse Theme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("", "0", "DEL")
        )
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { key ->
                    if (key.isEmpty()) {
                        Spacer(modifier = Modifier.size(64.dp))
                    } else {
                        NeumorphicCard(
                            modifier = Modifier
                                .size(64.dp)
                                .clickable {
                                    if (key == "DEL") {
                                        if (otpCode.isNotEmpty()) onOtpChange(otpCode.dropLast(1))
                                    } else {
                                        onOtpChange(otpCode + key)
                                    }
                                },
                            shape = CircleShape,
                            elevation = 2.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (key == "DEL") {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                                        contentDescription = null,
                                        tint = primaryColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Text(
                                        text = key,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black,
                                        color = textColor
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onPasswordToggle: (() -> Unit)? = null,
    primaryColor: Color,
    textColor: Color,
    textSecondary: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label.uppercase(),
            color = textSecondary.copy(alpha = 0.7f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = textSecondary.copy(alpha = 0.3f), fontSize = 14.sp) },
            leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp)) },
            trailingIcon = if (isPassword && onPasswordToggle != null) {
                {
                    IconButton(onClick = onPasswordToggle) {
                        Icon(
                            if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = textSecondary.copy(alpha = 0.6f)
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = textSecondary.copy(alpha = 0.2f),
                cursorColor = primaryColor,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor
            )
        )
    }
}
