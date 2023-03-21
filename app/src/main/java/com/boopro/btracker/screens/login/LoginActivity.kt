package com.boopro.btracker.screens.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.boopro.btracker.R
import com.boopro.btracker.data.Repository
import com.boopro.btracker.databinding.ActivityLoginBinding
import com.boopro.btracker.screens.main.MainActivity
import com.boopro.btracker.screens.register.RegisterActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.btnCreateAcc.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        val isRemembered = Repository.getChecked(this)

        if (isRemembered) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

            startActivity(intent)
            finish()
        }

        binding.btnLogin.setOnClickListener() {
            if (isValidate()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        Repository.login(binding.txtInputEmail.text.toString(), binding.txtInputPassword.text.toString()).collect {
                            withContext(Dispatchers.Main) {
                                intent = Intent(this@LoginActivity, MainActivity::class.java)

                                Repository.saveUserInfo(this@LoginActivity, it)
                                Repository.saveChecked(this@LoginActivity, binding.remember.isChecked)

                                startActivity(intent)
                            }
                        }

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

        }

    }

    //TODO: Abstract this methods in single file because they appear in more files
    private fun isValidate(): Boolean = validateUsername() && validatePassword()

    private fun validateUsername(): Boolean {
        if (binding.txtInputEmail.text.toString().trim().isEmpty()) {
            binding.txtLayoutEmail.error = getString(R.string.required_field)
            binding.txtLayoutEmail.requestFocus()
            return false
        } else {
            binding.txtLayoutEmail.isErrorEnabled = false
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
        } else {
            binding.txtLayoutPassword.isErrorEnabled = false
        }

        return true
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}