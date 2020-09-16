package com.example.whatsappclone.auth

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import com.example.whatsappclone.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_otp.*
import java.util.concurrent.TimeUnit

const val PHONE_NUMBER = "phonenumber"

class OtpActivity : AppCompatActivity(), View.OnClickListener {

    var phoneNumber:String? = null
    var mCountdownTimer:CountDownTimer? = null

    lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var mVerificationId:String? = null
    var mResendToken:PhoneAuthProvider.ForceResendingToken? = null

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        initViews()
        startVerify()

    }

    private fun startVerify() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber!!, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks) // OnVerificationStateChangedCallbacks

        showTimer(60000)   //TO IMPLEMENT THE COUNTDOWN TIMER
    }

    private fun showTimer(milliSecInFuture:Long) {
        resendBtn.isEnabled = false
        mCountdownTimer = object :CountDownTimer(milliSecInFuture,1000){
            override fun onFinish() {
                resendBtn.isEnabled = true
                counterTv.visibility = View.GONE
            }

            override fun onTick(milliSec: Long) {
                counterTv.visibility = View.VISIBLE
                counterTv.text = getString(R.string.seconds_remaining,milliSec/1000)
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mCountdownTimer != null){
            mCountdownTimer?.cancel()
        }
    }

    private fun initViews() {
        phoneNumber = intent.getStringExtra(PHONE_NUMBER)!!
        verifyTv.text = getString(R.string.verify_number,phoneNumber)
        setSpanableString()

        verificationBtn.setOnClickListener(this)
        resendBtn.setOnClickListener(this)

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                val smsCode = credential.smsCode
                if(!smsCode.isNullOrBlank()){
                    sentcodeEt.setText(smsCode)
                }
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {

                if (e is FirebaseAuthInvalidCredentialsException) { }
                else if (e is FirebaseTooManyRequestsException) { }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                mVerificationId = verificationId
                mResendToken = token
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val mAuth = FirebaseAuth.getInstance()

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    startActivity(Intent(this,
                        SignUpActivity::class.java))
                    finish()
                }
                else{
                    notifyUserAndRetry("Your phone number verification Failed. Try again!!")
                }
            }
    }


    //THIS IS SAME LIKE THE <span> TAG IN HTML
    private fun setSpanableString() {
        val span = SpannableString(getString(R.string.waiting_text, phoneNumber))
        val clickableSpan = object : ClickableSpan(){
            override fun onClick(p0: View) {
                showLoginActivity()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ds.linkColor
            }
        }
        span.setSpan(clickableSpan,span.length-13,span.length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        waitingTv.movementMethod = LinkMovementMethod.getInstance()
        waitingTv.text = span
    }

    private fun notifyUserAndRetry(message: String) {
        MaterialAlertDialogBuilder(this).apply {
            setMessage(message)
            setPositiveButton("Ok") { _, _ ->
                showLoginActivity()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun showLoginActivity() {
        startActivity((Intent(this,
            LoginActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        )
    }

    override fun onBackPressed() {

    }

    override fun onClick(p0: View?) {
        when(p0){
            verificationBtn->{
                val code = sentcodeEt.text.toString()
                if(code.isNotEmpty() and !mVerificationId.isNullOrBlank()){
                    progressDialog = createProgressDialog("Please Wait...",false)
                    progressDialog.show()

                    val credentials = PhoneAuthProvider.getCredential(mVerificationId!!,code)
                    signInWithPhoneAuthCredential(credentials)
                }
            }
            resendBtn->{
                val code = sentcodeEt.text.toString()
                if(mResendToken != null){
                    showTimer(60000)
                    progressDialog = createProgressDialog("Sending a verification code",false)
                    progressDialog.show()

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber!!, // Phone number to verify
                        60, // Timeout duration
                        TimeUnit.SECONDS, // Unit of timeout
                        this, // Activity (for callback binding)
                        callbacks,
                        mResendToken) // OnVerificationStateChangedCallbacks
                }
            }
        }
    }
}


fun Context.createProgressDialog(message:String, isCancellable:Boolean):ProgressDialog{
    return ProgressDialog(this).apply{
        setCancelable(false)
        setMessage(message)
        setCanceledOnTouchOutside(false)
    }
}