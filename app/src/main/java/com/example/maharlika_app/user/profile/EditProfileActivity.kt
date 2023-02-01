package com.example.maharlika_app.user.profile

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.maharlika_app.databinding.ActivityEditProfileBinding
import com.example.maharlika_app.user.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.HashMap

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var selectedImage : Uri
    private lateinit var storage : FirebaseStorage
    private lateinit var database : FirebaseDatabase
    private lateinit var progressDialog : ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("PLease wait")
        progressDialog.setCanceledOnTouchOutside(false)

        loadUsersInfo()
        binding.btnBack.setOnClickListener {
            startActivity(
                Intent(this,MainActivity::class.java)
            )
        }
        binding.imageView.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent,1)


        }
        binding.btnUpdate.setOnClickListener {
            validateData()
        }
    }
    private var fullName = ""
    private var password = ""
    private var address = ""
    private var imageLink = ""
    private fun validateData() {
        fullName = binding.etFullName.text.toString().trim()
        password = binding.etNewPass.text.toString().trim()
        address = binding.etNewPass.text.toString().trim()
        imageLink = binding.imageLink.text.toString().trim()

        if (fullName.isEmpty()|| password.isEmpty() || address.isEmpty()){
            Toast.makeText(this,"Empty Fields are note allowed..", Toast.LENGTH_SHORT).show()
        }
        else{
            uploadImage()
        }
    }

    private fun uploadImage() {
        progressDialog.setMessage("Uploading Image...")
        progressDialog.show()

        val filepath = "profile/"+auth.uid

        val reference = storage.reference.child(filepath)
        reference.putFile(selectedImage).addOnCompleteListener{
            if (it.isSuccessful){
                reference.downloadUrl.addOnSuccessListener {task->
                    uploadInfo(task.toString())
                }
            }
        }
    }

    private fun uploadInfo(imgUrl: String) {
        progressDialog.setMessage("Updating Profile...")
        progressDialog.show()


        val hashMap : HashMap<String, Any?> = HashMap()

        hashMap["password"] = password
        hashMap["fullName"] = fullName
        hashMap["address"] = address
        hashMap["image"] = imgUrl

        database.getReference("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .updateChildren(hashMap)
            .addOnCompleteListener{
                if (it.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(this,"Updated", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))

                }
                else{
                    Toast.makeText(this,it.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null){
            if (data.data != null){
                selectedImage = data.data!!
                binding.imageView.setImageURI(selectedImage)
            }
        }
    }
    private fun loadUsersInfo() {
        //reference
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(auth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get user info
                    val address = "${snapshot.child("address").value}"
                    val currentDate = "${snapshot.child("currentDate").value}"
                    val currentTime = "${snapshot.child("currentTime").value}"
                    val email = "${snapshot.child("email").value}"
                    val fullName = "${snapshot.child("fullName").value}"
                    val id = "${snapshot.child("id").value}"
                    val image = "${snapshot.child("image").value}"
                    val password = "${snapshot.child("password").value}"
                    val uid = "${snapshot.child("uid").value}"
                    val userType = "${snapshot.child("userType").value}"

                    //set data
                    binding.etFullName.setText(fullName)
                    binding.etNewPass.setText(password)
                    binding.etNewAddress.setText(address)

                    Glide.with(this@EditProfileActivity)
                        .load(image)
                        .into(binding.imageView)


                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}