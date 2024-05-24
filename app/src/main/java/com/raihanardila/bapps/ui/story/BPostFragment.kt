package com.raihanardila.bapps.ui.story

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.raihanardila.bapps.R
import com.raihanardila.bapps.core.data.viewmodel.BStoriesViewModel
import com.raihanardila.bapps.databinding.FragmentBPostBinding
import com.raihanardila.bapps.utils.GeocodingUtils
import com.raihanardila.bapps.utils.PermissionUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class BPostFragment : Fragment() {

    private var _binding: FragmentBPostBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BStoriesViewModel by viewModel()

    private lateinit var currentPhotoPath: String
    private var selectedImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double? = null
    private var longitude: Double? = null

    private val launcherGallery =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                binding.ivPreviewImage.setImageURI(it)
                binding.cardViewPreview.visibility = View.VISIBLE
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.ivCamera.setOnClickListener {
            if (PermissionUtils.checkPermission(requireContext(), Manifest.permission.CAMERA)) {
                openCamera()
            } else {
                requestCameraPermission()
            }
        }

        binding.ivAddImage.setOnClickListener {
            val storagePermissions = getStoragePermission()
            if (PermissionUtils.checkPermission(requireContext(), storagePermissions[0])) {
                openGallery()
            } else {
                requestGalleryPermission()
            }
        }

        binding.ivAddLocation.setOnClickListener {
            if (PermissionUtils.checkPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                checkGPSAndFetchLocation()
            } else {
                requestLocationPermission()
            }
        }

        binding.btnPost.setOnClickListener {
            if (selectedImageUri != null) {
                val descriptionText = binding.etPostText.text.toString()
                if (descriptionText.isNotBlank()) {
                    uploadImage(selectedImageUri!!, descriptionText)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please enter a description",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getStoragePermission(): Array<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            }

            else -> {
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun requestCameraPermission() {
        if (PermissionUtils.shouldShowRationale(requireActivity(), Manifest.permission.CAMERA)) {
            Toast.makeText(
                context,
                "Camera permission is required to take pictures",
                Toast.LENGTH_SHORT
            ).show()
        }
        PermissionUtils.requestPermission(
            requireActivity(),
            Manifest.permission.CAMERA,
            PermissionUtils.REQUEST_CAMERA_PERMISSION
        )
    }

    private fun requestGalleryPermission() {
        val storagePermissions = getStoragePermission()
        if (PermissionUtils.shouldShowRationale(requireActivity(), storagePermissions[0])) {
            Toast.makeText(
                context,
                "Storage permission is required to select pictures",
                Toast.LENGTH_SHORT
            ).show()
        }
        PermissionUtils.requestPermission(
            requireActivity(),
            storagePermissions[0],
            PermissionUtils.REQUEST_GALLERY_PERMISSION
        )
    }

    private fun requestLocationPermission() {
        if (PermissionUtils.shouldShowRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(
                context,
                "Location permission is required to get current location",
                Toast.LENGTH_SHORT
            ).show()
        }
        PermissionUtils.requestPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION,
            PermissionUtils.REQUEST_LOCATION_PERMISSION
        )
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            null
        }

        photoFile?.also {
            val photoURI = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                it
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        }

        startActivityForResult(intent, PermissionUtils.REQUEST_CAMERA_PERMISSION)
    }

    private fun openGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
    }

    private fun checkGPSAndFetchLocation() {
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
        } else {
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    val address =
                        GeocodingUtils.getLocationName(requireContext(), latitude, longitude)
                    binding.ivRecentLocation.visibility = View.VISIBLE
                    binding.tvLocation.visibility = View.VISIBLE
                    binding.tvLocation.text = address
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Unable to get current location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Failed to get location",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Bacot"
        )
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun resizeImage(file: File): File {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(file.path, options)
        val (width, height) = options.run { outWidth to outHeight }

        val maxSize = 1000
        var scaleFactor = 1

        if (width > height) {
            if (width > maxSize) scaleFactor = width / maxSize
        } else {
            if (height > maxSize) scaleFactor = height / maxSize
        }

        options.inJustDecodeBounds = false
        options.inSampleSize = scaleFactor

        val resizedBitmap = BitmapFactory.decodeFile(file.path, options)

        val resizedFile = createImageFile()
        val outputStream = FileOutputStream(resizedFile)
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        outputStream.flush()
        outputStream.close()

        return resizedFile
    }

    private fun uploadImage(uri: Uri, descriptionText: String) {
        binding.progressBar.visibility = View.VISIBLE

        val file = if (uri.scheme == "content") {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val tempFile = createImageFile()
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } else {
            File(currentPhotoPath)
        }

        val resizedFile = if (file.length() > 1_000_000) resizeImage(file) else file

        val requestFile = resizedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("photo", resizedFile.name, requestFile)
        val description = descriptionText.toRequestBody("text/plain".toMediaTypeOrNull())

        // Include latitude and longitude in the upload request
        val latBody = latitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        val lonBody = longitude?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

        viewModel.uploadImage(body, description, latBody, lonBody)
            .observe(viewLifecycleOwner) { response ->
                binding.progressBar.visibility = View.GONE

                if (response != null) {
                    if (!response.error) {
                        Toast.makeText(requireContext(), "Upload successful", Toast.LENGTH_SHORT)
                            .show()
                        findNavController().navigate(R.id.action_bpostFragment_to_homeFragment)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Upload failed: ${response.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Upload failed: Response is null",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionUtils.REQUEST_CAMERA_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openCamera()
                } else {
                    Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }

            PermissionUtils.REQUEST_GALLERY_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openGallery()
                } else {
                    Toast.makeText(context, "Storage permission denied", Toast.LENGTH_SHORT).show()
                }
            }

            PermissionUtils.REQUEST_LOCATION_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getCurrentLocation()
                } else {
                    Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PermissionUtils.REQUEST_CAMERA_PERMISSION -> {
                    val file = File(currentPhotoPath)
                    val uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.raihanardila.bapps.fileprovider",
                        file
                    )
                    binding.ivPreviewImage.setImageURI(uri)
                    binding.cardViewPreview.visibility = View.VISIBLE
                    // Don't call uploadImage here; call it when user confirms the upload action
                    selectedImageUri = uri
                    val mediaScanIntent = Intent(ACTION_MEDIA_SCANNER_SCAN_FILE)
                    val contentUri = Uri.fromFile(file)
                    mediaScanIntent.data = contentUri
                    requireActivity().sendBroadcast(mediaScanIntent)
                }

                PermissionUtils.REQUEST_GALLERY_PERMISSION -> {
                    val selectedImage: Uri? = data?.data
                    selectedImage?.let {
                        binding.ivPreviewImage.setImageURI(it)
                        binding.cardViewPreview.visibility = View.VISIBLE
                        // Don't call uploadImage here; call it when user confirms the upload action
                        selectedImageUri = it
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
