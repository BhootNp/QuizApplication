package com.example.quizapplication.view

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizapplication.model.UserModel
import com.example.quizapplication.ui.theme.NavyBlue
import com.example.quizapplication.ui.theme.ProfessionalBlue
import com.example.quizapplication.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: UserViewModel) {
    val context = LocalContext.current
    val activity = context as? Activity
    val userData by viewModel.userData.observeAsState()

    // Dialog States
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    // Form Temporary States
    var editFirstName by remember { mutableStateOf("") }
    var editLastName by remember { mutableStateOf("") }
    var editGender by remember { mutableStateOf("") }
    var editDob by remember { mutableStateOf("") }
    var genderExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getCurrentUser()?.uid?.let { uid ->
            viewModel.getUserData(uid)
        }
    }

    // --- DIALOG: EDIT PROFILE ---
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            shape = RoundedCornerShape(28.dp),
            title = { Text("Update Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editFirstName,
                        onValueChange = { editFirstName = it },
                        label = { Text("First Name") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editLastName,
                        onValueChange = { editLastName = it },
                        label = { Text("Last Name") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Gender Selection (Dropdown)
                    Box {
                        OutlinedTextField(
                            value = editGender,
                            onValueChange = { },
                            label = { Text("Gender") },
                            readOnly = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { genderExpanded = true }) {
                                    Icon(Icons.Default.ArrowDropDown, null)
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = genderExpanded,
                            onDismissRequest = { genderExpanded = false }
                        ) {
                            listOf("Male", "Female", "Other").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        editGender = option
                                        genderExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = editDob,
                        onValueChange = { editDob = it },
                        label = { Text("DOB (DD/MM/YYYY)") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val uid = viewModel.getCurrentUser()?.uid ?: ""
                        val updatedUser = userData?.copy(
                            firstName = editFirstName,
                            lastName = editLastName,
                            gender = editGender,
                            dob = editDob
                        )
                        if (updatedUser != null) {
                            viewModel.updateProfile(uid, updatedUser) { success, msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    showEditDialog = false
                                    viewModel.getUserData(uid) // Refresh UI
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
                ) { Text("Save Changes") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancel") }
            }
        )
    }

    // --- DIALOG: SIGN OUT ---
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text("Sign Out", fontWeight = FontWeight.Bold) },
            text = { Text("Ready to leave NeuroQuiz? Your progress is safely saved.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.signOut()
                        activity?.finish()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Logout", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) { Text("Stay") }
            }
        )
    }

    // --- MAIN UI ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5))
    ) {
        // Header Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyBlue)
                .padding(top = 40.dp, bottom = 60.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Surface(
                        modifier = Modifier.size(110.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.15f),
                        border = BorderStroke(3.dp, Color.White)
                    ) {
                        Icon(Icons.Default.Person, null, modifier = Modifier.padding(25.dp), tint = Color.White)
                    }
                    Surface(
                        color = if (userData?.role == "admin") Color(0xFFFFD700) else ProfessionalBlue,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.offset(y = 4.dp)
                    ) {
                        Text(
                            text = userData?.role?.uppercase() ?: "USER",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (userData?.role == "admin") Color.Black else Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${userData?.firstName ?: "Loading..."} ${userData?.lastName ?: ""}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(text = userData?.email ?: "", color = Color.White.copy(alpha = 0.6f))
            }
        }

        // Body Card
        Surface(
            modifier = Modifier.fillMaxSize().offset(y = (-30).dp),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStatCard(label = "Total XP", value = "${userData?.score ?: 0}")
                    VerticalDivider(modifier = Modifier.height(40.dp), thickness = 1.dp, color = Color(0xFFEEEEEE))
                    ProfileStatCard(label = "Status", value = "Legend")
                }

                Spacer(modifier = Modifier.height(32.dp))
                Text("Personal Information", fontWeight = FontWeight.ExtraBold, color = NavyBlue, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))

                InfoSectionCard {
                    DetailRowModern(label = "First Name", value = userData?.firstName ?: "---")
                    InfoDivider()
                    DetailRowModern(label = "Last Name", value = userData?.lastName ?: "---")
                    InfoDivider()
                    DetailRowModern(label = "Gender", value = userData?.gender?.ifEmpty { "Not Specified" } ?: "Not Specified")
                    InfoDivider()
                    DetailRowModern(label = "Date of Birth", value = userData?.dob?.ifEmpty { "Not Specified" } ?: "Not Specified")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // FIXED EDIT BUTTON
                Button(
                    onClick = {
                        editFirstName = userData?.firstName ?: ""
                        editLastName = userData?.lastName ?: ""
                        editGender = userData?.gender ?: ""
                        editDob = userData?.dob ?: ""
                        showEditDialog = true
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Profile Details", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { showSignOutDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = null
                ) {
                    Icon(Icons.Default.ExitToApp, null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out", fontWeight = FontWeight.Bold, color = Color.Red)
                }
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

// --- HELPERS ---

@Composable
fun DetailRowModern(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.Gray, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        Text(text = value, color = NavyBlue, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
fun InfoSectionCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFF8F9FA),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun InfoDivider() {
    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color(0xFFE0E0E0))
}

@Composable
fun ProfileStatCard(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Black, color = NavyBlue)
        Text(text = label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
    }
}