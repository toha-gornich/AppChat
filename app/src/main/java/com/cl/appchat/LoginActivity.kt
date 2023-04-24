package com.cl.appchat

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

    }
    fun onClick(view: View){
        startActivity(SignupActivity.newIntent(this))
        finish()
    }
    companion object{
        fun newIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }
}