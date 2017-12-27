package com.mywings.dictionary

import android.content.Intent
import android.os.Bundle
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login_screen.*
import kotlinx.android.synthetic.main.content_login_screen.*

class LoginScreen : DictionaryCompactActivity() {

    //region Field Declaration

    //endregion Field Declaration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        setContentView(R.layout.activity_login_screen)
        setSupportActionBar(toolbar)
        if (null != getCurrentUser()) {
            navigateToDictionary()
            return
        }

        btnFacebookSignIn.setReadPermissions(PERMISSION)
        btnFacebookSignIn.registerCallback(getFacebookCallbackManager()!!, facebookCallback)
        btnFacebookSignIn.setOnClickListener({
            getFacebookReadPermission(this@LoginScreen)
        })

        btnGoogleSignIn.setOnClickListener({ signInWithGoogle() })

        LoginManager.getInstance().registerCallback(getFacebookCallbackManager(), facebookCallback)

        btnSignUp.setOnClickListener({
            val intentRegistration = Intent(this@LoginScreen, RegistrationScreen::class.java)
            startActivity(intentRegistration)
        })
    }

    private fun navigateToDictionary() {
        val intent = Intent(this@LoginScreen, DictionaryScreen::class.java)
        startActivity(intent)
    }


    private var facebookCallback: FacebookCallback<LoginResult> = object : FacebookCallback<LoginResult> {
        override fun onCancel() {
        }

        override fun onError(error: FacebookException?) {
        }

        override fun onSuccess(result: LoginResult?) {
        }

    }

    private var googleCallback: OnCompleteListener<AuthResult> = OnCompleteListener { task ->
        if (task.isSuccessful) {
            navigateToDictionary()
        } else {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            RESULT_OK -> {
                when (requestCode) {
                    GOOGLE_SIGN_IN -> {
                        val task = GoogleSignIn.getSignedInAccountFromIntent(data!!)
                        val account: GoogleSignInAccount? = task.result
                        firebaseAuthWithGoogle(account!!)
                    }
                }
            }
            RESULT_CANCELED -> {
            }
        }
    }

    private fun signInWithGoogle() {
        val intent = getGoogleSignInClient()!!.signInIntent
        startActivityForResult(intent, GOOGLE_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        getSocialInstance()!!.signInWithCredential(credential)
                .addOnCompleteListener(this, googleCallback)
    }

    companion object {
        val GOOGLE_SIGN_IN: Int = 101
    }

}
