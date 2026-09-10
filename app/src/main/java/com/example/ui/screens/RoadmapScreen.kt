package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.components.GlassCard
import com.example.ui.components.TelegramBannerCard
import com.example.ui.components.Web3Badge
import com.example.ui.theme.HopeCyan
import com.example.ui.theme.HopeDarkCard
import com.example.ui.theme.HopeEmerald
import com.example.ui.theme.HopeGold
import com.example.ui.theme.HopePurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class RoadmapPhase(
    val phaseNumber: String,
    val title: String,
    val status: String, // "COMPLETED" | "IN PROGRESS" | "PLANNED"
    val description: String,
    val deliverables: List<String>
)

data class FaqItem(
    val question: String,
    val answer: String
)

@Composable
fun RoadmapScreen() {
    val phases = listOf(
        RoadmapPhase(
            "Phase 01",
            "Foundation & Engine",
            "COMPLETED",
            "Core architecture, server-side idle mining engine, immutable ledger, and Google AdSense-ready integration.",
            listOf(
                "Full-Stack Web & Android Unified Architecture",
                "Server-side 24h Mining Cycle & Ledger Engine",
                "Role-based Isolated Admin Control System",
                "Ad-Supported Revenue & Policy Framework"
            )
        ),
        RoadmapPhase(
            "Phase 02",
            "Mining Testnet & Community",
            "IN PROGRESS",
            "Initial 3.0 HOPE/day rate, pioneer user onboarding, referral rewards, and anti-abuse safeguards.",
            listOf(
                "Global Mining Cycle Launch @ 3.0 HOPE/day",
                "Dynamic Referral Attribution Engine",
                "Real-time Balance Reconciliation",
                "Emission Halving Monitoring"
            )
        ),
        RoadmapPhase(
            "Phase 03",
            "Ecosystem & Mobile Growth",
            "PLANNED",
            "Expansion into native Android/iOS store apps and community voting on humanitarian aid projects.",
            listOf(
                "Native Mobile Store Distribution (Google Play APK/AAB)",
                "Pioneer Reputation & Badging",
                "Humanitarian Fund Voting Framework"
            )
        ),
        RoadmapPhase(
            "Phase 04",
            "Utility Expansion",
            "PLANNED",
            "Research and smart contract design for tokenomics vesting and partner integrations.",
            listOf(
                "Emission Velocity Analysis",
                "Halving Event Milestone Trigger",
                "Merchant Utility Architecture"
            )
        ),
        RoadmapPhase(
            "Phase 05",
            "HOPE Gaming Launch",
            "PLANNED",
            "Web3 gaming integration with in-game items, tournaments, and exclusive passes.",
            listOf(
                "Esports Tournament Passes",
                "Cosmetic Game Assets Store",
                "Tiered Guild Rewards"
            )
        ),
        RoadmapPhase(
            "Phase 06",
            "Marketplace & Commerce",
            "PLANNED",
            "Peer-to-peer commerce and software subscriptions settled in \$HOPE.",
            listOf(
                "Creator Digital Goods Market",
                "Escrow Buyer Protection",
                "Partner Merchant Checkouts"
            )
        ),
        RoadmapPhase(
            "Phase 07",
            "HOPE Pay Settlement",
            "PLANNED",
            "Decentralized QR payment settlement for physical & online merchants.",
            listOf(
                "Instant Merchant QR Scanner",
                "Zero-Fee Freelancer Settlement",
                "Lightweight Point-of-Sale App"
            )
        ),
        RoadmapPhase(
            "Phase 08",
            "Global Network & Migration",
            "PLANNED",
            "Subject to regulatory & technical readiness: Full on-chain blockchain deployment and verified balance migration.",
            listOf(
                "On-chain Smart Contract Deployment",
                "Verified 1:1 Ledger Migration Bridge",
                "Decentralized Autonomous Impact Governance"
            )
        )
    )

    val faqs = listOf(
        FaqItem(
            "What is HOPE Network and \$HOPE token?",
            "HOPE Network is a community-powered digital ecosystem combining idle mining, digital utility, gaming, and a dedicated 20% Humanitarian Fund. During the initial testnet phase, \$HOPE is an internal reward unit with zero purchase cost."
        ),
        FaqItem(
            "How does the 24-hour mining cycle work?",
            "Click 'Start Mining' to initialize your session. The server tracks time and accrues rewards at 3.0 HOPE/day (0.125 HOPE/hour). You can claim accumulated rewards at any time or re-activate when the 24 hours finish."
        ),
        FaqItem(
            "What is the revenue model?",
            "HOPE Network is 100% ad-supported. We do not sell tokens, mining packages, or investment plans. The platform operates on a sustainable advertising-supported model."
        ),
        FaqItem(
            "How does the 20% Humanitarian Fund work?",
            "15,000,000 HOPE (20% of the total 75M supply) is dedicated to emergency relief, clean water projects, and social assistance. Transparent progress updates are published in the impact directory."
        ),
        FaqItem(
            "Can I withdraw or transfer \$HOPE right now?",
            "During Phase 1 Testnet, HOPE is an internal non-transferable reward unit. On-chain migration will be announced in later phases subject to regulatory and technical readiness."
        )
    )

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
                borderColor = HopeCyan.copy(alpha = 0.5f),
                cornerRadius = 20.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Web3Badge(text = "DEVELOPMENT ROADMAP", color = HopeCyan, icon = Icons.Default.Timeline)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "8-Phase Master Timeline",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "From Genesis Testnet to Global Utility Network. Follow our transparent, milestone-driven expansion.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // PHASES TIMELINE
        items(phases.size) { index ->
            val phase = phases[index]
            val statusColor = when (phase.status) {
                "COMPLETED" -> HopeEmerald
                "IN PROGRESS" -> HopeCyan
                else -> HopeGold
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = HopeDarkCard),
                border = BorderStroke(1.dp, Color(0x22263248))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (phase.status == "COMPLETED") Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${phase.phaseNumber} — ${phase.title}",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Web3Badge(text = phase.status, color = statusColor)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = phase.description,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        phase.deliverables.forEach { item ->
                            Text(
                                text = "• $item",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // OFFICIAL TELEGRAM
        item {
            TelegramBannerCard()
        }

        // FAQ SECTION
        item {
            Text(
                text = "FREQUENTLY ASKED QUESTIONS",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(faqs.size) { index ->
            val faq = faqs[index]
            var expanded by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = null,
                                tint = HopeCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = faq.question,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    AnimatedVisibility(visible = expanded) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = faq.answer,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}
