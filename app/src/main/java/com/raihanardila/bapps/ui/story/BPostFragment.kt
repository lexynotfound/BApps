package com.raihanardila.bapps.ui.story

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.raihanardila.bapps.databinding.FragmentBPostBinding
import com.raihanardila.bapps.utils.PermissionUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

@Suppress("DEPRECATION")
class BPostFragment : Fragment() {

    private var _binding: FragmentBPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var currentPhotoPath: String

    private val launcherGallery = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        uri?.let {
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
            if (PermissionUtils.checkPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                getCurrentLocation()
            } else {
                requestLocationPermission()
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
            Toast.makeText(context, "Camera permission is required to take pictures", Toast.LENGTH_SHORT).show()
        }
        PermissionUtils.requestPermission(requireActivity(), Manifest.permission.CAMERA, PermissionUtils.REQUEST_CAMERA_PERMISSION)
    }

    private fun requestGalleryPermission() {
        val storagePermissions = getStoragePermission()
        if (PermissionUtils.shouldShowRationale(requireActivity(), storagePermissions[0])) {
            Toast.makeText(context, "Storage permission is required to select pictures", Toast.LENGTH_SHORT).show()
        }
        PermissionUtils.requestPermission(requireActivity(), storagePermissions[0], PermissionUtils.REQUEST_GALLERY_PERMISSION)
    }

    private fun requestLocationPermission() {
        if (PermissionUtils.shouldShowRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(context, "Location permission is required to get current location", Toast.LENGTH_SHORT).show()
        }
        PermissionUtils.requestPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION, PermissionUtils.REQUEST_LOCATION_PERMISSION)
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

    private fun getCurrentLocation() {
        // Implement location retrieval logic
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Bacot")
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


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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
                    val uri = FileProvider.getUriForFile(requireContext(), "com.raihanardila.bapps.fileprovider", file)
                    binding.ivPreviewImage.setImageURI(uri)
                    binding.cardViewPreview.visibility = View.VISIBLE
                    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                    val contentUri = Uri.fromFile(file)
                    mediaScanIntent.data = contentUri
                    requireActivity().sendBroadcast(mediaScanIntent)
                }
                PermissionUtils.REQUEST_GALLERY_PERMISSION -> {
                    val selectedImage: Uri? = data?.data
                    selectedImage?.let {
                        binding.ivPreviewImage.setImageURI(it)
                        binding.cardViewPreview.visibility = View.VISIBLE
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
