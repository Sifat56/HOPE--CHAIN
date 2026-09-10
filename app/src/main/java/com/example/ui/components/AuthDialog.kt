package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.db.UserEntity
import com.example.ui.theme.HopeCyan
import com.example.ui.theme.HopeDarkCard
import com.example.ui.theme.HopeDarkCardBorder
import com.example.ui.theme.HopeEmerald
import com.example.ui.theme.HopeGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AuthDialog(
    currentUser: UserEntity?,
    onDismiss: () -> Unit,
    onLogin: (email: String, username: String?, refCode: String?) -> Unit,
    onSwitchUser: (userId: String) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var emailInput by remember { mutableStateOf("") }
    var usernameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var referralInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = HopeCyan,
            cornerRadius = 20.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "HOPE Network Auth",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Universal 1-Click & Email Access",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Current logged in badge
                if (currentUser != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0x3338BDF8), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(if (currentUser.role == "admin") HopeGold.copy(alpha = 0.2f) else HopeCyan.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (currentUser.role == "admin") Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (currentUser.role == "admin") HopeGold else HopeCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = currentUser.username,
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${currentUser.email} • ${currentUser.role.uppercase()}",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            Web3Badge(
                                text = "${String.format("%.2f", currentUser.hopeBalance)} HOPE",
                                color = HopeEmerald
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Preset Quick Switchers (For instantaneous testing of role-based features)
                Text(
                    text = "QUICK TEST ROLES (1-CLICK)",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onSwitchUser("user_pioneer_002")
                                onDismiss()
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131F33)),
                        border = BorderStroke(1.dp, if (currentUser?.id == "user_pioneer_002") HopeCyan else Color(0x33263248))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("👤 Pioneer Miner", color = HopeCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Standard User", color = TextMuted, fontSize = 10.sp)
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onSwitchUser("admin_root_001")
                                onDismiss()
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF261D15)),
                        border = BorderStroke(1.dp, if (currentUser?.id == "admin_root_001") HopeGold else Color(0x33263248))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("👑 Hope Architect", color = HopeGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Full Admin Access", color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Signup / Login Form
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = HopeCyan,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = HopeCyan
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Sign In", fontSize = 13.sp, color = if (selectedTab == 0) HopeCyan else TextSecondary) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Register", fontSize = 13.sp, color = if (selectedTab == 1) HopeCyan else TextSecondary) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedTab == 1) {
                    OutlinedTextField(
                        value = usernameInput,
                        onValueChange = { usernameInput = it },
                        label = { Text("Username") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HopeCyan,
                            unfocusedBorderColor = HopeDarkCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Mail, contentDescription = null, tint = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HopeCyan,
                        unfocusedBorderColor = HopeDarkCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HopeCyan,
                        unfocusedBorderColor = HopeDarkCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                if (selectedTab == 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = referralInput,
                        onValueChange = { referralInput = it },
                        label = { Text("Referral Code (Optional)") },
                        leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HopeCyan,
                            unfocusedBorderColor = HopeDarkCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (emailInput.isNotBlank()) {
                            onLogin(
                                emailInput.trim(),
                                if (selectedTab == 1) usernameInput.trim().ifBlank { null } else null,
                                if (selectedTab == 1) referralInput.trim().ifBlank { null } else null
                            )
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = HopeCyan),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (selectedTab == 0) "Sign In to HOPE" else "Create Account & Start Mining",
                        color = Color(0xFF0B0E14),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
