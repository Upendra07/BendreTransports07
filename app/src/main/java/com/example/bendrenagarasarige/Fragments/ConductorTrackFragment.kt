package com.example.bendrenagarasarige.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bendrenagarasarige.R
import com.example.bendrenagarasarige.Controllers.TrackerActivity
import kotlinx.android.synthetic.main.fragment_track_conductor.view.*

class ConductorTrackFragment: Fragment() {

    lateinit var buses : Array<String>
    companion object {
        fun newInstance() =
            BookTicketFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        buses = resources.getStringArray(R.array.buses)

        val view = layoutInflater.inflate(R.layout.fragment_track_conductor,null)

        view.btn1.setOnClickListener {

            val intent = Intent(context,
                TrackerActivity::class.java)
            startActivity(intent)

        }


   /*     val editor = activity!!.getSharedPreferences("Tracker",Context.MODE_PRIVATE).edit()
        editor.putString("bus", buses[0])
        editor.apply() */

        setupAdapters(view)


        view.radiogroupConductor.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId){


                R.id.towardsHubliRadioBtnConductor -> {

                    Toast.makeText(context,"Hubli", Toast.LENGTH_SHORT).show()
                    val editor = activity!!.getSharedPreferences("Tracker",Context.MODE_PRIVATE).edit()
                    editor.putString("towards", "towardsHubliLocation")
                    editor.apply()

                }

                R.id.towardsDharwadBtnConductor -> {

                    Toast.makeText(context,"Dharwad", Toast.LENGTH_SHORT).show()
                    val editor = activity!!.getSharedPreferences("Tracker",Context.MODE_PRIVATE).edit()
                    editor.putString("towards", "towardsDharwadLocation")
                    editor.apply()

                }


            }


        }


        return view

    }


    fun setupAdapters(view: View) {




        val adapter = ArrayAdapter(
            context!!.applicationContext,
            R.layout.support_simple_spinner_dropdown_item, buses
        )

        view.busNoSpinner.adapter = adapter


        view.busNoSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {

                val editor = activity!!.getSharedPreferences("Tracker",Context.MODE_PRIVATE).edit()
                editor.putString("bus", buses[position])
                editor.apply()

            }

            override fun onNothingSelected(parent: AdapterView<*>) {


            }


        }

    }

}