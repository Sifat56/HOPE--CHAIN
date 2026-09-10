package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.db.AdminAuditLogEntity
import com.example.data.db.AdvertisingSettingsEntity
import com.example.data.db.AdsgramRewardEventEntity
import com.example.data.db.SystemSettingsEntity
import com.example.data.db.UserEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.Web3Badge
import com.example.ui.theme.HopeCrimson
import com.example.ui.theme.HopeCyan
import com.example.ui.theme.HopeDarkCard
import com.example.ui.theme.HopeDarkCardBorder
import com.example.ui.theme.HopeDarkSurface
import com.example.ui.theme.HopeEmerald
import com.example.ui.theme.HopeGold
import com.example.ui.theme.HopePurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminScreen(
    currentUser: UserEntity?,
    systemSettings: SystemSettingsEntity?,
    adSettings: AdvertisingSettingsEntity?,
    allUsers: List<UserEntity>,
    auditLogs: List<AdminAuditLogEntity>,
    onUpdateMiningRate: (newRate: Double, reason: String) -> Unit,
    onScheduleHalving: (nextRate: Double, days: Int, reason: String) -> Unit,
    onAdjustBalance: (userId: String, newBalance: Double, reason: String) -> Unit,
    onToggleSuspension: (userId: String, isSuspended: Boolean, reason: String) -> Unit,
    onUpdateAdSettings: (settings: AdvertisingSettingsEntity, reason: String) -> Unit,
    onPostHumanitarian: (title: String, category: String, hope: Double, desc: String, date: String) -> Unit,
    onSwitchToAdminUser: () -> Unit,
    adsgramEvents: List<AdsgramRewardEventEntity> = emptyList(),
    onTestAdsgramCallback: (userId: String, eventId: String, secret: String?, onResult: (String, Boolean) -> Unit) -> Unit = { _, _, _, _ -> }
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    // If not admin, show protection gate
    if (currentUser?.role != "admin") {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = HopeCrimson.copy(alpha = 0.6f),
                cornerRadius = 20.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(HopeCrimson.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = HopeCrimson,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Admin Access Restricted",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "This area is isolated for authorized HOPE Network system administrators. Current session is logged in as '${currentUser?.username ?: "Guest"}' (${currentUser?.role ?: "none"}).",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onSwitchToAdminUser,
                        colors = ButtonDefaults.buttonColors(containerColor = HopeGold),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = Color(0xFF0B0E14),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Login as HopeArchitect (Admin)",
                            color = Color(0xFF0B0E14),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        return
    }

    var selectedAdminTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var rateInput by remember { mutableStateOf(systemSettings?.dailyMiningRate?.toString() ?: "3.0") }
    var rateReason by remember { mutableStateOf("") }

    var halvingRateInput by remember { mutableStateOf("1.5") }
    var halvingDaysInput by remember { mutableStateOf("30") }

    // Dialog state for balance editing
    var editingUser by remember { mutableStateOf<UserEntity?>(null) }
    var newBalanceInput by remember { mutableStateOf("") }
    var adjustmentReason by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // ADMIN CONTROL HEADER
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = HopeGold.copy(alpha = 0.6f),
                cornerRadius = 20.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF261D12), Color(0xFF13100B))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = HopeGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ISOLATED ADMIN PANEL",
                                color = HopeGold,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                        Web3Badge(text = "Root Session", color = HopeEmerald)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1B140B))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("Total Network Mined", color = TextMuted, fontSize = 10.sp)
                                Text(
                                    "${String.format("%.1f", systemSettings?.totalNetworkMined ?: 54200.0)} HOPE",
                                    color = HopeGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1B140B))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("Mining Cap Remaining", color = TextMuted, fontSize = 10.sp)
                                val remaining = (systemSettings?.miningSupplyCap ?: 30000000.0) - (systemSettings?.totalNetworkMined ?: 54200.0)
                                Text(
                                    "${String.format("%,.0f", remaining)} HOPE",
                                    color = HopeCyan,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // ADMIN NAVIGATION TABS
        item {
            TabRow(
                selectedTabIndex = selectedAdminTab,
                containerColor = Color.Transparent,
                contentColor = HopeGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedAdminTab]),
                        color = HopeGold
                    )
                }
            ) {
                Tab(
                    selected = selectedAdminTab == 0,
                    onClick = { selectedAdminTab = 0 },
                    text = { Text("Mining Engine", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedAdminTab == 1,
                    onClick = { selectedAdminTab = 1 },
                    text = { Text("Users (${allUsers.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedAdminTab == 2,
                    onClick = { selectedAdminTab = 2 },
                    text = { Text("Ad Controls", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedAdminTab == 3,
                    onClick = { selectedAdminTab = 3 },
                    text = { Text("Audit Logs (${auditLogs.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        // TAB 0: MINING ENGINE CONTROLLER
        if (selectedAdminTab == 0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = HopeDarkCard),
                    border = BorderStroke(1.dp, Color(0x22263248))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = HopeCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DYNAMIC MINING RATE CONTROLLER",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Modifying the daily rate instantly updates the database calculation parameters for all global pioneers.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = rateInput,
                            onValueChange = { rateInput = it },
                            label = { Text("Daily Mining Rate (HOPE / 24h)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HopeGold,
                                unfocusedBorderColor = HopeDarkCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = rateReason,
                            onValueChange = { rateReason = it },
                            label = { Text("Audit Justification / Reason") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HopeGold,
                                unfocusedBorderColor = HopeDarkCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val parsed = rateInput.toDoubleOrNull()
                                if (parsed != null && parsed > 0) {
                                    onUpdateMiningRate(parsed, rateReason)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = HopeGold),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                "Publish Mining Rate Change",
                                color = Color(0xFF0B0E14),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // HALVING SCHEDULER
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = HopeDarkCard),
                    border = BorderStroke(1.dp, Color(0x22263248))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HistoryEdu, contentDescription = null, tint = HopePurple, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SCHEDULE TOKENOMICS HALVING",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = halvingRateInput,
                                onValueChange = { halvingRateInput = it },
                                label = { Text("Next Rate (HOPE)") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = HopePurple,
                                    unfocusedBorderColor = HopeDarkCardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = halvingDaysInput,
                                onValueChange = { halvingDaysInput = it },
                                label = { Text("Days from Now") },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = HopePurple,
                                    unfocusedBorderColor = HopeDarkCardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val rate = halvingRateInput.toDoubleOrNull() ?: 1.5
                                val days = halvingDaysInput.toIntOrNull() ?: 30
                                onScheduleHalving(rate, days, "Milestone tokenomics halving event")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = HopePurple),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Schedule Halving Milestone", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // TAB 1: USER MANAGEMENT DIRECTORY
        if (selectedAdminTab == 1) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search users by email, name or referral code...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HopeGold,
                        unfocusedBorderColor = HopeDarkCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )
            }

            val filteredUsers = allUsers.filter {
                it.username.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true) ||
                it.referralCode.contains(searchQuery, ignoreCase = true)
            }

            items(filteredUsers) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = HopeDarkCard),
                    border = BorderStroke(1.dp, if (user.isSuspended) HopeCrimson.copy(alpha = 0.5f) else Color(0x22263248))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = user.username,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${user.email} • Code: ${user.referralCode}",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                            Web3Badge(
                                text = if (user.isSuspended) "SUSPENDED" else "${String.format("%.2f", user.hopeBalance)} HOPE",
                                color = if (user.isSuspended) HopeCrimson else HopeEmerald
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Referrals: ${user.totalReferrals} • Mining: ${if (user.isMiningActive) "Active" else "Idle"}",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        editingUser = user
                                        newBalanceInput = user.hopeBalance.toString()
                                        adjustmentReason = ""
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = HopeCyan),
                                    border = BorderStroke(1.dp, HopeCyan.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit Balance", fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        onToggleSuspension(user.id, !user.isSuspended, "Admin directory security toggle")
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = if (user.isSuspended) HopeEmerald else HopeCrimson
                                    ),
                                    border = BorderStroke(1.dp, if (user.isSuspended) HopeEmerald.copy(alpha = 0.5f) else HopeCrimson.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text(if (user.isSuspended) "Unsuspend" else "Suspend", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // TAB 2: ADVERTISING & ADSGRAM REWARD CONTROLLER
        if (selectedAdminTab == 2) {
            val currentAds = adSettings ?: AdvertisingSettingsEntity()
            item {
                AdSettingsCard(
                    currentAds = currentAds,
                    onUpdateAdSettings = onUpdateAdSettings
                )
            }

            item { Spacer(modifier = Modifier.height(14.dp)) }

            item {
                AdsgramWebhookTesterCard(
                    currentAds = currentAds,
                    onTestCallback = onTestAdsgramCallback
                )
            }

            item { Spacer(modifier = Modifier.height(14.dp)) }

            item {
                AdsgramRewardEventsCard(
                    events = adsgramEvents,
                    dateFormat = dateFormat
                )
            }
        }

        // TAB 3: ADMIN AUDIT LOGS
        if (selectedAdminTab == 3) {
            items(auditLogs) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = HopeDarkCard),
                    border = BorderStroke(1.dp, Color(0x22263248))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = log.action,
                                color = HopeGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = dateFormat.format(Date(log.timestamp)),
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Admin: ${log.adminUsername} • Reason: ${log.reason}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${log.previousValue} ➔ ${log.newValue}",
                            color = HopeCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    // BALANCE EDIT MODAL DIALOG
    if (editingUser != null) {
        val target = editingUser!!
        Dialog(onDismissRequest = { editingUser = null }) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = HopeCyan,
                cornerRadius = 18.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Reconcile User Balance",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "User: ${target.username} (${target.email})",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = newBalanceInput,
                        onValueChange = { newBalanceInput = it },
                        label = { Text("New Authoritative Balance (HOPE)") },
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
                        value = adjustmentReason,
                        onValueChange = { adjustmentReason = it },
                        label = { Text("Audit Reason / Reconciliation Memo") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HopeCyan,
                            unfocusedBorderColor = HopeDarkCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { editingUser = null },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Cancel", color = TextSecondary)
                        }

                        Button(
                            onClick = {
                                val amount = newBalanceInput.toDoubleOrNull()
                                if (amount != null) {
                                    onAdjustBalance(target.id, amount, adjustmentReason)
                                    editingUser = null
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = HopeCyan),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Save", color = Color(0xFF0B0E14), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdSettingsCard(
    currentAds: AdvertisingSettingsEntity,
    onUpdateAdSettings: (settings: AdvertisingSettingsEntity, reason: String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var adsEnabledState by remember(currentAds) { mutableStateOf(currentAds.adsEnabled) }
    var bannerState by remember(currentAds) { mutableStateOf(currentAds.bannerEnabled) }
    var rewardedState by remember(currentAds) { mutableStateOf(currentAds.rewardedEnabled) }
    var blockIdInput by remember(currentAds) { mutableStateOf(currentAds.adsgramBlockId) }
    var rewardAmountInput by remember(currentAds) { mutableStateOf(currentAds.adsgramRewardAmount.toString()) }
    var secretKeyInput by remember(currentAds) { mutableStateOf(currentAds.adsgramSecretKey) }
    var copyNotice by remember { mutableStateOf(false) }

    val generatedRewardUrl = "https://api.hope-network.app/api/adsgram/reward?userid=[userId]&secret=${secretKeyInput.trim()}&event_id=[eventId]"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HopeDarkCard),
        border = BorderStroke(1.dp, Color(0x22263248))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Campaign, contentDescription = null, tint = HopeGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ADSGRAM REWARDED ADS & AD ARCHITECTURE",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Web3Badge(text = "Production API", color = HopeCyan)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Global Advertising Engine", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("Master switch for all placements", color = TextMuted, fontSize = 10.sp)
                }
                Switch(
                    checked = adsEnabledState,
                    onCheckedChange = { adsEnabledState = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = HopeGold)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("AdsGram Rewarded Video Ads", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("Users watch rewarded video to receive HOPE reward", color = HopeEmerald, fontSize = 10.sp)
                }
                Switch(
                    checked = rewardedState,
                    onCheckedChange = { rewardedState = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = HopeEmerald)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Banner Ad Placements", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("Non-intrusive 728x90 & 320x50 slots", color = TextMuted, fontSize = 10.sp)
                }
                Switch(
                    checked = bannerState,
                    onCheckedChange = { bannerState = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = HopeGold)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = blockIdInput,
                onValueChange = { blockIdInput = it },
                label = { Text("AdsGram Block ID") },
                supportingText = { Text("Enter Block ID from your AdsGram dashboard (e.g. 47226)", color = TextMuted, fontSize = 10.sp) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HopeCyan,
                    unfocusedBorderColor = HopeDarkCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = rewardAmountInput,
                onValueChange = { rewardAmountInput = it },
                label = { Text("Reward Amount per Ad (HOPE)") },
                supportingText = { Text("Configurable token reward credited atomically into user ledger", color = TextMuted, fontSize = 10.sp) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HopeCyan,
                    unfocusedBorderColor = HopeDarkCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = secretKeyInput,
                onValueChange = { secretKeyInput = it },
                label = { Text("AdsGram Callback Secret Key") },
                supportingText = { Text("Secret token used to authenticate server-to-server callbacks", color = TextMuted, fontSize = 10.sp) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HopeCyan,
                    unfocusedBorderColor = HopeDarkCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // PRODUCTION REWARD URL CONTAINER FOR ADSGRAM DASHBOARD
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF090D16))
                    .border(1.dp, Color(0xFF24A1DE).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PRODUCTION REWARD URL (FOR ADSGRAM DASHBOARD)",
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy URL",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier
                                .size(18.dp)
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(generatedRewardUrl))
                                    copyNotice = true
                                }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = generatedRewardUrl,
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (copyNotice) "✓ URL Copied to clipboard!" else "Enter this exact URL into AdsGram Dashboard ➔ Reward URL setting.",
                        color = if (copyNotice) HopeEmerald else TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val parsedReward = rewardAmountInput.toDoubleOrNull() ?: 0.25
                    onUpdateAdSettings(
                        currentAds.copy(
                            adsEnabled = adsEnabledState,
                            bannerEnabled = bannerState,
                            rewardedEnabled = rewardedState,
                            adsgramBlockId = blockIdInput.trim(),
                            adsgramRewardAmount = parsedReward,
                            adsgramSecretKey = secretKeyInput.trim(),
                            adsgramWebhookUrl = generatedRewardUrl
                        ),
                        "Admin updated AdsGram rewarded ad configuration"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = HopeGold),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Ad & AdsGram Configuration", color = Color(0xFF0B0E14), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AdsgramWebhookTesterCard(
    currentAds: AdvertisingSettingsEntity,
    onTestCallback: (userId: String, eventId: String, secret: String?, onResult: (String, Boolean) -> Unit) -> Unit
) {
    var testUserId by remember { mutableStateOf("user_pioneer_002") }
    var testEventId by remember { mutableStateOf("evt_test_${System.currentTimeMillis().toString().takeLast(6)}") }
    var testSecret by remember(currentAds) { mutableStateOf(currentAds.adsgramSecretKey) }
    var testResponse by remember { mutableStateOf<String?>(null) }
    var isResponseSuccess by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HopeDarkCard),
        border = BorderStroke(1.dp, Color(0xFF24A1DE).copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Security, contentDescription = null, tint = HopeCyan, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ADSGRAM REWARD WEBHOOK & REPLAY ATTACK TESTER",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Simulate real AdsGram callback execution. Test atomic ledger crediting and duplicate prevention (replay attack protection).",
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = testUserId,
                onValueChange = { testUserId = it },
                label = { Text("User ID (userid)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HopeCyan,
                    unfocusedBorderColor = HopeDarkCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = testEventId,
                onValueChange = { testEventId = it },
                label = { Text("Ad Event ID (event_id)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HopeCyan,
                    unfocusedBorderColor = HopeDarkCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = testSecret,
                onValueChange = { testSecret = it },
                label = { Text("Secret Token (secret)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HopeCyan,
                    unfocusedBorderColor = HopeDarkCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        onTestCallback(testUserId, testEventId, testSecret) { result, success ->
                            testResponse = result
                            isResponseSuccess = success
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = HopeEmerald),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("1. Test Reward", color = Color(0xFF0B0E14), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        // Resend the SAME event ID to trigger duplicate check
                        onTestCallback(testUserId, testEventId, testSecret) { result, success ->
                            testResponse = result
                            isResponseSuccess = success
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = HopeCyan),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("2. Test Replay", color = Color(0xFF0B0E14), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            testResponse?.let { resp ->
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isResponseSuccess) Color(0xFF0D251D) else Color(0xFF2A1215))
                        .border(1.dp, if (isResponseSuccess) HopeEmerald.copy(alpha = 0.5f) else HopeCrimson.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = resp,
                        color = if (isResponseSuccess) HopeEmerald else HopeCrimson,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AdsgramRewardEventsCard(
    events: List<AdsgramRewardEventEntity>,
    dateFormat: SimpleDateFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HopeDarkCard),
        border = BorderStroke(1.dp, Color(0x22263248))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.HistoryEdu, contentDescription = null, tint = HopeGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ADSGRAM REWARD AUDIT LOG (${events.size})",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Web3Badge(text = "Anti-Replay Ledger", color = HopeGold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (events.isEmpty()) {
                Text(
                    text = "No AdsGram reward callbacks recorded yet. Watch a rewarded ad on the Home screen or simulate a webhook above.",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    events.take(10).forEach { event ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF141A26))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = event.eventId,
                                        color = HopeCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                when (event.status) {
                                                    "SUCCESS" -> HopeEmerald.copy(alpha = 0.2f)
                                                    "DUPLICATE" -> HopeGold.copy(alpha = 0.2f)
                                                    else -> HopeCrimson.copy(alpha = 0.2f)
                                                }
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = event.status,
                                            color = when (event.status) {
                                                "SUCCESS" -> HopeEmerald
                                                "DUPLICATE" -> HopeGold
                                                else -> HopeCrimson
                                            },
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "User: ${event.userId} • ${dateFormat.format(Date(event.timestamp))}",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }

                            Text(
                                text = "+${String.format("%.2f", event.rewardAmount)} HOPE",
                                color = HopeEmerald,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
