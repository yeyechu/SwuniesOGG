package com.swu.dimiz.ogg

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.swu.dimiz.ogg.databinding.ActivityMainBinding
import com.swu.dimiz.ogg.member.login.SignInActivity
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration : AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate()")
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)

        mainActivity = this

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavView: BottomNavigationView = binding.bottomNavView
        bottomNavView.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_env, R.id.navigation_myact, R.id.navigation_graph, R.id.navigation_feed
            )
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            //val toolbar = supportActionBar ?: return@addOnDestinationChangedListener
            if(destination.id == R.id.navigation_env
                || destination.id == R.id.navigation_myact
                || destination.id == R.id.navigation_graph
                || destination.id == R.id.navigation_feed
                || destination.id == R.id.navigation_main
            ) {
                bottomNavView.visibility = View.VISIBLE
            } else {
                bottomNavView.visibility = View.GONE
            }
        }
        binding.mainToolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)|| super.onSupportNavigateUp()
    }

    //                              수명 주기 체크
    override fun onStart() {
        super.onStart()

        val currentUser = OggApplication.auth.currentUser
        if (currentUser == null || !FirebaseAuth.getInstance().currentUser!!.isEmailVerified) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }else{
            Timber.i("이미 로그인 되어있습니다. ${currentUser.email.toString()}")
        }
    }

    override fun onDestroy() {
        mainActivity = null
        super.onDestroy()
    }

    fun hideBottomNavView(state: Boolean) {
        if(state) {
            binding.bottomNavView.visibility = View.GONE
        }
        else {
            binding.bottomNavView.visibility = View.VISIBLE
        }
    }

    companion object {
        var mainActivity: MainActivity? = null
    }
}
