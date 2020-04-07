package com.example.bendrenagarasarige.Controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.bendrenagarasarige.Helpers.EncryptionHelper
import com.example.bendrenagarasarige.Helpers.QRCodeHelper
import com.example.bendrenagarasarige.R
import com.example.bendrenagarasarige.Model.Ticket
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.android.synthetic.main.activity_qr.*

class QRActivity : AppCompatActivity() {



    lateinit var ticketType:String
    lateinit var from: String
    lateinit var to: String
    lateinit var amount: String
    lateinit var id : String
    lateinit var bookedOn : String

    lateinit var rootRef : DatabaseReference

    var isFirstTime = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)

        rootRef = FirebaseDatabase.getInstance().getReference("tickets")

         id = intent.getStringExtra("id")!!
         from = intent.getStringExtra("from")!!
         to = intent.getStringExtra("to")!!
         bookedOn = intent.getStringExtra("bookedOn")!!
         ticketType = intent.getStringExtra("ticketType")!!
         amount = intent.getStringExtra("amount")!!

        fromTxtQr.text = from
        toTxtQr.text = to
        amountTxtQr.text =   "₹$amount"
        bookedOnTxtQr.text = bookedOn
        ticketidQr.text = id
        ticketTypeTxtQr.text = ticketType


        setQRCode()
        setUpListeners()


    }


    fun setUpListeners(){



        val queryRef = rootRef.child(FirebaseAuth.getInstance().currentUser!!.uid).child(id)

        queryRef.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                if (isFirstTime){

                    isFirstTime = false

                }else{

                    val intent = Intent(baseContext,
                        MainActivity::class.java)
                    finishAffinity()
                    startActivity(intent)

                }

            }


        })



    }




    private fun setImageBitmap(encryptedString: String?) {


        val bitmap = QRCodeHelper.newInstance(this)
            .setContent(encryptedString).setErrorCorrectionLevel(ErrorCorrectionLevel.Q).setMargin(2).qrcOde

        qrCodeImageView.setColorFilter(ContextCompat.getColor(this,
            R.color.black
        ))

        qrCodeImageView.setImageBitmap(bitmap)


    }

    fun setQRCode(){

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        val ticket = Ticket(
            uid,
            id,
            from,
            to,
            bookedOn,
            ticketType,
            amount
        )

        val serializeString:String = Gson().toJson(ticket)
        val encryptedString:String = EncryptionHelper.getInstance()
            .encryptionString(serializeString).encryptMsg()
        val decryptedString:String = EncryptionHelper.getInstance()
            .getDecryptionString(encryptedString)

        setImageBitmap(decryptedString)


    }


}

