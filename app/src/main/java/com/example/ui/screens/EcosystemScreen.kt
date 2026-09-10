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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.Web3Badge
import com.example.ui.theme.HopeCrimson
import com.example.ui.theme.HopeCyan
import com.example.ui.theme.HopeCyanGlow
import com.example.ui.theme.HopeDarkCard
import com.example.ui.theme.HopeEmerald
import com.example.ui.theme.HopeGold
import com.example.ui.theme.HopePurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun EcosystemScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = HopePurple.copy(alpha = 0.5f),
                cornerRadius = 20.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1F1535), Color(0xFF101221))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Web3Badge(text = "ECOSYSTEM ARCHITECTURE", color = HopePurple, icon = Icons.Default.Bolt)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Real-World Utility Modules",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "HOPE Network is architected beyond simple reward distribution into multi-pillar real-world utilities across gaming, commerce, payments, and social aid.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // UTILITY MODULE 1: GAMING
        item {
            EcosystemModuleCard(
                title = "HOPE Gaming Hub",
                subtitle = "In-Game Assets & Tournament Utility",
                desc = "Gamers can utilize accumulated \$HOPE for in-game season passes, character skins, weapon upgrades, and community esports tournament buy-ins.",
                badge = "Phase 5 Development",
                badgeColor = HopePurple,
                icon = Icons.Default.SportsEsports,
                features = listOf(
                    "Exclusive Cosmetic & Character Skins",
                    "Community Esports Tournament Passes",
                    "Cross-Game Interoperable NFT Assets",
                    "Tiered Guild Rewards & Leaderboards"
                )
            )
        }

        // UTILITY MODULE 2: MARKETPLACE
        item {
            EcosystemModuleCard(
                title = "HOPE Marketplace",
                subtitle = "Decentralized Commerce & Digital Goods",
                desc = "A community marketplace for digital goods, creator subscriptions, developer software tools, and branded eco-merchandise settled with 100% \$HOPE.",
                badge = "Phase 6 Development",
                badgeColor = HopeGold,
                icon = Icons.Default.Storefront,
                features = listOf(
                    "Digital Software & Subscriptions Store",
                    "Verified Creator Collectibles & Assets",
                    "Zero-Commission Community Listings",
                    "Automated Escrow Purchase Protection"
                )
            )
        }

        // UTILITY MODULE 3: HOPE PAY
        item {
            EcosystemModuleCard(
                title = "HOPE Pay",
                subtitle = "Instant Merchant QR Settlement",
                desc = "Lightweight QR code payment layer enabling physical cafes, digital stores, and service freelancers to accept \$HOPE with instantaneous settlement.",
                badge = "Phase 7 Development",
                badgeColor = HopeCyan,
                icon = Icons.Default.QrCodeScanner,
                features = listOf(
                    "Instant QR-Code Point-of-Sale Integration",
                    "Zero Settlement Fees for Verified Merchants",
                    "Cross-Border Freelancer Remittance",
                    "Lightweight Mobile POS Terminal Mode"
                )
            )
        }

        // UTILITY MODULE 4: HOPE WALLET & IMPACT VAULT
        item {
            EcosystemModuleCard(
                title = "HOPE Core Wallet",
                subtitle = "Self-Custody & Impact Staking",
                desc = "Universal gateway for holding ecosystem assets, monitoring mining nodes, and allocating voting power to eligible humanitarian charity initiatives.",
                badge = "Phase 8 Network Ready",
                badgeColor = HopeEmerald,
                icon = Icons.Default.AccountBalanceWallet,
                features = listOf(
                    "Non-Custodial Multi-Signature Security",
                    "1-Click Bridge to Future On-Chain Token",
                    "Democratic Humanitarian Vote Delegation",
                    "Real-Time Emission & Ledger Explorer"
                )
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
fun EcosystemModuleCard(
    title: String,
    subtitle: String,
    desc: String,
    badge: String,
    badgeColor: Color,
    icon: ImageVector,
    features: List<String>
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
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(badgeColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = subtitle,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
                Web3Badge(text = badge, color = badgeColor)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = desc,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                features.forEach { feature ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(badgeColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = feature,
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
