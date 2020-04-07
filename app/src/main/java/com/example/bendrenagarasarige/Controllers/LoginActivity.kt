package com.example.bendretransportss

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.bendrenagarasarige.*
import com.example.bendrenagarasarige.Controllers.ConductorMainActivity
import com.example.bendrenagarasarige.Controllers.ForgotPasswordActivity
import com.example.bendrenagarasarige.Controllers.MainActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class LoginActivity : AppCompatActivity() {


    lateinit var mAuth : FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient
    val RC_SIGN_IN = 1
    lateinit var callbackManager : CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocate()
        setContentView(R.layout.activity_login)
        supportActionBar!!.hide()

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)



        if (mAuth.currentUser != null) {

            if(mAuth.currentUser!!.email == "conductor123@bendre.com"){

                val intent = Intent(this,
                    ConductorMainActivity::class.java)
                startActivity(intent)
                finish()

            }else{

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            }

        }


        sign_in_button.setOnClickListener {

            signIn()

        }

        //val fbLoginBtn1 = findViewById<LoginButton>(R.id.fbLoginBtn)

        callbackManager = CallbackManager.Factory.create()

        fbLoginBtn.setReadPermissions("email")
        fbLoginBtn.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

            override fun onSuccess(loginResult: LoginResult) {
                Log.d("FBLogin", "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d("FbLogin", "facebook:onCancel")
                // ...
            }

            override fun onError(error: FacebookException) {
                Log.d("FbLogin", "facebook:onError", error)
                // ...
            }
        })



    }

    fun takeToSignUp(view: View){

        val intent = Intent(this,SignUpActivity::class.java)
        startActivity(intent)

    }

    fun loginClicked(view: View){

        val email = emailTxtLogin.text.toString()
        val password = passwordTxtLogin.text.toString()

        if(email.isEmpty() || password.isEmpty() ) {

            if (email.isEmpty()) emailTxtLogin.error = resources.getString(R.string.enter_email)
            if (password.isEmpty()) passwordTxtLogin.error = resources.getString(R.string.enter_password)

        }else{

            login(email,password)

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

    fun forgotPasswordClicked(view: View){

        Log.d ("Login","${view.id}")
        val intent = Intent(this,
            ForgotPasswordActivity::class.java)
        startActivity(intent)

    }


    fun login(email: String, password: String){

        val progress = ProgressDialog(this)
        progress.setTitle(resources.getString(R.string.bendre_transports))
        progress.setMessage(resources.getString(R.string.logging_in))
        progress.setCancelable(false)
        progress.show()


        mAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {task ->

                if (task.isSuccessful) {

                    progress.dismiss()

                    Toast.makeText(this, resources.getString(R.string.login_success) , Toast.LENGTH_SHORT).show()

                    if(mAuth.currentUser!!.email == "conductor123@bendre.com")
                    {

                        val intent = Intent(this,
                            ConductorMainActivity::class.java)
                        startActivity(intent)
                        finish()

                    }else{

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    }



                }

            }
            .addOnFailureListener { err ->

                 progress.dismiss()
                 showFailure(err.message)

            }


    }

    fun setLocate(Lang: String) {

        val locale = Locale(Lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", Lang)
        editor.apply()

    }

    fun loadLocate() {

        val sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "")
        setLocate(language!!)

    }


    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d("Login", "${account!!.displayName}")
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.d("Login", "Google sign in failed", e)
                // ...
            }
        }



    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("Login", "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Login", "signInWithCredential:success")
                    val user = mAuth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Login", "signInWithCredential:failure", task.exception)

                }

                // ...
            }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("FBLogin", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("FBLogin", "signInWithCredential:success")
                    val user = mAuth.currentUser

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("FBLogin", "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }

                // ...
            }
    }

}
