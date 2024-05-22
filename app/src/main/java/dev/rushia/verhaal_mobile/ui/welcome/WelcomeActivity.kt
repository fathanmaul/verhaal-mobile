package dev.rushia.verhaal_mobile.ui.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.rushia.verhaal_mobile.R
import dev.rushia.verhaal_mobile.databinding.ActivityWelcomeBinding
import dev.rushia.verhaal_mobile.ui.auth.login.LoginActivity
import dev.rushia.verhaal_mobile.ui.auth.register.RegisterActivity

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        playAnimation()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.buttonRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageLogo, View.TRANSLATION_Y, -30f, 0f).apply {
            duration = 2500
        }.start()

        val tagline = ObjectAnimator.ofFloat(binding.textTagline, View.ALPHA, 1f).setDuration(400)
        val subTagline =
            ObjectAnimator.ofFloat(binding.textSubTagline, View.ALPHA, 1f).setDuration(400)

        AnimatorSet().apply {
            playSequentially(tagline, subTagline)
            start()
        }
    }
}