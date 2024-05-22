package dev.rushia.verhaal_mobile.ui.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.rushia.verhaal_mobile.R
import dev.rushia.verhaal_mobile.components.Button
import dev.rushia.verhaal_mobile.databinding.ActivityRegisterBinding
import dev.rushia.verhaal_mobile.ui.auth.login.LoginActivity
import dev.rushia.verhaal_mobile.utils.Validation


class RegisterActivity : AppCompatActivity() {

    private val viewModel = RegisterViewModel()
    private var registerSuccess: Boolean = false

    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playAnimation()

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.error.observe(this) {
            if (viewModel.error.value.toString().isEmpty()) {
                showToast("Register Success")
                registerSuccess = true
                openSheetDialog(
                    getString(R.string.success),
                    getString(R.string.register_success)
                )
            } else {
                showToast(it)
                registerSuccess = false
                openSheetDialog(
                    title = getString(R.string.an_error_occurred),
                    message = it
                )
            }
        }

        setupToolbar()
        with(binding) {
            buttonRegister.setOnClickListener {
                val name = editTextName.text.toString()
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()
                val confirmPassword = editTextConfirmPassword.text.toString()
                if (editTextPassword.error != null || editTextConfirmPassword.error != null) {
                    openSheetDialog(
                        title = getString(R.string.an_error_occurred),
                        message = getString(R.string.password_length_error)
                    )
                    return@setOnClickListener
                }

                if (!validate(name, email, password, confirmPassword)) return@setOnClickListener
                viewModel.register(name, email, password)
            }
        }
    }

    private fun setupToolbar() {
        findViewById<ImageView>(R.id.back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        findViewById<TextView>(R.id.titleToolbar).text = ""
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
            binding.buttonRegister.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.buttonRegister.isEnabled = true
        }
    }

    private fun toLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun openSheetDialog(
        title: String,
        message: String,
    ) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        view.findViewById<TextView>(R.id.bottom_sheet_heading).text = title
        view.findViewById<TextView>(R.id.bottom_sheet_sub_heading).text = message
        view.findViewById<Button>(R.id.bottom_sheet_button).text = if (registerSuccess) {
            getString(R.string.login)
        } else {
            getString(R.string.okay)
        }
        view.findViewById<Button>(R.id.bottom_sheet_button).setOnClickListener {
            dialog.dismiss()
            if (registerSuccess) {
                toLogin()
            }
        }
        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun validate(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            openSheetDialog(
                title = getString(R.string.an_error_occurred),
                message = getString(R.string.fill_all_field)
            )
            return false
        }
        if (password != confirmPassword) {
            openSheetDialog(
                title = getString(R.string.an_error_occurred),
                message = getString(R.string.password_not_match)
            )
            showToast(getString(R.string.password_not_match))
            return false
        }
        if (!Validation.isEmailValid(email)) {
            openSheetDialog(
                title = getString(R.string.an_error_occurred),
                message = getString(R.string.email_error)
            )
            return false
        }
        return true
    }

    private fun playAnimation() {
        val editTextName =
            ObjectAnimator.ofFloat(binding.editTextName, View.ALPHA, 1f).setDuration(400)
        val editTextEmail =
            ObjectAnimator.ofFloat(binding.editTextEmail, View.ALPHA, 1f).setDuration(400)
        val editTextPassword =
            ObjectAnimator.ofFloat(binding.editTextPassword, View.ALPHA, 1f).setDuration(400)
        val editTextConfirmPassword =
            ObjectAnimator.ofFloat(binding.editTextConfirmPassword, View.ALPHA, 1f).setDuration(400)
        AnimatorSet().apply {
            playSequentially(
                editTextName,
                editTextEmail,
                editTextPassword,
                editTextConfirmPassword
            )
            start()
        }
    }


}