package com.example.bendrenagarasarige.Controllers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.bendrenagarasarige.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*

class SettingsActivity : AppCompatActivity() {


    var lang = "en"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        title = "Settings"

        loadLocate()

        val adapter = ArrayAdapter(
            this,
            R.layout.support_simple_spinner_dropdown_item,resources.getStringArray(
                R.array.langs
            )
        )

        langSpinnerSettings.adapter = adapter

        langSpinnerSettings.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {

                when(position){

                    0 -> lang = "en"
                    1 -> lang = "kn"
                }


            }

            override fun onNothingSelected(parent: AdapterView<*>) {



            }


        }


        saveBtnSettings.setOnClickListener {

            setLocate(lang)

            if(FirebaseAuth.getInstance().currentUser!!.email == "conductor123@bendre.com") {

                val intent = Intent(baseContext, ConductorMainActivity::class.java)
                finishAffinity()
                startActivity(intent)

            }else{

                val intent = Intent(baseContext, MainActivity::class.java)
                finishAffinity()
                startActivity(intent)

            }

        }

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

    fun loadLocate() {

        val sharedPreferences = getSharedPreferences ("Settings", Activity.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "")
        setLocate(language!!)

    }


}
