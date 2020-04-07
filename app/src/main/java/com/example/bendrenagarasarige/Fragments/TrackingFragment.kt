package com.example.bendrenagarasarige.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bendrenagarasarige.R
import kotlinx.android.synthetic.main.fragment_tracking.view.*


class TrackingFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = layoutInflater.inflate(R.layout.fragment_tracking,null)

        view.towardsHubliRadioBtn.isChecked = true
        loadFragment(TowardsHubliMapFragment())

        view.radiogroup.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId){


                R.id.towardsHubliRadioBtn -> {

                    Toast.makeText(context,"Hubli",Toast.LENGTH_SHORT).show()
                    loadFragment(TowardsHubliMapFragment())

                }

                R.id.towardsDharwadBtn -> {

                    Toast.makeText(context,"Dharwad",Toast.LENGTH_SHORT).show()
                    loadFragment(TowardsDwdMapFragment())

                }


            }


        }

        return view

    }


    fun loadFragment(fragment: Fragment) {
        // load fragment
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.mapsContainer, fragment)
        // transaction.addToBackStack(null)
        transaction.commit()
    }



}