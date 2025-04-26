package com.reychango.rangolector.data.services

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DilveServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dilveService: DilveService
) {
    private val mutex = Mutex()
    private var cachedToken: String? = null
    private var tokenExpirationTime: Long = 0

    private val encryptedPrefs by lazy {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            "dilve_credentials",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    suspend fun setCredentials(username: String, password: String) {
        encryptedPrefs.edit()
            .putString("username", username)
            .putString("password", password)
            .apply()
    }

    private suspend fun getAuthToken(): String = mutex.withLock {
        val currentTime = System.currentTimeMillis()
        if (cachedToken != null && currentTime < tokenExpirationTime) {
            return@withLock cachedToken!!
        }

        val username = encryptedPrefs.getString("username", null)
        val password = encryptedPrefs.getString("password", null)

        if (username == null || password == null) {
            throw IllegalStateException("Credenciales de DILVE no configuradas")
        }

        val authResponse = dilveService.login(username, password)
        cachedToken = authResponse.token
        tokenExpirationTime = currentTime + (authResponse.expiresIn * 1000)
        return@withLock authResponse.token
    }

    suspend fun searchBooks(query: String): List<SpanishBook> {
        return try {
            val token = getAuthToken()
            dilveService.searchBooks(query, authToken = "Bearer $token")
                .items
                .map { it.toSpanishBook() }
        } catch (e: Exception) {
            emptyList()
        }
    }
} 