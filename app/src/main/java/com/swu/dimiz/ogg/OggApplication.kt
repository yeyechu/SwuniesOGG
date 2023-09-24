package com.swu.dimiz.ogg

import android.content.Intent
import android.util.Log
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import androidx.core.content.ContextCompat.startActivity
import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.swu.dimiz.ogg.member.login.SignInActivity
import com.swu.dimiz.ogg.oggdata.OggDatabase
import com.swu.dimiz.ogg.oggdata.OggRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

class OggApplication: MultiDexApplication() {

    private val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { OggDatabase.getInstance(this) }
    val repository by lazy { OggRepository(database)}

    companion object {
        // 파이어베이스 인증 상태 확인
        lateinit var auth: FirebaseAuth
        var email: String? = null


            fun checkAuth(): Boolean {
            val currentUser = auth.currentUser

            return currentUser?.let {

                email = currentUser.email

                if(!currentUser.isEmailVerified) {
                        //startActivity(Intent(this, SignInActivity::class.java))
                        //finish()
                    }else{

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