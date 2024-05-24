package com.raihanardila.bapps.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import java.io.IOException
import java.util.Locale

object GeocodingUtils {
    fun getLocationName(context: Context, lat: Double?, lon: Double?): String {
        if (lat == null || lon == null || lat !in -90.0..90.0 || lon !in -180.0..180.0) {
            return "Unknown location"
        }

        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses: List<Address>? = geocoder.getFromLocation(lat, lon, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                addresses[0].getAddressLine(0)
            } else {
                "Unknown location"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            "Unknown location"
        }
    }
}
