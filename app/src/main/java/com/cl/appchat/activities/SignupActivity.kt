package com.cl.appchat.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.cl.appchat.databinding.ActivitySignupBinding
import com.cl.appchat.util.DATA_USERS
import com.cl.appchat.util.User
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {
    private var firebaseDB = FirebaseFirestore.getInstance()
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user: String? = firebaseAuth.currentUser?.uid
        if (user != null) {
            startActivity(MainActivity.newIntent(this))
            finish()
        }
    }
    private var binding: ActivitySignupBinding? = null
    private var emailTIET: TextInputEditText? = null
    private var emailTIL: TextInputLayout? = null
    private var passwordTIET: TextInputEditText? = null
    private var passwordTIL: TextInputLayout? = null
    private var nameTIET: TextInputEditText? = null
    private var nameTIL: TextInputLayout? = null
    private var phoneTIET: TextInputEditText? = null
    private var phoneTIL: TextInputLayout? = null
    private var progressLayout: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        emailTIET = binding?.emailTIET
        emailTIL = binding?.emailTIL
        passwordTIET = binding?.passwordTIET
        passwordTIL = binding?.passwordTIL
        nameTIET = binding?.nameTIET
        nameTIL = binding?.nameTIL
        phoneTIET = binding?.passwordTIET
        phoneTIL = binding?.passwordTIL

        progressLayout = binding?.progressLayout

        setTextChangeListener(nameTIET!!, nameTIL!!)
        setTextChangeListener(phoneTIET!!, phoneTIL!!)
        setTextChangeListener(emailTIET!!, emailTIL!!)
        setTextChangeListener(passwordTIET!!, passwordTIL!!)
    }

    private fun setTextChangeListener(et: EditText, til: TextInputLayout) {
        et.addTextChangedListener {
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    til.isErrorEnabled = false
                }

                override fun afterTextChanged(s: Editable?) {

                }
            }
        }
    }

    fun onSignup(view: View) {
        var proceed = true
        if (binding!!.emailTIET.text.isNullOrEmpty()) {
            emailTIL!!.error = "Email is required"
            emailTIL!!.isErrorEnabled = true
            proceed = false
        }
        if (binding!!.passwordTIET.text.isNullOrEmpty()) {
            passwordTIET!!.error = "Password is required"
            passwordTIL!!.isErrorEnabled = true

            proceed = false
        }
        if (binding!!.nameTIET.text.isNullOrEmpty()) {
            nameTIET!!.error = "Name is required"
            nameTIL!!.isErrorEnabled = true

            proceed = false
        }
        if (binding!!.phoneTIET.text.isNullOrEmpty()) {
            phoneTIET!!.error = "Phone is required"
            phoneTIL!!.isErrorEnabled = true
            proceed = false
        }
        if (proceed) {
            progressLayout!!.visibility = View.VISIBLE
            firebaseAuth.createUserWithEmailAndPassword(
                emailTIET!!.text.toString(),
                passwordTIET!!.text.toString()
            ).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    progressLayout?.visibility = View.GONE
                    Toast.makeText(
                        this,
                        "Login error ${task.exception?.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (firebaseAuth.uid != null) {
                    val email = emailTIET!!.text.toString()
                    val phone = phoneTIET!!.text.toString()
                    val name = nameTIET!!.text.toString()
                    val user = User(email,phone, name,"","","Hello, I'm new","")
                    firebaseDB.collection(DATA_USERS).document(firebaseAuth.uid!!).set(user)
                }
                progressLayout?.visibility = View.GONE
            }.addOnFailureListener{e ->
            progressLayout?.visibility = View.GONE
            e.printStackTrace()}
        }

    }

    fun onLogin(view: View) {
        startActivity(LoginActivity.newIntent(this))
        finish()
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)

    }

    companion object {
        fun newIntent(context: Context) = Intent(context, SignupActivity::class.java)
    }
}