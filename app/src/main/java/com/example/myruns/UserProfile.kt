package com.example.myruns

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File


class UserProfile : AppCompatActivity() {
    //Global variable
    private lateinit var imageView: ImageView
    private lateinit var nameView: TextView
    private lateinit var emailAddressView: TextView
    private lateinit var phoneNumberView: TextView
    private lateinit var genderView: RadioGroup
    private lateinit var classYearView: TextView
    private lateinit var majorView: TextView

    //About profile image
    private val tempProfileImageName = "temp_profile_image.png"
    private val profileImageName = "profile_image.png"
    private lateinit var tempProfileImageUri: Uri
    private lateinit var profileImageUri: Uri
    private lateinit var tempProfileImageFile: File
    private lateinit var profileImageFile: File
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var galleryResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        //Find the views of profile layout
        imageView = findViewById(R.id.profile_picture)
        nameView = findViewById(R.id.profile_name)
        emailAddressView = findViewById(R.id.profile_email_address)
        phoneNumberView = findViewById(R.id.profile_phone_number)
        genderView = findViewById(R.id.profile_gender)
        classYearView = findViewById(R.id.profile_class_year)
        majorView = findViewById(R.id.profile_major)

        //Get the uri of image
        tempProfileImageFile = File(getExternalFilesDir(null), tempProfileImageName)
        profileImageFile = File(getExternalFilesDir(null), profileImageName)
//        tempProfileImageUri = FileProvider.getUriForFile(this, "com.example.myruns", tempProfileImageFile)
//        profileImageUri = FileProvider.getUriForFile(this, "com.example.myruns", profileImageFile)

        //Reload profile image and data from shared preferences
        if (profileImageFile.exists()) {
            imageView.setImageBitmap(getBitmap(profileImageUri))
        }
        loadWithSp()

        //savedInstanceState
        if(savedInstanceState != null) {
            nameView.text = savedInstanceState.getString("NAME_KEY")
            emailAddressView.text = savedInstanceState.getString("EMAIL_KEY")
            phoneNumberView.text = savedInstanceState.getString("PHONE_NUMBER_KEY")
            classYearView.text = savedInstanceState.getString("CLASS_KEY")
            majorView.text = savedInstanceState.getString("MAJOR_KEY")
            genderView.check(savedInstanceState.getInt("GENDER_KEY"))
        }

    }

    fun onChangePhotoClicked(view: View) {
        val dialogOptions = arrayOf("Open Camera", "Select from Gallery")
        val builder = AlertDialog.Builder(this)
        var intent: Intent
        builder.setTitle("Pick Profile Picture")
        builder.setItems(dialogOptions) { _, index ->
            when(dialogOptions[index]) {
                "Open Camera" -> {
                    intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, tempProfileImageUri)
                    cameraResult.launch(intent)
                }
                "Select from Gallery" -> {
                    intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    galleryResult.launch(intent)
                }
            }
        }
        builder.show()
    }

    fun onSaveClicked(view: View) {
        if (File(getExternalFilesDir(null), tempProfileImageName).exists()) {
            tempProfileImageFile.renameTo(profileImageFile)
        }
        if (imageView.drawable == null) {
            tempProfileImageFile.delete()
            profileImageFile.delete()
        }
        saveWithSp()
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        finish()
    }

    fun onCancelClicked(view: View) {
        if (File(getExternalFilesDir(null), tempProfileImageName).exists()) {
            tempProfileImageFile.delete()
        }
        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        finish()
    }

    fun onResetClicked(view: View) {
        imageView.setImageDrawable(null)
        nameView.text = ""
        emailAddressView.text = ""
        phoneNumberView.text = ""
        classYearView.text = ""
        majorView.text = ""
        genderView.clearCheck()
    }

    private fun getBitmap(imgUri: Uri): Bitmap {
        val bitmap = BitmapFactory.decodeStream(this.contentResolver.openInputStream(imgUri))
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, null, true)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("NAME_KEY", nameView.text.toString())
        outState.putString("EMAIL_KEY", emailAddressView.text.toString())
        outState.putString("PHONE_NUMBER_KEY", phoneNumberView.text.toString())
        outState.putString("CLASS_KEY", classYearView.text.toString())
        outState.putString("MAJOR_KEY", majorView.text.toString())
        val genderId = genderView.checkedRadioButtonId
        if (genderId != -1) {
            outState.putInt("GENDER_KEY", genderId)
        }
    }

    //Shared preferences save function
    private fun saveWithSp() {
        val sharedPref: SharedPreferences = getSharedPreferences("PROFILE_CONFIG", MODE_PRIVATE)
        val genderId: Int = genderView.checkedRadioButtonId
        sharedPref.edit()
            .putString("NAME_KEY", nameView.text.toString())
            .putString("EMAIL_KEY", emailAddressView.text.toString())
            .putString("PHONE_NUMBER_KEY", phoneNumberView.text.toString())
            .putInt("GENDER_KEY", genderId)
            .putString("CLASS_KEY", classYearView.text.toString())
            .putString("MAJOR_KEY", majorView.text.toString())
            .apply()
    }

    //Shared preferences load function
    private fun loadWithSp() {
        val sharedPref: SharedPreferences = getSharedPreferences("PROFILE_CONFIG", MODE_PRIVATE)
        nameView.text = sharedPref.getString("NAME_KEY", "")
        emailAddressView.text = sharedPref.getString("EMAIL_KEY", "")
        phoneNumberView.text = sharedPref.getString("PHONE_NUMBER_KEY", "")
        genderView.check(sharedPref.getInt("GENDER_KEY", -1))
        classYearView.text = sharedPref.getString("CLASS_KEY", "")
        majorView.text = sharedPref.getString("MAJOR_KEY", "")
    }
}