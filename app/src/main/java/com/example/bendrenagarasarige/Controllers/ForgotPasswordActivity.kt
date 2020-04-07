package com.example.bendrenagarasarige.Controllers

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.bendrenagarasarige.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        supportActionBar!!.hide()

        mAuth = FirebaseAuth.getInstance()
    }


    fun sendRecoveryEmailClicked(view: View){

        val email = emailEditTxtForgot.text.toString()

        if (email.isNotEmpty())
        {

            sendRecoveryEmail(email)
            //finish()

        }else{

            showFailure(resources.getString(R.string.enter_email_to_recover))

        }

    }


    private fun sendRecoveryEmail(email: String){

        val progress = ProgressDialog(this)
        progress.setTitle(resources.getString(R.string.bendre_transports))
        progress.setMessage(resources.getString(R.string.sending_email_recovery))
        progress.setCancelable(false)
        progress.show()

         mAuth.sendPasswordResetEmail(email)
             .addOnCompleteListener {task ->

                 progress.dismiss()
                 if (task.isSuccessful) showSuccess()
                 finish()

             }
             .addOnFailureListener {err ->

                 progress.dismiss()
                 showFailure(err.localizedMessage!!)
                 finish()

             }

    }


    private fun showFailure(message: String){

        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.bendre_transports))
        //set message for alert dialog
        builder.setMessage(message)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.ok)){ _, _ ->

        }

        builder.show()

    }


    private fun showSuccess(){

        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.bendre_transports))
        //set message for alert dialog
        builder.setMessage(resources.getString(R.string.recovery_mail_success))
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.ok)){ _, _ ->

        }

        builder.show()

    }


}
