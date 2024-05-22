package dev.rushia.verhaal_mobile.ui.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dev.rushia.verhaal_mobile.R
import dev.rushia.verhaal_mobile.data.local.AuthPreferences
import dev.rushia.verhaal_mobile.data.local.dataStore
import dev.rushia.verhaal_mobile.databinding.ActivityMapsBinding
import dev.rushia.verhaal_mobile.ui.auth.AuthViewModel
import dev.rushia.verhaal_mobile.viewmodel.ViewModelFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var authPreferences: AuthPreferences
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(this, authPreferences)
    }
    private val mapsViewModel = MapsViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        authPreferences = AuthPreferences.getInstance(this.dataStore)
        authViewModel.authToken.observe(this) {
            mapsViewModel.getStoryWithLocation(it!!)
            mapsViewModel.listStories.observe(this) { list ->
                list.forEach { story ->
                    val lat = story.lat
                    val lng = story.lon
                    if (lat != null && lng != null) {
                        val location = LatLng(lat, lng)
                        mMap.addMarker(MarkerOptions().position(location).title(story.name))
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                    }
                }
            }
        }

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
    }
}