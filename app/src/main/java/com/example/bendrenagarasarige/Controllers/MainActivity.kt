package com.example.bendrenagarasarige.Controllers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.bendrenagarasarige.*
import com.example.bendrenagarasarige.Fragments.BookTicketFragment
import com.example.bendrenagarasarige.Fragments.ConductorTrackFragment
import com.example.bendrenagarasarige.Fragments.MyTicketFragment
import com.example.bendrenagarasarige.Fragments.TrackingFragment
import com.example.bendretransportss.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {


    lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocate()
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        title=resources.getString(R.string.book_tickets)
        navigationView.selectedItemId =
            R.id.navigation_book_tickets
        loadFragment(BookTicketFragment())


        navigationView.setOnNavigationItemSelectedListener { view ->

            when(view.itemId){

                R.id.navigation_book_tickets -> {
                    title=resources.getString(R.string.book_tickets)
                    loadFragment(BookTicketFragment())
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigation_my_tickets -> {
                    title=resources.getString(R.string.my_tickets)
                    loadFragment(MyTicketFragment())
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigation_my_passes -> {

                    title=resources.getString(R.string.my_passes)
                    loadFragment(ConductorTrackFragment())
                    return@setOnNavigationItemSelectedListener true

                }

                R.id.navigation_map -> {

                    title = resources.getString(R.string.map)
                    loadFragment(TrackingFragment())
                    return@setOnNavigationItemSelectedListener true

                }

            }
            false

        }
    }




    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.menu_settings)
        {

            val intent = Intent(baseContext,
                SettingsActivity::class.java)
            startActivity(intent)
            return true

        }


        if(id == R.id.menu_logout){

            confirmLogout()

        }

        return super.onOptionsItemSelected(item)
    }

    fun loadFragment(fragment: Fragment) {
        // load fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        // transaction.addToBackStack(null)
        transaction.commit()
    }

    fun loadLocate() {

        val sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "")
        setLocate(language!!)

    }


    fun setLocate(Lang: String) {

        val locale = Locale(Lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", Lang)
        editor.apply()

    }

    fun confirmLogout(){

        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.bendre_transports))
        //set message for alert dialog
        builder.setMessage(resources.getString(R.string.confirm_sign_out))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)){ _, _ ->

            mAuth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }
        builder.setNegativeButton(resources.getString(R.string.no)){ _, _ ->

        }

        builder.show()


    }

}
