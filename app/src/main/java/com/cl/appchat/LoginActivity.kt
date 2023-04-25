package com.cl.appchat

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
import com.cl.appchat.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var firebaseAuthListener = FirebaseAuth.AuthStateListener { val user: String? = firebaseAuth.currentUser?.uid
    if (user != null){
        startActivity(MainActivity.newIntent(this))
        finish()
    }}
    private var binding: ActivityLoginBinding? = null
    private var emailTIET: TextInputEditText? = null
    private var emailTIL: TextInputLayout? = null
    private var passwordTIET: TextInputEditText? = null
    private var passwordTIL: TextInputLayout? = null
    private var progressLayout: LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding?.root)
        emailTIET = binding?.emailTIET
        emailTIL = binding?.emailTIL
        passwordTIET = binding?.passwordTIET
        passwordTIL = binding?.passwordTIL
        progressLayout = binding?.progressLayout

        setTextChangeListener(emailTIET!!,emailTIL!!)
        setTextChangeListener(passwordTIET!!,passwordTIL!!)

        progressLayout!!.setOnTouchListener{v, event->true}
    }

    private fun setTextChangeListener(et: EditText, til: TextInputLayout){
        et.addTextChangedListener { object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                til.isErrorEnabled = false
            }

            override fun afterTextChanged(s: Editable?) {

            }
        } }
    }

    fun onLogin(view: View) {
        var proceed = true
        if (binding!!.emailTIET.text.isNullOrEmpty()) {
            emailTIET!!.error = "Email is required"
            proceed = false
        }
        if (binding!!.passwordTIET.text.isNullOrEmpty()) {
            passwordTIET!!.error = "Email is required"
            proceed = false
        }
        if (proceed) {
            progressLayout!!.visibility = View.VISIBLE
            firebaseAuth.signInWithEmailAndPassword(
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
                }
            }.addOnFailureListener{e ->
                progressLayout?.visibility = View.GONE
                e.printStackTrace()
            }
        }
    }

    fun onSignup(view: View) {
        startActivity(SignupActivity.newIntent(this))
        finish()
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener (firebaseAuthListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener ( firebaseAuthListener)

    }
    companion object {
        fun newIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }
}