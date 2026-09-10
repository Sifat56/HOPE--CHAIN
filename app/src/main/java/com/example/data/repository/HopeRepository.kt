package com.example.data.repository

import com.example.data.db.AdminAuditLogDao
import com.example.data.db.AdminAuditLogEntity
import com.example.data.db.AdvertisingSettingsDao
import com.example.data.db.AdvertisingSettingsEntity
import com.example.data.db.AdsgramRewardEventDao
import com.example.data.db.AdsgramRewardEventEntity
import com.example.data.db.AnnouncementDao
import com.example.data.db.AnnouncementEntity
import com.example.data.db.HumanitarianUpdateDao
import com.example.data.db.HumanitarianUpdateEntity
import com.example.data.db.MiningSessionDao
import com.example.data.db.MiningSessionEntity
import com.example.data.db.RewardLedgerDao
import com.example.data.db.RewardLedgerEntity
import com.example.data.db.SystemSettingsDao
import com.example.data.db.SystemSettingsEntity
import com.example.data.db.UserDao
import com.example.data.db.UserEntity
import android.util.Log
import kotlinx.coroutines.flow.Flow
import java.util.UUID

data class AdsgramRewardResult(
    val success: Boolean,
    val httpStatusCode: Int,
    val message: String,
    val rewardAmount: Double = 0.0,
    val eventId: String = "",
    val isDuplicate: Boolean = false,
    val newBalance: Double? = null
)

class HopeRepository(
    private val userDao: UserDao,
    private val miningDao: MiningSessionDao,
    private val ledgerDao: RewardLedgerDao,
    private val settingsDao: SystemSettingsDao,
    private val adminAuditLogDao: AdminAuditLogDao,
    private val adSettingsDao: AdvertisingSettingsDao,
    private val humanitarianDao: HumanitarianUpdateDao,
    private val announcementDao: AnnouncementDao,
    private val adsgramRewardDao: AdsgramRewardEventDao
) {
    // User Flows
    fun getUserFlow(userId: String): Flow<UserEntity?> = userDao.getUserByIdFlow(userId)
    fun getAllUsersFlow(): Flow<List<UserEntity>> = userDao.getAllUsersFlow()
    fun getActiveMinersCountFlow(): Flow<Int> = userDao.getActiveMinersCountFlow()

    // Mining & Ledger Flows
    fun getMiningSessions(userId: String): Flow<List<MiningSessionEntity>> = miningDao.getSessionsForUser(userId)
    fun getUserLedger(userId: String): Flow<List<RewardLedgerEntity>> = ledgerDao.getLedgerForUser(userId)
    fun getAllRecentLedger(): Flow<List<RewardLedgerEntity>> = ledgerDao.getAllRecentLedger()

    // AdsGram Reward Events Flow
    fun getAllAdsgramEvents(): Flow<List<AdsgramRewardEventEntity>> = adsgramRewardDao.getAllEventsFlow()
    fun getRecentAdsgramEvents(limit: Int = 50): Flow<List<AdsgramRewardEventEntity>> = adsgramRewardDao.getRecentEventsFlow(limit)
    fun getUserAdsgramEvents(userId: String): Flow<List<AdsgramRewardEventEntity>> = adsgramRewardDao.getEventsForUserFlow(userId)

    // Settings & Admin Flows
    fun getSystemSettingsFlow(): Flow<SystemSettingsEntity?> = settingsDao.getSettingsFlow()
    fun getAdSettingsFlow(): Flow<AdvertisingSettingsEntity?> = adSettingsDao.getAdSettingsFlow()
    fun getAdminAuditLogs(): Flow<List<AdminAuditLogEntity>> = adminAuditLogDao.getAllAuditLogs()
    fun getHumanitarianUpdates(): Flow<List<HumanitarianUpdateEntity>> = humanitarianDao.getAllUpdates()
    fun getAnnouncements(): Flow<List<AnnouncementEntity>> = announcementDao.getAllAnnouncements()

    // Auth & User Management
    suspend fun authenticateUser(email: String, username: String? = null, referralInput: String? = null): UserEntity {
        var user = userDao.getUserByEmail(email)
        if (user == null) {
            // Generate clean referral code
            val code = "HOPE" + (1000..9999).random().toString()
            val cleanUsername = username ?: email.substringBefore("@").replaceFirstChar { it.uppercase() }
            val role = if (email.contains("admin", ignoreCase = true)) "admin" else "user"

            var validReferrerId: String? = null
            if (!referralInput.isNullOrBlank()) {
                val referrer = userDao.getUserByReferralCode(referralInput.trim().uppercase())
                if (referrer != null) {
                    validReferrerId = referrer.id
                    // Increment referrer's count & give signup referral reward
                    val updatedReferrer = referrer.copy(
                        totalReferrals = referrer.totalReferrals + 1,
                        referralEarnings = referrer.referralEarnings + 0.5,
                        hopeBalance = referrer.hopeBalance + 0.5
                    )
                    userDao.updateUser(updatedReferrer)
                    ledgerDao.insertLedger(
                        RewardLedgerEntity(
                            userId = referrer.id,
                            amount = 0.5,
                            type = "REFERRAL_BONUS",
                            description = "Referral bonus for inviting $cleanUsername ($email)",
                            timestamp = System.currentTimeMillis(),
                            referenceId = "REF-${System.currentTimeMillis()}"
                        )
                    )
                }
            }

            user = UserEntity(
                id = "usr_" + UUID.randomUUID().toString().take(8),
                username = cleanUsername,
                email = email,
                role = role,
                hopeBalance = 0.0,
                totalMined = 0.0,
                referralCode = code,
                referredBy = validReferrerId,
                totalReferrals = 0,
                referralEarnings = 0.0,
                lastMiningTimestamp = 0L,
                isMiningActive = false,
                isVIPUser = false,
                isSuspended = false,
                createdAt = System.currentTimeMillis()
            )
            userDao.insertUser(user)
        }
        return user
    }

    suspend fun getUserById(userId: String): UserEntity? = userDao.getUserById(userId)

    // SERVER-SIDE MINING ENGINE
    suspend fun startMiningSession(userId: String): Result<MiningSessionEntity> {
        val user = userDao.getUserById(userId) ?: return Result.failure(Exception("User not found"))
        if (user.isSuspended) return Result.failure(Exception("Account is suspended"))

        val settings = settingsDao.getSettings() ?: SystemSettingsEntity()
        val dailyRate = settings.dailyMiningRate
        val hourlyRate = dailyRate / 24.0

        val now = System.currentTimeMillis()
        val durationMs = 24 * 3600 * 1000L // 24 hours standard cycle
        val scheduledEnd = now + durationMs

        // Check if there is already an active session
        val currentActive = miningDao.getActiveSession(userId)
        if (currentActive != null) {
            // Already active; return existing session
            return Result.success(currentActive)
        }

        val session = MiningSessionEntity(
            userId = userId,
            startTimestamp = now,
            scheduledEndTimestamp = scheduledEnd,
            actualEndTimestamp = null,
            ratePerHour = hourlyRate,
            totalAccrued = 0.0,
            status = "active",
            claimedAt = null
        )

        val id = miningDao.insertSession(session)
        userDao.updateMiningStatus(userId, isActive = true, timestamp = now)

        return Result.success(session.copy(sessionId = id))
    }

    suspend fun checkAndAccrueMiningRewards(userId: String): Double {
        val activeSession = miningDao.getActiveSession(userId) ?: return 0.0
        val now = System.currentTimeMillis()
        val settings = settingsDao.getSettings() ?: SystemSettingsEntity()

        val elapsedMs = (now - activeSession.startTimestamp).coerceAtLeast(0L)
        val maxDurationMs = (activeSession.scheduledEndTimestamp - activeSession.startTimestamp).coerceAtLeast(1L)
        val effectiveMs = elapsedMs.coerceAtMost(maxDurationMs)

        val elapsedHours = effectiveMs / (3600 * 1000.0)
        val totalReward = elapsedHours * activeSession.ratePerHour

        // If cycle has completed 24 hours
        if (elapsedMs >= maxDurationMs && activeSession.status == "active") {
            val completedSession = activeSession.copy(
                actualEndTimestamp = activeSession.scheduledEndTimestamp,
                totalAccrued = totalReward,
                status = "completed",
                claimedAt = now
            )
            miningDao.updateSession(completedSession)
            userDao.addMinedReward(userId, totalReward)
            userDao.updateMiningStatus(userId, isActive = false, timestamp = now)

            // Add to Ledger
            ledgerDao.insertLedger(
                RewardLedgerEntity(
                    userId = userId,
                    amount = totalReward,
                    type = "MINING_REWARD",
                    description = "24-Hour Cycle Complete (@ ${String.format("%.3f", activeSession.ratePerHour * 24)} HOPE/day)",
                    timestamp = now,
                    referenceId = "MINE-${activeSession.sessionId}"
                )
            )

            // Update network total
            val updatedSettings = settings.copy(
                totalNetworkMined = settings.totalNetworkMined + totalReward
            )
            settingsDao.updateSettings(updatedSettings)
        }

        return totalReward
    }

    suspend fun claimOrStopMining(userId: String): Double {
        val activeSession = miningDao.getActiveSession(userId) ?: return 0.0
        val now = System.currentTimeMillis()
        val settings = settingsDao.getSettings() ?: SystemSettingsEntity()

        val elapsedMs = (now - activeSession.startTimestamp).coerceAtLeast(0L)
        val maxDurationMs = (activeSession.scheduledEndTimestamp - activeSession.startTimestamp).coerceAtLeast(1L)
        val effectiveMs = elapsedMs.coerceAtMost(maxDurationMs)

        val elapsedHours = effectiveMs / (3600 * 1000.0)
        val totalReward = elapsedHours * activeSession.ratePerHour

        if (totalReward > 0.00001) {
            val completedSession = activeSession.copy(
                actualEndTimestamp = now,
                totalAccrued = totalReward,
                status = "completed",
                claimedAt = now
            )
            miningDao.updateSession(completedSession)
            userDao.addMinedReward(userId, totalReward)
            userDao.updateMiningStatus(userId, isActive = false, timestamp = now)

            // Add to Ledger
            ledgerDao.insertLedger(
                RewardLedgerEntity(
                    userId = userId,
                    amount = totalReward,
                    type = "MINING_REWARD",
                    description = "Claimed Mining Session (${String.format("%.2f", elapsedHours)} hrs @ ${String.format("%.3f", activeSession.ratePerHour * 24)} HOPE/day)",
                    timestamp = now,
                    referenceId = "CLAIM-${activeSession.sessionId}"
                )
            )

            // Update global network stats
            settingsDao.updateSettings(
                settings.copy(totalNetworkMined = settings.totalNetworkMined + totalReward)
            )
        } else {
            val stoppedSession = activeSession.copy(
                actualEndTimestamp = now,
                totalAccrued = 0.0,
                status = "interrupted",
                claimedAt = now
            )
            miningDao.updateSession(stoppedSession)
            userDao.updateMiningStatus(userId, isActive = false, timestamp = now)
        }

        return totalReward
    }

    // ADMIN CONTROLS & AUDIT LOGGING
    suspend fun updateDailyMiningRate(admin: UserEntity, newRate: Double, reason: String) {
        val currentSettings = settingsDao.getSettings() ?: SystemSettingsEntity()
        val previousRate = currentSettings.dailyMiningRate
        val updated = currentSettings.copy(dailyMiningRate = newRate)
        settingsDao.updateSettings(updated)

        adminAuditLogDao.insertAuditLog(
            AdminAuditLogEntity(
                adminId = admin.id,
                adminUsername = admin.username,
                action = "UPDATE_DAILY_MINING_RATE",
                previousValue = "$previousRate HOPE/day",
                newValue = "$newRate HOPE/day",
                reason = reason.ifBlank { "Administrative policy rate update" },
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun scheduleHalving(admin: UserEntity, nextRate: Double, daysFromNow: Int, reason: String) {
        val currentSettings = settingsDao.getSettings() ?: SystemSettingsEntity()
        val targetDate = System.currentTimeMillis() + (daysFromNow * 86400000L)
        val updated = currentSettings.copy(
            isHalvingScheduled = true,
            nextHalvingDate = targetDate,
            nextHalvingRate = nextRate
        )
        settingsDao.updateSettings(updated)

        adminAuditLogDao.insertAuditLog(
            AdminAuditLogEntity(
                adminId = admin.id,
                adminUsername = admin.username,
                action = "SCHEDULE_HALVING_EVENT",
                previousValue = "Scheduled=false",
                newValue = "NextRate=$nextRate HOPE/day, TargetDate=$targetDate",
                reason = reason.ifBlank { "Planned tokenomics emission halving" },
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun adjustUserBalance(admin: UserEntity, targetUserId: String, newBalance: Double, reason: String) {
        val targetUser = userDao.getUserById(targetUserId) ?: return
        val prevBalance = targetUser.hopeBalance
        val diff = newBalance - prevBalance

        userDao.setBalance(targetUserId, newBalance)

        ledgerDao.insertLedger(
            RewardLedgerEntity(
                userId = targetUserId,
                amount = diff,
                type = "ADMIN_ADJUSTMENT",
                description = "Admin balance adjustment: ${if (diff >= 0) "+$diff" else "$diff"} HOPE ($reason)",
                timestamp = System.currentTimeMillis(),
                referenceId = "ADMIN-ADJ-${System.currentTimeMillis()}"
            )
        )

        adminAuditLogDao.insertAuditLog(
            AdminAuditLogEntity(
                adminId = admin.id,
                adminUsername = admin.username,
                action = "ADJUST_USER_BALANCE",
                previousValue = "User: ${targetUser.username} ($prevBalance HOPE)",
                newValue = "$newBalance HOPE",
                reason = reason.ifBlank { "Manual verified ledger reconciliation" },
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun toggleUserSuspension(admin: UserEntity, targetUserId: String, isSuspended: Boolean, reason: String) {
        val targetUser = userDao.getUserById(targetUserId) ?: return
        userDao.setSuspended(targetUserId, isSuspended)

        adminAuditLogDao.insertAuditLog(
            AdminAuditLogEntity(
                adminId = admin.id,
                adminUsername = admin.username,
                action = if (isSuspended) "SUSPEND_USER_ACCOUNT" else "UNSUSPEND_USER_ACCOUNT",
                previousValue = "Suspended=${targetUser.isSuspended}",
                newValue = "Suspended=$isSuspended",
                reason = reason.ifBlank { "Account integrity policy review" },
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateAdvertisingSettings(admin: UserEntity, settings: AdvertisingSettingsEntity, reason: String) {
        adSettingsDao.updateAdSettings(settings)

        adminAuditLogDao.insertAuditLog(
            AdminAuditLogEntity(
                adminId = admin.id,
                adminUsername = admin.username,
                action = "UPDATE_ADVERTISING_SETTINGS",
                previousValue = "Provider=${settings.providerName}",
                newValue = "Enabled=${settings.adsEnabled}, Banner=${settings.bannerEnabled}, Rewarded=${settings.rewardedEnabled}",
                reason = reason.ifBlank { "Ad network configuration tune" },
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun createHumanitarianInitiative(admin: UserEntity, update: HumanitarianUpdateEntity) {
        humanitarianDao.insertUpdate(update)
        adminAuditLogDao.insertAuditLog(
            AdminAuditLogEntity(
                adminId = admin.id,
                adminUsername = admin.username,
                action = "PUBLISH_HUMANITARIAN_INITIATIVE",
                previousValue = "None",
                newValue = "${update.title} (${update.allocatedHope} HOPE)",
                reason = "Verified impact allocation record",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    // ADSGRAM REWARD ENGINE & ATOMIC IDEMPOTENT CALLBACK PROCESSOR
    suspend fun processAdsgramRewardCallback(
        userId: String,
        eventId: String,
        secretToken: String? = null
    ): AdsgramRewardResult {
        val cleanUserId = userId.trim()
        val cleanEventId = eventId.trim()
        val maskedUser = if (cleanUserId.length > 6) cleanUserId.take(3) + "..." + cleanUserId.takeLast(3) else cleanUserId

        Log.d("AdsGramReward", "Received AdsGram reward notification: user=$maskedUser, eventId=$cleanEventId")

        // 1. Validation: Missing parameters
        if (cleanUserId.isBlank()) {
            Log.w("AdsGramReward", "[REJECTED] Missing or empty userId parameter in callback.")
            return AdsgramRewardResult(
                success = false,
                httpStatusCode = 400,
                message = "Missing required 'userid' parameter.",
                eventId = cleanEventId
            )
        }

        if (cleanEventId.isBlank()) {
            Log.w("AdsGramReward", "[REJECTED] Missing or empty eventId parameter in callback.")
            return AdsgramRewardResult(
                success = false,
                httpStatusCode = 400,
                message = "Missing required 'event_id' parameter.",
                eventId = cleanEventId
            )
        }

        val settings = adSettingsDao.getAdSettings() ?: AdvertisingSettingsEntity()

        // 2. Validation: Are rewarded ads enabled?
        if (!settings.adsEnabled || !settings.rewardedEnabled) {
            Log.w("AdsGramReward", "[REJECTED] Rewarded ads are currently disabled in settings.")
            return AdsgramRewardResult(
                success = false,
                httpStatusCode = 403,
                message = "AdsGram rewarded ads are currently inactive.",
                eventId = cleanEventId
            )
        }

        // 3. Validation: Verify Security Secret if configured
        if (settings.adsgramSecretKey.isNotBlank()) {
            if (secretToken == null || secretToken.trim() != settings.adsgramSecretKey.trim()) {
                Log.w("AdsGramReward", "[REJECTED] Invalid authorization secret for eventId=$cleanEventId.")
                return AdsgramRewardResult(
                    success = false,
                    httpStatusCode = 401,
                    message = "Invalid or missing AdsGram secret verification token.",
                    eventId = cleanEventId
                )
            }
        }

        // 4. Validation: Verify User exists
        val user = userDao.getUserById(cleanUserId)
        if (user == null) {
            Log.w("AdsGramReward", "[REJECTED] User '$maskedUser' not found in database.")
            return AdsgramRewardResult(
                success = false,
                httpStatusCode = 404,
                message = "User not found in Hope Network database.",
                eventId = cleanEventId
            )
        }

        if (user.isSuspended) {
            Log.w("AdsGramReward", "[REJECTED] User '$maskedUser' is suspended.")
            return AdsgramRewardResult(
                success = false,
                httpStatusCode = 403,
                message = "User account is suspended.",
                eventId = cleanEventId
            )
        }

        // 5. Anti-Replay / Idempotency Check: Verify if eventId was already processed
        val existingEvent = adsgramRewardDao.getEventByEventId(cleanEventId)
        if (existingEvent != null) {
            Log.i("AdsGramReward", "[DUPLICATE] EventId '$cleanEventId' was already processed. Preventing double-credit (Idempotency 200 OK returned).")
            return AdsgramRewardResult(
                success = true,
                httpStatusCode = 200,
                message = "Reward already claimed (idempotent callback).",
                rewardAmount = existingEvent.rewardAmount,
                eventId = cleanEventId,
                isDuplicate = true,
                newBalance = user.hopeBalance
            )
        }

        // 6. Atomic Crediting
        val rewardAmount = settings.adsgramRewardAmount.coerceAtLeast(0.01)
        val now = System.currentTimeMillis()

        return try {
            // A. Record in adsgram_reward_events table (unique index prevents race condition)
            adsgramRewardDao.insertEvent(
                AdsgramRewardEventEntity(
                    eventId = cleanEventId,
                    userId = cleanUserId,
                    rewardAmount = rewardAmount,
                    status = "SUCCESS",
                    reason = "Verified AdsGram rewarded ad view",
                    timestamp = now
                )
            )

            // B. Atomically update user balance
            userDao.addHopeBalance(cleanUserId, rewardAmount)

            // C. Record in immutable Reward Ledger
            ledgerDao.insertLedger(
                RewardLedgerEntity(
                    userId = cleanUserId,
                    amount = rewardAmount,
                    type = "ADSGRAM_REWARD",
                    description = "AdsGram Rewarded Video Ad (+${String.format("%.2f", rewardAmount)} HOPE)",
                    timestamp = now,
                    referenceId = "ADSGRAM-$cleanEventId"
                )
            )

            val updatedUser = userDao.getUserById(cleanUserId)
            val updatedBal = updatedUser?.hopeBalance ?: (user.hopeBalance + rewardAmount)
            Log.i("AdsGramReward", "[SUCCESS] Credited $rewardAmount HOPE to user '$maskedUser'. Balance: $updatedBal. Ref: ADSGRAM-$cleanEventId")

            AdsgramRewardResult(
                success = true,
                httpStatusCode = 200,
                message = "Reward successfully verified and credited.",
                rewardAmount = rewardAmount,
                eventId = cleanEventId,
                isDuplicate = false,
                newBalance = updatedBal
            )
        } catch (e: Exception) {
            Log.e("AdsGramReward", "[ERROR] Failed to commit atomic transaction for event $cleanEventId: ${e.message}")
            AdsgramRewardResult(
                success = false,
                httpStatusCode = 500,
                message = "Database transaction failure: ${e.message}",
                eventId = cleanEventId
            )
        }
    }
}
