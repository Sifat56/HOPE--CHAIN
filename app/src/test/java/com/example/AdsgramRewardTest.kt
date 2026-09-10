package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.AdvertisingSettingsEntity
import com.example.data.db.HopeDatabase
import com.example.data.db.UserEntity
import com.example.data.repository.HopeRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AdsgramRewardTest {

    private lateinit var database: HopeDatabase
    private lateinit var repository: HopeRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, HopeDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = HopeRepository(
            userDao = database.userDao(),
            miningDao = database.miningSessionDao(),
            ledgerDao = database.rewardLedgerDao(),
            settingsDao = database.systemSettingsDao(),
            adminAuditLogDao = database.adminAuditLogDao(),
            adSettingsDao = database.advertisingSettingsDao(),
            humanitarianDao = database.humanitarianUpdateDao(),
            announcementDao = database.announcementDao(),
            adsgramRewardDao = database.adsgramRewardEventDao()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `test successful adsgram reward increases balance and writes ledger`() = runBlocking {
        val user = UserEntity(
            id = "tg_user_123",
            username = "telegram_miner",
            email = "tg123@t.me",
            referralCode = "REF123",
            hopeBalance = 10.0
        )
        database.userDao().insertUser(user)

        val adSettings = AdvertisingSettingsEntity(
            adsEnabled = true,
            rewardedEnabled = true,
            adsgramBlockId = "47226",
            adsgramRewardAmount = 0.50,
            adsgramSecretKey = "super_secret_test_key"
        )
        database.advertisingSettingsDao().insertAdSettings(adSettings)

        // Process first valid reward callback
        val result = repository.processAdsgramRewardCallback(
            userId = "tg_user_123",
            eventId = "evt_unique_001",
            secretToken = "super_secret_test_key"
        )

        assertTrue(result.success)
        assertEquals(200, result.httpStatusCode)
        assertEquals(0.50, result.rewardAmount, 0.001)

        // Verify balance updated atomically to 10.50
        val updatedUser = database.userDao().getUserById("tg_user_123")
        assertEquals(10.50, updatedUser?.hopeBalance ?: 0.0, 0.001)

        // Verify event logged as SUCCESS
        val event = database.adsgramRewardEventDao().getEventByEventId("evt_unique_001")
        assertEquals("SUCCESS", event?.status)

        // Verify ledger entry
        val ledger = database.rewardLedgerDao().getLedgerForUser("tg_user_123").first()
        assertEquals(1, ledger.size)
        assertEquals("ADSGRAM_REWARD", ledger[0].type)
        assertEquals(0.50, ledger[0].amount, 0.001)
    }

    @Test
    fun `test replay attack with duplicate eventId is rejected and does not double credit`() = runBlocking {
        val user = UserEntity(
            id = "tg_user_456",
            username = "cryptominer",
            email = "tg456@t.me",
            referralCode = "REF456",
            hopeBalance = 5.0
        )
        database.userDao().insertUser(user)

        val adSettings = AdvertisingSettingsEntity(
            adsEnabled = true,
            rewardedEnabled = true,
            adsgramRewardAmount = 0.25,
            adsgramSecretKey = "test_key"
        )
        database.advertisingSettingsDao().insertAdSettings(adSettings)

        // First call
        val firstResult = repository.processAdsgramRewardCallback(
            userId = "tg_user_456",
            eventId = "evt_replay_test_999",
            secretToken = "test_key"
        )
        assertTrue(firstResult.success)

        // Second call with IDENTICAL eventId (Replay attack simulation)
        val duplicateResult = repository.processAdsgramRewardCallback(
            userId = "tg_user_456",
            eventId = "evt_replay_test_999",
            secretToken = "test_key"
        )

        assertTrue(duplicateResult.isDuplicate)
        assertEquals(200, duplicateResult.httpStatusCode)
        assertTrue(duplicateResult.message.contains("already claimed", ignoreCase = true))

        // Balance MUST STILL BE 5.25, not 5.50
        val updatedUser = database.userDao().getUserById("tg_user_456")
        assertEquals(5.25, updatedUser?.hopeBalance ?: 0.0, 0.001)

        // Ledger must have only 1 entry
        val ledger = database.rewardLedgerDao().getLedgerForUser("tg_user_456").first()
        assertEquals(1, ledger.size)
    }

    @Test
    fun `test invalid secret token returns 401 Unauthorized`() = runBlocking {
        val user = UserEntity(
            id = "tg_user_789",
            username = "testuser",
            email = "tg789@t.me",
            referralCode = "REF789",
            hopeBalance = 1.0
        )
        database.userDao().insertUser(user)

        val adSettings = AdvertisingSettingsEntity(
            adsEnabled = true,
            rewardedEnabled = true,
            adsgramSecretKey = "correct_secret"
        )
        database.advertisingSettingsDao().insertAdSettings(adSettings)

        val result = repository.processAdsgramRewardCallback(
            userId = "tg_user_789",
            eventId = "evt_invalid_secret_1",
            secretToken = "wrong_secret"
        )

        assertFalse(result.success)
        assertEquals(401, result.httpStatusCode)
        assertTrue(result.message.contains("Invalid", ignoreCase = true))

        // Balance unchanged
        val updatedUser = database.userDao().getUserById("tg_user_789")
        assertEquals(1.0, updatedUser?.hopeBalance ?: 0.0, 0.001)
    }

    @Test
    fun `test suspended user callback is rejected`() = runBlocking {
        val user = UserEntity(
            id = "tg_user_suspended",
            username = "banned_user",
            email = "banned@t.me",
            referralCode = "REFBAN",
            hopeBalance = 0.0,
            isSuspended = true
        )
        database.userDao().insertUser(user)

        val adSettings = AdvertisingSettingsEntity(
            adsEnabled = true,
            rewardedEnabled = true,
            adsgramSecretKey = "key"
        )
        database.advertisingSettingsDao().insertAdSettings(adSettings)

        val result = repository.processAdsgramRewardCallback(
            userId = "tg_user_suspended",
            eventId = "evt_suspended_1",
            secretToken = "key"
        )

        assertFalse(result.success)
        assertEquals(403, result.httpStatusCode)
        assertTrue(result.message.contains("suspended", ignoreCase = true))
    }
}
