package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityShoppingBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShoppingBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        drawerLayout = binding.drawerLayout
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        navController = Navigation.findNavController(this, R.id.hostFragment)

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        binding.navigationView.setupWithNavController(navController)

        binding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.profile -> {
                    navController.navigate(R.id.profileFragment)
                }

                R.id.aboutUs -> {
                    navController.navigate(R.id.aboutUsFragment)

                }

                R.id.contactUs -> {
                    navController.navigate(R.id.contactUsFragment)

                }

                R.id.logOut -> {
                    firebaseAuth.signOut()
                    Intent(this, LoginRegisterActivity::class.java).also { intent ->
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }

            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            navController, binding.drawerLayout
        )

    }
}

