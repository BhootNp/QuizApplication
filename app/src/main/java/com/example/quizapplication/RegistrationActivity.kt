package com.example.quizapplication

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.quizapplication.model.UserModel
import com.example.quizapplication.repository.UserRepoImpl
import com.example.quizapplication.ui.theme.LightBg
import com.example.quizapplication.ui.theme.NavyBlue
import com.example.quizapplication.ui.theme.ProfessionalBlue
import com.example.quizapplication.ui.theme.White
import com.example.quizapplication.viewmodel.UserViewModel
import java.util.Calendar
class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegisterBody()
        }
    }
}

@Composable
fun RegisterBody() {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }
    var terms by remember { mutableStateOf(false) }

    // Add a loading state to prevent multiple clicks and show progress
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as Activity
    val calendar = Calendar.getInstance()

    // Ensure UserRepoImpl() actually implements the methods and doesn't just have TODO()
    val userViewModel = remember { UserViewModel(UserRepoImpl()) }

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datepicker = DatePickerDialog(
        context, { _, y, m, d ->
            selectedDate = "$y/${m + 1}/$d"
        }, year, month, day
    )

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(LightBg)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                "Create Account",
                style = TextStyle(
                    fontSize = 28.sp,
                    color = NavyBlue,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                "Join NeuroQuiz Today",
                style = TextStyle(fontSize = 14.sp, color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // First Name and Last Name Row
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    placeholder = { Text("First Name") },
                    modifier = Modifier.weight(1f).padding(end = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = White,
                        focusedContainerColor = White,
                        focusedIndicatorColor = ProfessionalBlue
                    )
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    placeholder = { Text("Last Name") },
                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = White,
                        focusedContainerColor = White,
                        focusedIndicatorColor = ProfessionalBlue
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                placeholder = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = White,
                    focusedContainerColor = White,
                    focusedIndicatorColor = ProfessionalBlue
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Date of Birth Field
            OutlinedTextField(
                value = selectedDate,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Date of Birth (YYYY/MM/DD)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datepicker.show() },
                enabled = false,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    disabledContainerColor = White,
                    disabledTextColor = Color.Black,
                    disabledPlaceholderColor = Color.Gray,
                    disabledIndicatorColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password") },
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

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = terms,
                    onCheckedChange = { terms = it },
                    colors = CheckboxDefaults.colors(checkedColor = ProfessionalBlue)
                )
                Text("I agree to the Terms & Conditions", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                CircularProgressIndicator(color = ProfessionalBlue)
            } else {
                Button(
                    onClick = {
                        if (!terms) {
                            Toast.makeText(context, "Please agree to terms", Toast.LENGTH_SHORT).show()
                        } else if (email.isEmpty() || password.isEmpty() || firstName.isEmpty()) {
                            Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
                        } else {
                            isLoading = true
                            // NOTE: If UserRepoImpl still has TODO(), this will crash the app.
                            try {
                                userViewModel.register(email, password) { success, message, userId ->
                                    if (success) {
                                        val model = UserModel(
                                            userId = userId,
                                            email = email,
                                            firstName = firstName,
                                            lastName = lastName,
                                            dob = selectedDate,
                                            gender = gender
                                        )
                                        userViewModel.addUserToDatabase(userId, model) { dbSuccess, dbMessage ->
                                            isLoading = false
                                            Toast.makeText(context, dbMessage, Toast.LENGTH_LONG).show()
                                            if (dbSuccess) {
                                                context.startActivity(Intent(context, LoginActivity::class.java))
                                                activity.finish()
                                            }
                                        }
                                    } else {
                                        isLoading = false
                                        Toast.makeText(context, message ?: "Registration Failed", Toast.LENGTH_LONG).show()
                                    }
                                }
                            } catch (e: Exception) {
                                isLoading = false
                                Toast.makeText(context, "Error: Check Repo Implementation", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                ) {
                    Text("Sign Up", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = buildAnnotatedString {
                    append("Already have an account? ")
                    withStyle(SpanStyle(color = ProfessionalBlue, fontWeight = FontWeight.Bold)) {
                        append("Sign In")
                    }
                },
                modifier = Modifier.clickable {
                    context.startActivity(Intent(context, LoginActivity::class.java))
                    activity.finish()
                },
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegister() {
    RegisterBody()
}