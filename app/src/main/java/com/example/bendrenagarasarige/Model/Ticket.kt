package com.example.bendrenagarasarige.Model

import com.google.firebase.auth.FirebaseAuth

class Ticket (val uid:String,val ticketid:String, val from:String, val to:String, val bookedOn: String,val ticketType: String, val amount: String)  {

    constructor():this(FirebaseAuth.getInstance().currentUser!!.uid,"","","","","","")

}