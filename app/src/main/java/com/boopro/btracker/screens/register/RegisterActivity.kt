package com.boopro.btracker.screens.register

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.boopro.btracker.R
import com.boopro.btracker.databinding.ActivityRegisterBinding
import com.boopro.btracker.helper.FieldValidations
import com.boopro.btracker.helper.FieldValidations.isValidEmail
import com.boopro.btracker.helper.FieldValidations.isValidPhone
import com.boopro.btracker.screens.login.LoginActivity
import com.boopro.btracker.screens.photo_upload.PhotoUploadActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)

        binding.imgBack.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnNext.setOnClickListener {
            if (isValidate()) {
                val intent = Intent(this, PhotoUploadActivity::class.java)

                intent.putExtra("email", binding.txtInputEmail.text.toString())
                intent.putExtra("password", binding.txtInputPassword.text.toString())
                intent.putExtra("username", binding.txtInputUsername.text.toString())
                intent.putExtra("firstname", binding.txtInputFirstName.text.toString())
                intent.putExtra("lastname", binding.txtInputLastName.text.toString())
                intent.putExtra("phone", binding.txtInputPhone.text.toString())

                startActivity(intent)
            }
        }
    }

    //TODO: Abstract this methods in single file because they appear in more files
    private fun isValidate(): Boolean = validateEmail() && validateUsername() && validatePassword() && validateRepeatPassword() && validateFirstName() && validateLastName() && validatePhone()

    private fun validateEmail(): Boolean {
        if (binding.txtInputEmail.text.toString().trim().isEmpty()) {
            binding.txtLayoutEmail.error = getString(R.string.required_field)
            binding.txtLayoutEmail.requestFocus()

            return false
        } else if (!isValidEmail(binding.txtInputEmail.text.toString())) {
            binding.txtLayoutEmail.error = getString(R.string.invalid_email)
            binding.txtInputEmail.requestFocus()

            return false
        } else {
            binding.txtLayoutEmail.isErrorEnabled = false
        }

        return true
    }

    private fun validateUsername(): Boolean {
        if (binding.txtInputUsername.text.toString().trim().isEmpty()) {
            binding.txtLayoutUsername.error = getString(R.string.required_field)
            binding.txtLayoutUsername.requestFocus()

            return false
        } else {
            binding.txtLayoutUsername.isErrorEnabled = false
        }

        return true
    }

    private fun validatePassword(): Boolean {
        if (binding.txtInputPassword.text.toString().trim().isEmpty()) {
            binding.txtLayoutPassword.error = getString(R.string.required_field)
            binding.txtLayoutPassword.requestFocus()

            return false
        } else if (binding.txtInputPassword.text.toString().length < 8) {
            binding.txtLayoutPassword.error = getString(R.string.password_less_than_8)
            binding.txtLayoutPassword.requestFocus()

            return false
        } else if (!FieldValidations.isStringContainNumber(binding.txtInputPassword.text.toString())) {
            binding.txtLayoutPassword.error = getString(R.string.required_at_least_1_digit)
            binding.txtLayoutPassword.requestFocus()

            return false
        } else if (!FieldValidations.isStringLowerAndUpperCase(binding.txtInputPassword.text.toString())) {
            binding.txtLayoutPassword.error = getString(R.string.upper_and_lower_case_letters)
            binding.txtLayoutPassword.requestFocus()

            return false
        } else if (!FieldValidations.isStringContainSpecialCharacter(binding.txtInputPassword.text.toString())) {
            binding.txtLayoutPassword.error = getString(R.string.required_special_character)
            binding.txtLayoutPassword.requestFocus()

            return false
        } else {
            binding.txtLayoutPassword.isErrorEnabled = false
        }

        return true
    }

    private fun validateRepeatPassword(): Boolean {
        when {
            binding.txtInputRepeatPassword.text.toString().trim().isEmpty() -> {
                binding.txtLayoutRepeatPassword.error = getString(R.string.required_field)
                binding.txtLayoutRepeatPassword.requestFocus()

                return false
            }
            binding.txtInputRepeatPassword.text.toString() != binding.txtInputPassword.text.toString() -> {
                binding.txtLayoutRepeatPassword.error = getString(R.string.passwords_don_not_match)
                binding.txtLayoutRepeatPassword.requestFocus()

                return false
            }
            else -> {
                binding.txtLayoutRepeatPassword.isErrorEnabled = false
            }
        }

        return true
    }

    private fun validateFirstName(): Boolean {
        if (binding.txtInputFirstName.text.toString().trim().isEmpty()) {
            binding.txtLayoutFirstName.error = getString(R.string.required_field)
            binding.txtLayoutFirstName.requestFocus()

            return false
        } else if (FieldValidations.isStringContainNumber(binding.txtInputFirstName.text.toString())) {
            binding.txtLayoutFirstName.error = getString(R.string.first_name_only_letters)
            binding.txtLayoutFirstName.requestFocus()

            return false
        } else{
            binding.txtLayoutFirstName.isErrorEnabled = false
        }

        return true
    }

    private fun validateLastName(): Boolean {
        if (binding.txtInputLastName.text.toString().trim().isEmpty()) {
            binding.txtLayoutLastName.error = getString(R.string.required_field)
            binding.txtLayoutLastName.requestFocus()

            return false
        } else if (FieldValidations.isStringContainNumber(binding.txtInputLastName.text.toString())) {
            binding.txtLayoutLastName.error = getString(R.string.last_name_only_letters)
            binding.txtLayoutLastName.requestFocus()

            return false
        } else{
            binding.txtLayoutLastName.isErrorEnabled = false
        }

        return true
    }

    private fun validatePhone(): Boolean {
        if (binding.txtInputPhone.text.toString().trim().isEmpty()) {
            binding.txtLayoutPhone.error = getString(R.string.required_field)
            binding.txtLayoutPhone.requestFocus()

            return false
        }else if (!isValidPhone(binding.txtInputPhone.text.toString())) {
            binding.txtLayoutPhone.error = getString(R.string.invalid_phone)
            binding.txtInputPhone.requestFocus()

            return false
        } else {
            binding.txtLayoutPhone.isErrorEnabled = false
        }

        return true
    }
}