package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        MiningSessionEntity::class,
        RewardLedgerEntity::class,
        SystemSettingsEntity::class,
        AdminAuditLogEntity::class,
        AdvertisingSettingsEntity::class,
        HumanitarianUpdateEntity::class,
        AnnouncementEntity::class,
        AdsgramRewardEventEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class HopeDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun miningSessionDao(): MiningSessionDao
    abstract fun rewardLedgerDao(): RewardLedgerDao
    abstract fun systemSettingsDao(): SystemSettingsDao
    abstract fun adminAuditLogDao(): AdminAuditLogDao
    abstract fun advertisingSettingsDao(): AdvertisingSettingsDao
    abstract fun humanitarianUpdateDao(): HumanitarianUpdateDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun adsgramRewardEventDao(): AdsgramRewardEventDao

    companion object {
        @Volatile
        private var INSTANCE: HopeDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): HopeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HopeDatabase::class.java,
                    "hope_network_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            suspend fun populateInitialData(db: HopeDatabase) {
                // 1. Initial System Settings
                db.systemSettingsDao().insertSettings(
                    SystemSettingsEntity(
                        id = 1,
                        dailyMiningRate = 3.0,
                        referralBonusRate = 0.5,
                        totalNetworkMined = 54200.0,
                        totalRegisteredUsers = 15840,
                        humanitarianFundAllocation = 15000000.0,
                        totalTokenSupply = 75000000.0,
                        miningSupplyCap = 30000000.0,
                        isHalvingScheduled = false,
                        nextHalvingDate = 0L,
                        nextHalvingRate = 1.5
                    )
                )

                // 2. Initial Advertising Settings
                db.advertisingSettingsDao().insertAdSettings(
                    AdvertisingSettingsEntity(
                        id = 1,
                        adsEnabled = true,
                        providerName = "Google AdSense Ready",
                        publisherId = "pub-hope-3829104",
                        bannerEnabled = true,
                        nativeEnabled = true,
                        interstitialEnabled = false,
                        rewardedEnabled = false, // strictly disabled per policy by default
                        frequencyLimitPerHour = 3
                    )
                )

                // 3. Initial Demo Accounts (Admin + Pioneer User)
                val adminUser = UserEntity(
                    id = "admin_root_001",
                    username = "HopeArchitect",
                    email = "admin@hopenetwork.io",
                    role = "admin",
                    hopeBalance = 150.0,
                    totalMined = 150.0,
                    referralCode = "HOPEADMIN",
                    referredBy = null,
                    totalReferrals = 12,
                    referralEarnings = 6.0,
                    isVIPUser = true,
                    isMiningActive = false,
                    createdAt = System.currentTimeMillis() - 86400000L * 15
                )
                db.userDao().insertUser(adminUser)

                val defaultUser = UserEntity(
                    id = "user_pioneer_002",
                    username = "PioneerMiner",
                    email = "miner@hopenetwork.io",
                    role = "user",
                    hopeBalance = 18.75,
                    totalMined = 18.75,
                    referralCode = "HOPE7799",
                    referredBy = "HOPEADMIN",
                    totalReferrals = 3,
                    referralEarnings = 1.5,
                    isVIPUser = false,
                    isMiningActive = false,
                    createdAt = System.currentTimeMillis() - 86400000L * 6
                )
                db.userDao().insertUser(defaultUser)

                // Initial ledger entry
                db.rewardLedgerDao().insertLedger(
                    RewardLedgerEntity(
                        userId = "user_pioneer_002",
                        amount = 18.75,
                        type = "MINING_REWARD",
                        description = "Genesis Mining Cycle Rewards (6 Cycles @ 3.0 HOPE/day + referral)",
                        timestamp = System.currentTimeMillis() - 86400000L * 1,
                        referenceId = "GEN-002"
                    )
                )

                // Initial audit log
                db.adminAuditLogDao().insertAuditLog(
                    AdminAuditLogEntity(
                        adminId = "admin_root_001",
                        adminUsername = "HopeArchitect",
                        action = "GENESIS_SYSTEM_INIT",
                        previousValue = "0",
                        newValue = "DailyRate=3.0, MaxCap=30,000,000 HOPE",
                        reason = "HOPE Network Phase 1 Testnet Deployment",
                        timestamp = System.currentTimeMillis() - 86400000L * 15
                    )
                )

                // 4. Initial Humanitarian Initiatives
                db.humanitarianUpdateDao().insertUpdate(
                    HumanitarianUpdateEntity(
                        title = "Clean Water Solar Well Initiative",
                        category = "Essential Assistance",
                        allocatedHope = 500000.0,
                        status = "Planned",
                        description = "Reserving 500,000 HOPE testnet allocation towards sustainable solar groundwater filtration systems in drylands.",
                        date = "Phase 1 - Q4"
                    )
                )
                db.humanitarianUpdateDao().insertUpdate(
                    HumanitarianUpdateEntity(
                        title = "Global Emergency Food Relief Vault",
                        category = "Food Support",
                        allocatedHope = 1200000.0,
                        status = "Active",
                        description = "Dedicated 1,200,000 HOPE reserve earmarked for rapid disaster assistance & food pantry networks.",
                        date = "Phase 1 - Ongoing"
                    )
                )
                db.humanitarianUpdateDao().insertUpdate(
                    HumanitarianUpdateEntity(
                        title = "Open Web3 Education & Digital Literacy",
                        category = "Education",
                        allocatedHope = 350000.0,
                        status = "Active",
                        description = "Funding developer grants and digital literacy programs for underserved community builders.",
                        date = "Phase 2 Target"
                    )
                )

                // 5. Initial Announcements
                db.announcementDao().insertAnnouncement(
                    AnnouncementEntity(
                        title = "HOPE Network Phase 1 Testnet Live",
                        content = "Welcome pioneers! Mining at 3.0 HOPE/day is now live. Share your link and build the decentralized impact ecosystem.",
                        tag = "SYSTEM",
                        timestamp = System.currentTimeMillis() - 86400000L * 2
                    )
                )
                db.announcementDao().insertAnnouncement(
                    AnnouncementEntity(
                        title = "Humanitarian Fund 20% Allocation Locked",
                        content = "15,000,000 \$HOPE strictly allocated for global social impact and emergency relief reserves.",
                        tag = "IMPACT",
                        timestamp = System.currentTimeMillis() - 86400000L * 5
                    )
                )
            }
        }
    }
}
