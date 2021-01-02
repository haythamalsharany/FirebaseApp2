package com.alsharany.firebaseapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,homePageFragment.GoToAnotherFragment {
    lateinit var draweLayout: DrawerLayout
    lateinit var toolbar: Toolbar
    lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        draweLayout=findViewById(R.id.drawer_layout)
        toolbar=findViewById(R.id.toolbar)
        navigationView=findViewById(R.id.nav_view)
        setSupportActionBar(toolbar)
        val actionBarDrawerToggle= ActionBarDrawerToggle(
            this,
            draweLayout,
            toolbar,
            R.string.open_drawer_content_des,
            R.string.close_drawer_content_des
        )
        val menu=navigationView.menu
        navigationView.bringToFront()
        draweLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)

        val fragment: Fragment =homePageFragment.newInstance()
        setFragment(fragment)

    }

    override fun onBackPressed() {
        if(draweLayout.isDrawerOpen(GravityCompat.START)){
            draweLayout.closeDrawer(GravityCompat.START)
        }
        else
            super.onBackPressed()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
      //  val category=Category(item.itemId,item.title.toString())
        /*val color =when(item.itemId){
            R.id.home-> Color.RED
            R.id.signIn-> Color.BLUE
            R.id.signup-> Color.GREEN
            R.id.signOut-> Color.YELLOW
            else-> Color.CYAN
        }*/



        when(item.itemId){
            R.id.home->{
                setFragment(homePageFragment.newInstance())
            }
            R.id.signIn->{
             setFragment(SignInFragment.newInstance())
            }
            R.id.signOut->{
                LoginPref.setStoredQuery(this,"")

                setFragment(SignInFragment.newInstance())
            }
            R.id.signup->{
                setFragment(SignUpFragment.newInstance())
            }
            else -> {

            }
        }
        draweLayout.closeDrawer(GravityCompat.START)
        return true
    }
    fun setFragment(fragment: Fragment){
        val fragmentManager= supportFragmentManager
        val ft = fragmentManager.beginTransaction()
        ft.replace(R.id.contaner,fragment).addToBackStack(null).commit()
    }

    override fun OnTransferToFragment(fr: Fragment) {
       setFragment(fr)
    }


}


