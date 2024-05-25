package dev.rushia.verhaal_mobile.ui.story.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dev.rushia.verhaal_mobile.R
import dev.rushia.verhaal_mobile.data.local.AuthPreferences
import dev.rushia.verhaal_mobile.data.local.dataStore
import dev.rushia.verhaal_mobile.data.local.database.StoryDatabase
import dev.rushia.verhaal_mobile.data.remote.retrofit.ApiConfig
import dev.rushia.verhaal_mobile.data.repository.StoryRepository
import dev.rushia.verhaal_mobile.databinding.FragmentHomeBinding
import dev.rushia.verhaal_mobile.ui.maps.MapsActivity
import dev.rushia.verhaal_mobile.ui.profile.ProfileActivity
import dev.rushia.verhaal_mobile.utils.Const
import dev.rushia.verhaal_mobile.ui.auth.AuthViewModel
import dev.rushia.verhaal_mobile.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeStoryViewModel
    private lateinit var authPreferences: AuthPreferences
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(requireActivity(), authPreferences)
    }


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
        isLoading(false)
        authPreferences = AuthPreferences.getInstance(
            this.requireContext().dataStore
        )
        authViewModel.authToken.observe(viewLifecycleOwner) {
            initializeViewModel(it!!)
            homeViewModel.getStories()
            getData()
            binding.swipeRefreshLayout.setOnRefreshListener {
                homeViewModel.getStories()
                binding.swipeRefreshLayout.isRefreshing = false
                Handler(Looper.getMainLooper()).postDelayed({
                    Toast.makeText(
                        this.requireContext(),
                        getString(R.string.refreshed),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.rvStories.scrollToPosition(0)
                }, Const.DELAY_REFRESH)
            }
        }

        with(binding) {
            fabAddStory.setOnClickListener { view ->
                view.findNavController().navigate(R.id.action_homeFragment_to_createFragment)
            }
            ivProfile.setOnClickListener {
                toProfile()
            }
            ivMaps.setOnClickListener {
                toMaps()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        binding.rvStories.scrollToPosition(0)
        authViewModel.authToken.observe(viewLifecycleOwner) {
            initializeViewModel(it!!)
            homeViewModel.getStories()
            getData()
        }
    }

    private fun getData() {
        lifecycleScope.launch {
            val adapter = StoryAdapter()
            binding.rvStories.adapter = adapter.withLoadStateFooter(
                footer = LoadingStateAdapter { adapter.retry() }
            )
            homeViewModel.story.observe(viewLifecycleOwner) {
                adapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
    }

    private fun initializeViewModel(token: String) {
        homeViewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(
                StoryRepository(
                    apiService = ApiConfig.getApiService(), token = "Bearer $token",
                    storyDatabase = StoryDatabase.getDatabase(this.requireContext())
                )
            )
        )[HomeStoryViewModel::class.java]
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

    private fun toMaps() {
        startActivity(Intent(this.requireContext(), MapsActivity::class.java))
    }
}