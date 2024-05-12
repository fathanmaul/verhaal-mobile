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

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Permission request granted")
            } else {
                showToast("Permission request denied")
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

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
                        viewModel.upload(
                            editTextStory.text.toString(),
                            uriToImage,
                            token = it.first()
                        )
                    }
                    showToast(getString(R.string.done_uploading))
                    view.findNavController().navigateUp()
                }
            }
            buttonAddCamera.setOnClickListener {
                startCamera()
            }
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
        if (allPermissionsGranted()) {
            currentImageUri = getImageUri(requireContext())
            launcherCamera.launch(currentImageUri)
        } else {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
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
    }
}