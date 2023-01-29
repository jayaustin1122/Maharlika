package com.example.maharlika_app.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.maharlika_app.admin.events.EventAdminFragment
import com.example.maharlika_app.admin.manageAcc.ManageAccountsFragment
import com.example.maharlika_app.admin.news.NewsAdminFragment
import com.example.maharlika_app.auth.LoginActivity
import com.example.maharlika_app.databinding.ActivityAdminHolderBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth

class AdminHolderActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAdminHolderBinding
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHolderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        checkUser()
        val eventAdminFragment = EventAdminFragment()
        val manageAccAdminFragment = ManageAccountsFragment()
        val newsAdminFragment = NewsAdminFragment()

        binding.btnLogoutAdmin.setOnClickListener {
            auth.signOut()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        // to call the initial fragment display in screen
        supportFragmentManager.beginTransaction().apply {
            replace(binding.fragmentMainAdmin.id,eventAdminFragment)
            commit()
        }
        // to bind the table layout in buttons navigations

        binding.tabLayout3.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    //accessing button 1 tab
                    supportFragmentManager.beginTransaction().apply {
                        replace(binding.fragmentMainAdmin.id, eventAdminFragment)
                        // creating a backstack
                        addToBackStack(null)
                        commit()
                    }
                } else if (tab?.position == 1) {
                    //accessing button
                    supportFragmentManager.beginTransaction().apply {
                        replace(binding.fragmentMainAdmin.id, newsAdminFragment)
                        // creating a backstack
                        addToBackStack(null)
                        commit()
                    }
                }
                else if (tab?.position == 2) {
                    //accessing button 2
                    supportFragmentManager.beginTransaction().apply {
                        replace(binding.fragmentMainAdmin.id, manageAccAdminFragment)
                        // creating a backstack
                        addToBackStack(null)
                        commit()
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })


    }
    private fun checkUser() {

        val firebaseUser = auth.currentUser
        if (firebaseUser == null){
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
            Toast.makeText(this,"   ", Toast.LENGTH_SHORT).show()
        }
        else{
            val email = firebaseUser.email
        }
    }
}