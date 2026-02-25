package com.example.quizapplication.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizapplication.model.UserModel
import com.example.quizapplication.ui.theme.NavyBlue
import com.example.quizapplication.ui.theme.ProfessionalBlue
import com.example.quizapplication.viewmodel.UserViewModel

@Composable
fun RankingScreen(viewModel: UserViewModel) {
    val allUsers by viewModel.allUsers.observeAsState(initial = emptyList())
    val context = LocalContext.current
    val currentUserId = viewModel.getCurrentUser()?.uid
    val currentUserData by viewModel.userData.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.getAllUser()
        currentUserId?.let { uid ->
            viewModel.getUserData(uid)
        }
    }

    // Sort users by score
    val sortedUsers = allUsers.sortedByDescending { it.score }
    val topThree = sortedUsers.take(3)
    val theRest = sortedUsers.drop(3)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5)) // Designer light gray
    ) {
        // --- SECTION 1: THE PODIUM HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyBlue)
                .padding(top = 32.dp, bottom = 48.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Leaderboard",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // 2nd Place
                    if (topThree.size >= 2) PodiumItem(topThree[1], 2, Color(0xFFC0C0C0))

                    // 1st Place (Large)
                    if (topThree.isNotEmpty()) PodiumItem(topThree[0], 1, Color(0xFFFFD700), isLarge = true)

                    // 3rd Place
                    if (topThree.size >= 3) PodiumItem(topThree[2], 3, Color(0xFFCD7F32))
                }
            }
        }

        // --- SECTION 2: THE SCROLLABLE LIST ---
        // This card "overlaps" the dark header for a modern look
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-24).dp),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (theRest.isEmpty() && sortedUsers.size <= 3) {
                    item {
                        Text(
                            "The race is on! More rankings coming soon.",
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }

                itemsIndexed(theRest) { index, user ->
                    val actualRank = index + 4
                    RankingRow(
                        user = user,
                        rank = actualRank,
                        isMe = user.userId == currentUserId,
                        isAdmin = currentUserData?.role == "admin",
                        isTargetAdmin = user.role == "admin",
                        onDelete = {
                            viewModel.deleteUser(user.userId) { _, msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PodiumItem(user: UserModel, rank: Int, color: Color, isLarge: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.BottomCenter) {
            Surface(
                modifier = Modifier.size(if (isLarge) 90.dp else 70.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.15f),
                border = BorderStroke(2.dp, color)
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(if (isLarge) 20.dp else 16.dp),
                    tint = Color.White
                )
            }
            // Rank Badge
            Surface(
                modifier = Modifier.size(26.dp).offset(y = 4.dp),
                shape = CircleShape,
                color = color
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("$rank", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(user.firstName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text("${user.score} XP", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
    }
}

@Composable
fun RankingRow(
    user: UserModel,
    rank: Int,
    isMe: Boolean,
    isAdmin: Boolean,
    isTargetAdmin: Boolean,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isMe) ProfessionalBlue.copy(alpha = 0.05f) else Color(0xFFF8F9FA),
        border = if (isMe) BorderStroke(1.dp, ProfessionalBlue.copy(alpha = 0.3f)) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$rank",
                modifier = Modifier.width(32.dp),
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                fontSize = 14.sp
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isMe) "${user.firstName} (You)" else "${user.firstName} ${user.lastName}",
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue,
                    fontSize = 16.sp
                )
                Text("${user.score} XP Earned", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            if (isAdmin && !isTargetAdmin && !isMe) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, null, tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                }
            } else if (isTargetAdmin) {
                Surface(
                    color = NavyBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "MOD",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = NavyBlue
                    )
                }
            }
        }
    }
}