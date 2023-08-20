@file:Suppress("DEPRECATION")

package com.example.spendease.userAuthentication

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.spendease.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.mindrot.jbcrypt.BCrypt

@Suppress("DEPRECATION")
class Signup : AppCompatActivity() {
    lateinit var username : TextInputEditText
    lateinit var email : TextInputEditText
    lateinit var password : TextInputEditText
    lateinit var signupbtn : Button
    lateinit var logintv : TextView
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        progressDialog = ProgressDialog(this)

        logintv = findViewById(R.id.logintv_id)
        logintv.setOnClickListener {
            val loginintent = Intent(this,Signin::class.java)
            startActivity(loginintent)
        }

        signupbtn = findViewById(R.id.signupbtn_id)
        signupbtn.setOnClickListener {
            signup()
        }
    }

    private fun signup(){
        username = findViewById(R.id.signupusername_id)
        email = findViewById(R.id.signupemail_id)
        password = findViewById(R.id.signuppassword_id)

        val getusername = username.text.toString()
        val getemail = email.text.toString().trim()
        val getpassword = password.text.toString()

        val validpassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$".toRegex()

//      creating object of firebaseauthentication & firestore
        val firebaseauth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        if(getusername.isEmpty()){
            username.setError("Username cannot be empty!")
        }
        else if(getemail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(getemail).matches()){
            email.setError("Email cannot be empty!")
        }
        else if(getpassword.isEmpty() && getpassword.matches(validpassword)){
            password.setError("Password cannot be empty!")
        }
        else {
            val hashpassword = BCrypt.hashpw(getpassword,BCrypt.gensalt())
            firebaseauth.createUserWithEmailAndPassword(getemail, getpassword)
                .addOnSuccessListener {authResult->
                    val user = authResult.user
                    user?.getIdToken(true)
                        ?.addOnCompleteListener{ task->
                            if(task.isSuccessful){
                                val token = task.result?.token
                                Log.d("Tag",token.toString())
                                firestore.collection("User")
                                    .document(FirebaseAuth.getInstance().uid.toString())
                                    .set(UserModal(getusername,getemail,hashpassword))
                                    .addOnCompleteListener {
                                        if (task.isSuccessful){
                                            val userDetails = this.getSharedPreferences("UserDetails", MODE_PRIVATE)
                                            val editor = userDetails.edit()
                                            editor.putBoolean("isFirstTime",true)
                                            editor.apply()
                                            val gettingInfo = Intent(this, GettingInfo::class.java)
                                            startActivity(gettingInfo)
                                            notifyUser("Sign Up Successfully")
                                            progressDialog.cancel()
                                        }
                                        else{
                                            notifyUser("Something went wrong!!")
                                        }
                                    }
                            }
                            else{
                                println("Failed to retrieve token!!")
                            }
                        }
                }
                .addOnFailureListener { e ->
                    notifyUser("Something went wrong!!"+e.message)
                    progressDialog.cancel()
                }
            progressDialog.setMessage("Signup ...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
        }
    }
    private fun notifyUser(msg : String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}