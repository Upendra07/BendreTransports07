package com.example.bendrenagarasarige.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bendrenagarasarige.Model.Ticket
import com.example.bendrenagarasarige.R

class TicketAdapter(val context: Context, val tickets: ArrayList<Ticket>, val itemclick: (String, String, String, String, String, String, Int)-> Unit ) :  RecyclerView.Adapter<TicketAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, ViewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.ticket_view, parent, false)
        return ViewHolder(view,itemclick)
    }

    override fun getItemCount(): Int {

        return tickets.count()

    }

    override fun onBindViewHolder(holder : ViewHolder, position: Int) {


        holder.bindMessage(context, tickets[position].ticketid, tickets[position].from,tickets[position].to,tickets[position].bookedOn,tickets[position].ticketType,tickets[position].amount,position)


    }




    inner class ViewHolder (itemView : View, itemclick : (String,String,String,String,String,String,Int)-> Unit): RecyclerView.ViewHolder(itemView){
        val ticketTypeTxtTicketView = itemView.findViewById<TextView>(R.id.ticketTypeTxtTicketView)
   //     val ticketIdTxt = itemView.findViewById<TextView>(R.id.ticketidTxtTicketView)
        val fromTxt = itemView.findViewById<TextView>(R.id.fromTxtTicketView)
        val toTxt = itemView.findViewById<TextView>(R.id.toTxtTicketView)
        val amountTxt = itemView.findViewById<TextView>(R.id.amountTxtTicketView)

        fun bindMessage(context: Context, id : String, from : String, to:String, date: String, ticketType: String, amount: String, position: Int){

            ticketTypeTxtTicketView.text = ticketType
          //  ticketIdTxt.text = id
            fromTxt.text = from
            toTxt.text = to
            amountTxt.text = "\u20B9$amount"



            itemView.setOnClickListener{

                itemclick(id,from,to,date,ticketType,amount,position)

            }
        }
    }
}