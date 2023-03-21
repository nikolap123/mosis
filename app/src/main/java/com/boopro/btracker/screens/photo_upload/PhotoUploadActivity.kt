package com.boopro.btracker.screens.photo_upload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.boopro.btracker.R
import com.boopro.btracker.data.Repository
import com.boopro.btracker.data.model.UserModel
import com.boopro.btracker.databinding.PhotoUploadBinding
import com.boopro.btracker.screens.login.LoginActivity
import com.boopro.btracker.screens.main.MainActivity
import com.boopro.btracker.screens.register.RegisterActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.aprilapps.easyphotopicker.ChooserType
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource

class PhotoUploadActivity : AppCompatActivity() {
    private lateinit var binding: PhotoUploadBinding

    //TODO: Move variables and methods for requesting permissions in Permissions.kt
    private val LEGACY_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 456
    private val LEGACY_WRITE_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private lateinit var imageURI: Uri
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var username: String
    private lateinit var firstname: String
    private lateinit var lastname: String
    private lateinit var phone: String

    private val easyImage = EasyImage.Builder(this).setChooserTitle("Pick media").setChooserType(ChooserType.CAMERA_AND_GALLERY).setFolderName("pictures").build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.photo_upload)

        email = intent.getStringExtra("email").toString()
        username = intent.getStringExtra("username").toString()
        password = intent.getStringExtra("password").toString()
        firstname = intent.getStringExtra("firstname").toString()
        lastname = intent.getStringExtra("lastname").toString()
        phone = intent.getStringExtra("phone").toString()

        binding.imgBack.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.addPhoto.setOnClickListener {
            if (isLegacyExternalStoragePermissionRequired()) {
                requestLegacyWriteExternalStoragePermission()
            } else {
                easyImage.openChooser(this@PhotoUploadActivity)
            }
        }

        binding.btnRegister.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    Repository.registerUser(email, password, username, firstname, lastname, phone, imageURI).collect {
                        withContext(Dispatchers.Main) {
                            intent = Intent(this@PhotoUploadActivity, LoginActivity::class.java)

                            intent.putExtra("email", it.email)
                            intent.putExtra("username", it.username)
                            intent.putExtra("firstname", it.firstname)
                            intent.putExtra("lastname", it.lastname)
                            intent.putExtra("phone", it.phoneNumber)
                            intent.putExtra("imageURL", it.imageURL)

//                            val user = UserModel(it.firstname, it.lastname, it.username, it.email, it.phoneNumber, it.imageURL, listOf("2", "3", "4", "6", "JMZZrZIt5jf73v9IhQFVvMulCcu2"), 0)

//                            Repository.saveUserInfo(this@PhotoUploadActivity, user)

                            startActivity(intent)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@PhotoUploadActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        easyImage.handleActivityResult(requestCode, resultCode, data, this, object : EasyImage.Callbacks {
                override fun onCanceled(source: MediaSource) {

                }

                override fun onImagePickerError(error: Throwable, source: MediaSource) {
                    error.printStackTrace()
                }

                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    for (file in imageFiles) {
                        imageURI = Uri.fromFile(file.file)
                        Glide.with(this@PhotoUploadActivity).load(file.file).into(binding.imgAddPhoto)
                    }
                }
            })
    }

    private fun isLegacyExternalStoragePermissionRequired(): Boolean {
        val permissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        return Build.VERSION.SDK_INT < 29 && !permissionGranted
    }

    private fun requestLegacyWriteExternalStoragePermission() {
        ActivityCompat.requestPermissions(this@PhotoUploadActivity, LEGACY_WRITE_PERMISSIONS, LEGACY_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE)
    }
}



