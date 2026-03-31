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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.synapse.R
import com.example.synapse.auth.AuthViewModel
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
    val primaryBlue = Primary
    val backgroundDark = DarkNeuBackground
    val cardBackground = DarkNeuShadowLight

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                authViewModel.signInWithCredential(credential)
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
            .background(backgroundDark)
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
                
                // Logo (Bolt icon as in the image)
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(listOf(primaryBlue, primaryBlue.copy(alpha = 0.7f)))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Logo",
                        modifier = Modifier.size(48.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                
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
                                // Demo: if email is "otp@test.com", go to verify
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
                            cardBackground = cardBackground,
                            primaryBlue = primaryBlue
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
                            primaryActionText = "Log In", 
                            onPrimaryAction = { authViewModel.signUpWithEmail(email, password) },
                            secondaryActionText = "Do you have an account? Log In",
                            onSecondaryAction = { screenState = AuthScreenState.LOGIN },
                            socialLogins = true,
                            onGoogleLogin = { /* Google Login */ },
                            isLoading = authViewModel.isLoading,
                            cardBackground = cardBackground,
                            primaryBlue = primaryBlue
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
                            primaryBlue = primaryBlue,
                            cardBackground = cardBackground
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
    cardBackground: Color,
    primaryBlue: Color
) {
    Text(
        text = title,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )

    Spacer(modifier = Modifier.height(32.dp))

    // Input Fields
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AuthTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Email",
            placeholder = "jamesbond123@gmail.com",
            leadingIcon = Icons.Default.Email,
            cardBackground = cardBackground,
            primaryBlue = primaryBlue
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
            cardBackground = cardBackground,
            primaryBlue = primaryBlue
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
                cardBackground = cardBackground,
                primaryBlue = primaryBlue
            )
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
                    checkedColor = primaryBlue,
                    uncheckedColor = Color.White.copy(alpha = 0.4f),
                    checkmarkColor = Color.White
                )
            )
            Text(
                text = rememberMeLabel,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }
        
        if (title == "Log In") {
            Text(
                text = "Forgotten Password?",
                color = primaryBlue,
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
        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
        } else {
            Text(
                text = primaryActionText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }

    if (socialLogins) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = if (title == "Log In") "Or Log In with" else "Or Create with",
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SocialLoginButton(
                text = "Log In with Google",
                iconText = "G",
                onClick = { onGoogleLogin?.invoke() },
                backgroundColor = cardBackground
            )
            SocialLoginButton(
                text = "Log In with Facebook",
                iconText = "f",
                onClick = { /* Facebook Login */ },
                backgroundColor = cardBackground
            )
        }
    }

    Spacer(modifier = Modifier.weight(1f))

    // Switch between states
    val splitText = secondaryActionText.split("?")
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = if (splitText.size > 1) splitText[0] + "?" else splitText[0],
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 14.sp
        )
        Text(
            text = if (splitText.size > 1) splitText[1] else "",
            color = primaryBlue,
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
    primaryBlue: Color,
    cardBackground: Color
) {
    Text(
        text = "Enter OTP",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
    
    Spacer(modifier = Modifier.height(8.dp))
    
    Text(
        text = "OTP sent to your email address\n$email. Enter the code to proceed.",
        fontSize = 14.sp,
        color = Color.White.copy(alpha = 0.6f),
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
            Surface(
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                color = Color.Transparent,
                border = BorderStroke(1.dp, if (char.isNotEmpty()) primaryBlue else Color.White.copy(alpha = 0.1f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = char,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
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
        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
    ) {
        Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }

    Spacer(modifier = Modifier.height(24.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Don't receive the OTP? ",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 14.sp
        )
        Text(
            text = "Resend OTP",
            color = primaryBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onResend() }
        )
    }

    Spacer(modifier = Modifier.weight(1f))
    
    // Numeric Keypad
    Column(
        modifier = Modifier.fillMaxWidth(),
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
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .clickable(enabled = key.isNotEmpty()) {
                                if (key == "DEL") {
                                    if (otpCode.isNotEmpty()) onOtpChange(otpCode.dropLast(1))
                                } else if (key.isNotEmpty()) {
                                    onOtpChange(otpCode + key)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (key == "DEL") {
                            Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = null, tint = Color.White)
                        } else if (key.isNotEmpty()) {
                            Text(text = key, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
    cardBackground: Color,
    primaryBlue: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = cardBackground,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.3f)) },
                leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = primaryBlue, modifier = Modifier.size(20.dp)) },
                trailingIcon = if (isPassword && onPasswordToggle != null) {
                    {
                        IconButton(onClick = onPasswordToggle) {
                            Icon(
                                if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.4f)
                            )
                        }
                    }
                } else null,
                visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    cursorColor = primaryBlue,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }
    }
}

@Composable
fun SocialLoginButton(
    text: String,
    iconText: String,
    onClick: () -> Unit,
    backgroundColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iconText,
                    color = if (iconText == "G") Color(0xFFEA4335) else Color(0xFF1877F2),
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}
