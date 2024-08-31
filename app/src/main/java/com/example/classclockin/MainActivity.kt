package com.example.classclockin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.transition.TransitionInflater
import com.example.classclockin.fragments.LoginFragment
import com.example.classclockin.fragments.homeCards.MarkAttendanceFragment

class MainActivity : AppCompatActivity() {

        private lateinit var navController: NavController

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_main)

            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.navHostFragmentContainerView) as NavHostFragment
            navController = navHostFragment.navController

            val inflater = TransitionInflater.from(this)
            val fadeTransition = inflater.inflateTransition(android.R.transition.fade)

            navHostFragment.childFragmentManager.fragmentFactory = object : FragmentFactory() {
                override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                    val fragment = super.instantiate(classLoader, className)
                    fragment.enterTransition = fadeTransition
                    fragment.exitTransition = fadeTransition
                    return fragment
                }
            }

            // Add the fragment to the activity
            if (savedInstanceState == null) {
                // Instead of manual transaction, use NavController for navigation
                navController.navigate(R.id.loginFragment)
            }
        }
        override fun onSupportNavigateUp(): Boolean {
            return navController.navigateUp() || super.onSupportNavigateUp()
        }
    }