package com.swu.dimiz.ogg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.swu.dimiz.ogg.databinding.ActivityMainBinding
import com.swu.dimiz.ogg.member.login.SignInActivity
import com.swu.dimiz.ogg.ui.myact.uploader.CameraActivity
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration : AppBarConfiguration
    private lateinit var navController: NavController

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate()")

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
        return navController.navigateUp(appBarConfiguration)
    }

    //                              수명 주기 체크
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        //auth.signOut() //-> 이동하는지 확인하고 싶으면 로그아웃 하면됨

        val currentUser = OggApplication.auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }else{
            Timber.i( auth.currentUser?.email.toString())
            Timber.i("이미 로그인 되어있습니다.")
        }
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
