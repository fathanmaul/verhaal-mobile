package dev.rushia.verhaal_mobile.ui.story.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import dev.rushia.verhaal_mobile.R
import dev.rushia.verhaal_mobile.data.local.AuthPreferences
import dev.rushia.verhaal_mobile.data.local.dataStore
import dev.rushia.verhaal_mobile.data.remote.response.StoryResponse
import dev.rushia.verhaal_mobile.databinding.FragmentDetailBinding
import dev.rushia.verhaal_mobile.ui.welcome.WelcomeViewModel
import dev.rushia.verhaal_mobile.viewmodel.ViewModelFactory

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel = DetailViewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(getString(R.string.detail_story))
        viewModel.isLoading.observe(viewLifecycleOwner) {
            isLoading(it)
        }

        val pref = AuthPreferences.getInstance(this.requireContext().dataStore)
        val welcomeVW = ViewModelProvider(
            this,
            ViewModelFactory(this.requireActivity(), pref)
        )[WelcomeViewModel::class.java]
        welcomeVW.getAuthToken().observe(viewLifecycleOwner) {
            viewModel.getDetailStory(
                it.first(),
                DetailFragmentArgs.fromBundle(arguments as Bundle).storyId
            )
        }

        viewModel.story.observe(viewLifecycleOwner) {
            setDetailStory(it)
        }

    }

    private fun setToolbar(title: String) {
        activity?.findViewById<TextView>(R.id.titleToolbar)?.text = title
        activity?.findViewById<ImageView>(R.id.back)?.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setDetailStory(story: StoryResponse) {
        binding.storyAuthor.text = story.story.name
        binding.storyDescription.text = story.story.description
        Glide.with(this).load(
            story.story.photoUrl
        ).into(binding.storyImage)
    }

    private fun isLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

}