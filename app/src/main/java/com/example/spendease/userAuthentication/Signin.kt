package com.example.spendease.userAuthentication

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spendease.R
import com.example.spendease.navigation.NavigationDrawer
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth


@Suppress("DEPRECATION")
class Signin : AppCompatActivity() {
    lateinit var email: TextInputEditText
    lateinit var password: TextInputEditText
    lateinit var signuptv: TextView
    lateinit var loginbtn: Button
    lateinit var forgetpassword: TextView
    lateinit var googleimgbtn: ImageView
    lateinit var progressDialog: ProgressDialog
    lateinit var firebaseAuth: FirebaseAuth
//    lateinit var GsignInOptions: GoogleSignInOptions
//    lateinit var GsignInClient: GoogleSignInClient
//    val RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        signuptv = findViewById(R.id.signuptv_id)
        loginbtn = findViewById(R.id.Loginbtn_id)
        forgetpassword = findViewById(R.id.forgetpasswordtv_id)
//        googleimgbtn = findViewById(R.id.googleimgvbtn)

        progressDialog = ProgressDialog(this)
        firebaseAuth = FirebaseAuth.getInstance()

        loginbtn.setOnClickListener {
            logIn()
        }
        signuptv.setOnClickListener {
            val signupintent = Intent(this, Signup::class.java)
            startActivity(signupintent)

        }
        forgetpassword.setOnClickListener {
            resetPassword()
        }
//        googleimgbtn.setOnClickListener {
//            googleSignIn()
//        }

    }

    private fun logIn(){
        email = findViewById(R.id.email_id)
        password = findViewById(R.id.password_id)

        val getemail = email.text.toString().trim()
        val getpassword = password.text.toString()
        firebaseAuth = FirebaseAuth.getInstance()
        if (getemail.isEmpty()){
            email.error = "Email cannot be empty!"
        }
        else if(getpassword.isEmpty()){
            password.error = "Password cannot be empty!"
        }
        else{
            firebaseAuth.signInWithEmailAndPassword(getemail, getpassword)
                .addOnSuccessListener {
                    val userDetails = this.getSharedPreferences("UserDetails", MODE_PRIVATE)
                    val editor = userDetails.edit()
                    editor.putBoolean("isFirstTime",true)
                    editor.putString("email",getemail)
                    editor.apply()
                    val mainActivity = Intent(this,NavigationDrawer::class.java)
                    startActivity(mainActivity)
                    finish()
                    notifyUser("Signing Successfully!")
                    progressDialog.cancel()
                }
                .addOnFailureListener { e->
                    notifyUser("Email or Password is Invalid\n"+e.message)
                    progressDialog.cancel()
                }
            progressDialog.setMessage("Signing...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
        }
    }

    private fun resetPassword(){
        email = findViewById(R.id.email_id)
        val getemail = email.text.toString()
        firebaseAuth = FirebaseAuth.getInstance()
        if (getemail.isEmpty()){
            email.error = "Please enter email for reset!"
        }
        else{
            firebaseAuth.sendPasswordResetEmail(getemail)
                .addOnSuccessListener {
                    notifyUser("Check Email box!!")
                    progressDialog.cancel()
                }
                .addOnFailureListener {e->
                    notifyUser("Invalid email"+e.message)
                    progressDialog.cancel()
                }
            progressDialog.setMessage("Sending mail")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
        }
    }
//    private fun googleSignIn(){
//        GsignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.defaulf_web_client_id))
//            .requestEmail()
//            .build()
//
//        GsignInClient = GoogleSignIn.getClient(this,GsignInOptions)
//        val signinIntent = GsignInClient.signInIntent
//        startActivityForResult(signinIntent,RC_SIGN_IN)
//    }
//
//    @Deprecated("Deprecated in Java")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode == RC_SIGN_IN){
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                val account = task.getResult(ApiException::class.java)
//                handleSignInResult(account)
//            }
//            catch (e:ApiException){
//                Log.w(TAG,"Google Sign in Failed!",e)
//            }
//        }
//    }
//
//    private fun handleSignInResult(acct: GoogleSignInAccount) {
//        Log.d(TAG, "FirebaseAuthWithGoogle" + acct.id)
//        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)   //set of identifying information = credential
//        firebaseAuth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    val user = firebaseAuth.currentUser
//                    addUserToFirestore(user!!)
//                } else {
//                    Log.w(TAG, "SignInWithCredential:Failure", task.exception)
//                }
//            }
//    }
//    private fun addUserToFirestore(user:FirebaseUser){
//        val Usermap = hashMapOf(
//            "name" to user.displayName,
//            "email" to user.email,
//            "photoUrl" to user.photoUrl
//        )
//        val firestore = FirebaseFirestore.getInstance()
//        firestore.collection("GoogleSignINUser")
//            .document(user.uid)
//            .set(Usermap)
//            .addOnSuccessListener {
//                val userDetails = this.getSharedPreferences("UserDetails", MODE_PRIVATE)
//                val editor = userDetails.edit()
//                editor.putBoolean("isFirstTime",true)
//                editor.putString("email",user.email)
//                editor.apply()
//                val gettingInfointent = Intent(this,GettingInfo::class.java)
//                startActivity(gettingInfointent)
//                notifyUser("Signing Successfully")
//                progressDialog.cancel()
//            }
//            .addOnFailureListener {e->
//                notifyUser("Check Internet Connectivity!"+e.message)
//                progressDialog.cancel()
//            }
//        progressDialog.setMessage("Signing...")
//        progressDialog.setCanceledOnTouchOutside(false)
//        progressDialog.show()
//    }

    private fun notifyUser(msg:String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}