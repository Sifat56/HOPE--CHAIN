package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserByIdFlow(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE referralCode = :code LIMIT 1")
    suspend fun getUserByReferralCode(code: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isMiningActive = :isActive, lastMiningTimestamp = :timestamp WHERE id = :userId")
    suspend fun updateMiningStatus(userId: String, isActive: Boolean, timestamp: Long)

    @Query("UPDATE users SET hopeBalance = hopeBalance + :amount, totalMined = totalMined + :amount WHERE id = :userId")
    suspend fun addMinedReward(userId: String, amount: Double)

    @Query("UPDATE users SET hopeBalance = hopeBalance + :amount WHERE id = :userId")
    suspend fun addHopeBalance(userId: String, amount: Double)

    @Query("UPDATE users SET hopeBalance = :newBalance WHERE id = :userId")
    suspend fun setBalance(userId: String, newBalance: Double)

    @Query("UPDATE users SET isSuspended = :isSuspended WHERE id = :userId")
    suspend fun setSuspended(userId: String, isSuspended: Boolean)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Query("SELECT COUNT(*) FROM users WHERE isMiningActive = 1")
    fun getActiveMinersCountFlow(): Flow<Int>
}

@Dao
interface MiningSessionDao {
    @Query("SELECT * FROM mining_sessions WHERE userId = :userId ORDER BY startTimestamp DESC")
    fun getSessionsForUser(userId: String): Flow<List<MiningSessionEntity>>

    @Query("SELECT * FROM mining_sessions WHERE userId = :userId AND status = 'active' ORDER BY startTimestamp DESC LIMIT 1")
    suspend fun getActiveSession(userId: String): MiningSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: MiningSessionEntity): Long

    @Update
    suspend fun updateSession(session: MiningSessionEntity)

    @Query("SELECT SUM(totalAccrued) FROM mining_sessions")
    fun getTotalMinedNetworkFlow(): Flow<Double?>
}

@Dao
interface RewardLedgerDao {
    @Query("SELECT * FROM reward_ledger WHERE userId = :userId ORDER BY timestamp DESC")
    fun getLedgerForUser(userId: String): Flow<List<RewardLedgerEntity>>

    @Query("SELECT * FROM reward_ledger ORDER BY timestamp DESC LIMIT 50")
    fun getAllRecentLedger(): Flow<List<RewardLedgerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLedger(entry: RewardLedgerEntity)
}

@Dao
interface SystemSettingsDao {
    @Query("SELECT * FROM system_settings WHERE id = 1")
    fun getSettingsFlow(): Flow<SystemSettingsEntity?>

    @Query("SELECT * FROM system_settings WHERE id = 1")
    suspend fun getSettings(): SystemSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: SystemSettingsEntity)

    @Update
    suspend fun updateSettings(settings: SystemSettingsEntity)
}

@Dao
interface AdminAuditLogDao {
    @Query("SELECT * FROM admin_audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AdminAuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AdminAuditLogEntity)
}

@Dao
interface AdvertisingSettingsDao {
    @Query("SELECT * FROM advertising_settings WHERE id = 1")
    fun getAdSettingsFlow(): Flow<AdvertisingSettingsEntity?>

    @Query("SELECT * FROM advertising_settings WHERE id = 1")
    suspend fun getAdSettings(): AdvertisingSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdSettings(settings: AdvertisingSettingsEntity)

    @Update
    suspend fun updateAdSettings(settings: AdvertisingSettingsEntity)
}

@Dao
interface HumanitarianUpdateDao {
    @Query("SELECT * FROM humanitarian_updates ORDER BY id DESC")
    fun getAllUpdates(): Flow<List<HumanitarianUpdateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdate(update: HumanitarianUpdateEntity)
}

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcements ORDER BY timestamp DESC")
    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementEntity)
}

@Dao
interface AdsgramRewardEventDao {
    @Query("SELECT * FROM adsgram_reward_events WHERE eventId = :eventId LIMIT 1")
    suspend fun getEventByEventId(eventId: String): AdsgramRewardEventEntity?

    @Query("SELECT * FROM adsgram_reward_events ORDER BY timestamp DESC")
    fun getAllEventsFlow(): Flow<List<AdsgramRewardEventEntity>>

    @Query("SELECT * FROM adsgram_reward_events ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentEventsFlow(limit: Int = 50): Flow<List<AdsgramRewardEventEntity>>

    @Query("SELECT * FROM adsgram_reward_events WHERE userId = :userId ORDER BY timestamp DESC")
    fun getEventsForUserFlow(userId: String): Flow<List<AdsgramRewardEventEntity>>

    @Query("SELECT COUNT(*) FROM adsgram_reward_events WHERE userId = :userId AND status = 'SUCCESS'")
    suspend fun countSuccessfulEventsForUser(userId: String): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertEvent(event: AdsgramRewardEventEntity): Long
}

