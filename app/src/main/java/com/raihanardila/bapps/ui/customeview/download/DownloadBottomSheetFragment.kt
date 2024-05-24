package com.raihanardila.bapps.ui.customeview.download

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.raihanardila.bapps.BuildConfig
import com.raihanardila.bapps.databinding.FragmentDownloadBottomSheetBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException

class DownloadBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDownloadBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var photoUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadBottomSheetBinding.inflate(inflater, container, false)

        // Handle edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemBars.bottom)
            insets
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set click listener on the download icon
        binding.downloadIcon.setOnClickListener {
            Log.d(TAG, "Download icon clicked")
            handleDownloadClick()
        }

        // Set click listener on the CardView
        binding.cvDownload.setOnClickListener {
            Log.d(TAG, "CardView clicked")
            handleDownloadClick()
        }
    }

    private fun handleDownloadClick() {
        photoUrl?.let {
            if (isStoragePermissionGranted()) {
                downloadPhoto(it)
            } else {
                requestStoragePermission()
            }
        }
    }

    private fun isStoragePermissionGranted(): Boolean {
        val granted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        Log.d(TAG, "Storage permission granted: $granted")
        return granted
    }

    private fun requestStoragePermission() {
        Log.d(TAG, "Requesting storage permission")
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_REQUEST_CODE
        )
    }

    private fun downloadPhoto(url: String) {
        Log.d(TAG, "Starting photo download: $url")
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val fileName = url.substring(url.lastIndexOf("/") + 1)
                val storageDir = File(
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "Bacot"
                )
                if (!storageDir.exists()) {
                    storageDir.mkdirs()
                }
                val file = File(storageDir, fileName)
                Log.d(TAG, "Saving photo as: $file")

                try {
                    val sink = file.sink().buffer()
                    sink.writeAll(response.body!!.source())
                    sink.close()

                    val uri: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "${BuildConfig.APPLICATION_ID}.fileprovider",
                        file
                    )
                    requireActivity().sendBroadcast(
                        Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            uri
                        )
                    )

                    showToast("Photo downloaded successfully")
                    Log.d(TAG, "Photo downloaded successfully")
                } catch (e: IOException) {
                    showToast("Failed to save photo")
                    Log.e(TAG, "Failed to save photo", e)
                }
            } else {
                showToast("Failed to download photo")
                Log.e(TAG, "Failed to download photo: ${response.message}")
            }
        }
    }

    private fun showToast(message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    fun setPhotoUrl(url: String) {
        photoUrl = url
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Storage permission granted by user")
                photoUrl?.let { downloadPhoto(it) }
            } else {
                showToast("Storage permission denied")
                Log.d(TAG, "Storage permission denied by user")
            }
        }
    }

    companion object {
        private const val TAG = "DownloadBottomSheet"
        private const val STORAGE_PERMISSION_REQUEST_CODE = 1001
    }
}
