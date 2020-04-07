package com.example.bendrenagarasarige.Controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bendrenagarasarige.R
import kotlinx.android.synthetic.main.activity_payment_faild_tickets.*

class PaymentFaildTicketsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_faild_tickets)

        val message = intent.getStringExtra("message")
        paymentFailedStatusTxt.text = message

        retryBookingBtnPaymentFailTicket.setOnClickListener {

            val intent = Intent(this,
                MainActivity::class.java)
            startActivity(intent)

        }

    }
}
