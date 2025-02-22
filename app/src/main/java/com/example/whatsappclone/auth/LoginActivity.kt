package com.example.whatsappclone.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import com.example.whatsappclone.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var countryCode:String
    lateinit var phoneNumber:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Add Hint request for phone number

        phoneNumberEt.addTextChangedListener {
            nextBtn.isEnabled = !(it.isNullOrEmpty() || it.length < 10)
        }

        nextBtn.setOnClickListener{
            checkNumber()
        }
    }

    private fun checkNumber() {
        countryCode = ccp.selectedCountryCodeWithPlus
        phoneNumber = countryCode + phoneNumberEt.text.toString()

        notifyUser()
    }

    private fun notifyUser() {
        MaterialAlertDialogBuilder(this).apply {
            setMessage("We will be verifying the phone number ${phoneNumber}\n"+
            "Is this OK, or would you like to edit the number?")
            setPositiveButton("Ok"){_,_ ->
                showOtpActivity()
            }
            setNegativeButton("Edit"){dialog,which->
                dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun showOtpActivity() {
        val i = Intent(this, OtpActivity::class.java)
        i.putExtra(PHONE_NUMBER,phoneNumber)
        startActivity(i)
        finish()
    }
}