package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.AdvertisingSettingsEntity
import com.example.data.db.MiningSessionEntity
import com.example.data.db.RewardLedgerEntity
import com.example.data.db.UserEntity
import com.example.ui.components.AdPlacementContainer
import com.example.ui.components.GlassCard
import com.example.ui.components.Web3Badge
import com.example.ui.theme.HopeCyan
import com.example.ui.theme.HopeDarkCard
import com.example.ui.theme.HopeDarkCardBorder
import com.example.ui.theme.HopeEmerald
import com.example.ui.theme.HopeGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletScreen(
    currentUser: UserEntity?,
    miningSessions: List<MiningSessionEntity>,
    userLedger: List<RewardLedgerEntity>,
    adSettings: AdvertisingSettingsEntity?
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // AUTHORITATIVE BALANCE CARD
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = HopeCyan.copy(alpha = 0.5f),
                cornerRadius = 20.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = HopeCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "HOPE WALLET & LEDGER",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Web3Badge(text = "Server Authoritative", color = HopeEmerald, icon = Icons.Default.Shield)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "TOTAL AVAILABLE BALANCE",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${String.format("%.4f", currentUser?.hopeBalance ?: 0.0)} HOPE",
                        color = TextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0F172A))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("Total Mined", color = TextMuted, fontSize = 10.sp)
                                Text(
                                    "${String.format("%.2f", currentUser?.totalMined ?: 0.0)} HOPE",
                                    color = HopeCyan,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0F172A))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("Referral Earnings", color = TextMuted, fontSize = 10.sp)
                                Text(
                                    "${String.format("%.2f", currentUser?.referralEarnings ?: 0.0)} HOPE",
                                    color = HopeEmerald,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // TABS FOR LEDGER & MINING HISTORY
        item {
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
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reward Ledger (${userLedger.size})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mining Sessions (${miningSessions.size})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                )
            }
        }

        // TAB CONTENT
        if (selectedTab == 0) {
            // REWARD LEDGER LIST
            if (userLedger.isEmpty()) {
                item {
                    EmptyHistoryCard("No ledger entries recorded yet. Start mining to generate rewards!")
                }
            } else {
                items(userLedger) { entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = HopeDarkCard),
                        border = BorderStroke(1.dp, Color(0x22263248))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (entry.amount >= 0) HopeEmerald.copy(alpha = 0.15f)
                                            else HopeGold.copy(alpha = 0.15f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (entry.amount >= 0) Icons.Default.TrendingUp else Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (entry.amount >= 0) HopeEmerald else HopeGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = entry.description,
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${entry.type} • ${dateFormat.format(Date(entry.timestamp))}",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            Text(
                                text = if (entry.amount >= 0) "+${String.format("%.4f", entry.amount)}" else String.format("%.4f", entry.amount),
                                color = if (entry.amount >= 0) HopeEmerald else HopeGold,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            // MINING SESSIONS LIST
            if (miningSessions.isEmpty()) {
                item {
                    EmptyHistoryCard("No mining sessions recorded yet. Start your first session on the Home screen!")
                }
            } else {
                items(miningSessions) { session ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = HopeDarkCard),
                        border = BorderStroke(1.dp, Color(0x22263248))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Web3Badge(
                                        text = "Session #${session.sessionId}",
                                        color = HopeCyan
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Web3Badge(
                                        text = session.status.uppercase(),
                                        color = if (session.status == "completed") HopeEmerald else if (session.status == "active") HopeCyan else HopeGold
                                    )
                                }
                                Text(
                                    text = "+${String.format("%.4f", session.totalAccrued)} HOPE",
                                    color = HopeEmerald,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Started: ${dateFormat.format(Date(session.startTimestamp))}",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "Rate: ${String.format("%.3f", session.ratePerHour * 24)}/day",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // AD CONTAINER
        item {
            AdPlacementContainer(
                placementLabel = "HOPE Wallet & Ledger Banner Slot",
                adsEnabled = adSettings?.adsEnabled ?: true
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
fun EmptyHistoryCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101726))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ReceiptLong,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = text,
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
