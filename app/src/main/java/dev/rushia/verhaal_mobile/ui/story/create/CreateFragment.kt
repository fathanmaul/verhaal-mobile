package dev.rushia.verhaal_mobile.ui.story.create

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yalantis.ucrop.UCrop
import dev.rushia.verhaal_mobile.R
import dev.rushia.verhaal_mobile.data.local.AuthPreferences
import dev.rushia.verhaal_mobile.data.local.dataStore
import dev.rushia.verhaal_mobile.databinding.FragmentCreateBinding
import dev.rushia.verhaal_mobile.ui.welcome.WelcomeViewModel
import dev.rushia.verhaal_mobile.utils.getImageUri
import dev.rushia.verhaal_mobile.utils.reduceFileImage
import dev.rushia.verhaal_mobile.utils.uriToFile
import dev.rushia.verhaal_mobile.viewmodel.ViewModelFactory

class CreateFragment : Fragment() {

    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!

    private var currentImageUri: Uri? = null
    private val viewModel = CreateViewModel()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var location: Array<Float?> = arrayOf(null, null)

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[REQUIRED_PERMISSION] == true -> {
                    startCamera()
                }

                permissions[REQUIRED_PERMISSION_FINE_LOCATION] == true -> {
                    getMyLocation()
                }

                permissions[REQUIRED_PERMISSION_COARSE_LOCATION] == true -> {
                    getMyLocation()
                }

                else -> {
                    showToast("Permission denied")
                }
            }
        }

    private fun cameraPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private fun locationPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this.requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(getString(R.string.create_story))

        val pref = AuthPreferences.getInstance(this.requireContext().dataStore)
        val welcomeVW = ViewModelProvider(
            this,
            ViewModelFactory(this.requireActivity(), pref)
        )[WelcomeViewModel::class.java]

        viewModel.isLoading.observe(viewLifecycleOwner) {
            isLoading(it)
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireContext())

        with(binding) {
            buttonAddMedia.setOnClickListener {
                startGallery()
            }
            buttonSubmit.setOnClickListener {
                if (editTextStory.text.toString().isEmpty()) {
                    showToast("Please add title first.")
                } else if (currentImageUri == null) {
                    showToast("Please add image first.")
                } else {
                    val uriToImage = uriToFile(requireContext(), currentImageUri!!)
                    uriToImage.reduceFileImage()
                    showToast(getString(R.string.uploading))
                    welcomeVW.getAuthToken().observe(viewLifecycleOwner) {
                        if (binding.checkboxLocation.isChecked) {
                            viewModel.uploadWithLocation(
                                description = editTextStory.text.toString(),
                                imageFile = uriToImage,
                                token = it ?: "",
                                lon = location[1] ?: 0f,
                                lat = location[0] ?: 0f
                            )
                        } else {
                            viewModel.upload(
                                editTextStory.text.toString(),
                                uriToImage,
                                token = it ?: ""
                            )
                        }
                        viewModel.isSuccess.observe(viewLifecycleOwner) { success ->
                            if (success) {
                                showToast(getString(R.string.done_uploading))
                            } else {
                                showToast(getString(R.string.failed_uploading))
                            }
                        }
                    }
                    view.findNavController().navigateUp()
                }
            }
            buttonAddCamera.setOnClickListener {
                startCamera()
            }
            checkboxLocation.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    getMyLocation()
                }
            }
        }
    }

    private fun getMyLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    showToast("Latitude: ${it.latitude}, Longitude: ${it.longitude}")
                    location[0] = it.latitude.toFloat()
                    location[1] = it.longitude.toFloat()
                } else {
                    showToast(getString(R.string.location_not_found_please_try_again))
                    binding.checkboxLocation.isChecked = false
                }
            }
        } else {
            binding.checkboxLocation.isChecked = false
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }


    private fun setupToolbar(title: String) {
        activity?.findViewById<TextView>(R.id.titleToolbar)?.text = title
        activity?.findViewById<ImageView>(R.id.back)?.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun createTempFile(): java.io.File {
        val tempFileName = "temp_${System.currentTimeMillis()}"
        return java.io.File.createTempFile(tempFileName, ".jpg", requireContext().cacheDir)
    }

    private fun launchUCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(createTempFile())
        val uCrop = UCrop.of(sourceUri, destinationUri).withAspectRatio(1f, 1f)
        uCrop.getIntent(requireContext()).let {
            uCropLauncher.launch(it)
        }
    }

    private fun startGallery() {
        binding.buttonAddMedia.isEnabled = false
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            launchUCrop(uri)
        } else {
            showToast("Failed to get image from gallery.")
            binding.buttonAddMedia.isEnabled = true
        }
    }

    private val uCropLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultUri = UCrop.getOutput(result.data!!)
                if (resultUri != null) {
                    currentImageUri = resultUri
                    showImage()
                    binding.buttonAddMedia.isEnabled = true
                }
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                binding.buttonAddMedia.isEnabled = true
            } else {
                binding.buttonAddMedia.isEnabled = true
            }
        }

    private fun startCamera() {
        if (cameraPermissionsGranted()) {
            currentImageUri = getImageUri(requireContext())
            launcherCamera.launch(currentImageUri)
        } else {
            requestPermissionLauncher.launch(
                arrayOf(REQUIRED_PERMISSION)
            )
        }
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess: Boolean ->
        if (isSuccess) {
            launchUCrop(currentImageUri!!)
        } else {
            showToast("Failed taking picture")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.storyImagePreview.setImageURI(it)
            binding.storyImagePreview.visibility = View.VISIBLE
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun isLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
            binding.buttonSubmit.isEnabled = false
            binding.buttonAddMedia.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.buttonSubmit.isEnabled = true
            binding.buttonAddMedia.isEnabled = true
        }
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        private const val REQUIRED_PERMISSION_FINE_LOCATION =
            Manifest.permission.ACCESS_FINE_LOCATION
        private const val REQUIRED_PERMISSION_COARSE_LOCATION =
            Manifest.permission.ACCESS_COARSE_LOCATION
    }
}