package dev.rushia.verhaal_mobile.ui.story.home

import android.content.Intent
import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dev.rushia.verhaal_mobile.R
import dev.rushia.verhaal_mobile.data.local.AuthPreferences
import dev.rushia.verhaal_mobile.data.local.dataStore
import dev.rushia.verhaal_mobile.data.remote.response.StoryItem
import dev.rushia.verhaal_mobile.databinding.FragmentHomeBinding
import dev.rushia.verhaal_mobile.ui.profile.ProfileActivity
import dev.rushia.verhaal_mobile.ui.welcome.WelcomeActivity
import dev.rushia.verhaal_mobile.ui.welcome.WelcomeViewModel
import dev.rushia.verhaal_mobile.viewmodel.ViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel = HomeViewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

        val layoutManager = LinearLayoutManager(this.context)
        binding.rvStories.layoutManager = layoutManager
        viewModel.isLoading.observe(viewLifecycleOwner) {
            isLoading(it)
        }

        val pref = AuthPreferences.getInstance(this.requireContext().dataStore)
        val welcomeVW = ViewModelProvider(
            this,
            ViewModelFactory(this.requireActivity(), pref)
        )[WelcomeViewModel::class.java]

        welcomeVW.getAuthToken().observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                startActivity(
                    Intent(this.requireContext(), WelcomeActivity::class.java).addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                )
            } else {
                viewModel.getListStories(it.first())
            }
        }

        viewModel.listStories.observe(viewLifecycleOwner) {
            setStories(it)
        }

        binding.fabAddStory.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_homeFragment_to_createFragment)
        }
        binding.ivProfile.setOnClickListener {
            toProfile()
        }
    }

    private fun setStories(story: List<StoryItem>) {
        val adapter = StoryAdapter()
        adapter.submitList(story)
        binding.rvStories.adapter = adapter
    }

    private fun isLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
            binding.rvStories.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.rvStories.visibility = View.VISIBLE
        }
    }

    private fun toProfile() {
        startActivity(Intent(this.requireContext(), ProfileActivity::class.java))
    }
}