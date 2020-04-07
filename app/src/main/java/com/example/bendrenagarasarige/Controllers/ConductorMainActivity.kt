package com.example.bendrenagarasarige.Controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.bendrenagarasarige.R
import com.example.bendrenagarasarige.Fragments.ConductorScanFragment
import com.example.bendrenagarasarige.Fragments.ConductorTrackFragment
import com.example.bendrenagarasarige.Model.Ticket
import com.example.bendretransportss.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main_conductor.*
import kotlinx.android.synthetic.main.ticket_dilog.view.*
import org.json.JSONException
import org.json.JSONObject

class ConductorMainActivity : AppCompatActivity() {

      lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_conductor)

        mAuth = FirebaseAuth.getInstance()

        conductorBottomNavView.selectedItemId =
            R.id.nav_scan_conductor
        loadFragment(ConductorScanFragment())
        conductorBottomNavView.setOnNavigationItemSelectedListener {


            when (it.itemId){

                R.id.nav_scan_conductor -> {

                    loadFragment(ConductorScanFragment())
                    return@setOnNavigationItemSelectedListener true

                }

                R.id.nav_track -> {

                    loadFragment(ConductorTrackFragment())
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



    fun loadFragment(fragment: Fragment) {
        // load fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.conductorContainer, fragment)
        // transaction.addToBackStack(null)
        transaction.commit()
    }

}
