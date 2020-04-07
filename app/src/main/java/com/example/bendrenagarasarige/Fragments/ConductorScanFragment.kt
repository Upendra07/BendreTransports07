package com.example.bendrenagarasarige.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.bendrenagarasarige.Controllers.ConductorMainActivity
import com.example.bendrenagarasarige.R
import com.example.bendrenagarasarige.Model.Ticket
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.fragment_scan_conductor.view.*
import kotlinx.android.synthetic.main.ticket_dilog.view.*
import org.json.JSONException
import org.json.JSONObject

class ConductorScanFragment : Fragment() {

    lateinit var qrScanIntegrator: IntentIntegrator
    lateinit var mAuth: FirebaseAuth
    lateinit var rootRef : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view : View = layoutInflater.inflate(R.layout.fragment_scan_conductor,null)

        mAuth = FirebaseAuth.getInstance()
        qrScanIntegrator = IntentIntegrator(activity)
        qrScanIntegrator.setOrientationLocked(false)
        // qrScanIntegrator.setOrientationLocked(true)
        rootRef = FirebaseDatabase.getInstance().getReference("tickets")

        view.startScanTicketBtnConductor.setOnClickListener {

            qrScanIntegrator.initiateScan()

        }

        return view

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            // If QRCode has no data.
            if (result.contents == null) {
                Toast.makeText(context, "Not found", Toast.LENGTH_LONG).show()
            } else {
                // If QRCode contains data.
                try {


                    Log.d("scan","hii")
                    // Converting the data to json format
                    val obj = JSONObject(result.contents)

                    val uid = obj.getString("uid")
                    val ticketid = obj.getString("ticketid")
                    val from = obj.getString("from")
                    val to = obj.getString("to")
                    val bookedOn = obj.getString("bookedOn")
                    val ticketType = obj.getString("ticketType")
                    val amount = obj.getString("amount")

                    val ticket : Ticket =
                        Ticket(
                            uid, ticketid, from, to, bookedOn, ticketType, amount
                        )

                    showTicketData(ticket)


                } catch (e: JSONException) {

                    e.printStackTrace()
                    Toast.makeText(ConductorMainActivity(), result.contents, Toast.LENGTH_LONG).show()

                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    fun showTicketData(ticket: Ticket){

        val dilogView = LayoutInflater.from(context).inflate(R.layout.ticket_dilog,null)

        dilogView.toTxtDilog.text = ticket.to
        dilogView.fromTxtDilog.text = ticket.from
        dilogView.bookedOnTxtDilog.text = ticket.bookedOn
        dilogView.amountTxtDilog.text = "₹${ticket.amount}"
        dilogView.ticketTypeDilog.text = ticket.ticketType



        val builder = AlertDialog.Builder(context!!)
            .setView(dilogView)
            .setTitle("Confirm Ticket")

        val alertDialog = builder.show()

        dilogView.confirmBtnDilog.setOnClickListener {

            deleteTicketFromFireBase(ticket)
            alertDialog.dismiss()

        }

        dilogView.cancelBtnDilog.setOnClickListener {

            alertDialog.dismiss()

        }

    }


    fun deleteTicketFromFireBase(ticket: Ticket)
    {

        val rootRef = FirebaseDatabase.getInstance().getReference("tickets")

        val qryRef: DatabaseReference = rootRef.child(ticket.uid).child(ticket.ticketid)




        qryRef.removeValue()

            .addOnCompleteListener {

                if (it.result != null){

                    Log.d("Conductor","${it.result}")
                    Toast.makeText(context,"Delete Success!!", Toast.LENGTH_LONG).show()

                }
            }
            .addOnFailureListener {

                Toast.makeText(context,it.localizedMessage, Toast.LENGTH_LONG).show()

            }

    }








}