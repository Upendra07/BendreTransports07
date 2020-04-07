package com.example.bendrenagarasarige.Fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bendrenagarasarige.Controllers.QRActivity
import com.example.bendrenagarasarige.R
import com.example.bendrenagarasarige.Model.Ticket
import com.example.bendrenagarasarige.Adapter.TicketAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_my_tickets.view.*

class MyTicketFragment : Fragment() {

    lateinit var rootRef: DatabaseReference
    lateinit var mAdapter: TicketAdapter
    var tickets: ArrayList<Ticket> = ArrayList()




    companion object {
        fun newInstance() =
            BookTicketFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = layoutInflater.inflate(R.layout.fragment_my_tickets,null)
        view.noTicketsImage.visibility = View.INVISIBLE
        view.noTicketTxt.visibility = View.INVISIBLE
        view.myTicketsRecyclerView.visibility = View.INVISIBLE

        setupAdapter(view)

        getTicketsFromFirebase(view)
        
        return view

    }





    fun setupAdapter(view: View) {


        mAdapter = TicketAdapter(
            context!!,
            tickets
        ) { id, from, to, date, ticketType, amount, position ->

            val intent = Intent(
                activity,
                QRActivity::class.java
            )

            intent.putExtra("id", id)
            intent.putExtra("from", from)
            intent.putExtra("to", to)
            intent.putExtra("bookedOn", date)
            intent.putExtra("ticketType", ticketType)
            intent.putExtra("amount", amount)
            startActivity(intent)

        }

        view.myTicketsRecyclerView.adapter = mAdapter
        val layoutManager = LinearLayoutManager(context)
        view.myTicketsRecyclerView.layoutManager = layoutManager


        val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        divider.setDrawable(getDrawable(context!!,
            R.drawable.divider
        )!!)

        view.myTicketsRecyclerView.addItemDecoration(divider)


    }

    fun getTicketsFromFirebase(view: View) {


        val progress = ProgressDialog(context)
        progress.setTitle(resources.getString(R.string.bendre_transports))
        progress.setMessage(resources.getString(R.string.fetching_tickets))
        progress.setCancelable(false)
        progress.show()

        rootRef = FirebaseDatabase.getInstance().getReference("tickets")
        val qryRef: DatabaseReference =
                 rootRef
                .child(FirebaseAuth.getInstance().currentUser!!.uid)


        qryRef.addValueEventListener(object : ValueEventListener {



            override fun onCancelled(p0: DatabaseError) {

                progress.dismiss()

            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {

                    tickets.clear()

                    for (h in p0.children) {

                        val ticket: Ticket? = h.getValue(
                            Ticket::class.java)
                        tickets.add(ticket!!)

                    }


                }



               if(tickets.isEmpty()){

                   view.noTicketTxt.visibility = View.VISIBLE
                   view.noTicketsImage.visibility = View.VISIBLE
                   progress.dismiss()

               }else{

                   mAdapter.notifyDataSetChanged()
                   view.myTicketsRecyclerView.visibility = View.VISIBLE
                   progress.dismiss()

               }


            }

        })

    }


}