package com.example.bendrenagarasarige.Fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.bendrenagarasarige.Controllers.PaymentFaildTicketsActivity
import com.example.bendrenagarasarige.Controllers.PaymentSuccesForTicketsActivity
import com.example.bendrenagarasarige.R
import com.example.bendrenagarasarige.Model.Ticket
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.wangsun.upi.payment.UpiPayment
import com.wangsun.upi.payment.model.PaymentDetail
import com.wangsun.upi.payment.model.TransactionDetails
import kotlinx.android.synthetic.main.fragment_book_tickets.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class BookTicketFragment : Fragment() {

    lateinit var mAuth: FirebaseAuth
    lateinit var rootRef : DatabaseReference

    var fromPos: Int = 0
    var toPos: Int = 0
    var price: Int = 0
    var ticketTypePos : Int = 0

    lateinit var stopsFrom: Array<String>
    lateinit var stopsTo: Array<String>
    lateinit var ticketTypes : Array<String>



    companion object {
        fun newInstance() =
            BookTicketFragment()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_tickets, container, false)

        stopsFrom  = resources.getStringArray(R.array.stop_locations_from)
        ticketTypes = resources.getStringArray(R.array.ticket_types)
        stopsTo = resources.getStringArray(R.array.stop_locations_to)


        view.payBtnHome.setOnClickListener {

            if(toPos>0 && fromPos>0)

            {

                 //payUPI()

                val current = LocalDateTime.now()

                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm a")
                val date = current.format(formatter)

                val uid = FirebaseAuth.getInstance().currentUser!!.uid

                rootRef = FirebaseDatabase.getInstance().getReference("tickets")
                val qryRef: DatabaseReference =
                    rootRef
                    .child(uid)

                val ticket = Ticket(
                    uid,
                    qryRef.push().key!!,
                    stopsFrom.get(fromPos),
                    stopsTo.get(toPos),
                    date,
                    ticketTypes.get(ticketTypePos),
                    price.toString()
                )

                qryRef.child(ticket.ticketid).setValue(ticket)

            }else{

                showFailure(resources.getString(R.string.select_stop_msg))

            }

        }

        mAuth = FirebaseAuth.getInstance()

        setupSpinner(view)

        return view
    }


    fun payUPI(){



        // note: always create new instance of PaymentDetail for every new payment/order
        val paymentDetail = PaymentDetail(
            vpa="bhatupendra07@ybl",
            name = resources.getString(R.string.bendre_transports),
            payeeMerchantCode = "",       // only if you have merchantCode else pass empty string
            txnRefId = "",                // if you pass empty string we will generate txnRefId for you
            description = resources.getString(R.string.pay_for_ticket),
            amount = "1.00")              // format of amount should be in decimal format x.x (eg 530.00), max. 2 decimal places


        val payment = UpiPayment(activity!!)
            .setPaymentDetail(paymentDetail)
            .setUpiApps(UpiPayment.UPI_APPS)
            .setCallBackListener(object : UpiPayment.OnUpiPaymentListener {
                override fun onSubmitted(data: TransactionDetails) {

                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onSuccess(data: TransactionDetails) {
                    rootRef = FirebaseDatabase.getInstance().getReference("tickets")

                    val uid = FirebaseAuth.getInstance().currentUser!!.uid

                    val qryRef: DatabaseReference = rootRef.child(uid)

                    val current = LocalDateTime.now()

                    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm a")
                    val date = current.format(formatter)


                    val ticket = Ticket(
                        uid,
                        qryRef.push().key!!,
                        stopsFrom.get(fromPos),
                        stopsTo.get(toPos),
                        date,
                        ticketTypes.get(ticketTypePos),
                        price.toString()
                    )

                    qryRef.child(ticket.ticketid).setValue(ticket)

                    val intent = Intent(activity, PaymentSuccesForTicketsActivity::class.java)
                    startActivity(intent)

                }


                override fun onError(message: String) {

                    val intent = Intent(
                        activity,
                        PaymentFaildTicketsActivity::class.java
                    )
                    intent.putExtra("message", message)
                    startActivity(intent)

                }


            })

        payment.pay()

    }


    private fun showFailure( message: String?) {

        val builder = AlertDialog.Builder(activity!!)
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.bendre_transports))
        //set message for alert dialog
        builder.setMessage(message)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.ok)){ p, q ->

        }

        builder.show()

    }


    fun setupSpinner(view: View) {

       // view.toSpinnerHomeActivity.visibility = View.INVISIBLE

        val adapterFrom = ArrayAdapter(
            context!!.applicationContext,
            R.layout.support_simple_spinner_dropdown_item, stopsFrom
        )

        val adapterTo = ArrayAdapter(
            context!!.applicationContext,
            R.layout.support_simple_spinner_dropdown_item,stopsTo
        )

        val adapterTicketType = ArrayAdapter(
            context!!.applicationContext,
            R.layout.support_simple_spinner_dropdown_item,ticketTypes
        )

        view.fromSpinnerHomeTicket.adapter = adapterFrom
        view.toSpinnerHomeActivity.adapter = adapterTo
        view.ticketTypeSpinnerHomeTicket.adapter = adapterTicketType


        view.fromSpinnerHomeTicket.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {

                fromPos = position
                //view.toSpinnerHomeActivity.visibility = View.VISIBLE


            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }


        }

       view.toSpinnerHomeActivity.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {

                toPos = position

                val units = toPos - fromPos
                price = units * 1

                if(price<0)
                    price = -price

            //    view.ticketpriceTxtHome.text = "\u20B9$price"


            }

            override fun onNothingSelected(parent: AdapterView<*>) {


            }


        }


        view.ticketTypeSpinnerHomeTicket.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {

                ticketTypePos = position

            }

            override fun onNothingSelected(parent: AdapterView<*>) {



            }


        }


    }

}
