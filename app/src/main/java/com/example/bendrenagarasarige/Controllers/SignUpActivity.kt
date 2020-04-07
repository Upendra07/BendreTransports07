package com.example.bendretransportss

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.bendrenagarasarige.Controllers.MainActivity
import com.example.bendrenagarasarige.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {


    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar!!.hide()
        mAuth = FirebaseAuth.getInstance()
    }

    fun createAccountClicked(view: View){

       val email = emailTxtSignUp.text.toString()
       val password = passwordTxtSignUp.text.toString()
       val confirmPassword = confirmPasswordTxtSignUp.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty())
        {
           if (password == confirmPassword)
           {

               createAccount(email,password)

           }else{

               showFailure(resources.getString(R.string.passwords_doesnt_match))

           }

        }else{

            showFailure(resources.getString(R.string.must_fill_all_fields))

        }
    }

    fun createAccount(email: String, password: String) {

        val progress = ProgressDialog(this)
        progress.setTitle(resources.getString(R.string.bendre_transports))
        progress.setMessage(resources.getString(R.string.creating_account_msg))
        progress.setCancelable(false)
        progress.show()

        mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful)
                {

                    progress.dismiss()
                    Log.d("SignUp",task.result!!.user.email!!)

                    val intent = Intent(this,
                        MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()

                    showSuccess()


                }
            }
            .addOnFailureListener { err ->

                progress.dismiss()
                showFailure(err.message!!)

            }

    }

    private fun showFailure( message: String?) {

        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.bendre_transports))
        //set message for alert dialog
        builder.setMessage(message)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.ok)){ p,q ->

        }

        builder.show()

    }

    fun showSuccess(){

        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.bendre_transports))
        //set message for alert dialog
        builder.setMessage(resources.getString(R.string.account_create_success))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.ok)){ p,q ->

        }

        builder.show()


    }

}
