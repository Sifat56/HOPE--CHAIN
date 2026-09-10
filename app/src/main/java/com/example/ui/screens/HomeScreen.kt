package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.AdvertisingSettingsEntity
import com.example.data.db.SystemSettingsEntity
import com.example.data.db.UserEntity
import com.example.ui.components.AdPlacementContainer
import com.example.ui.components.GlassCard
import com.example.ui.components.TelegramBannerCard
import com.example.ui.components.Web3Badge
import com.example.ui.theme.HopeCrimson
import com.example.ui.theme.HopeCyan
import com.example.ui.theme.HopeCyanGlow
import com.example.ui.theme.HopeDarkCard
import com.example.ui.theme.HopeDarkCardBorder
import com.example.ui.theme.HopeEmerald
import com.example.ui.theme.HopeEmeraldGlow
import com.example.ui.theme.HopeGold
import com.example.ui.theme.HopePurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.LiveMiningUiState

@Composable
fun HomeScreen(
    currentUser: UserEntity?,
    miningState: LiveMiningUiState,
    systemSettings: SystemSettingsEntity?,
    adSettings: AdvertisingSettingsEntity?,
    activeMinersCount: Int,
    onStartMining: () -> Unit,
    onClaimMining: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToEcosystem: () -> Unit,
    onNavigateToTokenomics: () -> Unit,
    onNavigateToRoadmap: () -> Unit,
    onNavigateToReferral: () -> Unit,
    onWatchAdsgramAd: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // HERO BANNER
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = HopeCyan.copy(alpha = 0.4f),
                cornerRadius = 20.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF131D30), Color(0xFF0D131F))
                            )
                        )
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Web3Badge(text = "PHASE 01 LIVE", color = HopeEmerald, icon = Icons.Default.Bolt)
                        Web3Badge(text = "TESTNET MINING", color = HopeCyan)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "HOPE NETWORK",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )

                    Text(
                        text = "Mine. Play. Pay. Help.",
                        color = HopeCyanGlow,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "A community-powered digital ecosystem built around \$HOPE. Earn through idle server-side mining, share with friends, and create verifiable humanitarian impact.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                if (miningState.isActive) onClaimMining() else onStartMining()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (miningState.isActive) HopeEmerald else HopeCyan
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = if (miningState.isActive) Icons.Default.AutoGraph else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color(0xFF0B0E14),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (miningState.isActive) "Mining Active" else "Start Mining",
                                color = Color(0xFF0B0E14),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Button(
                            onClick = onNavigateToReferral,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            border = BorderStroke(1.dp, HopeDarkCardBorder),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Invite Friends",
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // INTERACTIVE 24-HOUR MINING ENGINE CARD
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (miningState.isActive) HopeEmerald.copy(alpha = 0.6f) else HopeCyan.copy(alpha = 0.5f),
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
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (miningState.isActive) HopeEmerald else HopeGold)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (miningState.isActive) "24-HOUR CYCLE ACTIVE" else "MINING STANDBY",
                                color = if (miningState.isActive) HopeEmeraldGlow else HopeGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        Web3Badge(
                            text = "${String.format("%.2f", miningState.currentRatePerDay)} HOPE/day",
                            color = HopeCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Accrued Reward & Timer Display
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "SESSION ACCRUED REWARD",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "+${String.format("%.6f", miningState.accruedReward)}",
                                color = if (miningState.isActive) HopeEmeraldGlow else TextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "\$HOPE Internal Token",
                                color = HopeCyan,
                                fontSize = 11.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = if (miningState.isActive) HopeEmerald else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (miningState.isActive) miningState.formattedRemainingTime else "24:00:00",
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = if (miningState.isActive) "Remaining Time" else "Ready to Mine",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress bar
                    LinearProgressIndicator(
                        progress = { if (miningState.isActive) miningState.progressFraction else 0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = HopeEmerald,
                        trackColor = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Button
                    if (miningState.isActive) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onClaimMining,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = HopeEmerald),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Claim Accrued Rewards",
                                    color = Color(0xFF0B0E14),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = onStartMining,
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(if (!miningState.isActive) Modifier.scale(pulseScale) else Modifier),
                            colors = ButtonDefaults.buttonColors(containerColor = HopeCyan),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = Color(0xFF0B0E14),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Start 24-Hour Mining Cycle",
                                color = Color(0xFF0B0E14),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // 4 KEY STATS GRID
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatBox(
                        title = "Your Total Balance",
                        value = "${String.format("%.2f", currentUser?.hopeBalance ?: 0.0)} HOPE",
                        subtitle = "Ledger Verified",
                        color = HopeCyan,
                        icon = Icons.Default.AccountBalanceWallet,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToWallet() }
                    )

                    StatBox(
                        title = "Active Pioneers",
                        value = "${(systemSettings?.totalRegisteredUsers ?: 15840) + activeMinersCount}",
                        subtitle = "Global Miners",
                        color = HopeEmerald,
                        icon = Icons.Default.Groups,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatBox(
                        title = "Humanitarian Fund",
                        value = "15,000,000",
                        subtitle = "20% Total Allocation",
                        color = HopeCrimson,
                        icon = Icons.Default.Favorite,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToTokenomics() }
                    )

                    StatBox(
                        title = "Mining Supply Cap",
                        value = "30,000,000",
                        subtitle = "40% Mining Rewards",
                        color = HopeGold,
                        icon = Icons.Default.MonetizationOn,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToTokenomics() }
                    )
                }
            }
        }

        // TELEGRAM COMMUNITY CTA
        item {
            TelegramBannerCard()
        }

        // ADSGRAM REWARDED VIDEO AD EARNING SLOT
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF101B2E)
                ),
                border = BorderStroke(1.dp, Color(0xFF24A1DE).copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF24A1DE).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "AdsGram",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "AdsGram Rewarded Video",
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Web3Badge(
                                        text = "REWARDED",
                                        color = Color(0xFF24A1DE)
                                    )
                                }
                                Text(
                                    text = "Earn +${String.format("%.2f", adSettings?.adsgramRewardAmount ?: 0.25)} HOPE per completed ad",
                                    color = HopeCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Watch the sponsor ad until the countdown completes to earn verified HOPE tokens directly into your ledger. Closing early grants zero reward.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val isRewardedActive = (adSettings?.adsEnabled != false) && (adSettings?.rewardedEnabled != false)

                    Button(
                        onClick = onWatchAdsgramAd,
                        enabled = isRewardedActive,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF24A1DE),
                            disabledContainerColor = Color(0x3324A1DE)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = if (isRewardedActive) Color.White else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isRewardedActive) {
                                "Watch Rewarded Ad (+${String.format("%.2f", adSettings?.adsgramRewardAmount ?: 0.25)} HOPE)"
                            } else {
                                "Rewarded Ads Paused by Admin"
                            },
                            color = if (isRewardedActive) Color.White else TextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // ADVERTISEMENT CONTAINER (Google AdSense Ready)
        item {
            AdPlacementContainer(
                placementLabel = "HOPE Network Home Banner Slot • Responsive Display",
                adsEnabled = adSettings?.adsEnabled ?: true
            )
        }

        // 6 CORE PILLARS
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "HOW HOPE NETWORK WORKS",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                PillarItem(
                    title = "1. Mine ⛏️",
                    desc = "Activate your 24-hour idle mining cycle with zero battery or device drain.",
                    color = HopeCyan
                )
                PillarItem(
                    title = "2. Earn 🪙",
                    desc = "Accumulate daily internal \$HOPE testnet rewards straight into your verified ledger.",
                    color = HopeEmerald
                )
                PillarItem(
                    title = "3. Play 🎮",
                    desc = "Future Web3 gaming tournaments, exclusive in-game skins, and character upgrades.",
                    color = HopePurple
                )
                PillarItem(
                    title = "4. Purchase 🛒",
                    desc = "Future digital goods, creator subscriptions, and ecosystem marketplace utility.",
                    color = HopeGold
                )
                PillarItem(
                    title = "5. Pay 💳",
                    desc = "Decentralized QR ecosystem settlement and merchant point-of-sale layer.",
                    color = HopeCyanGlow
                )
                PillarItem(
                    title = "6. Help ❤️",
                    desc = "20% dedicated Humanitarian Fund supporting disaster relief, clean water, and food.",
                    color = HopeCrimson
                )
            }
        }

        // TESTNET DISCLAIMER
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111726)),
                border = BorderStroke(1.dp, Color(0x33334155))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Compliance Notice: During the initial phase, \$HOPE represents an internal testnet platform reward. It is non-transferable and does not guarantee future financial profits, listings, or asset values.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
fun StatBox(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        borderColor = color.copy(alpha = 0.3f),
        cornerRadius = 14.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun PillarItem(
    title: String,
    desc: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = HopeDarkCard),
        border = BorderStroke(1.dp, Color(0x22263248))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
