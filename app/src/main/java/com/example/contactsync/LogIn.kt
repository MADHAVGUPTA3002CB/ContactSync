package com.example.contactsync

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.contactsync.databinding.ActivityLogInBinding
import com.example.contactsync.databinding.ActivityMainBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LogIn : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth

    private lateinit var binding : ActivityLogInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()


        if(auth.currentUser!=null){
            startActivity(Intent(this , MainActivity::class.java))
        }



        binding.gnrtotp.setOnClickListener {
            if (binding.etphone.text.length >1){
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(binding.etphone.text.toString()) // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this) // Activity (for callback binding)
                    .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }else{
                Toast.makeText(this,"Enter a valid mobile number", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {


            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {

            val otpIntent = Intent(this@LogIn,Otp::class.java)
            otpIntent.putExtra("otpcr",verificationId)
            startActivity(otpIntent)
            finish()
//            verificationID = verificationId
//            Toast.makeText(this@LogIn, "Otp has been send , Enter It", Toast.LENGTH_SHORT).show()
//            binding.gnrtotp.text = "Verify Otp"

        }
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {


                    val user = task.result?.user
                    startActivity(Intent(this,MainActivity::class.java))

                } else {

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }
}
