package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.UserEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.TelegramBannerCard
import com.example.ui.components.Web3Badge
import com.example.ui.theme.HopeCyan
import com.example.ui.theme.HopeDarkCard
import com.example.ui.theme.HopeDarkCardBorder
import com.example.ui.theme.HopeEmerald
import com.example.ui.theme.HopeGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ReferralScreen(
    currentUser: UserEntity?
) {
    val context = LocalContext.current
    val refCode = currentUser?.referralCode ?: "HOPE7799"
    val referralLink = "https://hopenetwork.app?ref=$refCode"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // REFERRAL HERO
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = HopeEmerald.copy(alpha = 0.5f),
                cornerRadius = 20.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF0C241D), Color(0xFF091713))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Web3Badge(text = "COMMUNITY MULTIPLIER", color = HopeEmerald, icon = Icons.Default.GroupAdd)
                        Web3Badge(text = "+0.5 HOPE / FRIEND", color = HopeGold)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Invite Friends & Mine Together",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Share your unique referral link. Every pioneer who registers through your link earns you instant +0.5 \$HOPE and strengthens the network.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // REFERRAL CODE & LINK CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = HopeDarkCard),
                border = BorderStroke(1.dp, Color(0x22263248))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "YOUR UNIQUE REFERRAL CODE",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0x3338BDF8), RoundedCornerShape(10.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = refCode,
                                color = HopeCyan,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            )
                            Web3Badge(text = "Active Code", color = HopeEmerald)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "INVITATION LINK",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0B101B))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = referralLink,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("HOPE Referral Link", referralLink)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Referral link copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = HopeCyan),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = Color(0xFF0B0E14),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Link", color = Color(0xFF0B0E14), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "Join me on HOPE Network! Mine \$HOPE for free & support humanitarian aid: $referralLink")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Referral Link"))
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF229ED9)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Link", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // STATS SUMMARY
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCardItem(
                    title = "Total Invited",
                    value = "${currentUser?.totalReferrals ?: 0}",
                    subtitle = "Pioneers Joined",
                    color = HopeCyan,
                    icon = Icons.Default.Groups,
                    modifier = Modifier.weight(1f)
                )

                StatCardItem(
                    title = "Referral Rewards",
                    value = "+${String.format("%.2f", currentUser?.referralEarnings ?: 0.0)}",
                    subtitle = "\$HOPE Earned",
                    color = HopeEmerald,
                    icon = Icons.Default.MonetizationOn,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // OFFICIAL TELEGRAM
        item {
            TelegramBannerCard()
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
fun StatCardItem(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = HopeDarkCard),
        border = BorderStroke(1.dp, Color(0x22263248))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = TextSecondary, fontSize = 11.sp)
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text(text = subtitle, color = color, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
