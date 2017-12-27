package com.mywings.questionset.events

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater

/**
 * @author Tatyabhau Chavan
 * @created 11/12/2017
 */

interface OnLayoutListener {

    fun inflate(): LayoutInflater

    fun <T : ViewDataBinding> inflate(id: Int): T

    fun <T : ViewDataBinding> attach(id: Int): T

    fun <T : ViewDataBinding> setContentLayout(id: Int): T

    fun getLayoutManager(flow: Int): RecyclerView.LayoutManager

}
