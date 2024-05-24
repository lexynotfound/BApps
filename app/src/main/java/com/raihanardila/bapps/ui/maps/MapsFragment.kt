package com.raihanardila.bapps.ui.maps

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.raihanardila.bapps.R
import com.raihanardila.bapps.core.data.local.prefrences.AuthPreferences
import com.raihanardila.bapps.core.data.model.StoriesBModel
import com.raihanardila.bapps.core.data.viewmodel.BMainViewModel
import com.raihanardila.bapps.databinding.FragmentMapsBinding
import com.raihanardila.bapps.utils.GeocodingUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BMainViewModel by viewModel()
    private lateinit var authPreferences: AuthPreferences
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<MaterialCardView>
    private var selectedStory: StoriesBModel? = null // Define selectedStory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide system bars for full-screen mode
        hideSystemBars()

        authPreferences = AuthPreferences(requireContext())
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetCardView)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = 200

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        checkPermissions()
        checkGPS()

        // Check if there are arguments passed to the fragment
        val args = arguments
        if (args != null) {
            val name = args.getString("name") ?: ""
            val description = args.getString("description") ?: ""
            val lat = args.getDouble("lat")
            val lon = args.getDouble("lon")
            val photoUrl = args.getString("photoUrl") ?: ""
            val id = args.getString("id") ?: ""
            val createdAt = args.getString("createdAt") ?: ""

            selectedStory = StoriesBModel(name, id, description, photoUrl, createdAt, lat, lon)
        }
    }

    private fun hideSystemBars() {
        // Adjust padding to handle system gestures and buttons
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkGPS() {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder(requireContext())
                .setMessage("GPS is disabled. Do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .show()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true

        googleMap.setOnMarkerClickListener { marker ->
            val story = marker.tag as? StoriesBModel
            story?.let {
                showBottomSheet(it)
            }
            true
        }

        val token = authPreferences.getToken()
        if (token != null) {
            viewModel.getStoriesMap(token).observe(viewLifecycleOwner, Observer { storiesList ->
                googleMap.clear()
                storiesList.forEach { story ->
                    val location = LatLng(story.lat, story.lon)
                    val marker =
                        googleMap.addMarker(MarkerOptions().position(location).title(story.name))
                    marker?.tag = story
                }

                val firstStory = storiesList.firstOrNull()
                firstStory?.let {
                    val firstLocation = LatLng(it.lat, it.lon)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 10f))
                }

                // Automatically select the story passed from HomeFragment
                selectedStory?.let {
                    val selectedLocation = LatLng(it.lat, it.lon)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 15f))
                    showBottomSheet(it)
                }
            })
        }
    }

    private fun addMarker(
        location: LatLng,
        title: String,
        snippet: String,
        photoUrl: String,
        id: String,
        createdAt: String
    ): Marker? {
        val marker =
            googleMap.addMarker(MarkerOptions().position(location).title(title).snippet(snippet))
        val story = StoriesBModel(
            id = id,
            name = title,
            description = snippet,
            photoUrl = photoUrl,
            createdAt = createdAt,
            lat = location.latitude,
            lon = location.longitude
        )
        marker?.tag = story
        return marker
    }

    private fun showBottomSheet(story: StoriesBModel) {
        binding.apply {
            bottomSheet.findViewById<TextView>(R.id.nameDetails).text = story.name
            bottomSheet.findViewById<TextView>(R.id.locationDetails).text =
                getLocationName(story.lat, story.lon)
            bottomSheet.findViewById<TextView>(R.id.descPost).text = story.description
            val imageView = bottomSheet.findViewById<ImageView>(R.id.imagePreview)
            val cardView =
                bottomSheet.findViewById<androidx.cardview.widget.CardView>(R.id.cardView)
            if (story.photoUrl.isNotEmpty()) {
                Glide.with(this@MapsFragment).load(story.photoUrl).into(imageView)
                imageView.visibility = View.VISIBLE
                cardView.visibility = View.VISIBLE
            } else {
                imageView.visibility = View.GONE
                cardView.visibility = View.GONE
            }
        }
        selectedStory = story
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getLocationName(lat: Double, lon: Double): String {
        return GeocodingUtils.getLocationName(requireContext(), lat, lon)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
