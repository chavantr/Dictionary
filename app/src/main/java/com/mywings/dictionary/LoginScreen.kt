package com.mywings.dictionary

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login_screen.*
import kotlinx.android.synthetic.main.content_login_screen.*

class LoginScreen : AppCompatActivity() {

    //region Field Declaration
    private var callbackManager: CallbackManager? = null
    private val permission: List<String> = mutableListOf("public_profile", "email")
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mFirebaseAuth: FirebaseAuth
    //endregion Field Declaration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        setContentView(R.layout.activity_login_screen)
        setSupportActionBar(toolbar)
        mFirebaseAuth = FirebaseAuth.getInstance()
        if (null != mFirebaseAuth.currentUser) {
            val intent = Intent(this@LoginScreen, DictionaryScreen::class.java)
            startActivity(intent)
            return
        }
        callbackManager = CallbackManager.Factory.create()
        btnFacebookSignIn.setReadPermissions(permission)
        btnFacebookSignIn.registerCallback(callbackManager!!, facebookCallback)
        btnFacebookSignIn.setOnClickListener({
            LoginManager.getInstance().logInWithReadPermissions(this@LoginScreen, permission)
        })
        btnGoogleSignIn.setOnClickListener({ signInWithGoogle() })
        LoginManager.getInstance().registerCallback(callbackManager, facebookCallback)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
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
        } else {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GOOGLE_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data!!)
                val account: GoogleSignInAccount? = task.result
                firebaseAuthWithGoogle(account!!)
            }
        }
    }

    private fun signInWithGoogle() {
        val intent = mGoogleSignInClient.signInIntent
        startActivityForResult(intent, GOOGLE_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, googleCallback)
    }

    companion object {
        val GOOGLE_SIGN_IN: Int = 101
    }

}
