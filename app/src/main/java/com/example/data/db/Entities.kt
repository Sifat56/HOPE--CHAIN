package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val email: String,
    val role: String = "user", // "user" | "admin"
    val hopeBalance: Double = 0.0,
    val totalMined: Double = 0.0,
    val referralCode: String,
    val referredBy: String? = null,
    val totalReferrals: Int = 0,
    val referralEarnings: Double = 0.0,
    val lastMiningTimestamp: Long = 0L,
    val isMiningActive: Boolean = false,
    val miningDurationHours: Int = 24,
    val isVIPUser: Boolean = false,
    val isSuspended: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "mining_sessions")
data class MiningSessionEntity(
    @PrimaryKey(autoGenerate = true) val sessionId: Long = 0L,
    val userId: String,
    val startTimestamp: Long,
    val scheduledEndTimestamp: Long,
    val actualEndTimestamp: Long? = null,
    val ratePerHour: Double,
    val totalAccrued: Double,
    val status: String, // "active" | "completed" | "interrupted"
    val claimedAt: Long? = null
)

@Entity(tableName = "reward_ledger")
data class RewardLedgerEntity(
    @PrimaryKey(autoGenerate = true) val ledgerId: Long = 0L,
    val userId: String,
    val amount: Double,
    val type: String, // "MINING_REWARD" | "REFERRAL_BONUS" | "ADMIN_ADJUSTMENT" | "SYSTEM_REDUCTION"
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val referenceId: String = ""
)

@Entity(tableName = "system_settings")
data class SystemSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val dailyMiningRate: Double = 3.0, // 3.0 HOPE/day = 0.125 HOPE/hour
    val referralBonusRate: Double = 0.5,
    val totalNetworkMined: Double = 42890.5,
    val totalRegisteredUsers: Int = 12480,
    val humanitarianFundAllocation: Double = 15000000.0, // 20% of 75M
    val totalTokenSupply: Double = 75000000.0,
    val miningSupplyCap: Double = 30000000.0, // 40% of 75M
    val isHalvingScheduled: Boolean = false,
    val nextHalvingDate: Long = 0L,
    val nextHalvingRate: Double = 1.5
)

@Entity(tableName = "admin_audit_logs")
data class AdminAuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val adminId: String,
    val adminUsername: String,
    val action: String,
    val previousValue: String,
    val newValue: String,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "advertising_settings")
data class AdvertisingSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val adsEnabled: Boolean = true,
    val providerName: String = "AdsGram & Google AdSense",
    val publisherId: String = "pub-hope-preview-env",
    val bannerEnabled: Boolean = true,
    val nativeEnabled: Boolean = true,
    val interstitialEnabled: Boolean = false,
    val rewardedEnabled: Boolean = true, // Enabled for AdsGram rewarded video ads
    val frequencyLimitPerHour: Int = 3,
    val adsgramBlockId: String = "47226",
    val adsgramRewardAmount: Double = 0.25, // Configurable HOPE reward per completed ad
    val adsgramSecretKey: String = "hope_sec_live_9981",
    val adsgramWebhookUrl: String = "https://api.hope-network.app/api/adsgram/reward?userid=[userId]&secret=hope_sec_live_9981&event_id=[eventId]"
)

@Entity(tableName = "humanitarian_updates")
data class HumanitarianUpdateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val category: String, // "Disaster Relief", "Education", "Food Support", "Healthcare"
    val allocatedHope: Double,
    val status: String, // "Planned", "Active", "Verified Complete"
    val description: String,
    val date: String
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val content: String,
    val tag: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "adsgram_reward_events",
    indices = [androidx.room.Index(value = ["eventId"], unique = true)]
)
data class AdsgramRewardEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val eventId: String, // Unique transaction ID for idempotency & replay protection
    val userId: String,
    val rewardAmount: Double,
    val status: String, // "SUCCESS" | "DUPLICATE" | "REJECTED" | "INVALID"
    val reason: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
