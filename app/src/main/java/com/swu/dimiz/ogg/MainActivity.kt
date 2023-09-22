package com.swu.dimiz.ogg

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.swu.dimiz.ogg.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration : AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate()")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)

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
        //setupActionBarWithNavController(navController, appBarConfiguration)
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
        Timber.i("onStart()")
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume()")
    }

    override fun onPause() {
        super.onPause()
        Timber.i("onPause()")
    }

    override fun onStop() {
        super.onStop()
        Timber.i("onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy()")
    }
}
