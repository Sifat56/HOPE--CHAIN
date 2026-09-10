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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.HumanitarianUpdateEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.Web3Badge
import com.example.ui.theme.HopeCrimson
import com.example.ui.theme.HopeCyan
import com.example.ui.theme.HopeCyanGlow
import com.example.ui.theme.HopeDarkCard
import com.example.ui.theme.HopeDarkCardBorder
import com.example.ui.theme.HopeEmerald
import com.example.ui.theme.HopeGold
import com.example.ui.theme.HopePurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class TokenAllocation(
    val category: String,
    val percentage: Int,
    val amountTokens: Double,
    val color: Color
)

@Composable
fun TokenomicsScreen(
    humanitarianUpdates: List<HumanitarianUpdateEntity>
) {
    val allocations = listOf(
        TokenAllocation("Mining Rewards", 40, 30000000.0, HopeCyan),
        TokenAllocation("Humanitarian Fund", 20, 15000000.0, HopeCrimson),
        TokenAllocation("Team & Advisors", 15, 11250000.0, HopePurple),
        TokenAllocation("Ecosystem & Development", 10, 7500000.0, HopeEmerald),
        TokenAllocation("Liquidity", 5, 3750000.0, HopeGold),
        TokenAllocation("Marketing & Partnerships", 5, 3750000.0, Color(0xFF0284C7)),
        TokenAllocation("Treasury / Reserve", 5, 3750000.0, Color(0xFF64748B))
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // TOKENOMICS HEADER
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = HopeCyan.copy(alpha = 0.5f),
                cornerRadius = 20.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF131D30), Color(0xFF0F1524))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Web3Badge(text = "TOKENOMICS MATRIX", color = HopeCyan, icon = Icons.Default.PieChart)
                        Web3Badge(text = "100% FIXED SUPPLY", color = HopeEmerald)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "75,000,000 \$HOPE",
                        color = TextPrimary,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = "Total Fixed Native Asset Supply Cap",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Visual allocation bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                    ) {
                        allocations.forEach { alloc ->
                            Box(
                                modifier = Modifier
                                    .weight(alloc.percentage.toFloat())
                                    .fillMaxSize()
                                    .background(alloc.color)
                            )
                        }
                    }
                }
            }
        }

        // ALLOCATION BREAKDOWN TABLE
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = HopeDarkCard),
                border = BorderStroke(1.dp, Color(0x22263248))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "SUPPLY ALLOCATION MATRIX",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    allocations.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(item.color)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = item.category,
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${String.format("%,.0f", item.amountTokens)} HOPE",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            Web3Badge(text = "${item.percentage}%", color = item.color)
                        }
                    }
                }
            }
        }

        // HUMANITARIAN FUND SPOTLIGHT
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = HopeCrimson.copy(alpha = 0.5f),
                cornerRadius = 18.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF28111A), Color(0xFF13090F))
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
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(HopeCrimson.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = HopeCrimson,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "HUMANITARIAN FUND",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "20% Dedicated Impact Vault",
                                    color = HopeCrimson,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Web3Badge(text = "15,000,000 HOPE", color = HopeCrimson)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "15 Million \$HOPE is permanently allocated for rapid disaster relief, clean solar water projects, emergency food distribution, and grassroots digital education.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // HUMANITARIAN INITIATIVES LIST
        item {
            Text(
                text = "VERIFIED IMPACT DIRECTORY",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(humanitarianUpdates) { initiative ->
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
                        Column {
                            Text(
                                text = initiative.title,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${initiative.category} • ${initiative.date}",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                        Web3Badge(
                            text = initiative.status,
                            color = if (initiative.status == "Active") HopeEmerald else HopeCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = initiative.description,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Allocated Reserve:", color = TextMuted, fontSize = 10.sp)
                        Text(
                            "${String.format("%,.0f", initiative.allocatedHope)} HOPE",
                            color = HopeCrimson,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}
