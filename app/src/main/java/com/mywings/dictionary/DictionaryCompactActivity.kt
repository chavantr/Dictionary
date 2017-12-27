package com.mywings.dictionary

import android.app.Activity
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mywings.dictionary.archutils.LoginListener
import com.mywings.questionset.events.OnInformativeListener


abstract class DictionaryCompactActivity : AppCompatActivity(), LoginListener, OnInformativeListener {


    override fun getSocialInstance(): FirebaseAuth? {
        return FirebaseAuth.getInstance()
    }

    override fun getGoogleSignInClient(): GoogleSignInClient? {
        return GoogleSignIn.getClient(this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build())
    }

    override fun getFacebookCallbackManager(): CallbackManager? {
        return CallbackManager.Factory.create()
    }

    override fun getCurrentUser(): FirebaseUser? {
        return getSocialInstance()!!.currentUser
    }

    override fun signOut() {
        getSocialInstance()!!.signOut()
    }

    override fun getFacebookReadPermission(activity: Activity?) {
        LoginManager.getInstance().logInWithReadPermissions(activity, PERMISSION)
    }

    override fun hide(view: View?) {

        inputMethodManger()!!.hideSoftInputFromInputMethod(view!!.applicationWindowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)

    }

    private fun inputMethodManger(): InputMethodManager? {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        return inputMethodManager
    }


    override fun show(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    override fun show(message: String, view: View?) {
        Snackbar.make(view!!, message, Snackbar.LENGTH_LONG).setAction("OK", { }).show()
    }

    override fun show(view: View?) {
        inputMethodManger()!!.hideSoftInputFromInputMethod(view!!.applicationWindowToken, 1)
    }

    override fun getGroup(): ViewGroup {
        return this.findViewById(android.R.id.content)
    }

    override fun <T : ViewDataBinding> attach(id: Int): T {
        return DataBindingUtil.inflate(inflate(), id, getGroup(), true)
    }

    override fun getLayoutManager(flow: Int): RecyclerView.LayoutManager {
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.orientation = flow
        return linearLayoutManager
    }

    override fun inflate(): LayoutInflater {
        return LayoutInflater.from(this)
    }

    override fun <T : ViewDataBinding> inflate(id: Int): T {
        return DataBindingUtil.inflate(inflate(), id, getGroup(), false)
    }

    override fun <T : ViewDataBinding> setContentLayout(id: Int): T {
        return DataBindingUtil.setContentView(this, id)
    }

    companion object {
        val PUBLIC_PROFILE = "public_profile"
        val EMAIL = "email"
        val PERMISSION = mutableListOf(PUBLIC_PROFILE, EMAIL)
    }
}