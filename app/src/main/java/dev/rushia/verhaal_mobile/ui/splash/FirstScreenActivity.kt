package dev.rushia.verhaal_mobile.ui.splash

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.rushia.verhaal_mobile.R
import dev.rushia.verhaal_mobile.data.local.AuthPreferences
import dev.rushia.verhaal_mobile.data.local.dataStore
import dev.rushia.verhaal_mobile.databinding.ActivitySplashBinding
import dev.rushia.verhaal_mobile.ui.story.MainActivity
import dev.rushia.verhaal_mobile.ui.welcome.WelcomeActivity
import dev.rushia.verhaal_mobile.utils.Const
import dev.rushia.verhaal_mobile.ui.auth.AuthViewModel
import dev.rushia.verhaal_mobile.viewmodel.ViewModelFactory

class FirstScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var authPreferences: AuthPreferences
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(this, authPreferences)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var token: String?
        authPreferences = AuthPreferences.getInstance(
            this.dataStore
        )
        authViewModel.authToken.observe(this) {
            token = it
            Handler(mainLooper).postDelayed({
                if (token != null) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                }
            }, Const.DELAY_SPLASH)
        }
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.relativeLayoutLogo, View.TRANSLATION_X, -50f, 0f).apply {
            duration = 1000
            start()
        }
    }
}