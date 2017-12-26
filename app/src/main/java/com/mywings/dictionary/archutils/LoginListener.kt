package com.mywings.dictionary.archutils

import android.app.Activity
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


interface LoginListener {


    fun getSocialInstance(): FirebaseAuth?

    fun getGoogleSignInClient(): GoogleSignInClient?

    fun getFacebookCallbackManager(): CallbackManager?

    fun getCurrentUser(): FirebaseUser?

    fun signOut()


    fun getFacebookReadPermission(activity: Activity?)

}