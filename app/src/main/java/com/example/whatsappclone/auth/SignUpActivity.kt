package com.example.whatsappclone.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.whatsappclone.MainActivity
import com.example.whatsappclone.R
import com.example.whatsappclone.modals.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_sign_up.*

// IN THIS WE UPLOAD THE IMAGE TO THE FIREBASE STORAGE AND SAVE THE USER T THE FIREBASE FIRESTORE

class SignUpActivity : AppCompatActivity() {

    val storage by lazy {
        FirebaseStorage.getInstance()
    }
    val auth by lazy {
        FirebaseAuth.getInstance()
    }
    val database by lazy {
        FirebaseFirestore.getInstance()
    }
    lateinit var download_url:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        userImgView.setOnClickListener{
            pickImage()
            //Firebase Extension :- Image Thumbnail
        }
        nextBtn.setOnClickListener{
            nextBtn.isEnabled = false

            val name = nameEt.text.toString()
            if(name.isEmpty()){
                Toast.makeText(this,"Please enter your name",Toast.LENGTH_SHORT).show()
            } else if (!::download_url.isInitialized){
                Toast.makeText(this,"Please select a image",Toast.LENGTH_SHORT).show()
            }else{
                val user = User(
                    name,
                    download_url,
                    download_url,
                    auth.uid!!
                )
                database.collection("users").document(auth.uid!!).set(user).addOnSuccessListener {
                    startActivity(Intent(this,
                        MainActivity::class.java))
                }.addOnFailureListener{
                    nextBtn.isEnabled = true
                }
            }
        }
    }

    private fun pickImage() {
        val i = Intent(Intent.ACTION_PICK)
        i.type = "image/*"
        startActivityForResult(i,1000)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1000){
            data?.data?.let {
                userImgView.setImageURI(it)
                uploadImage(it)
            }
        }
    }

    private fun uploadImage(it: Uri){
        nextBtn.isEnabled = false
        val ref = storage.reference.child("uploads/"+ auth.uid.toString())
        ref.putFile(it)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    download_url = it.toString()
                    nextBtn.isEnabled = true
                }
                    .addOnFailureListener {  }
            }
            .addOnFailureListener {  }
//        val uploadTask = ref.putFile(it)
//        uploadTask.continueWithTask(com.google.android.gms.tasks.Continuation<UploadTask.TaskSnapshot,Task<Uri>> {task->
//            if(!task.isSuccessful){
//                task.exception?.let {
//                    throw it
//                }
//            }
//            return@Continuation ref.downloadUrl
//        }).addOnCompleteListener {
//            nextBtn.isEnabled = true
//            if(it.isSuccessful){
//                download_url = it.result.toString()
//                Log.i("URL","download url: ${download_url}")
//            }else{
//
//            }
//        }
//            .addOnFailureListener {
//
//            }
    }
}