package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AdminAuditLogEntity
import com.example.data.db.AdvertisingSettingsEntity
import com.example.data.db.AdsgramRewardEventEntity
import com.example.data.db.AnnouncementEntity
import com.example.data.db.HopeDatabase
import com.example.data.db.HumanitarianUpdateEntity
import com.example.data.db.MiningSessionEntity
import com.example.data.db.RewardLedgerEntity
import com.example.data.db.SystemSettingsEntity
import com.example.data.db.UserEntity
import com.example.data.repository.HopeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

data class LiveMiningUiState(
    val isActive: Boolean = false,
    val remainingSeconds: Long = 0L,
    val formattedRemainingTime: String = "00:00:00",
    val accruedReward: Double = 0.0,
    val progressFraction: Float = 0f,
    val ratePerHour: Double = 0.125,
    val currentRatePerDay: Double = 3.0
)

class HopeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = HopeDatabase.getDatabase(application, viewModelScope)
    private val repository = HopeRepository(
        userDao = db.userDao(),
        miningDao = db.miningSessionDao(),
        ledgerDao = db.rewardLedgerDao(),
        settingsDao = db.systemSettingsDao(),
        adminAuditLogDao = db.adminAuditLogDao(),
        adSettingsDao = db.advertisingSettingsDao(),
        humanitarianDao = db.humanitarianUpdateDao(),
        announcementDao = db.announcementDao(),
        adsgramRewardDao = db.adsgramRewardEventDao()
    )

    // Current User Session State
    private val _currentUserId = MutableStateFlow<String>("user_pioneer_002") // default pioneer user
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Global Flows
    val systemSettings: StateFlow<SystemSettingsEntity?> = repository.getSystemSettingsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val adSettings: StateFlow<AdvertisingSettingsEntity?> = repository.getAdSettingsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsersFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeMinersCount: StateFlow<Int> = repository.getActiveMinersCountFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val auditLogs: StateFlow<List<AdminAuditLogEntity>> = repository.getAdminAuditLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val humanitarianUpdates: StateFlow<List<HumanitarianUpdateEntity>> = repository.getHumanitarianUpdates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val announcements: StateFlow<List<AnnouncementEntity>> = repository.getAnnouncements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adsgramEvents: StateFlow<List<AdsgramRewardEventEntity>> = repository.getAllAdsgramEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active User Specific Flows
    private val _miningSessions = MutableStateFlow<List<MiningSessionEntity>>(emptyList())
    val miningSessions: StateFlow<List<MiningSessionEntity>> = _miningSessions.asStateFlow()

    private val _userLedger = MutableStateFlow<List<RewardLedgerEntity>>(emptyList())
    val userLedger: StateFlow<List<RewardLedgerEntity>> = _userLedger.asStateFlow()

    // Live Mining UI Engine State
    private val _liveMiningState = MutableStateFlow(LiveMiningUiState())
    val liveMiningState: StateFlow<LiveMiningUiState> = _liveMiningState.asStateFlow()

    // Toast / Feedback message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Ad Simulation Dialog State
    private val _showAdGateDialog = MutableStateFlow(false)
    val showAdGateDialog: StateFlow<Boolean> = _showAdGateDialog.asStateFlow()

    // AdsGram Rewarded Video Modal Player State
    private val _showAdsgramPlayer = MutableStateFlow(false)
    val showAdsgramPlayer: StateFlow<Boolean> = _showAdsgramPlayer.asStateFlow()

    private val _adWatchSecondsRemaining = MutableStateFlow(15)
    val adWatchSecondsRemaining: StateFlow<Int> = _adWatchSecondsRemaining.asStateFlow()

    private val _isAdEligibleForReward = MutableStateFlow(false)
    val isAdEligibleForReward: StateFlow<Boolean> = _isAdEligibleForReward.asStateFlow()

    private var adCountdownJob: Job? = null

    init {
        // Observe Current User
        viewModelScope.launch {
            _currentUserId.collect { id ->
                repository.getUserFlow(id).collect { user ->
                    _currentUser.value = user
                }
            }
        }

        // Observe Sessions & Ledger
        viewModelScope.launch {
            _currentUserId.collect { id ->
                launch {
                    repository.getMiningSessions(id).collect { sessions ->
                        _miningSessions.value = sessions
                    }
                }
                launch {
                    repository.getUserLedger(id).collect { ledger ->
                        _userLedger.value = ledger
                    }
                }
            }
        }

        // Live 1-second ticker for server-accurate mining timer & accrued reward
        viewModelScope.launch {
            while (isActive) {
                updateMiningTicker()
                delay(1000)
            }
        }
    }

    private suspend fun updateMiningTicker() {
        val user = _currentUser.value ?: return
        val settings = systemSettings.value ?: SystemSettingsEntity()
        val dailyRate = settings.dailyMiningRate
        val hourlyRate = dailyRate / 24.0

        if (!user.isMiningActive) {
            _liveMiningState.value = LiveMiningUiState(
                isActive = false,
                remainingSeconds = 0L,
                formattedRemainingTime = "00:00:00",
                accruedReward = 0.0,
                progressFraction = 0f,
                ratePerHour = hourlyRate,
                currentRatePerDay = dailyRate
            )
            return
        }

        val now = System.currentTimeMillis()
        val start = user.lastMiningTimestamp
        val durationMs = 24 * 3600 * 1000L
        val end = start + durationMs
        val remainingMs = (end - now).coerceAtLeast(0L)
        val elapsedMs = (now - start).coerceAtLeast(0L)

        val progress = (elapsedMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
        val elapsedHours = elapsedMs.coerceAtMost(durationMs) / (3600 * 1000.0)
        val accrued = elapsedHours * hourlyRate

        val remSeconds = remainingMs / 1000
        val hours = remSeconds / 3600
        val mins = (remSeconds % 3600) / 60
        val secs = remSeconds % 60
        val formatted = String.format("%02d:%02d:%02d", hours, mins, secs)

        _liveMiningState.value = LiveMiningUiState(
            isActive = true,
            remainingSeconds = remSeconds,
            formattedRemainingTime = formatted,
            accruedReward = accrued,
            progressFraction = progress,
            ratePerHour = hourlyRate,
            currentRatePerDay = dailyRate
        )

        // If cycle elapsed, trigger server check to finalize session
        if (remainingMs <= 0L) {
            repository.checkAndAccrueMiningRewards(user.id)
        }
    }

    fun triggerStartMiningWithAdGate() {
        val user = _currentUser.value ?: return
        if (user.isSuspended) {
            _userMessage.value = "Your account is suspended. Contact support."
            return
        }
        if (user.isMiningActive) {
            _userMessage.value = "Mining cycle is already active."
            return
        }

        val ads = adSettings.value
        if (ads != null && ads.adsEnabled && (ads.interstitialEnabled || ads.nativeEnabled)) {
            // Show Ad Gate Dialog before starting
            _showAdGateDialog.value = true
        } else {
            proceedStartMining()
        }
    }

    fun proceedStartMining() {
        _showAdGateDialog.value = false
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.startMiningSession(user.id)
            if (result.isSuccess) {
                _userMessage.value = "Mining cycle started! 24-hour reward accumulation is now live."
                updateMiningTicker()
            } else {
                _userMessage.value = result.exceptionOrNull()?.message ?: "Failed to start mining"
            }
        }
    }

    fun dismissAdGate() {
        _showAdGateDialog.value = false
    }

    fun claimOrStopMining() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val reward = repository.claimOrStopMining(user.id)
            if (reward > 0.0001) {
                _userMessage.value = "Successfully claimed +${String.format("%.4f", reward)} HOPE to your balance!"
            } else {
                _userMessage.value = "Mining session ended."
            }
            updateMiningTicker()
        }
    }

    // Authentication & Account Switcher
    fun switchUser(userId: String) {
        _currentUserId.value = userId
        _userMessage.value = "Switched active account."
    }

    fun loginWithEmail(email: String, username: String? = null, referralCode: String? = null) {
        viewModelScope.launch {
            val user = repository.authenticateUser(email, username, referralCode)
            _currentUserId.value = user.id
            _userMessage.value = "Welcome back, ${user.username}!"
        }
    }

    fun logout() {
        _currentUserId.value = "user_pioneer_002"
        _userMessage.value = "Logged out. Switched to guest mode."
    }

    // Admin Operations
    fun adminUpdateMiningRate(newRate: Double, reason: String) {
        val admin = _currentUser.value ?: return
        if (admin.role != "admin") {
            _userMessage.value = "Unauthorized: Admin privileges required."
            return
        }
        viewModelScope.launch {
            repository.updateDailyMiningRate(admin, newRate, reason)
            _userMessage.value = "Daily mining rate successfully updated to $newRate HOPE/day."
        }
    }

    fun adminScheduleHalving(nextRate: Double, days: Int, reason: String) {
        val admin = _currentUser.value ?: return
        if (admin.role != "admin") return
        viewModelScope.launch {
            repository.scheduleHalving(admin, nextRate, days, reason)
            _userMessage.value = "Halving event scheduled: $nextRate HOPE/day in $days days."
        }
    }

    fun adminAdjustBalance(targetUserId: String, newBalance: Double, reason: String) {
        val admin = _currentUser.value ?: return
        if (admin.role != "admin") return
        viewModelScope.launch {
            repository.adjustUserBalance(admin, targetUserId, newBalance, reason)
            _userMessage.value = "User balance reconciled to $newBalance HOPE."
        }
    }

    fun adminToggleSuspension(targetUserId: String, isSuspended: Boolean, reason: String) {
        val admin = _currentUser.value ?: return
        if (admin.role != "admin") return
        viewModelScope.launch {
            repository.toggleUserSuspension(admin, targetUserId, isSuspended, reason)
            _userMessage.value = if (isSuspended) "Account suspended." else "Account unsuspended."
        }
    }

    fun adminUpdateAdSettings(newSettings: AdvertisingSettingsEntity, reason: String) {
        val admin = _currentUser.value ?: return
        if (admin.role != "admin") return
        viewModelScope.launch {
            repository.updateAdvertisingSettings(admin, newSettings, reason)
            _userMessage.value = "Advertising settings saved."
        }
    }

    fun adminCreateHumanitarianInitiative(title: String, category: String, hopeAmount: Double, desc: String, date: String) {
        val admin = _currentUser.value ?: return
        if (admin.role != "admin") return
        viewModelScope.launch {
            val item = HumanitarianUpdateEntity(
                title = title,
                category = category,
                allocatedHope = hopeAmount,
                status = "Active",
                description = desc,
                date = date
            )
            repository.createHumanitarianInitiative(admin, item)
            _userMessage.value = "Humanitarian initiative published."
        }
    }

    // AdsGram Rewarded Ad Watch Flow
    fun startWatchingAdsgramRewardedAd() {
        val user = _currentUser.value
        if (user == null) {
            _userMessage.value = "Please sign in to earn ad rewards."
            return
        }
        if (user.isSuspended) {
            _userMessage.value = "Account is suspended. Cannot earn rewards."
            return
        }
        val settings = adSettings.value
        if (settings?.adsEnabled != true || settings.rewardedEnabled != true) {
            _userMessage.value = "Rewarded video ads are currently disabled in settings."
            return
        }

        adCountdownJob?.cancel()
        _adWatchSecondsRemaining.value = 15
        _isAdEligibleForReward.value = false
        _showAdsgramPlayer.value = true

        adCountdownJob = viewModelScope.launch {
            while (_adWatchSecondsRemaining.value > 0) {
                delay(1000)
                _adWatchSecondsRemaining.value -= 1
            }
            _isAdEligibleForReward.value = true
        }
    }

    fun closeAdsgramAdEarly() {
        adCountdownJob?.cancel()
        _showAdsgramPlayer.value = false
        _isAdEligibleForReward.value = false
        _userMessage.value = "Ad closed before completion. Zero HOPE reward credited."
    }

    fun claimAdsgramAdReward() {
        if (!_isAdEligibleForReward.value) {
            _userMessage.value = "You must watch the entire ad to receive rewards."
            return
        }
        val user = _currentUser.value ?: return
        val settings = adSettings.value

        _showAdsgramPlayer.value = false
        _isAdEligibleForReward.value = false

        viewModelScope.launch {
            val uniqueEventId = "evt_${UUID.randomUUID().toString().replace("-", "").take(12)}"
            val result = repository.processAdsgramRewardCallback(
                userId = user.id,
                eventId = uniqueEventId,
                secretToken = settings?.adsgramSecretKey
            )
            if (result.success) {
                _userMessage.value = "🎉 Rewarded +${String.format("%.2f", result.rewardAmount)} HOPE! Recorded in ledger."
            } else {
                _userMessage.value = "Ad reward failed: ${result.message}"
            }
        }
    }

    // Admin Webhook Simulation & Anti-Replay Testing
    fun adminTestAdsgramCallback(
        targetUserId: String,
        eventId: String,
        secretToken: String?,
        onComplete: (String, Boolean) -> Unit
    ) {
        val admin = _currentUser.value ?: return
        if (admin.role != "admin") {
            onComplete("Unauthorized: Admin role required", false)
            return
        }

        viewModelScope.launch {
            val result = repository.processAdsgramRewardCallback(
                userId = targetUserId,
                eventId = eventId,
                secretToken = secretToken
            )

            val statusText = if (result.success) {
                if (result.isDuplicate) {
                    "HTTP 200 OK (DUPLICATE): Event '$eventId' was already processed. Replay attack prevented (0 additional tokens minted)."
                } else {
                    "HTTP 200 OK (SUCCESS): Credited +${result.rewardAmount} HOPE to user '$targetUserId'. Event '$eventId' recorded."
                }
            } else {
                "HTTP ${result.httpStatusCode} ERROR: ${result.message}"
            }
            onComplete(statusText, result.success)
        }
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }
}
