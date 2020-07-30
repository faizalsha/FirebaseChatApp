package com.faizal.shadab.firebasechatapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class LoginActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 1
    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sign_in.setOnClickListener {
            val intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
            startActivityForResult(intent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            val response = IdpResponse.fromResultIntent(data)

            if(resultCode == Activity.RESULT_OK){
                val progressDialog = indeterminateProgressDialog("Setting up your account")
                //TODO: Initialize current user in Firestore
                startActivity(intentFor<MainActivity>().newTask().clearTask())
                progressDialog.dismiss()
            }else{
                if(resultCode == Activity.RESULT_CANCELED){
                    if(response == null) return;

                    when (response.error?.errorCode) {
                        ErrorCodes.NO_NETWORK ->
                            Toast.makeText(this, "Network Error", Toast.LENGTH_LONG).show()
                        ErrorCodes.UNKNOWN_ERROR ->
                            Toast.makeText(this, "Unknown Error", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
