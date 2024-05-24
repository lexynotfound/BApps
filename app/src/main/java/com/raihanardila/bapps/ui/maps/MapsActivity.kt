/*
package com.raihanardila.bapps.ui.maps

import android.location.Geocoder
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.raihanardila.bapps.R
import com.raihanardila.bapps.core.data.local.prefrences.AuthPreferences
import com.raihanardila.bapps.core.data.model.StoriesBModel
import com.raihanardila.bapps.core.data.viewmodel.BMainViewModel
import com.raihanardila.bapps.databinding.ActivityMapsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel: BMainViewModel by viewModel()
    private lateinit var authPreferences: AuthPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.frame_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        authPreferences = AuthPreferences(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val token = authPreferences.getToken()
        if (token != null) {
            viewModel.getStoriesMap(token).observe(this, Observer { storiesList ->
                storiesList.forEach { story ->
                    val location = LatLng(story.lat, story.lon)
                    val marker = mMap.addMarker(MarkerOptions().position(location).title(story.name))
                    marker?.tag = story
                }

                val firstStory = storiesList.firstOrNull()
                firstStory?.let {
                    val firstLocation = LatLng(it.lat, it.lon)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 10f))
                }

                mMap.setOnMarkerClickListener { marker ->
                    val story = marker.tag as? StoriesBModel
                    story?.let {
                        resolveAddressAndShowBottomSheet(it)
                    }
                    true
                }
            })
        }
    }

    private fun resolveAddressAndShowBottomSheet(story: StoriesBModel) {
        lifecycleScope.launch(Dispatchers.IO) {
            val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
            val addresses = geocoder.getFromLocation(story.lat, story.lon, 1)
            val address = addresses?.firstOrNull()?.getAddressLine(0) ?: "Address not found"

            withContext(Dispatchers.Main) {
                showStoryBottomSheet(story.name, address, story.description, story.photoUrl)
            }
        }
    }

    private fun showStoryBottomSheet(title: String, address: String, description: String, image: String) {
        val bottomSheet = StoryBottomSheetFragment.newInstance(title, address, description, image)
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
    }
}
*/
