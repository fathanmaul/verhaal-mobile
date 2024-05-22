package dev.rushia.verhaal_mobile.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.rushia.verhaal_mobile.R
import dev.rushia.verhaal_mobile.data.local.AuthPreferences
import dev.rushia.verhaal_mobile.data.local.dataStore
import dev.rushia.verhaal_mobile.databinding.ActivityProfileBinding
import dev.rushia.verhaal_mobile.ui.welcome.WelcomeActivity
import dev.rushia.verhaal_mobile.ui.auth.AuthViewModel
import dev.rushia.verhaal_mobile.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var authPreferences: AuthPreferences
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(this, authPreferences)
    }

    private val pref = AuthPreferences.getInstance(this.dataStore)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_profile)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        authPreferences = AuthPreferences.getInstance(
            this.dataStore
        )
        authViewModel.authToken.observe(this) {
            if (it.isNullOrEmpty()) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }




        with(binding) {
            btnLogout.setOnClickListener {
                MaterialAlertDialogBuilder(this@ProfileActivity)
                    .setTitle(getString(R.string.logout))
                    .setMessage(getString(R.string.logout_message))
                    .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                        lifecycleScope.launch {
                            pref.clearAuthToken()
                            Toast.makeText(
                                this@ProfileActivity,
                                getString(R.string.logout_success),
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    }
                    .setNegativeButton(getString(R.string.no)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
            authViewModel.userName.observe(this@ProfileActivity) {
                tvName.text = it
            }

        }
    }
}