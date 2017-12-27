package com.mywings.dictionary

import android.os.Bundle
import com.mywings.dictionary.databinding.ActivityRegistrationScreenBinding

class RegistrationScreen : DictionaryCompactActivity() {
    private var activityRegistration: ActivityRegistrationScreenBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRegistration = setContentLayout(R.layout.activity_registration_screen)
    }
}
