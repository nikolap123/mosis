package com.boopro.btracker.screens.profile

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.boopro.btracker.R
import com.boopro.btracker.data.Repository
import com.boopro.btracker.data.model.UserModel
import com.boopro.btracker.databinding.FragmentProfileBinding
import com.boopro.btracker.helper.Consts
import com.boopro.btracker.helper.FieldValidations
import com.boopro.btracker.screens.login.LoginActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.aprilapps.easyphotopicker.ChooserType
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var userInfo: UserModel
    private lateinit var easyImage: EasyImage
    private var imageURI: Uri? = null

    //TODO: Move variables and methods for requesting permissions in Permissions.kt
    private val LEGACY_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 456
    private val LEGACY_WRITE_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        userInfo = Repository.getUserInfo(requireContext())

        println("currentUserInfo" + userInfo.toString())
        binding.user = userInfo
        Glide.with(requireContext()).load(userInfo.imageURL).into(binding.profileFragmentImageView)

        binding.profileFragmentEditProfileBtn.setOnClickListener {
            changeAttributes(View.VISIBLE, true)
        }

        binding.profileFragmentSaveChangesBtn.setOnClickListener {
            changeAttributes(View.INVISIBLE, false)

            updateUserInformations()
        }

        binding.profileFragmentLogoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut();

            Repository.saveChecked(requireContext(), false)

            requireContext().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE).edit().clear().apply()

            val intent = Intent(requireContext(), LoginActivity::class.java)

            startActivity(intent)


        }

        binding.profileFragmentChangePictureBtn.setOnClickListener {
            easyImage = EasyImage.Builder(requireContext()).setChooserTitle("Pick media").setChooserType(ChooserType.CAMERA_AND_GALLERY).setFolderName("pictures").build()

            if (isLegacyExternalStoragePermissionRequired()) {
                requestLegacyWriteExternalStoragePermission()
            } else {
                easyImage.openChooser(this)
            }
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        easyImage.handleActivityResult(requestCode, resultCode, data, requireActivity(), object : EasyImage.Callbacks {
                override fun onCanceled(source: MediaSource) {

                }

                override fun onImagePickerError(error: Throwable, source: MediaSource) {
                    error.printStackTrace()
                }

                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    for (file in imageFiles) {
                        imageURI = Uri.fromFile(file.file)

                        Glide.with(requireContext()).load(imageURI).into(binding.profileFragmentImageView)
                    }
                }
            })
    }

    private fun isLegacyExternalStoragePermissionRequired(): Boolean {
        val permissionGranted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        return Build.VERSION.SDK_INT < 29 && !permissionGranted
    }

    private fun requestLegacyWriteExternalStoragePermission() {
        ActivityCompat.requestPermissions(requireActivity(), LEGACY_WRITE_PERMISSIONS, LEGACY_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE)
    }


    private fun changeAttributes(visible: Int, change: Boolean) {
        binding.profileFragmentFirstname.isEnabled = change
        binding.profileFragmentLastname.isEnabled = change
        binding.profileFragmentUsername.isEnabled = change
        binding.profileFragmentEmail.isEnabled = change
        binding.profileFragmentPhoneNumber.isEnabled = change

        binding.profileFragmentSaveChangesBtn.visibility = visible
        binding.profileFragmentChangePictureBtn.visibility = visible
        binding.profileFragmentEditProfileBtn.visibility = if (visible == View.VISIBLE) View.INVISIBLE else View.VISIBLE
        binding.profileFragmentLogoutBtn.visibility = if (visible == View.VISIBLE) View.INVISIBLE else View.VISIBLE
    }

    private fun updateUserInformations() {
        val user = UserModel()

        if (isValidate()) {
            user.firstname = binding.profileFragmentFirstname.text.toString()
            user.lastname = binding.profileFragmentLastname.text.toString()
            user.username = binding.profileFragmentUsername.text.toString()
            user.email = binding.profileFragmentEmail.text.toString()
            user.phoneNumber = binding.profileFragmentPhoneNumber.text.toString()
            user.friendList = Consts.currentUser.friendList
            user.imageURL = userInfo.imageURL
            user.points = userInfo.points

            lifecycleScope.launch(Dispatchers.IO) {
                Repository.updateUserWithPicture(user, imageURI)

                Repository.saveUserInfo(requireContext(),user)
            }
        }
    }

    //TODO: Abstract this methods in single file because they appear in more files
    private fun isValidate(): Boolean = validateFirstName() && validateLastName() && validateUsername() && validateEmail() && validatePhone()

    private fun validateFirstName(): Boolean {
        if (binding.profileFragmentFirstname.text.toString().trim().isEmpty()) {
            binding.profileFragmentFirstnameLayout.error = getString(R.string.required_field)
            binding.profileFragmentFirstnameLayout.requestFocus()

            return false
        } else if (FieldValidations.isStringContainNumber(binding.profileFragmentFirstname.text.toString())) {
            binding.profileFragmentFirstnameLayout.error = getString(R.string.first_name_only_letters)
            binding.profileFragmentFirstnameLayout.requestFocus()

            return false
        } else {
            binding.profileFragmentFirstnameLayout.isErrorEnabled = false
        }

        return true
    }

    private fun validateLastName(): Boolean {
        if (binding.profileFragmentLastname.text.toString().trim().isEmpty()) {
            binding.profileFragmentLastnameLayout.error = getString(R.string.required_field)
            binding.profileFragmentLastnameLayout.requestFocus()

            return false
        } else if (FieldValidations.isStringContainNumber(binding.profileFragmentLastname.text.toString())) {
            binding.profileFragmentLastnameLayout.error = getString(R.string.last_name_only_letters)
            binding.profileFragmentLastnameLayout.requestFocus()

            return false
        } else {
            binding.profileFragmentLastnameLayout.isErrorEnabled = false
        }

        return true
    }

    private fun validateUsername(): Boolean {
        if (binding.profileFragmentUsername.text.toString().trim().isEmpty()) {
            binding.profileFragmentUsernameLayout.error = getString(R.string.required_field)
            binding.profileFragmentUsernameLayout.requestFocus()

            return false
        } else {
            binding.profileFragmentUsernameLayout.isErrorEnabled = false
        }

        return true
    }

    private fun validatePhone(): Boolean {
        if (binding.profileFragmentPhoneNumber.text.toString().trim().isEmpty()) {
            binding.profileFragmentPhoneNumberLayout.error = getString(R.string.required_field)
            binding.profileFragmentPhoneNumberLayout.requestFocus()

            return false
        } else if (!FieldValidations.isValidPhone(binding.profileFragmentPhoneNumber.text.toString())) {
            binding.profileFragmentPhoneNumberLayout.error = getString(R.string.invalid_phone)
            binding.profileFragmentPhoneNumberLayout.requestFocus()

            return false
        } else {
            binding.profileFragmentPhoneNumberLayout.isErrorEnabled = false
        }

        return true
    }

    private fun validateEmail(): Boolean {
        if (binding.profileFragmentEmail.text.toString().trim().isEmpty()) {
            binding.profileFragmentEmailLayout.error = getString(R.string.required_field)
            binding.profileFragmentEmailLayout.requestFocus()

            return false
        } else if (!FieldValidations.isValidEmail(binding.profileFragmentEmail.text.toString())) {
            binding.profileFragmentEmailLayout.error = getString(R.string.invalid_email)
            binding.profileFragmentEmailLayout.requestFocus()

            return false
        } else {
            binding.profileFragmentEmailLayout.isErrorEnabled = false
        }

        return true
    }
}