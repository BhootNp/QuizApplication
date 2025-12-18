package com.example.quizapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizapplication.repository.UserRepoImpl
import com.example.quizapplication.ui.theme.LightBg
import com.example.quizapplication.ui.theme.NavyBlue
import com.example.quizapplication.ui.theme.ProfessionalBlue
import com.example.quizapplication.ui.theme.White
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginBody()
        }
    }
}

@Composable
fun LoginBody() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as Activity
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Using the Implementation from the earlier steps
    val repo = remember { UserRepoImpl() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(LightBg)
        ) {
            // Header Section with Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(NavyBlue, ProfessionalBlue)
                        ),
                        shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "NeuroQuiz",
                        style = TextStyle(
                            fontSize = 36.sp,
                            color = White,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Text(
                        "Your Smart Learning Companion",
                        color = White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    "Welcome Back!",
                    style = TextStyle(
                        fontSize = 24.sp,
                        color = NavyBlue,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(25.dp))

                // Social Media Login Row
                Row(modifier = Modifier.fillMaxWidth()) {
                    SocialMediaCard(
                        modifier = Modifier.weight(1f),
                        image = R.drawable.facebook, // Replace with your google icon
                        label = "Google"
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    SocialMediaCard(
                        modifier = Modifier.weight(1f),
                        image = R.drawable.gmail, // Replace with your facebook icon
                        label = "Facebook"
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Divider
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                    Text(
                        "OR",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Input Fields
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    placeholder = { Text("example@mail.com") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = White,
                        focusedContainerColor = White,
                        focusedIndicatorColor = ProfessionalBlue
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    placeholder = { Text("********") },
                    trailingIcon = {
                        IconButton(onClick = { visibility = !visibility }) {
                            Icon(
                                painter = if (visibility)
                                    painterResource(R.drawable.baseline_visibility_off_24)
                                else
                                    painterResource(R.drawable.baseline_visibility_24),
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (!visibility) PasswordVisualTransformation() else VisualTransformation.None,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = White,
                        focusedContainerColor = White,
                        focusedIndicatorColor = ProfessionalBlue
                    )
                )

                Text(
                    "Forgot Password?",
                    color = ProfessionalBlue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clickable {
                            context.startActivity(Intent(context, ForgetPasswordActivity::class.java))
                        },
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Login Button
                if (isLoading) {
                    CircularProgressIndicator(color = ProfessionalBlue)
                } else {
                    Button(
                        onClick = {
                            if (email.isEmpty() || password.isEmpty()) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Please fill all fields")
                                }
                                return@Button
                            }
                            isLoading = true
                            repo.login(email, password) { success, message ->
                                isLoading = false
                                if (success) {
                                    Toast.makeText(context, "Welcome back!", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(context, DashboardActivity::class.java)
                                    intent.putExtra("email", email)
                                    context.startActivity(intent)
                                    activity.finish()
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(message ?: "Authentication failed")
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                    ) {
                        Text("Sign In", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = buildAnnotatedString {
                        append("Don't have an account? ")
                        withStyle(SpanStyle(color = ProfessionalBlue, fontWeight = FontWeight.Bold)) {
                            append("Sign Up")
                        }
                    },
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(context, RegistrationActivity::class.java))
                        activity.finish()
                    }
                )
            }
        }
    }
}

@Composable
fun SocialMediaCard(modifier: Modifier, image: Int, label: String) {
    Card(
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    LoginBody()
}