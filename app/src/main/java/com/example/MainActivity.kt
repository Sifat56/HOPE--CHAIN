package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AdGateDialog
import com.example.ui.components.AdsgramRewardedAdPlayerDialog
import com.example.ui.components.AuthDialog
import com.example.ui.components.Web3Badge
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.EcosystemScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ReferralScreen
import com.example.ui.screens.RoadmapScreen
import com.example.ui.screens.TokenomicsScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.HopeCyan
import com.example.ui.theme.HopeDarkBackground
import com.example.ui.theme.HopeDarkCard
import com.example.ui.theme.HopeDarkSurface
import com.example.ui.theme.HopeEmerald
import com.example.ui.theme.HopeGold
import com.example.ui.theme.HopePurple
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.HopeViewModel

enum class NavigationDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    HOME("home", "Mine", Icons.Default.Bolt),
    WALLET("wallet", "Ledger", Icons.Default.AccountBalanceWallet),
    ECOSYSTEM("ecosystem", "Ecosystem", Icons.Default.SportsEsports),
    TOKENOMICS("tokenomics", "Tokenomics", Icons.Default.PieChart),
    ROADMAP("roadmap", "Roadmap", Icons.Default.Timeline),
    REFERRALS("referrals", "Invite", Icons.Default.GroupAdd),
    ADMIN("admin", "Admin", Icons.Default.AdminPanelSettings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: HopeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                HopeNetworkApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HopeNetworkApp(viewModel: HopeViewModel) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var currentScreen by remember { mutableStateOf(NavigationDestination.HOME) }
    var showAuthDialog by remember { mutableStateOf(false) }

    val currentUser by viewModel.currentUser.collectAsState()
    val systemSettings by viewModel.systemSettings.collectAsState()
    val adSettings by viewModel.adSettings.collectAsState()
    val miningState by viewModel.liveMiningState.collectAsState()
    val activeMinersCount by viewModel.activeMinersCount.collectAsState()
    val miningSessions by viewModel.miningSessions.collectAsState()
    val userLedger by viewModel.userLedger.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val humanitarianUpdates by viewModel.humanitarianUpdates.collectAsState()
    val showAdGate by viewModel.showAdGateDialog.collectAsState()
    val showAdsgramPlayer by viewModel.showAdsgramPlayer.collectAsState()
    val adWatchSecondsRemaining by viewModel.adWatchSecondsRemaining.collectAsState()
    val isAdEligibleForReward by viewModel.isAdEligibleForReward.collectAsState()
    val adsgramEvents by viewModel.adsgramEvents.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    // Show snackbars for system alerts
    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = HopeDarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.hope_token_logo),
                            contentDescription = "HOPE Logo",
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .border(1.dp, HopeCyan.copy(alpha = 0.5f), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "HOPE",
                                    color = TextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "NETWORK",
                                    color = HopeCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Mine. Play. Pay. Help.",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                actions = {
                    // Telegram Community Quick Link
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/hopenetworkofficial"))
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Telegram",
                            tint = Color(0xFF229ED9),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Account & Role Switcher Chip
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showAuthDialog = true },
                        color = if (currentUser?.role == "admin") HopeGold.copy(alpha = 0.15f) else Color(0xFF162238),
                        border = BorderStroke(1.dp, if (currentUser?.role == "admin") HopeGold.copy(alpha = 0.5f) else HopeCyan.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (currentUser?.isMiningActive == true) HopeEmerald else if (currentUser?.role == "admin") HopeGold else HopeCyan)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentUser?.username ?: "Guest",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HopeDarkSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = HopeDarkSurface,
                tonalElevation = 8.dp
            ) {
                val destinations = listOf(
                    NavigationDestination.HOME,
                    NavigationDestination.WALLET,
                    NavigationDestination.ECOSYSTEM,
                    NavigationDestination.TOKENOMICS,
                    NavigationDestination.ROADMAP,
                    NavigationDestination.REFERRALS,
                    NavigationDestination.ADMIN
                )

                destinations.forEach { dest ->
                    val isSelected = currentScreen == dest
                    val iconColor = if (isSelected) {
                        if (dest == NavigationDestination.ADMIN) HopeGold else HopeCyan
                    } else {
                        TextMuted
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = dest },
                        icon = {
                            Icon(
                                imageVector = dest.icon,
                                contentDescription = dest.title,
                                tint = iconColor,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = dest.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = iconColor
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = if (dest == NavigationDestination.ADMIN) HopeGold.copy(alpha = 0.2f) else HopeCyan.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(HopeDarkBackground)
        ) {
            when (currentScreen) {
                NavigationDestination.HOME -> {
                    HomeScreen(
                        currentUser = currentUser,
                        miningState = miningState,
                        systemSettings = systemSettings,
                        adSettings = adSettings,
                        activeMinersCount = activeMinersCount,
                        onStartMining = { viewModel.triggerStartMiningWithAdGate() },
                        onClaimMining = { viewModel.claimOrStopMining() },
                        onNavigateToWallet = { currentScreen = NavigationDestination.WALLET },
                        onNavigateToEcosystem = { currentScreen = NavigationDestination.ECOSYSTEM },
                        onNavigateToTokenomics = { currentScreen = NavigationDestination.TOKENOMICS },
                        onNavigateToRoadmap = { currentScreen = NavigationDestination.ROADMAP },
                        onNavigateToReferral = { currentScreen = NavigationDestination.REFERRALS },
                        onWatchAdsgramAd = { viewModel.startWatchingAdsgramRewardedAd() }
                    )
                }
                NavigationDestination.WALLET -> {
                    WalletScreen(
                        currentUser = currentUser,
                        miningSessions = miningSessions,
                        userLedger = userLedger,
                        adSettings = adSettings
                    )
                }
                NavigationDestination.ECOSYSTEM -> {
                    EcosystemScreen()
                }
                NavigationDestination.TOKENOMICS -> {
                    TokenomicsScreen(
                        humanitarianUpdates = humanitarianUpdates
                    )
                }
                NavigationDestination.ROADMAP -> {
                    RoadmapScreen()
                }
                NavigationDestination.REFERRALS -> {
                    ReferralScreen(
                        currentUser = currentUser
                    )
                }
                NavigationDestination.ADMIN -> {
                    AdminScreen(
                        currentUser = currentUser,
                        systemSettings = systemSettings,
                        adSettings = adSettings,
                        allUsers = allUsers,
                        auditLogs = auditLogs,
                        onUpdateMiningRate = { rate, reason -> viewModel.adminUpdateMiningRate(rate, reason) },
                        onScheduleHalving = { rate, days, reason -> viewModel.adminScheduleHalving(rate, days, reason) },
                        onAdjustBalance = { targetId, balance, reason -> viewModel.adminAdjustBalance(targetId, balance, reason) },
                        onToggleSuspension = { targetId, isSusp, reason -> viewModel.adminToggleSuspension(targetId, isSusp, reason) },
                        onUpdateAdSettings = { settings, reason -> viewModel.adminUpdateAdSettings(settings, reason) },
                        onPostHumanitarian = { title, cat, hope, desc, date -> viewModel.adminCreateHumanitarianInitiative(title, cat, hope, desc, date) },
                        onSwitchToAdminUser = { viewModel.switchUser("admin_root_001") },
                        adsgramEvents = adsgramEvents,
                        onTestAdsgramCallback = { userId, eventId, secret, onResult ->
                            viewModel.adminTestAdsgramCallback(userId, eventId, secret, onResult)
                        }
                    )
                }
            }
        }
    }

    // Ad Gate Dialog
    if (showAdGate) {
        AdGateDialog(
            onDismiss = { viewModel.dismissAdGate() },
            onProceed = { viewModel.proceedStartMining() }
        )
    }

    // AdsGram Rewarded Video Ad Player Dialog
    if (showAdsgramPlayer) {
        AdsgramRewardedAdPlayerDialog(
            secondsRemaining = adWatchSecondsRemaining,
            isEligibleForReward = isAdEligibleForReward,
            rewardAmount = adSettings?.adsgramRewardAmount ?: 0.25,
            blockId = adSettings?.adsgramBlockId ?: "47226",
            onCloseEarly = { viewModel.closeAdsgramAdEarly() },
            onClaimReward = { viewModel.claimAdsgramAdReward() }
        )
    }

    // Authentication / Fast Switch Dialog
    if (showAuthDialog) {
        AuthDialog(
            currentUser = currentUser,
            onDismiss = { showAuthDialog = false },
            onLogin = { email, username, refCode ->
                viewModel.loginWithEmail(email, username, refCode)
            },
            onSwitchUser = { userId ->
                viewModel.switchUser(userId)
            },
            onLogout = {
                viewModel.logout()
            }
        )
    }
}
