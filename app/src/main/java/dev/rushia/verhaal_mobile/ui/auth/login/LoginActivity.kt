package dev.rushia.verhaal_mobile.ui.auth.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.rushia.verhaal_mobile.R
import dev.rushia.verhaal_mobile.components.Button
import dev.rushia.verhaal_mobile.databinding.ActivityLoginBinding
import dev.rushia.verhaal_mobile.ui.story.MainActivity

import dev.rushia.verhaal_mobile.utils.Validation

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var loginSuccess: Boolean = false

    private val viewModel = LoginViewModel(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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
            if (!it) {
                loginSuccess = true
                openSheetDialog(
                    getString(R.string.success),
                    getString(R.string.login_success)
                )
            } else {
                loginSuccess = false
                openSheetDialog(
                    title = getString(R.string.an_error_occurred),
                    message = getString(R.string.email_or_password_wrong)
                )
            }
        }
        setupToolbar()
        with(binding) {
            buttonLogin.setOnClickListener {
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()
                if (!validate(email, password)) return@setOnClickListener
                viewModel.login(email, password)

            }
        }
    }

    private fun setupToolbar() {
        findViewById<ImageView>(R.id.back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        findViewById<TextView>(R.id.titleToolbar).text = ""
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.buttonLogin.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.buttonLogin.isEnabled = true
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun validate(email: String, password: String): Boolean {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            return if (Validation.loginValidate(email = email, password = password)) {
                true
            } else {
                openSheetDialog(
                    title = getString(R.string.an_error_occurred),
                    message = getString(R.string.email_and_password_invalid)
                )
                false
            }
        } else {
            openSheetDialog(
                title = getString(R.string.an_error_occurred),
                message = getString(R.string.fill_all_field)
            )
            return false
        }
    }

    private fun openSheetDialog(
        title: String,
        message: String,
    ) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        view.findViewById<TextView>(R.id.bottom_sheet_heading).text = title
        view.findViewById<TextView>(R.id.bottom_sheet_sub_heading).text = message
        view.findViewById<Button>(R.id.bottom_sheet_button).text = if (loginSuccess) {
            getString(R.string._continue)
        } else {
            getString(R.string.okay)
        }
        view.findViewById<Button>(R.id.bottom_sheet_button).setOnClickListener {
            dialog.dismiss()
            if (loginSuccess) {
                toHome()
            }
        }
        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun playAnimation() {
        val loginHeading =
            ObjectAnimator.ofFloat(binding.loginHeading, View.ALPHA, 1f).setDuration(200)
        val loginSubHeading =
            ObjectAnimator.ofFloat(binding.loginSubHeading, View.ALPHA, 1f).setDuration(500)
        val loginEmailEditText =
            ObjectAnimator.ofFloat(binding.editTextEmail, View.ALPHA, 1f).setDuration(200)
        val loginPasswordEditText =
            ObjectAnimator.ofFloat(binding.editTextPassword, View.ALPHA, 1f).setDuration(200)


        AnimatorSet().apply {
            playSequentially(
                loginHeading,
                loginSubHeading,
                loginEmailEditText,
                loginPasswordEditText
            )
            start()
        }
    }

    private fun toHome() {
        startActivity(
            Intent(this, MainActivity::class.java).addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            )
        )
        finish()
    }
}