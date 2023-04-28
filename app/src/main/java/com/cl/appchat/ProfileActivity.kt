package com.cl.appchat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cl.appchat.databinding.ActivityProfileBinding
import com.cl.appchat.util.*
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : AppCompatActivity() {

    private var binding: ActivityProfileBinding? = null
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var progressLayout: LinearLayout? = null
    private var nameEt: TextInputEditText? = null
    private var emailEt: TextInputEditText? = null
    private var phoneEt: TextInputEditText? = null
    private var photoIV: ImageView? = null
    private var imageUrl: String? = null
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)

        setContentView(binding?.root)
        progressLayout = binding?.progressLayout
        nameEt = binding?.nameET
        emailEt = binding?.emailET
        phoneEt = binding?.phoneET
        photoIV = binding?.photoIV

        if (userId.isNullOrEmpty()) {
            finish()
        }
        progressLayout!!.setOnTouchListener { v, event -> true }
        photoIV?.setOnClickListener {
            val intent: Intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_PHOTO)
        }


        populateInfo()
    }

    fun onApply(view: View) {
        progressLayout?.visibility = View.GONE
        val name = nameEt?.text.toString()
        val email = emailEt?.text.toString()
        val phone = phoneEt?.text.toString()
        val map = HashMap<String, Any>()
        map[DATA_USERS_NAME] = name
        map[DATA_USERS_EMAIL] = email
        map[DATA_USERS_PHONE] = phone


        firebaseDB.collection(DATA_USERS).document(userId!!).update(map).addOnSuccessListener {
            Toast.makeText(this, "Update successful", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener { e ->
            e.printStackTrace()

            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
            progressLayout?.visibility = View.GONE
        }
    }

    fun populateInfo() {
        progressLayout?.visibility = View.GONE
        firebaseDB.collection(DATA_USERS).document(userId!!).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                imageUrl = user?.imageUrl
                nameEt?.setText(user?.name, TextView.BufferType.EDITABLE)
                emailEt?.setText(user?.email, TextView.BufferType.EDITABLE)
                phoneEt?.setText(user?.phone, TextView.BufferType.EDITABLE)
                if (imageUrl != null) {
                    populateImage(this, user?.imageUrl, photoIV!!, R.drawable.default_user)
                }
                progressLayout?.visibility = View.GONE
            }.addOnFailureListener { e ->
                e.printStackTrace()
                finish()
            }
    }

    fun onDelete(view: View) {
        progressLayout?.visibility = View.VISIBLE
        AlertDialog.Builder(this).setTitle("Delete account")
            .setMessage("This delete your profile information. Are you sure?")
            .setPositiveButton("Yes") { dialog, which ->
                Toast.makeText(this, "Profile deleted", Toast.LENGTH_SHORT).show()
                firebaseDB.collection(DATA_USERS).document(userId!!).delete()
                firebaseStorage.child(DATA_IMAGES).child(userId).delete()
                firebaseAuth.currentUser?.delete()?.addOnSuccessListener {
                    finish()
                }?.addOnFailureListener {
                    finish()
                }

            }
            .setNegativeButton("No") { dialog, which ->
                progressLayout?.visibility = View.GONE

            }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
            storeImage(data?.data)
        }
    }

    private fun storeImage(imageURI: Uri?) {
        if (imageURI != null) {
            Toast.makeText(this, "uploading", Toast.LENGTH_SHORT).show()
            progressLayout?.visibility = View.VISIBLE
            val filePath = firebaseStorage.child(DATA_IMAGES).child(userId!!)
            filePath.putFile(imageURI).addOnSuccessListener {
                filePath.downloadUrl
                    .addOnSuccessListener { taskSnapShot ->
                        val url = taskSnapShot.toString()
                        firebaseDB.collection(DATA_USERS).document(userId)
                            .update(DATA_USERS_IMAGE_URL, url)
                            .addOnSuccessListener {
                                imageUrl = url
                                populateImage(this, imageUrl, photoIV!!, R.drawable.default_user)
                            }
                        progressLayout?.visibility = View.GONE
                    }
                    .addOnFailureListener { uploadFailure() }

            }.addOnFailureListener {
                uploadFailure()
            }
        }
    }

    private fun uploadFailure() {
        Toast.makeText(
            this,
            "Image upload failed. Please try again later",
            Toast.LENGTH_SHORT
        ).show()
        progressLayout?.visibility = View.GONE
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, ProfileActivity::class.java)
    }
}