package com.mywings.dictionary

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_registration_screen.*

class RegistrationScreen : DictionaryCompactActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_screen)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
