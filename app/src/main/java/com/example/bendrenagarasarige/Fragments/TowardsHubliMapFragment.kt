package com.example.bendrenagarasarige.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bendrenagarasarige.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_towards_hubli.*
import java.util.*
import kotlin.collections.HashMap

class TowardsHubliMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    private val mMarkers = HashMap<String, Marker>()

    var dbref: DatabaseReference = FirebaseDatabase.getInstance().getReference("towardsHubliLocation")



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mapViewTowardsHubli.onCreate(savedInstanceState)
        mapViewTowardsHubli.onResume()

        mapViewTowardsHubli.getMapAsync(this)

    }

    override fun onMapReady(map: GoogleMap?) {

        map!!.let {

            googleMap = it

            googleMap.isMyLocationEnabled = true
         //googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            googleMap.setMaxZoomPreference(16f)

            listenToChild()


        }


    }

    fun listenToChild(){


        dbref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                if(p0.exists()){


                    Log.d("Map","OnChangedFired")
                    setupMarker(p0)

                }


            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                if(p0.exists()){

                    setupMarker(p0)

                }

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }


        })



        /*    val sydney = LatLng(-34.0, 151.0)
            googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney)) */

    }

    fun addMarker(dataSnapshot: DataSnapshot){

        var value = dataSnapshot.getValue() as HashMap<String, Any>
        var latitude = java.lang.Double.parseDouble(value.get("latitude").toString())
        var longitude = java.lang.Double.parseDouble(value.get("longitude").toString())


        val place = LatLng(latitude, longitude)
        googleMap.addMarker(
            MarkerOptions().position(place).title(dataSnapshot.key).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.bus)))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(place))



    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = layoutInflater.inflate(R.layout.fragment_towards_hubli,null)
        return view

    }



    fun setupMarker(dataSnapshot: DataSnapshot){

        val key = dataSnapshot.getKey()
        val value = dataSnapshot.getValue() as HashMap<String, Any>
        val lat = java.lang.Double.parseDouble(value.get("latitude").toString())
        val lng = java.lang.Double.parseDouble(value.get("longitude").toString())
        val location = LatLng(lat, lng)
        if (!mMarkers.containsKey(key))
        {
            mMarkers.put(key!!, googleMap.addMarker(
                MarkerOptions().title(key).position(location).icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.bus))))
        }
        else
        {
            Objects.requireNonNull(mMarkers[key])!!.setPosition(location)
        }
        val builder = LatLngBounds.Builder()
        for (marker in mMarkers.values)
        {
            builder.include(marker.getPosition())
        }



        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300))


    }


}