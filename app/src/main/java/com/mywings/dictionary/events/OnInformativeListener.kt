package com.mywings.questionset.events

import android.view.View
import android.view.ViewGroup

/**
 * @author Tatyabhau Chavan
 * @created 11/12/2017
 */

interface OnInformativeListener : OnLayoutListener, OnInputManagerListener {
    fun show(message: String)
    fun show(message: String, view: View?)
    fun getGroup(): ViewGroup
}
