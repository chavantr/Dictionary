package com.mywings.questionset.events

import android.view.View
import android.view.ViewGroup


interface OnInformativeListener : OnLayoutListener, OnInputManagerListener {
    fun show(message: String)
    fun show(message: String, view: View?)
    fun getGroup(): ViewGroup
}
