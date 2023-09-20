package com.swu.dimiz.ogg

import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.oggdata.OggDatabase
import com.swu.dimiz.ogg.oggdata.OggRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

class OggApplication: MultiDexApplication() {

    private val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { OggDatabase.getInstance(this, applicationScope) }
    val repository by lazy { OggRepository(database)}

    companion object {
        // 파이어베이스 인증 상태 확인
        lateinit var auth: FirebaseAuth
        var email: String? = null

        fun checkAuth(): Boolean {
            val currentUser = auth.currentUser
            return currentUser?.let {

                email = currentUser.email

                if(currentUser.isEmailVerified) {
                    true
                }
                false

            } ?: let {
                false
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        auth = Firebase.auth
    }
}