package com.example.contactsync

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.contactsync.databinding.ActivityOtpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class Otp : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var binding : ActivityOtpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        val authId = intent.getStringExtra("otpcr").toString()

        binding.btnSubmit.setOnClickListener {
            val otp = binding.editTextOtp.text.toString()

            if (otp.isNotEmpty()){
                val credential = PhoneAuthProvider.getCredential(authId,otp)
                signInWithPhoneAuthCredential(credential)
            }
        }
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {


                    val user = task.result?.user
                    startActivity(Intent(this,UserInformation::class.java))
                    finish()
                } else {

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }
}